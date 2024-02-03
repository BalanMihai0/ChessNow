package fontys.sem3.chess.controller.dto.chessdto;

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
public class MakeMoveRequest {
    private @NotNull long matchId;
    private @NotBlank String after;
    private @NotBlank String lan;
    private @NotNull boolean isGameOver;
    private @NotBlank String opponent; //AI, or player username
    private @NotBlank String player;
}
