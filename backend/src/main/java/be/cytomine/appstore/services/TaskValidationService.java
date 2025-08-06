package be.cytomine.appstore.services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import be.cytomine.appstore.dto.inputs.task.UploadTaskArchive;
import be.cytomine.appstore.dto.responses.errors.AppStoreError;
import be.cytomine.appstore.dto.responses.errors.ErrorBuilder;
import be.cytomine.appstore.dto.responses.errors.ErrorCode;
import be.cytomine.appstore.dto.responses.errors.details.ParameterError;
import be.cytomine.appstore.exceptions.ValidationException;
import be.cytomine.appstore.models.task.Task;
import be.cytomine.appstore.repositories.TaskRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskValidationService
{

    private final TaskRepository repository;

    public void checkIsNotDuplicate(UploadTaskArchive task) throws ValidationException {
        Task found = repository.findByNamespaceAndVersion(
            task.getDescriptorFileAsJson().get("namespace").textValue(),
            task.getDescriptorFileAsJson().get("version").textValue()
        );
        if (found != null) {
            AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_TASK_EXISTS);
            throw new ValidationException(error, false, true);
        }
    }

    public void validateImage(UploadTaskArchive task) throws ValidationException {
        checkManifestJsonExists(task);
    }

    private void checkManifestJsonExists(UploadTaskArchive task) throws ValidationException {
        try (
            FileInputStream fis = new FileInputStream(task.getDockerImage());
            BufferedInputStream bis = new BufferedInputStream(fis);
            TarArchiveInputStream tais = new TarArchiveInputStream(bis)
        ) {
            TarArchiveEntry tarArchiveEntry;

            while ((tarArchiveEntry = tais.getNextTarEntry()) != null) {
                String name = tarArchiveEntry.getName();
                if (name.equalsIgnoreCase("manifest.json")) {
                    return;
                }
            }
        } catch (IOException e) {
            log.error("Failed to check for manifest.json in the Docker image", e);
            AppStoreError error = ErrorBuilder.build(
                ErrorCode.INTERNAL_DOCKER_IMAGE_EXTRACTION_FAILED
            );
            throw new ValidationException(error);
        }

        log.info("Validation error [manifest.json does not exist]");
        AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_DOCKER_IMAGE_MANIFEST_MISSING);
        throw new ValidationException(error);
    }

    public void validateDescriptorFile(UploadTaskArchive archive) throws ValidationException {
        Set<ValidationMessage> errors = getDescriptorJsonSchemaV7()
            .validate(archive.getDescriptorFileAsJson());
        // prepare error list just in case
        List<AppStoreError> multipleErrors = new ArrayList<>();
        for (ValidationMessage message : errors) {
            AppStoreError error = buildErrorFromValidationMessage(message);
            multipleErrors.add(error);
        }

        if (!multipleErrors.isEmpty()) {
            AppStoreError error = ErrorBuilder.buildSchemaValidationError(multipleErrors);
            throw new ValidationException(error);
        }
    }

    @NotNull
    private static AppStoreError buildErrorFromValidationMessage(ValidationMessage message) {
        String parameterPathInSchema = message
            .getMessage()
            .substring(0, message.getMessage().indexOf(':'))
            .replace("$.", "");
        ParameterError parameterError = new ParameterError(parameterPathInSchema);
        String formattedMessage = message
            .getMessage()
            .replace("$.", "")
            .replace(":", "")
            .replace(".", " ");
        AppStoreError error = ErrorBuilder.buildWithMessage(
            ErrorCode.INTERNAL_PARAMETER_SCHEMA_VALIDATION_ERROR,
            formattedMessage,
            parameterError
        );
        return error;
    }

    private static JsonSchema getDescriptorJsonSchemaV7() throws ValidationException {
        ClassPathResource resource = new ClassPathResource("/schemas/tasks/task.v0.json");

        JsonNode schemaJson;
        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            try (JsonParser jsonParser = mapper.getFactory().createParser(content)) {
                schemaJson = jsonParser.readValueAsTree();
            }
        } catch (IOException e) {
            AppStoreError error = ErrorBuilder.build(
                ErrorCode.INTERNAL_DESCRIPTOR_EXTRACTION_FAILED
            );
            throw new ValidationException(error, true);
        }

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        return schemaFactory.getSchema(schemaJson);
    }
}
