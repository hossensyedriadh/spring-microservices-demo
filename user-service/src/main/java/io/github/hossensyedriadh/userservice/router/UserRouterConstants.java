package io.github.hossensyedriadh.userservice.router;

public enum UserRouterConstants {
    V1_USERS_PAGEABLE("/v1/users/"),
    V1_USERS_BY_USERNAME("/v1/users/{username}"),
    V1_USER_UPDATE("/v1/users/"),
    V1_USER_DELETE("/v1/users/{username}");

    private final String route;

    UserRouterConstants(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
