package io.github.hossensyedriadh.orderservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.orderservice.OrderServiceApplication;
import io.github.hossensyedriadh.orderservice.entity.Item;
import io.github.hossensyedriadh.orderservice.enumerator.OrderStatus;
import io.github.hossensyedriadh.orderservice.router.OrderRouter;
import io.github.hossensyedriadh.orderservice.router.OrderRouterConstants;
import io.github.hossensyedriadh.orderservice.service.OrderService;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrderServiceApplication.class, OrderHandler.class, OrderRouter.class})
@DirtiesContext
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderHandlerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrderService orderService;

    @Order(1)
    @Test
    public void when_get_orders_should_return_2xx() {
        this.webTestClient.get().uri(OrderRouterConstants.V1_ORDERS_PAGEABLE.getRoute())
                .exchange().expectStatus().is2xxSuccessful().returnResult(io.github.hossensyedriadh.orderservice.entity.Order.class)
                .consumeWith(System.out::println);
    }

    @Order(2)
    @Test
    public void when_get_order_should_return_2xx() {
        this.webTestClient.get().uri(OrderRouterConstants.V1_ORDER_BY_ID.getRoute(), UUID.randomUUID().toString())
                .exchange().expectStatus().is2xxSuccessful().returnResult(io.github.hossensyedriadh.orderservice.entity.Order.class)
                .consumeWith(System.out::println);
    }

    @Order(3)
    @Test
    public void when_post_order_should_return_400() {
        io.github.hossensyedriadh.orderservice.entity.Order order =
                new io.github.hossensyedriadh.orderservice.entity.Order(UUID.randomUUID().toString(),
                        List.of(new Item(UUID.randomUUID().toString(), 0, 500, UUID.randomUUID().toString())),
                        500, OrderStatus.ACTIVE, ZonedDateTime.now(), "test");

        this.webTestClient.post().uri(OrderRouterConstants.V1_CREATE_ORDER.getRoute())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(order), io.github.hossensyedriadh.orderservice.entity.Order.class).exchange()
                .expectStatus().isBadRequest().expectBody(io.github.hossensyedriadh.orderservice.entity.Order.class)
                .consumeWith(System.out::println);
    }

    @Order(4)
    @Test
    public void when_post_order_should_return_201() {
        io.github.hossensyedriadh.orderservice.entity.Order order =
                new io.github.hossensyedriadh.orderservice.entity.Order(UUID.randomUUID().toString(),
                        List.of(new Item(UUID.randomUUID().toString(), 1, 500, UUID.randomUUID().toString())),
                        500, OrderStatus.ACTIVE, ZonedDateTime.now(), "test");

        this.webTestClient.post().uri(OrderRouterConstants.V1_CREATE_ORDER.getRoute())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(order), io.github.hossensyedriadh.orderservice.entity.Order.class).exchange()
                .expectStatus().isCreated().expectBody(io.github.hossensyedriadh.orderservice.entity.Order.class)
                .consumeWith(System.out::println);
    }

    @Order(5)
    @Test
    public void when_put_order_should_return_200() {

    }
}
