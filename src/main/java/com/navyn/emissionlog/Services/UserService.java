package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Exceptions.EmailAlreadyExistsException;
import com.navyn.emissionlog.Exceptions.UnmatchingPasswordsException;
import com.navyn.emissionlog.Models.User;
import com.navyn.emissionlog.Payload.Requests.LoginDTO;
import com.navyn.emissionlog.Payload.Requests.SignUpDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    public Optional<User> getUser(String email);
    public String registerUser(SignUpDTO payload) throws UnmatchingPasswordsException, EmailAlreadyExistsException;
    public List<User> getUsers();
    public String login(LoginDTO user);

    public boolean deleteUser(String email);

    User updateUser(String email, SignUpDTO payload);
}
