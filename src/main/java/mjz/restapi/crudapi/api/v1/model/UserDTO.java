package mjz.restapi.crudapi.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDTO {

    private String id;
    private String first_name;
    private String last_name;

    @JsonProperty("avatar")
    private String avatarBase64;
}
