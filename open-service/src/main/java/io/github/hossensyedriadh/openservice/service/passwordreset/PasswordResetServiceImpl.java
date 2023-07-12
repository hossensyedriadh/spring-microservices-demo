package io.github.hossensyedriadh.openservice.service.passwordreset;

import io.github.hossensyedriadh.openservice.configuration.mail.MailModel;
import io.github.hossensyedriadh.openservice.entity.Otp;
import io.github.hossensyedriadh.openservice.entity.UserAccount;
import io.github.hossensyedriadh.openservice.enumerator.OtpType;
import io.github.hossensyedriadh.openservice.exception.ResourceException;
import io.github.hossensyedriadh.openservice.model.PasswordResetRequest;
import io.github.hossensyedriadh.openservice.repository.r2dbc.OtpRepository;
import io.github.hossensyedriadh.openservice.repository.r2dbc.UserAccountRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public final class PasswordResetServiceImpl implements PasswordResetService {
    private final UserAccountRepository userAccountRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveKafkaProducerTemplate<String, MailModel> reactiveKafkaProducerTemplate;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${kafka.producer.topic.mail}")
    private String mailTopic;

    @Autowired
    public PasswordResetServiceImpl(UserAccountRepository userAccountRepository, OtpRepository otpRepository,
                                    PasswordEncoder passwordEncoder, ReactiveKafkaProducerTemplate<String, MailModel> reactiveKafkaProducerTemplate,
                                    SpringTemplateEngine springTemplateEngine) {
        this.userAccountRepository = userAccountRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
        this.springTemplateEngine = springTemplateEngine;
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

                    return this.otpRepository.save(otp).publishOn(Schedulers.boundedElastic())
                            .doOnSuccess(o -> this.sendForgotPasswordEmail(userAccount, plainOtp)
                                    .publishOn(Schedulers.boundedElastic()).subscribe());
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
                    userAccount.setIsNew(false);
                    return this.userAccountRepository.save(userAccount).publishOn(Schedulers.boundedElastic())
                            .doOnSuccess(s -> this.otpRepository.deleteById(otp.getId()).publishOn(Schedulers.boundedElastic()).subscribe())
                            .doOnSuccess(account -> this.sendConfirmationEmail(account).publishOn(Schedulers.boundedElastic()).subscribe());
                }
                return Mono.error(new ResourceException("Invalid OTP", HttpStatus.BAD_REQUEST));
            }).then();
        }).then();
    }

    private Mono<Void> sendForgotPasswordEmail(UserAccount account, String otp) {
        Context context = new Context(Locale.ENGLISH);
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", account.getFirstName().concat(" ").concat(account.getLastName()));
        variables.put("otp", otp);
        variables.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a Z")));
        variables.put("expiry", ZonedDateTime.now().plusMinutes(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a Z")));

        context.setVariables(variables);

        try {
            final String htmlContent = this.springTemplateEngine.process("mail/password-reset-verification.html", context);

            MailModel mailModel = new MailModel(account.getEmail(), htmlContent);

            return this.reactiveKafkaProducerTemplate.send(this.mailTopic, mailModel).then();
        } catch (MessagingException e) {
            throw new ResourceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Mono<Void> sendConfirmationEmail(UserAccount account) {
        Context context = new Context(Locale.ENGLISH);
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", account.getFirstName().concat(" ").concat(account.getLastName()));
        variables.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a Z")));

        context.setVariables(variables);

        try {
            final String htmlContent = this.springTemplateEngine.process("mail/password-reset-confirmation.html", context);

            MailModel mailModel = new MailModel(account.getEmail(), htmlContent);

            return this.reactiveKafkaProducerTemplate.send(this.mailTopic, mailModel).then();
        } catch (MessagingException e) {
            throw new ResourceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
