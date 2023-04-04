package mjz.restapi.crudapi.controllers.v1;

import mjz.restapi.crudapi.api.v1.model.UserDTO;
import mjz.restapi.crudapi.services.UserService;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
    public static final String BASE_URL = "/api/v1/users";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/paging")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDTO> getAllUsers(@RequestParam( name = "page", defaultValue = "0", required = false) Integer page,
                                     @RequestParam( name = "size", defaultValue = "20", required = false) Integer size,
                                     @RequestParam( name = "sort", defaultValue = "last_name", required = false) String sort) {
/*
        PagedListHolder pg = new PagedListHolder(list);
        pg.setPageSize(10); // number of items per page
        pg.setPage(0);      // set to first page

        // Retrieval
        pg.getPageCount(); // number of pages
        pg.getPageList();  // a List which represents the current page

 */


        PageRequest pr = PageRequest.of(page,size, Sort.by(sort).ascending());

        Page<UserDTO> returnedUsers = userService.getAllUsersPageable(pr);

        System.out.println("Total Elements: " + returnedUsers.getTotalElements() +
                " Total pages: " + returnedUsers.getTotalPages());


        return returnedUsers;
    }

    @GetMapping("/v2/paging")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDTO> getAllUsersPaged(@RequestParam( name = "page", defaultValue = "0", required = false) Integer page,
                                          @RequestParam( name = "size", defaultValue = "20", required = false) Integer size,
                                          @RequestParam( name = "sort", defaultValue = "last_name", required = false) String sort) {

        List<UserDTO> allUsers = userService.getAllUsers();
        PageRequest pr = PageRequest.of(page,size, Sort.by(sort));

        int start = (int) pr.getOffset();
        int end = (int) ((start + pr.getPageSize()) > allUsers.size() ? allUsers.size()
                : (start + pr.getPageSize()));

        // sorting with Page does not work with this method, so we MUST sort the list first and then paginate it
        allUsers.sort(Comparator.comparing(UserDTO::getLast_name));

        Page<UserDTO> pagedUsers = new PageImpl<UserDTO>(allUsers.subList(start, end), pr, allUsers.size());


        return pagedUsers;
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
