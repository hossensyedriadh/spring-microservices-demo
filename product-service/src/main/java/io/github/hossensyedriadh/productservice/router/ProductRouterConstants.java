package io.github.hossensyedriadh.productservice.router;

public enum ProductRouterConstants {
    V1_PRODUCTS_PAGEABLE("/v1/products/"),
    V1_PRODUCT_BY_ID("/v1/products/{id}"),
    V1_ADD_PRODUCT("/v1/products/"),
    V1_MODIFY_PRODUCT("/v1/products/"),
    V1_DELETE_PRODUCT("/v1/products/{id}");

    private final String route;

    ProductRouterConstants(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
