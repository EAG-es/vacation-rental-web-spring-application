package com.vacationstay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuration class for internationalization (i18n).
 * <p>
 * This class configures locale resolution and language switching
 * for the VacationStay application.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {

    /**
     * Creates a session-based locale resolver.
     * <p>
     * This resolver stores the user's locale preference in the HTTP session.
     * </p>
     *
     * @return the configured LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return sessionLocaleResolver;
    }

    /**
     * Creates a locale change interceptor.
     * <p>
     * This interceptor allows changing the locale by passing a 'lang' parameter
     * in the request (e.g., ?lang=es for Spanish).
     * </p>
     *
     * @return the configured LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    /**
     * Registers the locale change interceptor.
     *
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}