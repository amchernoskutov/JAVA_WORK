package com.rgd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import com.rgd.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserService userService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/", "/index", "/customer", "/order", "/orderadd", "/orderedit", "/product", "/report").hasAuthority("ROLE_USER")
        .antMatchers("/login", "/registration").access("permitAll")
        .and().formLogin().loginPage("/login").usernameParameter("username")
        .passwordParameter("password").and().logout().logoutSuccessUrl("/") 
        .and().headers().frameOptions().sameOrigin();
    http.csrf().disable().authorizeRequests().anyRequest().permitAll(); 
  } 

  @Bean
  public PasswordEncoder encoder() {
    return new StandardPasswordEncoder("53cr3t");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(encoder());
  }
}
