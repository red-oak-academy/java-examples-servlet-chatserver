package academy.redoak.servlet.chatserver.http.auth;

import academy.redoak.servlet.chatserver.http.AbstractChatRoomServlet;
import academy.redoak.servlet.chatserver.http.Status;
import academy.redoak.servlet.chatserver.model.User;
import academy.redoak.servlet.chatserver.service.AuthService;
import academy.redoak.servlet.chatserver.util.Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/users")
public class AuthenticationServlet extends AbstractChatRoomServlet {

    private AuthService authService = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthResponse response = new AuthResponse();
        try {
            Optional<User> optional = getAuthorizedUser(req);
            if (!optional.isPresent()) {
                unauthorized(response, resp);
                return;
            }

            User user = optional.get();
            response.setUser(toJson(user));
            response.setStatus(Status.OK);
            writeResponse(response, resp, 200);
            return;
        } catch (Exception up) {
            writeResponse(response, resp, 500, up.getMessage());
            throw up;
        } finally {
            if (!resp.isCommitted()) {
                writeResponse(response, resp, 500, "unknown error, please contact me");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthResponse response = new AuthResponse();
        try {
            String requestString = getRequestBody(req);
            UserJson input = Mapper.getAsObject(requestString, UserJson.class);
            if(input.getName() == null) {
                throw new JsonProcessingException("Missing name") {};
            } else {
                User registeredUser = authService.register(input.getName());
                response.setUser(toJson(registeredUser));
                ok(response, resp);
                return;
            }
        } catch(JsonProcessingException e) {
            writeResponse(response, resp, 400, "Your message s not valid: " + e.getMessage());
            return;
        } catch (Exception up) {
            writeResponse(response, resp, 500, up.getMessage());
            throw up;
        } finally {
            if (!resp.isCommitted()) {
                writeResponse(response, resp, 500, "unknown error, please contact me");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthResponse response = new AuthResponse();
        try {
            Optional<User> optional = getAuthorizedUser(req);
            if (!optional.isPresent()) {
                unauthorized(response, resp);
                return;
            }

            authService.unregister(optional.get());
            ok(response, resp);
            return;
        } catch (Exception up) {
            writeResponse(response, resp, 500, up.getMessage());
            throw up;
        } finally {
            if (!resp.isCommitted()) {
                writeResponse(response, resp, 500, "unknown error, please contact me");
            }
        }
    }

    private UserJson toJson(User user) {
        UserJson json = new UserJson();
        json.setId(user.getId());
        json.setName(user.getName());
        return json;
    }

    private User fromJson(UserJson json) {
        User user = new User();
        user.setName(json.getName());
        user.setId(json.getId());
        return user;
    }
}
