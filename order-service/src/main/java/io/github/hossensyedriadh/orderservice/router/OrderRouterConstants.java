package io.github.hossensyedriadh.orderservice.router;

public enum OrderRouterConstants {
    V1_ORDERS_PAGEABLE("/v1/orders/"),
    V1_ORDER_BY_ID("/v1/orders/{id}"),
    V1_CREATE_ORDER("/v1/orders/"),
    V1_MODIFY_ORDER("/v1/orders/");

    private final String route;

    OrderRouterConstants(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
