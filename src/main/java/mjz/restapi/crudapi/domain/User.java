package mjz.restapi.crudapi.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@Document("user")
public class User {
    @Id
    private String id; //= UUID.randomUUID().toString();

    private String first_name;
    private String last_name;
    private byte[] avatar;

    public User() {
    }

    public User(String first_name, String last_name, byte[] avatar) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.avatar = avatar;
    }

}
