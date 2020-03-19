package academy.redoak.servlet.chatserver.service;

import academy.redoak.servlet.chatserver.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthService {

    private List<User> users;

    private AuthService() {
        this.users = new ArrayList<>();
    }

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

    public void unregister(User user) {
        this.users.remove(user);
    }

    public List<User> getUsers() {
        return users;
    }

    // --- Singleton

    private static AuthService instance = new AuthService();

    public static AuthService getInstance() {
        return instance;
    }
}
