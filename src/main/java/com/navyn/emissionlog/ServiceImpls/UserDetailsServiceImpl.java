package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.User;
import com.navyn.emissionlog.Models.UserPrincipal;
import com.navyn.emissionlog.Repositories.UserRepository;
import com.navyn.emissionlog.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User with email: "+email+" does not exist.");
        }

        return new UserPrincipal(user.get());
    }
}
