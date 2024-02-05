package com.huseyincan.financeportfolio.service.auth;

import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.exception.EmailExistsException;
import com.huseyincan.financeportfolio.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(String email, String password) throws EmailExistsException {
        if (userRepository.existsUserByEmail(email)) {
            String errorMessage = String.format("There is an account with that email adress: %s", email);
            log.error(errorMessage);
            throw new EmailExistsException(errorMessage);
        }
        userRepository.insert(new User(email, passwordEncoder.encode(password)));
    }

    public String getUserForEmail(String email) {
        return userRepository.findItemByEmail(email).toString();
    }
}
