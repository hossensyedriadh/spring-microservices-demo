package io.github.hossensyedriadh.openservice.router;

public enum PasswordResetRoutes {
    V1_FORGOT_PASSWORD("/v1/password-reset/{username}"),
    V1_VERIFY_PASSWORD_RESET("/v1/password-reset/verify/"),
    V1_PASSWORD_RESET("/v1/password-reset/confirm/");

    private final String route;

    PasswordResetRoutes(String route) {
        this.route = route;
    }

    public String getRoute() {
        return route;
    }
}
