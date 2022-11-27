package io.github.hossensyedriadh.userservice.handler;

import io.github.hossensyedriadh.userservice.entity.UserAccount;
import io.github.hossensyedriadh.userservice.exception.ResourceException;
import io.github.hossensyedriadh.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserHandler {
    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    private int defaultSize;

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public Mono<ServerResponse> users(ServerRequest serverRequest) {
        int size = serverRequest.queryParam("size").isPresent() ?
                Integer.parseInt(serverRequest.queryParam("size").get()) : defaultSize;

        int page = serverRequest.queryParam("page").isPresent() ?
                Integer.parseInt(serverRequest.queryParam("page").get()) : 0;

        List<Sort.Order> sortOrders = new ArrayList<>();
        if (serverRequest.queryParam("sort").isPresent()) {
            String sortString = serverRequest.queryParam("sort").get();
            if (sortString.contains(",")) {
                String[] sort = serverRequest.queryParam("sort").get().split(",");
                sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1])
                        .orElse(Sort.Direction.ASC), sort[0]));
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));
        Mono<Page<UserAccount>> users = this.userService.users(pageable);
        return users.hasElement().flatMap(b -> b ? ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(users, UserAccount.class) : ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> user(ServerRequest serverRequest) {
        String username = serverRequest.pathVariable("username");
        Mono<UserAccount> userMono = this.userService.user(username);

        return userMono.hasElement().flatMap(b -> b ? ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(userMono, UserAccount.class) : ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        Mono<UserAccount> userAccountMono = serverRequest.bodyToMono(UserAccount.class);

        return userAccountMono.flatMap(user -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(this.userService.update(user).onErrorResume(e -> Mono.error(new ResourceException(e.getMessage(),
                        HttpStatus.BAD_REQUEST, serverRequest))), UserAccount.class));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        String username = serverRequest.pathVariable("username");

        return userService.delete(username).then(ServerResponse.noContent().build()).onErrorResume(e -> Mono.error(new ResourceException(e.getMessage(),
                HttpStatus.BAD_REQUEST, serverRequest)));
    }
}
