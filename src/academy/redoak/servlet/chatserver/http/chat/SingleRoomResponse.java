package academy.redoak.servlet.chatserver.http.chat;

import academy.redoak.servlet.chatserver.http.Response;
import com.fasterxml.jackson.annotation.JsonProperty;

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
