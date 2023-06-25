package io.github.hossensyedriadh.edgeservice.configuration.security;

import io.github.hossensyedriadh.edgeservice.configuration.authentication.entrypoint.GlobalAuthenticationEntrypoint;
import io.github.hossensyedriadh.edgeservice.configuration.authentication.handler.GlobalAccessDeniedHandler;
import io.github.hossensyedriadh.edgeservice.configuration.authentication.service.GlobalUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private final GlobalAuthenticationEntrypoint globalAuthenticationEntrypoint;
    private final GlobalAccessDeniedHandler globalAccessDeniedHandler;
    private final GlobalUserDetailsService globalUserDetailsService;

    @Autowired
    public SecurityConfiguration(GlobalAuthenticationEntrypoint globalAuthenticationEntrypoint,
                                 GlobalAccessDeniedHandler globalAccessDeniedHandler, GlobalUserDetailsService globalUserDetailsService) {
        this.globalAuthenticationEntrypoint = globalAuthenticationEntrypoint;
        this.globalAccessDeniedHandler = globalAccessDeniedHandler;
        this.globalUserDetailsService = globalUserDetailsService;
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(this.globalUserDetailsService);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.cors(ServerHttpSecurity.CorsSpec::disable).csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers("/actuator/**", "/products-api/**", "/orders-api/**",
                        "/users-api/**", "/auth-api/**").permitAll())
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.accessDeniedHandler(this.globalAccessDeniedHandler)
                        .authenticationEntryPoint(this.globalAuthenticationEntrypoint));

        httpSecurity.headers(headerSpec -> headerSpec.frameOptions(frameOptionsSpec -> frameOptionsSpec.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY)));

        return httpSecurity.build();
    }
}
