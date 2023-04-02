package mjz.restapi.crudapi.controllers.v1;

import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
    public static final String BASE_URL = "/api/v1/users";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable String id) throws Exception {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //public UserDTO createUserWithImage( @RequestBody UserDTO user, @RequestBody MultipartFile avatar) throws IOException {
    public UserDTO createUserWithImage( @RequestPart("user") UserDTO user,
                                        @RequestPart("avatar") MultipartFile avatar) throws IOException {
        // To be able to post both text data (json) and file we can use @RequestPart or use a wrapper for text values and multipartfile

        UserDTO usr = user;
        MultipartFile img = avatar;

        return userService.createNewUserWithImage(user, avatar);
        //return userService.createNewUser(user);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateUserByDto(@PathVariable String id, @RequestBody UserDTO userDTO) throws Exception {

        return userService.updateUserByDto(id, userDTO);

    }

}
