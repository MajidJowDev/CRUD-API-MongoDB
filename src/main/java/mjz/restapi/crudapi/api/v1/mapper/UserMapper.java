package mjz.restapi.crudapi.api.v1.mapper;

import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);

    User userDtoToUser(UserDTO userDTO);

}
