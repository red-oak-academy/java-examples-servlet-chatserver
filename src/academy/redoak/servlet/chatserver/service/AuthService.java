package academy.redoak.servlet.chatserver.service;

import academy.redoak.servlet.chatserver.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton service offering methods for registering and unregistering users. Singleton instance may be retrieved
 * by {@link #getInstance()}.
 */
public class AuthService {

    private List<User> users;

    private AuthService() {
        this.users = new ArrayList<>();
    }

    /**
     * Registers a new user with given username.
     *
     * @param username The user name to register.
     * @return If name is already taken, the existing user is returned, otherwise a
     *      new one is created.
     */
    public User register(String username) {
        Optional<User> optional = this.users.stream().filter(user -> user.getName().equals(username)).findFirst();
        if(optional.isPresent()) {
            return optional.get();
        } else {
            User user = new User(UUID.randomUUID().toString(), username);
            this.users.add(user);
            return user;
        }
    }

    /**
     * Unregisters given user in application.
     *
     * @param user The user to be unregistered.
     */
    public void unregister(User user) {
        user.setName(user.getName() + " (removed)");
        this.users.remove(user);
    }

    /**
     * @return An unmodifiable list of registeres users. For modification see other methods.
     */
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    // --- Singleton

    private static AuthService instance = new AuthService();

    /**
     * @return The singleton instance.
     */
    public static AuthService getInstance() {
        return instance;
    }
}
