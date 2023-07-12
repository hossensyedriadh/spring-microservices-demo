package io.github.hossensyedriadh.openservice.service.passwordreset;

import io.github.hossensyedriadh.openservice.entity.Otp;
import io.github.hossensyedriadh.openservice.entity.UserAccount;
import io.github.hossensyedriadh.openservice.enumerator.OtpType;
import io.github.hossensyedriadh.openservice.exception.ResourceException;
import io.github.hossensyedriadh.openservice.model.PasswordResetRequest;
import io.github.hossensyedriadh.openservice.repository.r2dbc.OtpRepository;
import io.github.hossensyedriadh.openservice.repository.r2dbc.UserAccountRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public final class PasswordResetServiceImpl implements PasswordResetService {
    private final UserAccountRepository userAccountRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetServiceImpl(UserAccountRepository userAccountRepository, OtpRepository otpRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Void> forgotPassword(String username) {
        Mono<UserAccount> userAccountMono = this.userAccountRepository.findById(username);

        return userAccountMono.switchIfEmpty(Mono.error(new ResourceException("User not found", HttpStatus.BAD_REQUEST)))
                .flatMap(userAccount -> {
                    String plainOtp = RandomStringUtils.randomNumeric(6);
                    Otp otp = new Otp();
                    otp.setOtp(this.passwordEncoder.encode(plainOtp));
                    otp.setExpiresOn(Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli());
                    otp.setType(OtpType.PASSWORD_RESET);
                    otp.setForUser(username);

                    return this.otpRepository.save(otp).doOnSuccess(o -> this.sendForgotPasswordEmail(userAccount, plainOtp));
                }).then();
    }

    @Override
    public Mono<Void> verifyPasswordReset(PasswordResetRequest passwordResetRequest) {
        return this.userAccountRepository.findById(passwordResetRequest.getUsername())
                .switchIfEmpty(Mono.error(new ResourceException("User not found", HttpStatus.BAD_REQUEST)))
                .flatMap(userAccount -> this.otpRepository.findValidOtps(userAccount.getUsername(), OtpType.PASSWORD_RESET)
                        .switchIfEmpty(Mono.error(new ResourceException("Invalid OTP", HttpStatus.BAD_REQUEST)))
                        .flatMap(otp -> {
                            if (!this.passwordEncoder.matches(passwordResetRequest.getOtp(), otp.getOtp())) {
                                return Mono.error(new ResourceException("Invalid OTP", HttpStatus.BAD_REQUEST));
                            }
                            return Mono.just(otp);
                        }).then());
    }

    @Override
    public Mono<Void> resetPassword(PasswordResetRequest passwordResetRequest) {
        Mono<UserAccount> userAccountMono = this.userAccountRepository.findById(passwordResetRequest.getUsername());

        return userAccountMono.switchIfEmpty(Mono.error(new ResourceException("User not found", HttpStatus.BAD_REQUEST))).flatMap(userAccount -> {
            Flux<Otp> otpFlux = this.otpRepository.findValidOtps(userAccount.getUsername(), OtpType.PASSWORD_RESET);

            return otpFlux.switchIfEmpty(Mono.error(new ResourceException("Invalid OTP", HttpStatus.BAD_REQUEST))).flatMap(otp -> {
                if (this.passwordEncoder.matches(passwordResetRequest.getOtp(), otp.getOtp())) {
                    userAccount.setPassword(this.passwordEncoder.encode(passwordResetRequest.getNewPassword()));
                    return this.userAccountRepository.save(userAccount).doOnSuccess(this::sendConfirmationEmail)
                            .publishOn(Schedulers.boundedElastic())
                            .doOnSuccess(s -> this.otpRepository.deleteById(otp.getId()).subscribe());
                }
                return Mono.error(new ResourceException("Invalid OTP", HttpStatus.BAD_REQUEST));
            }).then();
        }).then();
    }

    private void sendForgotPasswordEmail(UserAccount account, String otp) {
        //todo: to be implemented using kafka
    }

    private void sendConfirmationEmail(UserAccount account) {
        //todo: to be implemented using kafka
    }
}
