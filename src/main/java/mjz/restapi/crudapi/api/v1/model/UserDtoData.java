package mjz.restapi.crudapi.api.v1.model;

import java.util.List;

public class UserDtoData {

    List<UserDTO> data;

    public List<UserDTO> getData() {
        return data;
    }

    public void setData(List<UserDTO> data) {
        this.data = data;
    }
}
