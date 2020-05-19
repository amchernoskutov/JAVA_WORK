package asuter.admin.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import asuter.admin.config.session.SessionConfig;
import asuter.admin.domain.SystemName;
import asuter.admin.repository.SystemNameRepository;

@Service
public class SystemNameService implements SystemNameRepository {
  
  @Autowired
  SessionConfig sessionConfig;
  
  public List<SystemName> findByAll() throws Exception {
    return sessionConfig.getUserConfig().getJdbcTemplate().query("select id_system, mnem_system, description from config.get_system_name()", 
        new RowMapper<SystemName>() {

          @Override
          public SystemName mapRow(ResultSet rs, int rowNum) throws SQLException {
            SystemName systemName = new SystemName();
            systemName.setIdSystem(Math.toIntExact(rs.getLong("id_system")));
            systemName.setMnemSystem(rs.getString("mnem_system"));
            systemName.setDestination(rs.getString("description"));
            return systemName;
          }
        });
  }

  public SystemName findOne(int id) throws Exception {
    
    return sessionConfig.getUserConfig().getJdbcTemplate()
      .query("select id_system, mnem_system, description from config.get_one_system_name(?) as (id_system smallint, mnem_system varchar, description varchar)", 
      new Object[] {id},
      new RowMapper<SystemName>() {
        @Override
        public SystemName mapRow(ResultSet rs, int rowNum) throws SQLException {
          SystemName systemName = new SystemName();
          systemName.setIdSystem(Math.toIntExact(rs.getLong("id_system")));
          systemName.setMnemSystem(rs.getString("mnem_system"));
          systemName.setDestination(rs.getString("description"));
          return systemName;
        }
    }).get(0);
  }

  public void add(String mnem_system, String description) throws Exception {
    sessionConfig.getUserConfig().getJdbcTemplate().update("call config.add_system_name(?,?)", mnem_system, description);
  };

  public void delete(int id) throws Exception {
    sessionConfig.getUserConfig().getJdbcTemplate().update("call config.delete_system_name(?)", id);
  };
  
  public void update(int id, String mnem_system, String description) throws Exception {
    sessionConfig.getUserConfig().getJdbcTemplate().update("call config.update_system_name(?, ?, ?)", id, mnem_system, description);
  };


}
