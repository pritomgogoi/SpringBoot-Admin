package com.poc.springadmin;

import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.logging.Logger;

/**
 * @author by Pritom Gogoi
 */

@EnableAdminServer
@Configuration
@SpringBootApplication
public class ApplicationInvoker extends SpringBootServletInitializer {

    private String environment = System.getProperty("CLOUD_ENVIRONMENT");
    private static final Logger LOG = Logger.getLogger(ApplicationInvoker.class.getName());


    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(ApplicationInvoker.class);
    }

    public static void main(final String[] args) {
        SpringApplication.run(ApplicationInvoker.class, args);
    }


    @Configuration
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Page with login form is served as /login.html and does a POST on /login
            http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll();
            // The UI does a POST on /logout on logout
            http.logout().logoutUrl("/logout");
            // The ui currently doesn't support csrf
            http.csrf().disable();

            // Requests for the login page and the static assets are allowed
            http.authorizeRequests()
                    .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
                    .permitAll();
            // ... and any other request needs to be authorized
            http.authorizeRequests().antMatchers("/**").authenticated();

            // Enable so that the clients can authenticate via HTTP basic for registering
            http.httpBasic();
        }
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        if (environment != null) {
            LOG.info("Running in aws .....");
            return (container -> container.setPort(8080));
        } else {
            LOG.info("Running locally .....");
            return (container -> container.setPort(9411));
        }

    }

}