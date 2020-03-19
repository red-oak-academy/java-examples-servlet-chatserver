package academy.redoak.servlet.chatserver.service;

import academy.redoak.servlet.chatserver.model.ChatRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChatService {

    private List<ChatRoom> rooms;

    private ChatService() {
        this.rooms = new ArrayList<>();
    }

    public ChatRoom addChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(UUID.randomUUID().toString(), name);
        rooms.add(chatRoom);
        return chatRoom;
    }

    public Optional<ChatRoom> getChatRoom(String id) {
        return this.rooms.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public List<ChatRoom> getRooms() {
        return rooms;
    }

    // --- Singleton

    private static ChatService instance = new ChatService();

    public static ChatService getInstance() {
        return instance;
    }
}
