package asuter.admin.jdbc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import asuter.admin.domain.Users;

@Repository
public class JdbcUserRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;
  
  public Users findOne(String username) {
    return jdbcTemplate.queryForObject("select users.usesysid as usesysid, users.usename as username, overlay(authid.rolpassword placing '' from 1 for 3) as password, case when (((users.valuntil is null) or (users.valuntil > now()))) then true else false end as enabled from pg_catalog.pg_user users, pg_authid authid where (authid.oid = users.usesysid) and (users.usename = ?)",
        new RowMapper<Users>() {

          @Override
          public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
            Users user = new Users();
            user.setUsesysid(rs.getLong("usesysid"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setActive(rs.getBoolean("enabled"));
            return user;
          }
        }, username);
  }
  
}
