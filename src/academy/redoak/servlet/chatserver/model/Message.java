package academy.redoak.servlet.chatserver.model;

/**
 * A POJO representing a Message inide a {@link ChatRoom}.
 */
public class Message {
	
	private User user;
	private String message;

	public Message(User user, String message) {
		super();
		this.user = user;
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Messages [user=" + user + ", message=" + message + "]";
	}
	
	
}
