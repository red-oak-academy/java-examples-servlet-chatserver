package academy.redoak.servlet.chatserver.http.auth;

import academy.redoak.servlet.chatserver.http.Response;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * POJO for {@link Response}s of {@link AuthenticationServlet}. Consists of basic properties from
 * {@link Response} class and has additionally a user represented by the {@link UserJson} object.
 */
public class AuthResponse extends Response {

    @JsonProperty
    private UserJson user;

    public UserJson getUser() {
        return user;
    }

    public void setUser(UserJson user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}
