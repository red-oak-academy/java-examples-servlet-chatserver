package academy.redoak.servlet.chatserver.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Base POJO for {@link javax.servlet.http.HttpServlet} responses. Offers base properties like a {@link Status} and
 * error message.
 */
public class Response {

    @JsonProperty(required = true)
    protected Status status = Status.FAIL;

    @JsonProperty("error_message")
    protected String errorMessage;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return status == response.status &&
                Objects.equals(errorMessage, response.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, errorMessage);
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
