package academy.redoak.servlet.chatserver.http;

/**
 * A status representation, being written into {@link Response} objects.
 */
public enum Status {

    /**
     * Response is OK, everything is fine.
     */
    OK,
    /**
     * There was some issue, look into the error message.
     */
    FAIL,
    /**
     * Authorization failed, probably due to missing auth HTTP header field value.
     */
    UNAUTHORIZED;
}
