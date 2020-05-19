package asuter.admin.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import asuter.admin.config.session.SessionConfig;
import lombok.extern.log4j.Log4j2;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@Log4j2
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private DataSource dataSource;
  
  @Autowired
  private SessionConfig sessionConfig;
  
  @Override 
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
    .antMatchers("/", "/asuteradmin").hasAuthority("asuter_admin") 
    .and()
        .formLogin().loginPage("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .failureForwardUrl("/login/failed")
    .and()
        .logout().logoutSuccessUrl("/") 
    .and()
        .headers()
        .frameOptions()
        .sameOrigin();

    http.csrf().disable().authorizeRequests().anyRequest().permitAll();
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.jdbcAuthentication()
      .dataSource(dataSource)
      .usersByUsernameQuery("select users.usename as username, overlay(authid.rolpassword placing '' from 1 for 3) as password, case when (((users.valuntil is null) or (users.valuntil > now()))) then true else false end as enabled from pg_catalog.pg_user users, pg_authid authid where (authid.oid = users.usesysid) and (users.usename = ?)") 
      .authoritiesByUsernameQuery("select users.usename as username, roles.rolname as role from pg_catalog.pg_user users, pg_catalog.pg_roles roles, pg_catalog.pg_auth_members members where (users.usesysid = members.member) and (members.roleid = roles.oid) and (users.usename = ?)");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PasswordEncoder() {
      @Override
      public String encode(CharSequence charSequence) {
        String username = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest().getParameter("username");
        return getMd5(charSequence.toString() + username);
      }

      @Override
      public boolean matches(CharSequence charSequence, String s) {
        String username = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest().getParameter("username");
        String password = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest().getParameter("password");
        
        try {
          sessionConfig.initSession(username);
          sessionConfig.initJdbcTemplate(username, password);
        } catch (Exception e) {
          log.error(e.getMessage());
        }

        return getMd5(charSequence.toString() + username).equals(s);
      }
    };
  }

  public static String getMd5(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] messageDigest = md.digest(input.getBytes());
      BigInteger no = new BigInteger(1, messageDigest);
      String hashtext = no.toString(16);

      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }

      return hashtext;
    } catch (NoSuchAlgorithmException e) {
      System.out.println("Exception thrown for incorrect algorithm: " + e);
      return null;
    }
  }  
  
}
