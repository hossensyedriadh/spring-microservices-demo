package io.github.hossensyedriadh.authservice.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataR2dbcTest
public class UserRepositoryTests {
}
