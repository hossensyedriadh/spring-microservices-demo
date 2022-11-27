package io.github.hossensyedriadh.authservice.router;

public enum AuthenticationRouterConstants {
    V1_AUTHENTICATE("/v1/authentication/"),
    V1_RENEW_ACCESS_TOKEN("/v1/authentication/access-token/");

    private final String route;

    AuthenticationRouterConstants(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
