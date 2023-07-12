package io.github.hossensyedriadh.openservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;

@SuppressWarnings("unused")
@Getter
public class ResourceException extends ResponseStatusException {
    @Serial
    private static final long serialVersionUID = 162328240875319330L;

    private HttpStatus httpStatus;
    private ServerRequest serverRequest;
    private String message;

    /**
     * Constructor with a response status.
     *
     * @param status the HTTP status (required)
     */
    public ResourceException(HttpStatus status) {
        super(status);
    }

    /**
     * Constructor with a response status and a reason to add to the exception
     * message as explanation, as well as a nested exception.
     *
     * @param status the HTTP status (required)
     * @param reason the associated reason (optional)
     * @param cause  a nested exception (optional)
     */
    public ResourceException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
        this.message = reason;
    }

    /**
     * Constructor with a response status and a reason to add to the exception
     * message as explanation, as well as a nested exception.
     *
     * @param rawStatusCode the HTTP status code value
     * @param reason        the associated reason (optional)
     * @param cause         a nested exception (optional)
     * @since 5.3
     */
    public ResourceException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
        this.message = reason;
    }

    public ResourceException(String message, HttpStatus httpStatus, ServerRequest serverRequest) {
        super(httpStatus, message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.serverRequest = serverRequest;
    }

    public ResourceException(String message, HttpStatus httpStatus) {
        super(httpStatus, message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
