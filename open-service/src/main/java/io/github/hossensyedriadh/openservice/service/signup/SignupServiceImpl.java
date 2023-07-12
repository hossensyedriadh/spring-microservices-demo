package io.github.hossensyedriadh.openservice.service.signup;

import io.github.hossensyedriadh.openservice.configuration.mail.MailModel;
import io.github.hossensyedriadh.openservice.entity.UserAccount;
import io.github.hossensyedriadh.openservice.enumerator.Authority;
import io.github.hossensyedriadh.openservice.exception.ResourceException;
import io.github.hossensyedriadh.openservice.repository.r2dbc.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Locale;

@Service
public final class SignupServiceImpl implements SignupService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveKafkaProducerTemplate<String, MailModel> reactiveKafkaProducerTemplate;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${kafka.producer.topic.mail}")
    private String mailTopic;

    @Autowired
    public SignupServiceImpl(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder,
                             ReactiveKafkaProducerTemplate<String, MailModel> reactiveKafkaProducerTemplate,
                             SpringTemplateEngine springTemplateEngine) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
        this.springTemplateEngine = springTemplateEngine;
    }

    @Override
    public Mono<Void> signup(UserAccount userAccount) {
        userAccount.setPassword(this.passwordEncoder.encode(userAccount.getPassword()));
        userAccount.setAuthority(Authority.ROLE_CUSTOMER);

        return this.userAccountRepository.findById(userAccount.getUsername())
                .flatMap(user -> {
                    if (user != null) {
                        return Mono.error(new ResourceException("Duplicate user", HttpStatus.BAD_REQUEST));
                    }
                    return Mono.empty();
                }).switchIfEmpty(this.userAccountRepository.save(userAccount).publishOn(Schedulers.boundedElastic())
                        .doOnSuccess(account -> this.sendSignupEmail(account).publishOn(Schedulers.boundedElastic()).subscribe()))
                .then();
    }

    @Override
    public Mono<Boolean> checkUsername(String username) {
        return this.userAccountRepository.findById(username).hasElement().map(exists -> !exists);
    }

    private Mono<Void> sendSignupEmail(UserAccount account) {
        Context context = new Context(Locale.ENGLISH);
        context.setVariable("name", account.getFirstName().concat(" ").concat(account.getLastName()));

        try {
            final String htmlContent = this.springTemplateEngine.process("mail/signup-success.html", context);

            MailModel mailModel = new MailModel(account.getEmail(), htmlContent);

            return this.reactiveKafkaProducerTemplate.send(this.mailTopic, mailModel).then();
        } catch (MessagingException e) {
            throw new ResourceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
