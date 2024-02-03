package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.domain.Move;
import fontys.sem3.chess.persistence.entities.MoveEntity;

public final class MoveEntityConverter {
    private MoveEntityConverter(){}

    public static Move convert(MoveEntity moveEntity) {
        if (moveEntity == null) {
            return null;
        }

        return Move.builder()
                .moveId(moveEntity.getMoveId())
                .match(MatchEntityConverter.convert(moveEntity.getMatch()))
                .player(UserEntityConverter.convert(moveEntity.getPlayer()) )
                .fenString(moveEntity.getFenString())
                .moveNotation(moveEntity.getMove())
                .build();
    }
}
