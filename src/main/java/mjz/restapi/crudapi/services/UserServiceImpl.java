package mjz.restapi.crudapi.services;

import mjz.restapi.crudapi.api.v1.mapper.UserMapper;
import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.api.v1.model.UserDtoData;
import mjz.restapi.crudapi.domain.User;
import mjz.restapi.crudapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final String api_url;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RestTemplate restTemplate, @Value("${api.url}") String api_url) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
        this.api_url = api_url;
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
    public List<UserDTO> saveUsersFromOtherAPI() throws IOException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(api_url);
                //.queryParam("limit", limit);

        UserDtoData users = restTemplate.getForObject(uriBuilder.toUriString(), UserDtoData.class );

        for (UserDTO userData: users.getData() ) {
            createNewUserFromAPI(userData);
        }

        return getAllUsers();

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

    public UserDTO createNewUserFromAPI(UserDTO userDTO) throws IOException {
        User tobeSavedUser = userMapper.userDtoToUser(userDTO);
        tobeSavedUser.setAvatar(readBytesOfFileFromUrl(userDTO.getAvatarBase64()));
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

    private byte[] readBytesOfFileFromUrl(String fileUrl) throws IOException {

        URL url = new URL(fileUrl);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = url.openStream ();
            byte[] byteChunk = new byte[1024]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
            return null;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }

        /*
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(new URL(url).getFile())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            return dataBuffer;
        } catch (IOException e) {
            // handle exception
            return null;
        }

         */
    }
}
