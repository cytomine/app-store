package be.cytomine.appstore.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;

import be.cytomine.appstore.dto.inputs.task.UploadTaskArchive;
import be.cytomine.appstore.dto.responses.errors.AppStoreError;
import be.cytomine.appstore.dto.responses.errors.ErrorBuilder;
import be.cytomine.appstore.dto.responses.errors.ErrorCode;
import be.cytomine.appstore.exceptions.BundleArchiveException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ArchiveUtils {

    private static final String DEFAULT_IMAGE_NAME = "image.tar";

    private boolean isZip(MultipartFile archive) throws BundleArchiveException {
        Tika tika = new Tika();
        try {
            String type = tika.detect(archive.getInputStream());
            log.info("ArchiveUtils: archive detected {}", type);

            return type.equalsIgnoreCase("application/zip");
        } catch (IOException e) {
            AppStoreError error = ErrorBuilder.build(
                ErrorCode.INTERNAL_UNKNOWN_IMAGE_ARCHIVE_FORMAT
            );
            throw new BundleArchiveException(error);
        }
    }

    public UploadTaskArchive readArchive(MultipartFile archive) throws BundleArchiveException {
        if (isZip(archive)) {
            return readZipArchive(archive);
        }

        AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_UNKNOWN_BUNDLE_ARCHIVE_FORAMT);
        throw new BundleArchiveException(error);
    }

    public UploadTaskArchive readZipArchive(MultipartFile archive) throws BundleArchiveException {
        File descriptorData = getDescriptorFileFromZip(archive);
        File imageData = getDockerImageFromZip(archive, getCustomImageName(descriptorData));

        return new UploadTaskArchive(descriptorData, imageData);
    }

    private String getCustomImageName(File descriptorData) {
        try {
            return DescriptorHelper.parseDescriptor(descriptorData)
                .get("configuration")
                .get("image")
                .get("file")
                .textValue();
        } catch (Exception e) {
            log.info(
                "Bundle/Archive processing failure: "
                + "no image location is configured in descriptor.yml, "
                + "will fallback to default image location in root"
            );
        }

        return DEFAULT_IMAGE_NAME;
    }

    private File getDescriptorFileFromZip(MultipartFile archive) throws BundleArchiveException {
        log.info("ArchiveUtils: parse descriptor file...");

        ZipEntry entry;
        try (ZipArchiveInputStream zais = new ZipArchiveInputStream(archive.getInputStream())) {
            while ((entry = zais.getNextZipEntry()) != null) {
                if (entry.getName().toLowerCase().matches("descriptor\\.(yml|yaml)")) {
                    File tempFile = Files.createTempFile("descriptor-", ".yaml").toFile();
                    tempFile.deleteOnExit(); 

                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        zais.transferTo(fos);
                    }

                    log.info("ArchiveUtils: descriptor file successfully extracted");
                    return tempFile;
                }
            }
        } catch (Exception e) {
            log.error(
                "Failed to extract descriptor file from archive: {}",
                archive.getOriginalFilename(),
                e
            );
            AppStoreError error = ErrorBuilder.build(
                ErrorCode.INTERNAL_DESCRIPTOR_EXTRACTION_FAILED
            );
            throw new BundleArchiveException(error);
        }

        log.error("Descriptor file not found in archive: {}", archive.getOriginalFilename());
        AppStoreError error = ErrorBuilder.build(
            ErrorCode.INTERNAL_DESCRIPTOR_NOT_IN_DEFAULT_LOCATION
        );
        throw new BundleArchiveException(error);
    }

    private File getDockerImageFromZip(
        MultipartFile archive,
        String imageName
    ) throws BundleArchiveException {
        log.info("ArchiveUtils: read image from location [{}]", imageName);

        if (imageName.startsWith("/")) {
            imageName = imageName.substring(1);
        }

        ZipEntry entry;
        try (ZipArchiveInputStream zais = new ZipArchiveInputStream(archive.getInputStream())) {
            while ((entry = zais.getNextZipEntry()) != null) {
                if (entry.getName().equalsIgnoreCase(imageName)) {
                    File dockerImage = File.createTempFile("docker-image-", ".tar");

                    try (FileOutputStream fos = new FileOutputStream(dockerImage)) {
                        IOUtils.copyLarge(zais, fos);
                    }

                    log.info("ArchiveUtils: Image successfully extracted");
                    return dockerImage;
                }
            }
        } catch (IOException e) {
            log.error(
                "Failed to extract Docker image from archive: {}",
                archive.getOriginalFilename(),
                e
            );
            AppStoreError error = ErrorBuilder.build(
                ErrorCode.INTERNAL_DOCKER_IMAGE_EXTRACTION_FAILED
            );
            throw new BundleArchiveException(error);
        }

        log.error("Docker image not found in archive: {}", imageName);
        AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_DOCKER_IMAGE_TAR_NOT_FOUND);
        throw new BundleArchiveException(error);
    }
}
