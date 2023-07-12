package io.github.hossensyedriadh.openservice.repository.r2dbc;

import io.github.hossensyedriadh.openservice.entity.UserAccount;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends R2dbcRepository<UserAccount, String> {
}
