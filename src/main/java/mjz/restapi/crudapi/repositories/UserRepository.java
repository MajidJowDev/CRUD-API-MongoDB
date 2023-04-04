package mjz.restapi.crudapi.repositories;

import mjz.restapi.crudapi.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, String> {


    //Optional<User> findByFirst_nameAndLast_name(String firstName, String lastName);
}
