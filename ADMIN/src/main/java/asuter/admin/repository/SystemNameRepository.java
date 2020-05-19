package asuter.admin.repository;

import java.util.List;
import asuter.admin.domain.SystemName;

public interface SystemNameRepository {

  List<SystemName> findByAll() throws Exception;
  SystemName findOne(int id) throws Exception;
  void add(String mnem_system, String description) throws Exception;
  void delete(int id) throws Exception;
  void update(int id, String mnem_system, String description) throws Exception;

}
