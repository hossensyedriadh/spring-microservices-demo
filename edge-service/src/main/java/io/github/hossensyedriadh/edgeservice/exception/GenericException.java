package io.github.hossensyedriadh.edgeservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serial;

@SuppressWarnings("unused")
@Getter
public class GenericException extends ResponseStatusException {
    @Serial
    private static final long serialVersionUID = 4057052440998667918L;

    private String message;
    private final HttpStatus status;
    private ServerRequest serverRequest;

    private String path;

    public GenericException(HttpStatus status) {
        super(status);
        this.status = status;
    }

    public GenericException(HttpStatus status, String reason) {
        super(status, reason);
        this.status = status;
        this.message = reason;
    }

    public GenericException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
        this.status = status;
        this.message = reason;
    }

    public GenericException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
        this.status = HttpStatus.resolve(rawStatusCode);
        this.message = reason;
    }

    public GenericException(HttpStatus status, String reason, ServerRequest serverRequest) {
        super(status, reason);
        this.status = status;
        this.message = reason;
        this.serverRequest = serverRequest;
    }

    public GenericException(HttpStatus status, String reason, String path) {
        super(status, reason);
        this.status = status;
        this.message = reason;
        this.path = path;
    }
}
