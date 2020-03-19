package academy.redoak.servlet.chatserver.http.chat;

import academy.redoak.servlet.chatserver.http.Response;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * POJO for {@link Response}s of {@link ChatServlet}, when retrieving all rooms. Consists of basic properties from
 * {@link Response} class and has additionally a list of rooms available.
 */
public class RoomsGetResponse extends Response {

    @JsonProperty
    private List<RoomJson> rooms;

    public List<RoomJson> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomJson> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RoomsGetResponse that = (RoomsGetResponse) o;
        return Objects.equals(rooms, that.rooms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rooms);
    }
}
