package mjz.restapi.crudapi.services;

import mjz.restapi.crudapi.api.v1.model.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(String id) throws Exception;
    UserDTO createNewUser(UserDTO userDTO);

    UserDTO createNewUserWithImage(UserDTO userDTO, MultipartFile avatar) throws IOException;

    UserDTO updateUserByDto(String id, UserDTO userDTO) throws Exception;
    void deleteUserById(String id);


}