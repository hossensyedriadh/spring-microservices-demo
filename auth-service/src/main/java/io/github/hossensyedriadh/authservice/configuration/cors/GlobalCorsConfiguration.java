package io.github.hossensyedriadh.authservice.configuration.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class GlobalCorsConfiguration implements WebFluxConfigurer {
    /**
     * Configure "global" cross-origin request processing. The configured CORS
     * mappings apply to annotated controllers, functional endpoints, and static
     * resources.
     * <p>Annotated controllers can further declare more fine-grained config via
     * {@link CrossOrigin @CrossOrigin}.
     * In such cases "global" CORS configuration declared here is
     * {@link CorsConfiguration#combine(CorsConfiguration) combined}
     * with local CORS configuration defined on a controller method.
     *
     * @see CorsRegistry
     * @see CorsConfiguration#combine(CorsConfiguration)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*").allowedHeaders(HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE)
                .allowedMethods(HttpMethod.POST.toString(), HttpMethod.OPTIONS.toString(), HttpMethod.HEAD.toString())
                .allowCredentials(false).maxAge(3600);
    }
}
