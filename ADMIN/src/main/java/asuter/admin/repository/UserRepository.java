package asuter.admin.repository;

import java.util.List;
import asuter.admin.domain.Users;

public interface UserRepository {

  List<Users> findByLogin(String login);
  Users findOne(String login);

}
