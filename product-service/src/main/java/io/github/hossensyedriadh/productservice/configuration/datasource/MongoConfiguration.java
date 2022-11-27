package io.github.hossensyedriadh.productservice.configuration.datasource;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.MongoConfigurationSupport;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUrl;

    /**
     * Return the name of the database to connect to.
     *
     * @return must not be {@literal null}.
     */
    @Override
    protected String getDatabaseName() {
        return this.databaseName;
    }

    /**
     * Return the Reactive Streams {@link MongoClient} instance to connect to. Annotate with {@link Bean} in case you want
     * to expose a {@link MongoClient} instance to the {@link ApplicationContext}. <br />
     * Override {@link #mongoClientSettings()} to configure connection details.
     *
     * @return never {@literal null}.
     * @see #mongoClientSettings()
     */
    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(this.mongoUrl);
    }

    /**
     * Returns the base packages to scan for MongoDB mapped entities at startup. Will return the package name of the
     * configuration class' (the concrete class, not this one here) by default. So if you have a
     * {@code com.acme.AppConfig} extending {@link MongoConfigurationSupport} the base package will be considered
     * {@code com.acme} unless the method is overridden to implement alternate behavior.
     *
     * @return the base packages to scan for mapped classes or an empty collection to not enable scanning
     * for entities.
     * @since 1.10
     */
    @Override
    protected Collection<String> getMappingBasePackages() {
        return Collections.singleton("io.github.hossensyedriadh.productservice");
    }
}
