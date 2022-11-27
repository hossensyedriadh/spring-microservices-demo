package io.github.hossensyedriadh.productservice.handler;

import io.github.hossensyedriadh.productservice.ProductServiceApplication;
import io.github.hossensyedriadh.productservice.entity.Product;
import io.github.hossensyedriadh.productservice.router.ProductRouter;
import io.github.hossensyedriadh.productservice.router.ProductRouterConstants;
import io.github.hossensyedriadh.productservice.service.ProductService;
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

import java.util.UUID;

@SuppressWarnings("unused")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductServiceApplication.class, ProductHandler.class, ProductRouter.class})
@DirtiesContext
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductHandlerTests {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductService productService;

    @Order(1)
    @Test
    public void when_get_products_should_return_2xx() {
        this.webTestClient.get().uri(ProductRouterConstants.V1_PRODUCTS_PAGEABLE.getRoute())
                .exchange().expectStatus().is2xxSuccessful().returnResult(Product.class)
                .consumeWith(System.out::println);
    }

    @Order(2)
    @Test
    public void when_get_product_should_return_2xx() {
        this.webTestClient.get().uri(ProductRouterConstants.V1_PRODUCT_BY_ID.getRoute(), UUID.randomUUID().toString())
                .exchange().expectStatus().is2xxSuccessful().returnResult(Product.class)
                .consumeWith(System.out::println);
    }

    @Order(3)
    @Test
    public void when_post_product_should_return_400() {
        Product product = new Product(UUID.randomUUID().toString(), "Test product", "Test", "Test specs", 3, 10);

        this.webTestClient.post().uri(ProductRouterConstants.V1_ADD_PRODUCT.getRoute())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class).exchange().expectStatus().isBadRequest()
                .expectBody(Product.class).consumeWith(System.out::println);
    }

    @Order(4)
    @Test
    public void when_post_product_should_return_201() {
        Product product = new Product(UUID.randomUUID().toString(), "Test product", "Category", "Test specs", 3, 10);

        this.webTestClient.post().uri(ProductRouterConstants.V1_ADD_PRODUCT.getRoute())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class).exchange().expectStatus().isCreated()
                .expectBody(Product.class).consumeWith(System.out::println);
    }

    @Order(5)
    @Test
    public void when_put_product_should_return_400() {
        Product product = new Product(UUID.randomUUID().toString(), "Product", "Category", "Test specs", 5, 100);

        this.webTestClient.put().uri(ProductRouterConstants.V1_MODIFY_PRODUCT.getRoute())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class).exchange().expectStatus().isBadRequest()
                .expectBody(Product.class).consumeWith(System.out::println);
    }

    @Order(6)
    @Test
    public void when_put_product_should_return_200() {
        String id = UUID.randomUUID().toString();
        Product product = new Product(id, "Test product", "Category", "Test specs", 3, 10);

        this.productService.add(product).block();

        product.setPrice(15);
        this.webTestClient.put().uri(ProductRouterConstants.V1_MODIFY_PRODUCT.getRoute())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class).exchange().expectStatus().isOk()
                .returnResult(Product.class).consumeWith(System.out::println);
    }

    @Order(7)
    @Test
    public void when_delete_product_should_return_400() {
        this.webTestClient.delete().uri(ProductRouterConstants.V1_DELETE_PRODUCT.getRoute(), UUID.randomUUID().toString())
                .exchange().expectStatus().isBadRequest().returnResult(Void.class).consumeWith(System.out::println);
    }
}
