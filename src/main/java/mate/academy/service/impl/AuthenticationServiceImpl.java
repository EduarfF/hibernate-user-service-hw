package mate.academy.service.impl;

import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        if (isInvalidEmailOrPassword(email, password)) {
            throw new AuthenticationException("Email or password are invalid");
        }

        User userFromDb = userService.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException(
                        "User with such email doesn't already exist"));

        byte[] salt = userFromDb.getSalt();
        String hashPass = HashUtil.hashPassword(password, salt);
        if (userFromDb.getPassword().equals(hashPass)) {
            return userFromDb;
        }
        throw new AuthenticationException("Wrong password");
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (isInvalidEmailOrPassword(email, password)) {
            throw new RegistrationException("Email or password are invalid");
        }

        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("User with such email has already exist: " + email);
        }
        User newUser = new User(email, password);
        return userService.add(newUser);
    }

    private boolean isInvalidEmailOrPassword(String email, String password) {
        return email == null || email.isBlank()
                || password == null || password.isBlank();
    }
}