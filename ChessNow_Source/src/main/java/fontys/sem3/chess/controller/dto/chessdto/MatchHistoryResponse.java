package fontys.sem3.chess.controller.dto.chessdto;

import fontys.sem3.chess.domain.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchHistoryResponse {
    private List<Match> matches;
}
