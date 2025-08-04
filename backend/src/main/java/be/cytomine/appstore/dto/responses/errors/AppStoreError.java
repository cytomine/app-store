package be.cytomine.appstore.dto.responses.errors;

import be.cytomine.appstore.dto.responses.errors.details.BaseErrorDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppStoreError {
    @JsonProperty("error_code")
    private String errorCode;

    private String message;

    private BaseErrorDetails details;
}
