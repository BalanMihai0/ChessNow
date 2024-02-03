package fontys.sem3.chess.business;

import fontys.sem3.chess.business.exception.MatchNotFoundException;
import fontys.sem3.chess.domain.Match;
import fontys.sem3.chess.domain.Move;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IChessCRUD {

    Long storeMoveInExistingMatch( Move move) throws MatchNotFoundException;
    Match getMatchById(@NotNull long matchId) throws MatchNotFoundException;
    List<Move> getMovesByMatchId(long macthId);
    Long startMatch(String player1, String player2, Long matchId) throws MatchNotFoundException;
    List<Match> getMatchHistory(Long userId);
    void endMatch(Long matchId, String matchResult) throws MatchNotFoundException;
}
