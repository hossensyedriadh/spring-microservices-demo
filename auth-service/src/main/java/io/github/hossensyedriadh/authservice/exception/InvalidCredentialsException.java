package io.github.hossensyedriadh.authservice.exception;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.io.Serial;

@SuppressWarnings("unused")
public class InvalidCredentialsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1828492093593628850L;

    private ServerRequest serverRequest;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message       the detail message. The detail message is saved for
     *                      later retrieval by the {@link #getMessage()} method.
     * @param serverRequest request that caused the exception
     */
    public InvalidCredentialsException(String message, ServerRequest serverRequest) {
        super(message);
        this.serverRequest = serverRequest;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message       the detail message (which is saved for later retrieval
     *                      by the {@link #getMessage()} method).
     * @param cause         the cause (which is saved for later retrieval by the
     *                      {@link #getCause()} method).  (A {@code null} value is
     *                      permitted, and indicates that the cause is nonexistent or
     *                      unknown.)
     * @param serverRequest request that caused the exception
     * @since 1.4
     */
    public InvalidCredentialsException(String message, Throwable cause, ServerRequest serverRequest) {
        super(message, cause);
        this.serverRequest = serverRequest;
    }

    public ServerRequest getHttpServletRequest() {
        return this.serverRequest;
    }
}
