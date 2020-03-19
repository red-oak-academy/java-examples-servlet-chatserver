package academy.redoak.servlet.chatserver.http.chat;

import academy.redoak.servlet.chatserver.http.AbstractChatRoomServlet;
import academy.redoak.servlet.chatserver.http.Response;
import academy.redoak.servlet.chatserver.model.ChatRoom;
import academy.redoak.servlet.chatserver.model.Message;
import academy.redoak.servlet.chatserver.model.User;
import academy.redoak.servlet.chatserver.service.ChatRoomService;
import academy.redoak.servlet.chatserver.util.Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet endpoint for retrieving, creating chat rooms and corresponding messages.
 */
@WebServlet("/rooms/*")
public class ChatServlet extends AbstractChatRoomServlet {

    private ChatRoomService service = ChatRoomService.getInstance();

    /**
     * <b>
     *     Requires Authentication! Authenticated user must be declared by
     *     setting the "auth" header value to the id of the user.
     * </b>
     * <br/>
     * Handles HTTP GET messages. Path must match following pattern:
     * <code>/rooms/{room_id}</code> <br/>
     * Where room_id may be omitted. If omitted, all rooms (without contained messages) are retrieved like follows (See {@link RoomListResponse}):
     * <code>
     *     {
     *          "status": "OK",
     *          "rooms": [
     *                  {
     *                      "id": "3a285f0d-3541-4c30-830d-1b4bbad98672",
     *                      "name": "Group A"
     *                  }, {
     *                      "id": "fdda5b1a-497d-4d27-81bf-04560ccacdcd",
     *                      "name": "Group B"
     *                  }
     *          ]
     *     }
     * </code>
     * If a room_id is given, the response may look like that (See {@link SingleRoomResponse}):
     * <code>
     *     {
     *         "status": "OK",
     *         "room": {
     *             "id": "3a285f0d-3541-4c30-830d-1b4bbad98672",
     *             "name": "Group A",
     *             "messages": [
     *                 {
     *                     "username": "Benjamin",
     *                     "message": "Moin"
     *                 },
     *                 {
     *                     "username": "Benjamin",
     *                     "message": "Was geht, Freunde?"
     *                 }
     *             ]
     *         }
     *     }
     * </code>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Response response = new RoomListResponse();
        resp.getWriter().write(req.getPathInfo() + "\n");
        try {
            Optional<User> authorizedUser = getAuthorizedUser(req);
            if (!authorizedUser.isPresent()) {
                unauthorized(response, resp);
                return;
            }
            String pathInfo = req.getPathInfo() != null ? req.getPathInfo(): "/";
            if ("/".equals(pathInfo)) {
                response = deliverAllRooms(req, resp);
            } else if (pathInfo.matches("^/[a-zA-Z0-9\\-]{36}/?$")) {
                response = deliverSingleRoom(response,resp, pathInfo.replace("/", ""));
            } else {
                unknownPath(response, resp);
            }
            return;
        } catch (Exception up) {
            writeResponse(response, resp, 500, up.getMessage());
            throw up;
        } finally {
            if (!resp.isCommitted()) {
                writeResponse(response, resp, 500, "unknown error, please contact me");
            }
        }
    }

    private Response deliverSingleRoom(Response response, HttpServletResponse resp, String roomId) throws IOException {
        Optional<ChatRoom> chatRoom = service.getChatRoom(roomId);
        if(chatRoom.isPresent()) {
            SingleRoomResponse myResponse = new SingleRoomResponse();
            myResponse.setRoom(toJson(chatRoom.get()));
            ok(myResponse, resp);
        } else {
            writeResponse(response, resp, 404, "ChatRoom not found");
        }
        return response;
    }

    private RoomListResponse deliverAllRooms(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomListResponse response = new RoomListResponse();
        List<RoomJson> rooms = service.getRooms().stream().map(ChatServlet::toJson).collect(Collectors.toList());
        // just want to return the bare list of chat rooms
        rooms.forEach(r -> r.setMessages(null));
        response.setRooms(rooms);
        ok(response, resp);
        return response;
    }

    /**
     * <b>
     *     Requires Authentication! Authenticated user must be declared by
     *     setting the "auth" header value to the id of the user.
     * </b>
     * <br/>
     * Handles HTTP POST messages. Path must match following pattern:
     * <code>/rooms/{room_id}/messages</code> <br/>
     * <code>room_id</code> may be omitted. If omitted, following messages part must be omitted, too.<br/>
     * Calling a POST on <code>/rooms</code> without a specific <code>room_id</code> creates a new chat room.
     * Therefor the request must conform to the {@link RoomJson} schema. See following example request:
     * <code>
     *     {
     *         "name": "Group A"
     *     }
     * </code>
     * As response, the newly created chat room will be shown in the response in {@link SingleRoomResponse} schema:
     * <code>
     *     {
     *          "status": "OK",
     *          "room": {
     *              "id": "3a285f0d-3541-4c30-830d-1b4bbad98672",
     *              "name": "Group A",
     *              "messages": []
     *          }
     *      }
     * </code>
     *
     * When sending a POST on <code>/rooms/{room_id}/messages</code>, a new message is being created in given room.
     * The request must conform to the {@link MessageJson} schema, see following example:
     * <code>
     *     {
     *         "message": "Moin"
     *     }
     * </code>
     * As response, the chat room with the newest messages will be returned, like following example ({@link SingleRoomResponse} schema again):
     * <code>
     *     {
     *          "status": "OK",
     *          "room": {
     *              "id": "3a285f0d-3541-4c30-830d-1b4bbad98672",
     *              "name": "Group A",
     *              "messages": [
     *                  {
     *                      "username": "Benjamin",
     *                      "message": "Moin"
     *                  }
     *              ]
     *          }
     *      }
     * </code>
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Response response = new SingleRoomResponse();
        try {
            Optional<User> authorizedUser = getAuthorizedUser(req);
            if (!authorizedUser.isPresent()) {
                unauthorized(response, resp);
                return;
            }

            String requestString = getRequestBody(req);
            String pathInfo = req.getPathInfo() != null ? req.getPathInfo(): "/";
            if ("/".equals(pathInfo)) {
                response = createRoom(resp, requestString);
            } else if (pathInfo.matches("^/[a-zA-Z0-9\\-]{36}/messages$")) {
                response = postMessage(response,resp, authorizedUser.get(), pathInfo.replace("/messages", "").replace("/",""), requestString);
            } else {
                unknownPath(response, resp);
            }
            return;

        } catch(JsonProcessingException e) {
            writeResponse(response, resp, 400, "Your message s not valid: " + e.getMessage());
            return;
        } catch (Exception up) {
            writeResponse(response, resp, 500, up.getMessage());
            throw up;
        } finally {
            if (!resp.isCommitted()) {
                writeResponse(response, resp, 500, "unknown error, please contact me");
            }
        }
    }

    private Response postMessage(Response response, HttpServletResponse resp, User user, String roomId, String requestString) throws IOException {
        Optional<ChatRoom> chatRoom = service.getChatRoom(roomId);
        if(chatRoom.isPresent()) {
            MessageJson json = Mapper.getAsObject(requestString, MessageJson.class);
            chatRoom.get().getMessages().add(new Message(user, json.getMessage()));
            SingleRoomResponse myResponse = new SingleRoomResponse();
            myResponse.setRoom(toJson(chatRoom.get()));
            ok(myResponse, resp);
        } else {
            writeResponse(response, resp, 404, "ChatRoom not found");
        }
        return response;
    }

    private SingleRoomResponse createRoom(HttpServletResponse resp, String requestString) throws IOException {
        RoomJson input = Mapper.getAsObject(requestString, RoomJson.class);
        if(input.getName() == null) {
            throw new JsonProcessingException("Missing name") {};
        } else {
            SingleRoomResponse response = new SingleRoomResponse();
            ChatRoom registeredRoom = service.addChatRoom(input.getName());
            response.setRoom(toJson(registeredRoom));
            ok(response, resp);
            return response;
        }
    }

    private static RoomJson toJson(ChatRoom chatRoom) {
        RoomJson json = new RoomJson();
        json.setId(chatRoom.getId());
        json.setName(chatRoom.getName());
        json.setMessages(chatRoom.getMessages().stream().map(m -> {
            MessageJson msg = new MessageJson();
            msg.setUsername(m.getUser().getName());
            msg.setMessage(m.getMessage());
            return msg;
        }).collect(Collectors.toList()));
        return json;
    }

}
