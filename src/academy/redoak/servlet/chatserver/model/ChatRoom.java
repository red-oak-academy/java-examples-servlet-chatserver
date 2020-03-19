package academy.redoak.servlet.chatserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatRoom {

    private String id;
    private String name;
    private List<Message> messages;

    public ChatRoom(String id, String name) {
        this.id = id;
        this.name = name;
        this.messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(id, chatRoom.id) &&
                Objects.equals(name, chatRoom.name) &&
                Objects.equals(messages, chatRoom.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, messages);
    }
}
