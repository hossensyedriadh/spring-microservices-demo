package io.github.hossensyedriadh.mailservice.service;

import io.github.hossensyedriadh.mailservice.model.MailModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j
@Service
public class MailProcessingService implements CommandLineRunner {
    private final ReactiveKafkaConsumerTemplate<String, MailModel> mailModelReactiveKafkaConsumerTemplate;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Autowired
    public MailProcessingService(ReactiveKafkaConsumerTemplate<String, MailModel> mailModelReactiveKafkaConsumerTemplate, JavaMailSender javaMailSender) {
        this.mailModelReactiveKafkaConsumerTemplate = mailModelReactiveKafkaConsumerTemplate;
        this.javaMailSender = javaMailSender;
    }

    private Flux<MailModel> listenToMailTopic() {
        return this.mailModelReactiveKafkaConsumerTemplate.receiveAutoAck().map(ConsumerRecord::value).doOnNext(mail -> log.info("New mail in queue [" + "Recipient: " + mail.getTo()
                        + ", Sender: " + mail.getFrom() + ", Subject: " + mail.getSubject() + "]"))
                .doOnError(throwable -> log.error("Consumption error: " + throwable.getMessage()));
    }

    @Override
    public void run(String... args) {
        this.listenToMailTopic().flatMap(mail -> {
            try {
                MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, this.charset);
                mimeMessageHelper.setFrom(mail.getFrom());
                mimeMessageHelper.setTo(mail.getTo());
                mimeMessageHelper.setSubject(mail.getSubject());
                mimeMessageHelper.setText(mail.getBody(), true);
                this.javaMailSender.send(mimeMessage);
            } catch (MessagingException e) {
                log.error("Failed to send email: " + e.getMessage());
            }

            return Mono.empty();
        }).subscribe();
    }
}
