package io.github.hossensyedriadh.authservice.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity security) {
        security.cors().and().csrf().disable()
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .authorizeExchange().anyExchange()
                .authenticated();

        security.headers().referrerPolicy(config -> config.policy(
                ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
        ));
        security.headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY);
        security.headers().hsts().includeSubdomains(true).maxAge(Duration.of(365, ChronoUnit.DAYS));

        return security.build();
    }
}
