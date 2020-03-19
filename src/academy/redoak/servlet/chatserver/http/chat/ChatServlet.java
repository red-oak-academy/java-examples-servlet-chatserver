package academy.redoak.servlet.chatserver.http.chat;

import academy.redoak.servlet.chatserver.http.AbstractChatRoomServlet;
import academy.redoak.servlet.chatserver.http.Response;
import academy.redoak.servlet.chatserver.model.ChatRoom;
import academy.redoak.servlet.chatserver.model.Message;
import academy.redoak.servlet.chatserver.model.User;
import academy.redoak.servlet.chatserver.service.ChatService;
import academy.redoak.servlet.chatserver.util.Mapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/rooms/*")
public class ChatServlet extends AbstractChatRoomServlet {

    private ChatService service = ChatService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Response response = new RoomsGetResponse();
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
            } else if (pathInfo.matches("^/[a-zA-Z0-9\\-]{32,36}/?$")) {
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

    private RoomsGetResponse deliverAllRooms(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomsGetResponse response = new RoomsGetResponse();
        List<RoomJson> rooms = service.getRooms().stream().map(ChatServlet::toJson).collect(Collectors.toList());
        response.setRooms(rooms);
        ok(response, resp);
        return response;
    }

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
            } else if (pathInfo.matches("^/[a-zA-Z0-9\\-]{32,36}/messages$")) {
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
