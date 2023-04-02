package mjz.restapi.crudapi.repositories;

import mjz.restapi.crudapi.domain.User;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    //Optional<User> findByFirst_nameAndLast_name(String firstName, String lastName);
}
