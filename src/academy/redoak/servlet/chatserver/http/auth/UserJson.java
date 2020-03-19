package academy.redoak.servlet.chatserver.http.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * POJO for Jackson mapping representing a {@link academy.redoak.servlet.chatserver.model.User} object.
 */
public class UserJson {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserJson userJson = (UserJson) o;
        return Objects.equals(id, userJson.id) &&
                Objects.equals(name, userJson.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
