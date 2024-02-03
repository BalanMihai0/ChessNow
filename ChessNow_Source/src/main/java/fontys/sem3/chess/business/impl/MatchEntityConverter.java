package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.domain.Match;
import fontys.sem3.chess.persistence.entities.MatchEntity;

public final class MatchEntityConverter {

    private MatchEntityConverter(){}
    public static Match convert(MatchEntity match){
        if(match == null) return null;
        return Match.builder()
                .matchId(match.getMatchId())
                .player1(match.getPlayer1().getUsername())
                .player2(match.getPlayer2().getUsername())
                .startTime(match.getStartTimestamp())
                .result(match.getResult())
                .build();
    }

}
