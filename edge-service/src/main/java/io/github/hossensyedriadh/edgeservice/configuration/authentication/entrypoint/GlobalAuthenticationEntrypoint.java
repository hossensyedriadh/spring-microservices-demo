package io.github.hossensyedriadh.edgeservice.configuration.authentication.entrypoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.edgeservice.exception.GenericErrorResponse;
import io.github.hossensyedriadh.edgeservice.exception.GenericException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

@Component
public class GlobalAuthenticationEntrypoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setDate(ZonedDateTime.now(ZoneId.systemDefault()));
        response.getHeaders().setContentLanguage(Locale.ENGLISH);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        GenericErrorResponse errorResponse = new GenericErrorResponse(request, HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource");

        try {
            JsonMapper jsonMapper = new JsonMapper();
            DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(jsonMapper.writeValueAsBytes(errorResponse));
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            return Mono.error(new GenericException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request.getPath().value()));
        }
    }
}
