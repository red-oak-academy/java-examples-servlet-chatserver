package academy.redoak.servlet.chatserver.http.chat;

import academy.redoak.servlet.chatserver.http.Response;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO for {@link Response}s of {@link ChatServlet}, when returning a single chat room. Consists of basic properties
 * from {@link Response} class and has additionally a the room available.
 */
public class SingleRoomResponse extends Response {

    @JsonProperty
    RoomJson room;

    public RoomJson getRoom() {
        return room;
    }

    public void setRoom(RoomJson room) {
        this.room = room;
    }
}
