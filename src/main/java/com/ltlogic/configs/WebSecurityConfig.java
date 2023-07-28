/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ltlogic.configs;

/**
 *
 * @author Jaimel
 */
import com.ltlogic.service.springsecurity.SecurityContextAccessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
@EnableWebSecurity
//@EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityContextAccessorImpl securityContextAccessorImpl(){
        return new SecurityContextAccessorImpl(); 
    }
    
    @Bean 
    public AuthenticationTrustResolver authenticationTrustResolver(){
        return new AuthenticationTrustResolver() {
            @Override
            public boolean isAnonymous(Authentication a) {
                if(a instanceof AnonymousAuthenticationToken){
                    return true;
                }
                return false;
            }

            @Override
            public boolean isRememberMe(Authentication a) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }
    
//    @Override
//    public void configure(final WebSecurity web) throws Exception {
//        web.ignoring()
//            .antMatchers("/resources/**", "/static/**");
//    }
    
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
    
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/register", "/login", "/rest/test", "/rules/*").permitAll()
                .antMatchers("/password/forgot", "/password/reset", "/password/reset/successful", "/username/forgot").permitAll()
                .antMatchers("/resources/**", "../templates/**").permitAll()
                .antMatchers("/upload").permitAll()
                .antMatchers("/account", "/account/*", "/account/profile/*", "/account/bank/*").authenticated()
                .antMatchers("/bank/account/deposit", "/bank/account/transfer", "/bank/account/transfer/*", "/bank/account/withdraw", "/bank/account/withdraw/*").authenticated()
                .antMatchers("/teams", "/teams/*", "/teams/**", "/*/teams/*", "/*/teams/*/", "/*/teams/*/invites/*").authenticated()
                .antMatchers("/*/profile/*", "/*/invites/*", "/*/matches/*", "/*/tournaments/*", "/*/notifications/*").authenticated()
                .antMatchers("/matches/*/accept", "/matches/*/accept/*", "/matches/*/request-cancel", "/matches/*/disputes", "/matches/*/report-score").authenticated()
                .antMatchers("/matches/*/cancel-match-game").authenticated()
                .antMatchers("/*/disputes/my-disputes", "/*/disputes/my-disputes/*", "/disputes/*").authenticated()
                .antMatchers("/tournaments/*/join").authenticated()
                .antMatchers("/shop/authenticate").authenticated()
                .antMatchers("/dashboard").authenticated()
                .antMatchers("/matches/create/team-select").authenticated()
                .anyRequest().permitAll()
                .and()
                    .rememberMe()
                    .key("secretkeyxms123")
                    .rememberMeCookieName("xms-remember-me")
                    .tokenValiditySeconds(48 * 60 * 60)
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/loginerror")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
            .csrf().disable()
            .logout().logoutSuccessUrl("/")
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
