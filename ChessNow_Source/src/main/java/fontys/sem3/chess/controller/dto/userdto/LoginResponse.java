package fontys.sem3.chess.controller.dto.userdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    @NotBlank String accesToken;
}
