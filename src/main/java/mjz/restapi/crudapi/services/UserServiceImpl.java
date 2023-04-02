package mjz.restapi.crudapi.services;

import mjz.restapi.crudapi.api.v1.mapper.UserMapper;
import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.domain.User;
import mjz.restapi.crudapi.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> getAllUsers() {

        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(user -> {
                    UserDTO userDTO = userMapper.userToUserDTO(user);
                    if(user.getAvatar() != null) {
                        userDTO.setAvatarBase64(bytesToBase64Converter(user.getAvatar()));
                    }
                    return userDTO;
                })
                .collect(Collectors.toList());

    }

    @Override
    public UserDTO getUserById(String id) throws Exception {

        return userRepository.findById(id)
                .map(user -> {
                    UserDTO userDTO = userMapper.userToUserDTO(user);
                    userDTO.setAvatarBase64(bytesToBase64Converter(user.getAvatar()));
                    return userDTO;
                }).orElseThrow(() -> {
                    return new Exception("User not found");
                });
    }

    @Override
    public UserDTO createNewUser(UserDTO userDTO) {
        User tobeSavedUser = userMapper.userDtoToUser(userDTO);
        tobeSavedUser.setAvatar(null);
        UserDTO savedUserDto = saveAndReturnDto(tobeSavedUser);

        return savedUserDto;
    }


    @Override
    public UserDTO createNewUserWithImage(UserDTO userDTO, MultipartFile avatar) throws IOException {
        User tobeSavedUser = userMapper.userDtoToUser(userDTO);
        tobeSavedUser.setAvatar(avatar.getBytes());
        UserDTO savedUserDto = saveAndReturnDto(tobeSavedUser);

        return savedUserDto;
    }

    @Override
    public UserDTO updateUserByDto(String id, UserDTO userDTO) throws Exception {

        //Patch Logic
        return userRepository.findById(id)
                .map(user -> {
                    if(userDTO.getFirst_name() != null) {
                        user.setFirst_name(userDTO.getFirst_name());
                    }

                    if(userDTO.getLast_name() != null) {
                        user.setLast_name(userDTO.getLast_name());
                    }

                    return saveAndReturnDto(userRepository.save(user));

                }).orElseThrow(() -> {
                    return new Exception("User not found");
                });

        /*
        //Update Logic
        User user = userMapper.userDtoToUser(userDTO);

        user.setId(id);

        return saveAndReturnDto(user);
         */
    }

    @Override
    public void deleteUserById(String id) {

        userRepository.deleteById(id);
    }

    private String bytesToBase64Converter(byte[] bytes) {

         byte[] encoded =  Base64Utils.encode(bytes);

         return new String(encoded);
    }

    private UserDTO saveAndReturnDto (User user) {

        User savedUser = userRepository.save(user);

        UserDTO returnDto = userMapper.userToUserDTO(savedUser);
        if(user.getAvatar() != null) {
            returnDto.setAvatarBase64(bytesToBase64Converter(user.getAvatar()));
        }
        return returnDto;

    }
}
