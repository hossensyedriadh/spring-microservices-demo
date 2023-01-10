package io.github.hossensyedriadh.edgeservice.configuration.security;

import io.github.hossensyedriadh.edgeservice.configuration.authentication.entrypoint.GlobalAuthenticationEntrypoint;
import io.github.hossensyedriadh.edgeservice.configuration.authentication.handler.GlobalAccessDeniedHandler;
import io.github.hossensyedriadh.edgeservice.configuration.authentication.service.GlobalUserDetailsService;
import io.github.hossensyedriadh.edgeservice.configuration.filters.BearerAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private final BearerAuthenticationFilter bearerAuthenticationFilter;
    private final GlobalAuthenticationEntrypoint globalAuthenticationEntrypoint;
    private final GlobalAccessDeniedHandler globalAccessDeniedHandler;
    private final GlobalUserDetailsService globalUserDetailsService;

    @Autowired
    public SecurityConfiguration(BearerAuthenticationFilter bearerAuthenticationFilter,
                                 GlobalAuthenticationEntrypoint globalAuthenticationEntrypoint,
                                 GlobalAccessDeniedHandler globalAccessDeniedHandler,
                                 GlobalUserDetailsService globalUserDetailsService) {
        this.bearerAuthenticationFilter = bearerAuthenticationFilter;
        this.globalAuthenticationEntrypoint = globalAuthenticationEntrypoint;
        this.globalAccessDeniedHandler = globalAccessDeniedHandler;
        this.globalUserDetailsService = globalUserDetailsService;
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(this.globalUserDetailsService);
    }

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity security) {
        security.cors().and().csrf().disable().authorizeExchange(s -> s.pathMatchers("/auth-service/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling()
                .accessDeniedHandler(this.globalAccessDeniedHandler).authenticationEntryPoint(this.globalAuthenticationEntrypoint);

        security.headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY);
        security.headers().hsts().includeSubdomains(true).maxAge(Duration.of(365, ChronoUnit.DAYS));

        security.addFilterBefore(bearerAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return security.build();
    }
}
