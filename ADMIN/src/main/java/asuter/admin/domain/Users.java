package asuter.admin.domain;

import java.util.Arrays;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Users { 

  private Long usesysid;

  @NotNull
  @Size(min = 5, max = 100, message = "Логин пользователя должен быть не менее 5-ти и не более 100 символов в длинну")
  private String username; 
  
  private String password; 

  private boolean active;
  
  public Users() {
    //
  }

}
