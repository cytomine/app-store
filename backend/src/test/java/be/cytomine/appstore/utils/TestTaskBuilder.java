package be.cytomine.appstore.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import be.cytomine.appstore.dto.inputs.task.UploadTaskArchive;
import be.cytomine.appstore.dto.misc.TaskIdentifiers;
import be.cytomine.appstore.models.task.Author;
import be.cytomine.appstore.models.task.Parameter;
import be.cytomine.appstore.models.task.ParameterType;
import be.cytomine.appstore.models.task.Task;
import be.cytomine.appstore.models.task.Type;
import be.cytomine.appstore.models.task.TypeFactory;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

public class TestTaskBuilder {

    public static Task buildHardcodedAddInteger(UUID taskUUID) {
        String storageIdentifier = "task-" + taskUUID + "-def";
        String imageRegistryCompliantName = "com/cytomine/dummy/arithmetic/integer/addition:1.0.0";
        TaskIdentifiers taskIdentifiersForTaskOne = new TaskIdentifiers(
            taskUUID,
            storageIdentifier,
            imageRegistryCompliantName
        );

        // store two tasks in the database
        Task task = new Task();
        task.setIdentifier(taskIdentifiersForTaskOne.getLocalTaskIdentifier());
        task.setStorageReference(taskIdentifiersForTaskOne.getStorageIdentifier());
        task.setName("Integers addition");
        task.setNameShort("add_integers");
        task.setDescriptorFile("com.cytomine.dummy.arithmetic.integer.addition");
        task.setNamespace("com.cytomine.dummy.arithmetic.integer.addition");
        task.setVersion("1.0.0");
        task.setDescription("");
        task.setInputFolder("/inputs");
        task.setOutputFolder("/outputs");

        // add authors
        Set<Author> authors = new HashSet<>();
        Author author = new Author();
        author.setFirstName("some");
        author.setLastName("developer");
        author.setOrganization("some company");
        author.setEmail("some.developer@some.company.com");
        author.setContact(true);
        authors.add(author);
        task.setAuthors(authors);

        // add inputs

        Set<Parameter> inputs = new HashSet<>();
        Parameter inputa = new Parameter();
        inputa.setName("a");
        inputa.setDisplayName("Operand A");
        inputa.setDescription("First operand");
        Type inputType1_1 = new Type();
        inputType1_1.setId("integer");
        inputType1_1.setCharset("UTF_8");
        inputa.setType(inputType1_1);
        inputa.setDefaultValue("0");
        inputa.setParameterType(ParameterType.INPUT);

        Parameter inputb = new Parameter();
        inputb.setName("b");
        inputb.setDisplayName("Operand B");
        inputb.setDescription("Second operand");
        Type inputType1_2 = new Type();
        inputType1_2.setId("integer");
        inputType1_2.setCharset("UTF_8");
        inputb.setType(inputType1_2);
        inputb.setDefaultValue("0");
        inputb.setParameterType(ParameterType.INPUT);

        inputs.add(inputa);
        inputs.add(inputb);
        task.setParameters(inputs);
        // add outputs for task one
        Set<Parameter> outputs = new HashSet<>();
        Parameter output = new Parameter();
        output.setName("sum");
        output.setDisplayName("Sum");
        output.setDescription("Sum of operands A and B");
        Type outputType = new Type();
        outputType.setId("integer");
        outputType.setCharset("UTF_8");
        output.setType(outputType);
        output.setParameterType(ParameterType.OUTPUT);
        outputs.add(output);
        task.getParameters().addAll(outputs);

        return task;
    }

    public static Task buildHardcodedAddInteger() {
        return buildHardcodedAddInteger(UUID.randomUUID());
    }

    public static Task buildHardcodedSubtractInteger(UUID taskUUID) {
        String storageIdentifierForTaskOne = "task-" + taskUUID + "-def";
        String imageRegistryCompliantNameForTaskOne = "com/cytomine/dummy/arithmetic/integer/subtraction:1.0.0";
        TaskIdentifiers taskIdentifiersForTaskOne = new TaskIdentifiers(
            taskUUID,
            storageIdentifierForTaskOne,
            imageRegistryCompliantNameForTaskOne
        );

        // store two tasks in the database
        Task task = new Task();
        task.setIdentifier(taskIdentifiersForTaskOne.getLocalTaskIdentifier());
        task.setStorageReference(taskIdentifiersForTaskOne.getStorageIdentifier());
        task.setName("Integers subtraction");
        task.setNameShort("sub_integers");
        task.setDescriptorFile("com.cytomine.dummy.arithmetic.integer.subtraction");
        task.setNamespace("com.cytomine.dummy.arithmetic.integer.subtraction");
        task.setVersion("1.0.0");
        task.setDescription("");

        // add authors
        Set<Author> authors = new HashSet<>();
        Author author = new Author();
        author.setFirstName("some");
        author.setLastName("developer");
        author.setOrganization("some uni");
        author.setEmail("some.developer@some.uni.com");
        author.setContact(true);
        authors.add(author);
        task.setAuthors(authors);

        // add inputs

        Set<Parameter> inputs = new HashSet<>();
        Parameter inputa = new Parameter();
        inputa.setName("a");
        inputa.setDisplayName("Operand A");
        inputa.setDescription("First operand");
        Type inputType1_1 = new Type();
        inputType1_1.setId("integer");
        inputa.setType(inputType1_1);
        inputa.setDefaultValue("0");
        inputa.setParameterType(ParameterType.INPUT);

        Parameter inputb = new Parameter();
        inputb.setName("b");
        inputb.setDisplayName("Operand B");
        inputb.setDescription("Second operand");
        Type inputType1_2 = new Type();
        inputType1_2.setId("integer");
        inputb.setType(inputType1_2);
        inputb.setDefaultValue("0");
        inputb.setParameterType(ParameterType.INPUT);

        inputs.add(inputa);
        inputs.add(inputb);
        task.setParameters(inputs);
        // add outputs for task one
        Set<Parameter> outputs = new HashSet<>();
        Parameter output = new Parameter();
        output.setName("out");
        output.setDisplayName("Difference");
        output.setDescription("Difference of operands A and B");
        Type outputType = new Type();
        outputType.setId("integer");
        output.setType(outputType);
        output.setParameterType(ParameterType.OUTPUT);
        outputs.add(output);
        task.getParameters().addAll(outputs);
        return task;
    }

    public static Task buildHardcodedSubtractInteger() {
        return buildHardcodedSubtractInteger(UUID.randomUUID());
    }

    public static ClassPathResource buildSubtractIntegerFromResourceBundle(UUID taskUUID) {
        return buildByBundleFilename("com.cytomine.dummy.arithmetic.integer.subtraction-1.0.0.zip");
    }

    public static ClassPathResource buildAddIntegerFromResourceBundle() {
        return buildByBundleFilename("com.cytomine.dummy.arithmetic.integer.addition-1.0.0.zip");
    }

    public static ClassPathResource buildWrongArchiveFormatTask() {
        return buildByBundleFilename("test_wrong_archive_format_task.7z");
    }

    public static ClassPathResource buildCustomImageLocationTask() {
        return buildByBundleFilename("test_custom_image_location_task.zip");
    }

    public static ClassPathResource buildDefaultImageLocationTask() {
        return buildByBundleFilename("test_default_image_location_task.zip");
    }

    public static ClassPathResource buildByBundleFilename(String bundleFilename) {
        return new ClassPathResource("/artifacts/" + bundleFilename);
    }

    public static Task buildTaskFromResource(String bundleFilename) {
        return buildTaskFromResource(bundleFilename, UUID.randomUUID());
    }

    public static Task buildTaskFromResource(String bundleFilename, UUID taskUUID) {
        ClassPathResource resource = buildByBundleFilename(bundleFilename);
        ArchiveUtils archiveUtils = new ArchiveUtils();

        try {
            MockMultipartFile taskMultipartFile = new MockMultipartFile(
                bundleFilename,
                resource.getInputStream()
            );
            UploadTaskArchive taskArchive = archiveUtils.readArchive(taskMultipartFile);

            Task task = new Task();
            task.setIdentifier(taskUUID);
            task.setStorageReference("task-" + taskUUID + "-def");
            JsonNode taskDescriptorJson = taskArchive.getDescriptorFileAsJson();
            task.setName(taskDescriptorJson.get("name").textValue());
            task.setNameShort(taskDescriptorJson.get("name_short").textValue());
            task.setDescriptorFile(taskDescriptorJson.get("namespace").textValue());
            task.setNamespace(taskDescriptorJson.get("namespace").textValue());
            task.setVersion(taskDescriptorJson.get("version").textValue());
            task.setInputFolder(taskDescriptorJson.get("configuration").get("input_folder").textValue());
            task.setOutputFolder(taskDescriptorJson.get("configuration").get("output_folder").textValue());
            if (taskDescriptorJson.get("description") != null) {
                task.setDescription(taskDescriptorJson.get("description").textValue());
            }

            JsonNode resources = taskDescriptorJson.get("configuration").get("resources");
            if (Objects.nonNull(resources)) {
                task.setRam(resources.path("ram").asText());
                task.setCpus(resources.path("cpus").asInt());
                task.setGpus(resources.path("gpus").asInt(0));
            }

            task.setAuthors(getAuthors(taskArchive));
            task.setParameters(getInputs(taskArchive));
            task.getParameters().addAll(getOnputs(taskArchive));
            return task;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File getDescriptorFromBundleResource(String bundleFilename) {
        ClassPathResource resource = buildByBundleFilename(bundleFilename);
        ArchiveUtils archiveUtils = new ArchiveUtils();
        try {
            MockMultipartFile taskMultipartFile = new MockMultipartFile(bundleFilename, resource.getInputStream());
            UploadTaskArchive taskArchive = archiveUtils.readArchive(taskMultipartFile);
            return taskArchive.getDescriptorFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<Parameter> getInputs(UploadTaskArchive uploadTaskArchive) {
        Set<Parameter> inputs = new HashSet<>();
        JsonNode inputsNode = uploadTaskArchive.getDescriptorFileAsJson().get("inputs");
        if (inputsNode.isObject()) {
            Iterator<String> fieldNames = inputsNode.fieldNames();
            while (fieldNames.hasNext()) {
                String inputKey = fieldNames.next();
                JsonNode inputValue = inputsNode.get(inputKey);

                Parameter input = new Parameter();
                input.setName(inputKey);
                input.setDisplayName(inputValue.get("display_name").textValue());
                input.setDescription(inputValue.get("description").textValue());
                input.setParameterType(ParameterType.INPUT);
                // use type factory to generate the correct type
                input.setType(TypeFactory.createType(inputValue, "UTF_8"));
                switch (TypeFactory.getTypeId(inputValue.get("type"))) {
                    case "boolean":
                        input.setDefaultValue("false");
                        break;
                    case "integer":
                        input.setDefaultValue("0");
                        break;
                    default:
                        input.setDefaultValue("");
                        break;
                }

                inputs.add(input);
            }
        }
        return inputs;
    }

    private static Set<Parameter> getOnputs(UploadTaskArchive uploadTaskArchive) {
        Set<Parameter> outputs = new HashSet<>();
        JsonNode outputsNode = uploadTaskArchive.getDescriptorFileAsJson().get("outputs");
        if (outputsNode.isObject()) {
            Iterator<String> fieldNames = outputsNode.fieldNames();
            while (fieldNames.hasNext()) {
                String outputKey = fieldNames.next();
                JsonNode inputValue = outputsNode.get(outputKey);

                Parameter output = new Parameter();
                output.setName(outputKey);
                output.setDisplayName(inputValue.get("display_name").textValue());
                output.setDescription(inputValue.get("description").textValue());
                output.setParameterType(ParameterType.OUTPUT);
                // use type factory to generate the correct type
                output.setType(TypeFactory.createType(inputValue, "UTF_8"));

                outputs.add(output);

            }
        }
        return outputs;
    }

    private static Set<Author> getAuthors(UploadTaskArchive uploadTaskArchive) {
        Set<Author> authors = new HashSet<>();
        JsonNode authorNode = uploadTaskArchive.getDescriptorFileAsJson().get("authors");
        if (authorNode.isArray()) {
            for (JsonNode author : authorNode) {
                Author a = new Author();
                a.setFirstName(author.get("first_name").textValue());
                a.setLastName(author.get("last_name").textValue());
                a.setOrganization(author.get("organization").textValue());
                a.setEmail(author.get("email").textValue());
                a.setContact(author.get("is_contact").asBoolean());
                authors.add(a);
            }
        }
        return authors;
    }
}
