package com.huseyincan.financeportfolio.util;

import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.dto.UserPrincipal;
import com.huseyincan.financeportfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component //Spring context'de bu classtan bir bean oluşturmak için kullanıyoruz.
public class UserDetailService implements UserDetailsService {

    @Autowired
    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findItemByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email + " is not found.");
        }
        return new UserPrincipal(user);
    }
}
