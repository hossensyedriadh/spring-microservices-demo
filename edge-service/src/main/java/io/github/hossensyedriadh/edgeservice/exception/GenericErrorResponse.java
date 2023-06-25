package io.github.hossensyedriadh.edgeservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@SuppressWarnings("unused")
@Getter
@Setter
public final class GenericErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 6978437307884195492L;

    private int status;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssa z")
    private ZonedDateTime timestamp;

    private String message;
    private String error;
    private String path;

    public GenericErrorResponse(ServerHttpRequest serverHttpRequest, HttpStatus status, String message) {
        this.timestamp = ZonedDateTime.now(ZoneId.systemDefault());
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = serverHttpRequest.getPath().value();
    }

    public GenericErrorResponse(ServerHttpRequest serverHttpRequest, HttpStatus status, Throwable throwable) {
        this.timestamp = ZonedDateTime.now(ZoneId.systemDefault());
        this.status = status.value();
        this.message = status.getReasonPhrase();
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = serverHttpRequest.getPath().value();
    }
}
