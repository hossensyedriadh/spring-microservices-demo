package io.github.hossensyedriadh.authservice.handler;

import io.github.hossensyedriadh.authservice.AuthServiceApplication;
import io.github.hossensyedriadh.authservice.router.AuthenticationRouter;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebFluxTest
@AutoConfigureWebTestClient
@SpringBootTest(classes = {AuthServiceApplication.class, AuthenticationHandler.class, AuthenticationRouter.class})
public class AuthenticationHandlerTests {

}
