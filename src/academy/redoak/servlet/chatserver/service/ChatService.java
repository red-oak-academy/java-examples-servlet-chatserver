package academy.redoak.servlet.chatserver.service;

import academy.redoak.servlet.chatserver.model.ChatRoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton service offering methods for retrieving chat rooms. Singleton instance may be retrieved
 * by {@link #getInstance()}.
 */
public class ChatService {

    private List<ChatRoom> rooms;

    private ChatService() {
        // private constructor due to singleton class
        this.rooms = new ArrayList<>();
    }

    /**
     * Creates a new {@link ChatRoom} with given name and returns the newly created object, after
     * adding it to the application.
     *
     * @param name The name of the chat room to be created.
     *
     * @return The newly created {@link ChatRoom}.
     */
    public ChatRoom addChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(UUID.randomUUID().toString(), name);
        rooms.add(chatRoom);
        return chatRoom;
    }

    /**
     * Searches for the {@link ChatRoom} with given id and returns it.
     *
     * @param id The id to look for.
     * @return An {@link Optional<ChatRoom>}, which may contain the found chat room or may be empty, if there is no
     * chat room with given id.
     */
    public Optional<ChatRoom> getChatRoom(String id) {
        return this.rooms.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    /**
     * @return An unmodifiable list with all existing rooms.
     */
    public List<ChatRoom> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    // --- Singleton

    private static ChatService instance = new ChatService();

    /**
     * @return The singleton instance.
     */
    public static ChatService getInstance() {
        return instance;
    }
}
