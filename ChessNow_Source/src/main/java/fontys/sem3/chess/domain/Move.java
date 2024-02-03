package fontys.sem3.chess.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    private Long moveId;
    private Match match;
    private User player;
    private String fenString;
    private String moveNotation;

    public Move(Match match, User player, String fenString, String moveNotation) {
        this.match = match;
        this.player = player;
        this.fenString = fenString;
        this.moveNotation = moveNotation;
    }
}
