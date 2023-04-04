package mjz.restapi.crudapi.services;

import lombok.extern.slf4j.Slf4j;
import mjz.restapi.crudapi.api.v1.mapper.UserMapper;
import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.config.RabbitMQConfig;
import mjz.restapi.crudapi.domain.User;
import mjz.restapi.crudapi.repositories.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.stream;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final RabbitTemplate rabbitTemplate;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    //todo: refactor this method for pagination with correct format
    @Override
    public Page<UserDTO> getAllUsersPageable(Pageable pageable) {
        List<UserDTO> users = userRepository.findAll(pageable)
                .stream()
                .map(user -> {
                    UserDTO userDTO = userMapper.userToUserDTO(user);
                    if(user.getAvatar() != null) {
                        userDTO.setAvatarBase64(bytesToBase64Converter(user.getAvatar()));
                    }
                    return userDTO;
                }).collect(Collectors.toList());

        //Page<Foo> page = new PageImpl<Foo>(fooList.subList(start, end), pageable, fooList.size());
        Page<UserDTO> pagedUsers = new PageImpl<UserDTO>(users.subList(0, users.size() - 1 ), pageable, users.size());
        return pagedUsers;
        //return new PageImpl<>(users);

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

        sendRabbitMessage(savedUserDto);

        return savedUserDto;
    }


    @Override
    public UserDTO createNewUserWithImage(UserDTO userDTO, MultipartFile avatar) throws IOException {
        User tobeSavedUser = userMapper.userDtoToUser(userDTO);
        tobeSavedUser.setAvatar(avatar.getBytes());
        UserDTO savedUserDto = saveAndReturnDto(tobeSavedUser);

        sendRabbitMessage(savedUserDto);

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

    private void sendRabbitMessage(UserDTO userDTO) {
        log.info("Sending Rabbit Message!!!");

        String message = "Username Created, Id: " + userDTO.getId() + ", First name: " + userDTO.getFirst_name() + ", Last name: " + userDTO.getLast_name();
        rabbitTemplate.convertAndSend(RabbitMQConfig.MESSAGE_QUEUE, message);
        //rabbitTemplate.convertAndSend(RabbitMQConfig.MESSAGE_QUEUE, userDTO);
        log.info("Rabbit Message sent");
    }
}
