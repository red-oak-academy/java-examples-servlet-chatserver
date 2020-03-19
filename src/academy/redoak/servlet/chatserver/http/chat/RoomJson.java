package academy.redoak.servlet.chatserver.http.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * POJO for Jackson mapping representing a {@link academy.redoak.servlet.chatserver.model.ChatRoom} object.
 */
public class RoomJson {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private List<MessageJson> messages;

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

    public List<MessageJson> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageJson> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomJson roomJson = (RoomJson) o;
        return Objects.equals(id, roomJson.id) &&
                Objects.equals(name, roomJson.name) &&
                Objects.equals(messages, roomJson.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, messages);
    }
}
