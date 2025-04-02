package com.navyn.emissionlog.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Value("${spring.mvc.cors.allowed-origins}")
    private String allowedOrigin;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

//        if("dev".equals(activeProfile)){
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
//        }
//        else {
//            ArrayList<String> allowedOrigins = new ArrayList<>(Arrays.asList(allowedOrigin.split(",")));
//            allowedOrigins.forEach(config::addAllowedOrigin);
//        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
