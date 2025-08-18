package be.cytomine.appstore.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import be.cytomine.appstore.dto.responses.errors.AppStoreError;
import be.cytomine.appstore.dto.responses.errors.ErrorBuilder;
import be.cytomine.appstore.dto.responses.errors.ErrorCode;
import be.cytomine.appstore.exceptions.BundleArchiveException;
import be.cytomine.appstore.exceptions.TaskServiceException;
import be.cytomine.appstore.exceptions.ValidationException;

@Slf4j
@ControllerAdvice
@Order(0)
public class UploadTaskApiExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String max;

    @ExceptionHandler({ TaskServiceException.class })
    public final ResponseEntity<AppStoreError> handleUploadProcessingExceptionException(
        TaskServiceException e
    ) {
        log.info("Internal server error [" + e.getMessage() + "]");
        e.printStackTrace();
        return new ResponseEntity<AppStoreError>(e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ ValidationException.class })
    public final ResponseEntity<AppStoreError> handleTaskBundleValidationException(
        ValidationException e
    ) {
        log.info("Validation failure [" + e.getError().getMessage() + "]");

        if (internalError(e)) {
            return new ResponseEntity<AppStoreError>(
                e.getError(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        if (violatingRequest(e)) {
            return new ResponseEntity<AppStoreError>(e.getError(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<AppStoreError>(e.getError(), HttpStatus.BAD_REQUEST);
    }

    private static boolean internalError(ValidationException e) {
        return e.isInternalError() && !e.isIntegrityViolated();
    }

    private static boolean violatingRequest(ValidationException e) {
        return !e.isInternalError() && e.isIntegrityViolated();
    }

    @ExceptionHandler({ BundleArchiveException.class })
    public final ResponseEntity<AppStoreError> handleTaskBundleZipException(
        BundleArchiveException e,
        WebRequest request
    ) {
        log.info("Bundle/Archive processing failure [{}]", e.getMessage());
        return new ResponseEntity<AppStoreError>(e.getError(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MaxUploadSizeExceededException.class })
    public final ResponseEntity<AppStoreError> handleUploadSizeExceeded(
        Exception e,
        WebRequest request
    ) {
        String message = "Maximum app bundle size of " + max + " exceeded";
        log.info("Bundle/Archive processing failure [{}]", message);

        AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_MAX_UPLOAD_SIZE_EXCEEDED);
        return new ResponseEntity<AppStoreError>(error, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    public final ResponseEntity<AppStoreError> handleMalformattedBundle(Exception e) {
        String message = "Bundle is not formatted correctly";
        log.info("Bundle/Archive processing failure [{}]", e.getMessage());

        AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_INVALID_BUNDLE_FORMAT);
        return new ResponseEntity<AppStoreError>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ NullPointerException.class })
    public final ResponseEntity<AppStoreError> handleNullpointerException(Exception e) {
        // TODO temp handler remove later
        String message = "missing properties in descriptor.yml";
        log.info("Bundle/Archive processing failure [{}]", message);

        AppStoreError error = ErrorBuilder.build(ErrorCode.INTERNAL_INVALID_BUNDLE_FORMAT);
        return new ResponseEntity<AppStoreError>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
