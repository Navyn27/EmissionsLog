package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Exceptions.EmailAlreadyExistsException;
import com.navyn.emissionlog.Exceptions.UnmatchingPasswordsException;
import com.navyn.emissionlog.Models.User;
import com.navyn.emissionlog.Payload.Requests.LoginDTO;
import com.navyn.emissionlog.Payload.Requests.SignUpDTO;
import com.navyn.emissionlog.Repositories.UserRepository;
import com.navyn.emissionlog.Repositories.WorkspaceRepository;
import com.navyn.emissionlog.Services.UserService;
import com.navyn.emissionlog.Utils.GenerateOTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    private WorkspaceRepository recordingEntityRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private UserServiceImpl(UserRepository userRepository, WorkspaceRepository recordingEntityRepository) {
        this.userRepository = userRepository;
        this.recordingEntityRepository = recordingEntityRepository;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }

    @Override
    public String registerUser(SignUpDTO payload) throws UnmatchingPasswordsException, EmailAlreadyExistsException {
        if (!Objects.equals(payload.getPassword(), payload.getConfirmPassword())) {
            throw new UnmatchingPasswordsException();
        }
        double otp = GenerateOTP.generateOTP();

        if (userRepository.findByEmail(payload.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setEmail(payload.getEmail());
        user.setFirstname(payload.getFirstName());
        user.setLastname(payload.getLastName());
        user.setPhoneNumber(payload.getPhoneNumber());
        user.setPassword(encoder.encode(payload.getPassword()));
        user.setRole(payload.getRole());
        user.setOtp(otp);
        user.setRecord(null);
        User savedUser = userRepository.save(user);

        if (payload.getRecord() == null) {
            com.navyn.emissionlog.Models.Workspace record = new com.navyn.emissionlog.Models.Workspace();
            record.setAdmin(savedUser);
            com.navyn.emissionlog.Models.Workspace savedRecord = recordingEntityRepository.save(record);
            savedUser.setRecord(savedRecord);
            userRepository.save(savedUser);
        }
        return jwtService.generateToken(savedUser.getEmail());
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public String login(LoginDTO user) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()
                        ));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getEmail());
        }
        return null;
    }

    @Override
    public boolean deleteUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            userRepository.deleteById(user.get().getId());
            return true;
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User updateUser(String email, SignUpDTO payload) {
        Optional<User> user1 = userRepository.findByEmail(email);
        if (user1.isPresent()) {
            User user = user1.get();
            user.setEmail(payload.getEmail());
            user.setFirstname(payload.getFirstName());
            user.setLastname(payload.getLastName());
            user.setPhoneNumber(payload.getPhoneNumber());
            user.setPassword(payload.getPassword());
            user.setRole(payload.getRole());
            return userRepository.save(user);
        }
        throw new UsernameNotFoundException("User not found");
    }
}