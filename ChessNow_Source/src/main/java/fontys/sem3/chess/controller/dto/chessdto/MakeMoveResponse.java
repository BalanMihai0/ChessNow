package fontys.sem3.chess.controller.dto.chessdto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakeMoveResponse {
    private @NotNull boolean isValidMove;
    private @NotNull boolean wasMoveMade;
    //currently replaced with responseEntity, could be used later
}
