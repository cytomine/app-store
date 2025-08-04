package be.cytomine.appstore.dto.inputs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskOutput {
    private String id;

    private String name;

    @JsonProperty(value = "display_name")
    private String displayName;

    private String description;

    private boolean optional;

    private TaskParameterType type;

    @JsonProperty(value = "derived_from")
    private String derivedFrom;
}
