package academy.redoak.servlet.chatserver.http.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class MessageJson {

    @JsonProperty
    private String username;

    @JsonProperty
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageJson that = (MessageJson) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, message);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
