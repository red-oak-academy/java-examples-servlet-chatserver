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

/**
 * Servlet endpoint for registering and removing users.
 */
@WebServlet("/users")
public class AuthenticationServlet extends AbstractChatRoomServlet {

    private AuthService authService = AuthService.getInstance();

    /**
     * <b>
     *     Requires Authentication! Authenticated user must be declared by
     *     setting the "auth" header value to the id of the user.
     * </b>
     * <br/>
     * Handles HTTP GET messages. Basically just returns the authenticated user. <br/>
     * Response will be in Format of the {@link UserJson}.
     * <br/>
     * Example: <br/>
     * <code>
     *     {
     *         "status": "OK",
     *         "user": {
     *              "name": "Benjamin",
     *              "id": "0b5f0027-4041-479f-8a20-a16ed1d24a13"
     *         }
     *     }
     * </code>
     *
     * @See {@link javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}
     */
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

    /**
     * Handles HTTP POST messages. Offers the ability to register new users. Request and Response body conform to
     * the {@link UserJson} schema. Requests only require to have a name. The saved user is being returned in response.
     * <br/>
     * Example request: <br/>
     * <code>
     *     {
     *         "name": "Benjamin"
     *     }
     * </code>
     *
     * Example response: <br/>
     * <code>
     *     {
     *         "status": "OK",
     *         "user": {
     *              "name": "Benjamin",
     *              "id": "0b5f0027-4041-479f-8a20-a16ed1d24a13"
     *         }
     *     }
     * </code>
     */
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

    /**
     * <b>
     *     Requires Authentication! Authenticated user must be declared by
     *     setting the "auth" header value to the id of the user.
     * </b>
     * <br/>
     * Handles HTTP DELETE messages. Deletes the authenticated user.
     * Doesnt require any request body. Response body consists of meta information only.
     * <br/>
     * Example response: <br/>
     * <code>
     *     {
     *         "status": "OK",
     *     }
     * </code>
     */
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
}
