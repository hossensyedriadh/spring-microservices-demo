package io.github.hossensyedriadh.authservice.repository.r2dbc;

import io.github.hossensyedriadh.authservice.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends R2dbcRepository<User, String> {
}
