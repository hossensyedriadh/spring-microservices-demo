package io.github.hossensyedriadh.edgeservice.configuration.authentication.bearer.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Log4j
@Configuration
public class BearerTokenUtils {
    @Value("${bearer-authentication.public-key.path}")
    private String filePath;

    @Bean
    public RSAPublicKey publicKey() {
        try {
            File file = ResourceUtils.getFile(this.filePath);
            InputStream inputStream = new FileInputStream(file);
            byte[] bytes = inputStream.readAllBytes();
            String key = new String(bytes);

            key = key.replace("-----BEGIN CERTIFICATE-----", "")
                    .replaceAll("\\R", "")
                    .replace("-----END CERTIFICATE-----", "").trim();

            byte[] keyBytes = Base64.getDecoder().decode(key);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            if (publicKey instanceof RSAPublicKey) {
                inputStream.close();
                return (RSAPublicKey) publicKey;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Unable to load RSA Public Key");
    }
}
