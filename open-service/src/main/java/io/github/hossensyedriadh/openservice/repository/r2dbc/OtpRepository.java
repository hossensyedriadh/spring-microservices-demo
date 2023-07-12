package io.github.hossensyedriadh.openservice.repository.r2dbc;

import io.github.hossensyedriadh.openservice.entity.Otp;
import io.github.hossensyedriadh.openservice.enumerator.OtpType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OtpRepository extends R2dbcRepository<Otp, Long> {
    Flux<Otp> findAllByForUserAndType(String username, OtpType type);

    @Query("select * from user_otps where for_user = :username and type = :type and expires_on > unix_timestamp() * 1000;")
    Flux<Otp> findValidOtps(@Param("username") String username, @Param("type") OtpType type);
}
