package mjz.restapi.crudapi.services;

import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.api.v1.model.UserDtoData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {


    Page<UserDTO> getAllUsersPageable(Pageable pageable);
    List<UserDTO> getAllUsers();

    List<UserDTO> saveUsersFromOtherAPI() throws IOException;

    UserDTO getUserById(String id) throws Exception;
    UserDTO createNewUser(UserDTO userDTO);

    UserDTO createNewUserWithImage(UserDTO userDTO, MultipartFile avatar) throws IOException;

    UserDTO updateUserByDto(String id, UserDTO userDTO) throws Exception;
    void deleteUserById(String id);


}
