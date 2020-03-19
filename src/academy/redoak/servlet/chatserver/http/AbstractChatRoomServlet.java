package academy.redoak.servlet.chatserver.http;

import academy.redoak.servlet.chatserver.model.User;
import academy.redoak.servlet.chatserver.service.AuthService;
import academy.redoak.servlet.chatserver.util.Mapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Abstract base {@link HttpServlet} implementation for being extended by specific Servlets. <br/>
 * Offers several convenience methods for printing out the message to the client or retrieving
 * the authenticated user or request body as a string.
 */
public abstract class AbstractChatRoomServlet extends HttpServlet {

    private AuthService authService = AuthService.getInstance();

    /**
     * Reads all lines from {@link HttpServletRequest#getReader()} and retrieves them as a string object.
     *
     * @param req The {@link HttpServletRequest} to read the body from.
     * @return The request body as a string.
     * @throws IOException {@link HttpServletRequest#getReader()} may throw.
     */
    protected String getRequestBody(HttpServletRequest req) throws IOException {
        return req.getReader().lines().reduce("", (s1, s2) -> s1 + s2);
    }

    /**
     * Identifies the authenticated user by the "auth" header field.
     *
     * @param req The {@link HttpServletRequest} to read the user from.
     * @return An {@link Optional<User>}, which may contain the authenticated user.
     *          May be empty, if no valid auth header field value is given.
     */
    protected Optional<User> getAuthorizedUser(HttpServletRequest req) {
        String auth = req.getHeader("auth").trim();
        if (auth != null) {
            return authService.getUsers().stream()
                    .filter(user -> user.getId().equals(auth))
                    .findFirst();
        }
        return Optional.empty();
    }

    /**
     * Writes given {@link Response} to {@link HttpServletResponse#getWriter()} and closes it. Sends HTTP 200
     * status code and writes {@link Status#OK} into {@link Response} object.
     *
     * @param response The {@link Response} to be sent.
     * @param resp The {@link HttpServletResponse} for writing the response.
     * @throws IOException {@link HttpServletResponse#getWriter()} and {@link Mapper#getAsJson(Response)} may throw.
     */
    protected void ok(Response response, HttpServletResponse resp) throws IOException {
        response.setStatus(Status.OK);
        writeResponse(response, resp, 200);
    }

    /**
     * Writes given {@link Response} to {@link HttpServletResponse#getWriter()} and closes it. Sends HTTP 401
     * status code and writes {@link Status#UNAUTHORIZED} and <code>error_message="Unauthorized"</code> into {@link Response} object.
     *
     * @param response The {@link Response} to be sent.
     * @param resp The {@link HttpServletResponse} for writing the response.
     * @throws IOException {@link HttpServletResponse#getWriter()} and {@link Mapper#getAsJson(Response)} may throw.
     */
    protected void unauthorized(Response response, HttpServletResponse resp) throws IOException {
        response.setStatus(Status.UNAUTHORIZED);
        response.setErrorMessage("Unauthorized");
        writeResponse(response, resp, 401);
    }

    /**
     * Writes given {@link Response} to {@link HttpServletResponse#getWriter()} and closes it. Sends HTTP 404
     * status code and writes {@link Status#FAIL} and <code>error_message="Path not found"</code> into {@link Response} object.
     *
     * @param response The {@link Response} to be sent.
     * @param resp The {@link HttpServletResponse} for writing the response.
     * @throws IOException {@link HttpServletResponse#getWriter()} and {@link Mapper#getAsJson(Response)} may throw.
     */
    protected void unknownPath(Response response, HttpServletResponse resp) throws IOException {
        this.writeResponse(response, resp, 404, "Path not found");
    }

    /**
     * Writes given {@link Response} to {@link HttpServletResponse#getWriter()} and closes it. Sends given HTTP
     * status code. Writes no {@link Status} or <code>error_message</code> to {@link Response} object.
     *
     * @param response The {@link Response} to be sent.
     * @param resp The {@link HttpServletResponse} for writing the response.
     * @param httpStatus The HTTP status code to be written in the HTTP response.
     * @throws IOException {@link HttpServletResponse#getWriter()} and {@link Mapper#getAsJson(Response)} may throw.
     */
    protected void writeResponse(Response response, HttpServletResponse resp, int httpStatus) throws IOException {
        writeResponse(response, resp, httpStatus, null);
    }

    /**
     * Writes given {@link Response} to {@link HttpServletResponse#getWriter()} and closes it. Sends given HTTP
     * status code. Writes no {@link Status} to {@link Response} object, but writes given <code>error_message</code>.
     *
     * @param response The {@link Response} to be sent.
     * @param resp The {@link HttpServletResponse} for writing the response.
     * @param httpStatus The HTTP status code to be written in the HTTP response.
     * @param errorMessage The error_message to be written into the {@link Response} object.
     * @throws IOException {@link HttpServletResponse#getWriter()} and {@link Mapper#getAsJson(Response)} may throw.
     */
    protected void writeResponse(Response response, HttpServletResponse resp, int httpStatus, String errorMessage) throws IOException {
        response.setErrorMessage(errorMessage);
        resp.setStatus(httpStatus);
        resp.addHeader("Content-Type", "application/json;charset=utf8");
        resp.getWriter().write(Mapper.getAsJson(response));
        resp.getWriter().close();
    }
}
