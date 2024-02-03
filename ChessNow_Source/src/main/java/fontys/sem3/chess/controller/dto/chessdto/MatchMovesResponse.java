package fontys.sem3.chess.controller.dto.chessdto;

import fontys.sem3.chess.domain.Move;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchMovesResponse {
    private List<Move> moves;
}
