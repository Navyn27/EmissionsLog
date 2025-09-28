package com.navyn.emissionlog.modules.users.services;

import com.navyn.emissionlog.Exceptions.EmailAlreadyExistsException;
import com.navyn.emissionlog.Exceptions.UnmatchingPasswordsException;
import com.navyn.emissionlog.modules.users.User;
import com.navyn.emissionlog.modules.auth.dtos.LoginDTO;
import com.navyn.emissionlog.modules.auth.dtos.SignUpDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public User getUserByEmail(String email);
    public String registerUser(SignUpDTO payload) throws UnmatchingPasswordsException, EmailAlreadyExistsException;
    public List<User> getUsers();
    public String login(LoginDTO user);

    public boolean deleteUser(String email);

    User updateUser(String email, SignUpDTO payload);
}
