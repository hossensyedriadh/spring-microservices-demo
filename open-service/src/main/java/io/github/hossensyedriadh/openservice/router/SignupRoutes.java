package io.github.hossensyedriadh.openservice.router;

public enum SignupRoutes {
    V1_SIGNUP("/v1/sign-up/"),
    V1_CHECK_USERNAME("/v1/sign-up/check-username/{username}");

    private final String route;

    SignupRoutes(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
