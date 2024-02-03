package fontys.sem3.chess.controller.dto.userdto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private @NotBlank String username;
    private @NotNull boolean isAdmin;
    private @NotNull String password;
    private @NotBlank String email;
}
