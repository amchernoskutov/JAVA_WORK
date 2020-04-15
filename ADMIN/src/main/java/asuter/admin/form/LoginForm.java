package asuter.admin.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginForm {
  private String username; 
  private Boolean usernameIsActive; 
  private Boolean usernameIsFound; 
  private String password; 
  private Boolean passwordIsFound; 

}
