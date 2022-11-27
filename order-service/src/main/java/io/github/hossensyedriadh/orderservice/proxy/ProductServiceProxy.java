package io.github.hossensyedriadh.orderservice.proxy;

import io.github.hossensyedriadh.orderservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "api-gateway-service")
public interface ProductServiceProxy {
    @GetMapping(value = "/product-service/v1/products/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Product retrieveProduct(@PathVariable("id") String id);
}
