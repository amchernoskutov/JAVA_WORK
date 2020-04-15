package mainscheduler.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * WebSecurityConfig
 * 
 * Определяем URL открытые и закрытые. 
 * Считываем логин и пароль из файла application.properties
 *
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Value("${mainscheduler.username}")
  private String userName;
  
  @Value("${mainscheduler.password}")
  private String password;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/", "/mainscheduler").hasAuthority("ADMIN")
        .and().formLogin().loginPage("/login").usernameParameter("username")
        .passwordParameter("password").and().logout().logoutSuccessUrl("/") 
        .and().headers().frameOptions().sameOrigin();
    http.csrf().disable().authorizeRequests().anyRequest().permitAll(); 
  }  

  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }

  @Override 
  protected void configure(AuthenticationManagerBuilder auth)
      throws Exception {
//    System.out.println(passwordEncoder().encode(password));
//    passwordEncoder().encode(password)
    auth
      .inMemoryAuthentication().withUser(userName).password(password).authorities("ADMIN");
  }
}
