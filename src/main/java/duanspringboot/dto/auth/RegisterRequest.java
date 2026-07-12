package duanspringboot.dto.auth;
import duanspringboot.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email khong duoc de trong")
    private String email;
    @NotBlank(message = "Password khong duoc de trong")
    @Size(min = 6, message = "Password phai co it nhat 6 ky tu")
    private String password;

    @NotNull(message = "Role khong duoc de trong")
    private Role role;
}
