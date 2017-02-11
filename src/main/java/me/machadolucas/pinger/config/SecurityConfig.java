package me.machadolucas.pinger.config;

import me.machadolucas.pinger.service.CustomMongoSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().antMatcher("/**").authorizeRequests().anyRequest().hasAnyAuthority("USER").and()
                .httpBasic();
    }

    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        public CustomMongoSecurityService mongoSecurityService;

        @Override
        public void init(final AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(this.mongoSecurityService);
        }

    }


}
