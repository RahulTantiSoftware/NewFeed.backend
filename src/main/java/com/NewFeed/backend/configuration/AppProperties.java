package com.NewFeed.backend.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth ;
    private final OAuth2 oauth2;
    private final Cors cors;
    public AppProperties(){
        auth = new Auth();
        oauth2 = new OAuth2();
        cors = new Cors();
    }

    @Getter
    @Setter
    public static class Auth {
        private String jwtCookieName;
        private String jwtRefreshCookieName;
        private String jwtSecret;
        private String cookieDomain;
        private long   jwtExpirationMs;
        private long   jwtRefreshExpirationMs;
    }

    @Getter
    @Setter
    public static class Cors {
        private boolean allowCredentials;
        private List<String> allowedOrigins ;
        private List<String> allowedMethods ;
        private List<String> allowedHeaders ;
    }

    @Getter
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }
}
