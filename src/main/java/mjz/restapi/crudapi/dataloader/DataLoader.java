package mjz.restapi.crudapi.dataloader;

import lombok.extern.slf4j.Slf4j;
import mjz.restapi.crudapi.domain.User;
import mjz.restapi.crudapi.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadUsers();
    }

    private void loadUsers() {
        User majid = new User();
        majid.setFirst_name("Majid");
        majid.setLast_name("Zoghi");
        majid.setAvatar(loadFileAndConvertToBytes("01.jpg"));
        userRepository.save(majid);

        User ali = new User();
        ali.setFirst_name("Ali");
        ali.setLast_name("Karimi");
        ali.setAvatar(loadFileAndConvertToBytes("03.jpg"));
        userRepository.save(ali);

        User julia = new User();
        julia.setFirst_name("Julia");
        julia.setLast_name("Stone");
        julia.setAvatar(loadFileAndConvertToBytes("02.jpg"));
        userRepository.save(julia);

        User jane = new User();
        jane.setFirst_name("Jane");
        jane.setLast_name("Harper");
        jane.setAvatar(loadFileAndConvertToBytes("04.jpg"));
        userRepository.save(jane);

        System.out.println("Users Loaded: " + userRepository.count()) ;

    }

    //Load files from classpath, Resources/static/avatars
    private byte[] loadFileAndConvertToBytes(String fileName) {

        try {
            File file = ResourceUtils.getFile("classpath:static/avatars/" + fileName);

            //File is found
            System.out.println("File '" + fileName + "' Found: " + file.exists());
            log.info("File '" + fileName + "' Found: " + file.exists());

            //Read File Content
           // String content = new String(Files.readAllBytes(file.toPath()));
            //System.out.println(content);
            return Files.readAllBytes(file.toPath());

        } catch (Exception x) {

            System.out.println("Error Loading file: " + fileName + " Exception: " + x.getMessage());
            log.error("Error Loading file: " + fileName + " Exception: " + x.getMessage());
            return null;

        }
    }
}
