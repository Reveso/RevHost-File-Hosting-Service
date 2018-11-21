package com.lukasrosz.revhost.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import javax.sql.DataSource;

@Configuration
@EnableSocial
@Primary
public class SocialConfig extends SocialConfigurerAdapter {

    private DataSource mainDataSource;

    @Autowired
    public SocialConfig(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    @Bean
    public DataSourceInitializer databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(
                new ClassPathResource(
                        "org/springframework/social/connect/jdbc/JdbcUsersConnectionRepository.sql"));
//        populator.addScript(new ClassPathResource("sql/create_users.sql"));
//        populator.addScript(new ClassPathResource("sql/init_users.sql"));
        populator.setContinueOnError(true); // Continue in case the create script already ran
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDatabasePopulator(populator);
        initializer.setDataSource(mainDataSource);
        return initializer;
    }

    @Override
    @Bean
    @Primary
    public UsersConnectionRepository getUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(mainDataSource, connectionFactoryLocator,
                Encryptors.noOpText());
    }

    @Bean
    public ProviderSignInUtils providerSignInUtils(
            ConnectionFactoryLocator connectionFactoryLocator,
            UsersConnectionRepository usersConnectionRepository) {
        return new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Configuration
    public static class TwitterConfigurer extends SocialConfigurerAdapter {

        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer,
                                           Environment env) {

            connectionFactoryConfigurer.addConnectionFactory(
                    new TwitterConnectionFactory(
                            env.getRequiredProperty("twitter.appId"),
                            env.getRequiredProperty("twitter.appSecret")
                    )
            );
        }

        @Bean
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Twitter twitterTemplate(ConnectionRepository connectionRepository) {
            Connection<Twitter> connection = connectionRepository.
                    findPrimaryConnection(Twitter.class);
            return connection != null ? connection.getApi() : null;
        }
    }

    @Configuration
    public static class FacebookConfigurer extends SocialConfigurerAdapter {
        @Override
        public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer,
                                           Environment env) {

            connectionFactoryConfigurer.addConnectionFactory(
                    new FacebookConnectionFactory(
                            env.getRequiredProperty("facebook.appId"),
                            env.getRequiredProperty("facebook.appSecret")
                    )
            );
        }

        @Bean
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Facebook facebookTemplate(ConnectionRepository connectionRepository) {
            Connection<Facebook> connection = connectionRepository.
                    findPrimaryConnection(Facebook.class);
            return connection != null ? connection.getApi() : null;
        }
    }

}
