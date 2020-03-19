package academy.redoak.servlet.chatserver.http;

import academy.redoak.servlet.chatserver.http.auth.AuthResponse;
import academy.redoak.servlet.chatserver.model.User;
import academy.redoak.servlet.chatserver.service.AuthService;
import academy.redoak.servlet.chatserver.util.Mapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public abstract class AbstractChatRoomServlet extends HttpServlet {

    private AuthService authService = AuthService.getInstance();

    protected String getRequestBody(HttpServletRequest req) throws IOException {
        return req.getReader().lines().reduce("", (s1, s2) -> s1 + s2);
    }

    protected Optional<User> getAuthorizedUser(HttpServletRequest req) {
        String auth = req.getHeader("auth").trim();
        if (auth != null) {
            return authService.getUsers().stream()
                    .filter(user -> user.getId().equals(auth))
                    .findFirst();
        }
        return Optional.empty();
    }

    protected void ok(Response response, HttpServletResponse resp) throws IOException {
        response.setStatus(Status.OK);
        writeResponse(response, resp, 200);
    }

    protected void unauthorized(Response response, HttpServletResponse resp) throws IOException {
        response.setStatus(Status.UNAUTHORIZED);
        response.setErrorMessage("Unauthorized");
        writeResponse(response, resp, 401);
    }

    protected void unknownPath(Response response, HttpServletResponse resp) throws IOException {
        this.writeResponse(response, resp, 404, "Path not found");
    }

    protected void writeResponse(Response response, HttpServletResponse resp, int httpStatus) throws IOException {
        writeResponse(response, resp, httpStatus, null);
    }

    protected void writeResponse(Response response, HttpServletResponse resp, int httpStatus, String errorMessage) throws IOException {
        response.setErrorMessage(errorMessage);
        resp.setStatus(httpStatus);
        resp.addHeader("Content-Type", "application/json;charset=utf8");
        resp.getWriter().write(Mapper.getAsJson(response));
        resp.getWriter().close();
    }
}
