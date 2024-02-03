package fontys.sem3.chess.controller;

import fontys.sem3.chess.business.impl.MatchEntityConverter;
import fontys.sem3.chess.business.impl.MoveEntityConverter;
import fontys.sem3.chess.business.impl.UserEntityConverter;
import fontys.sem3.chess.domain.Match;
import fontys.sem3.chess.domain.Move;
import fontys.sem3.chess.domain.User;
import fontys.sem3.chess.persistence.entities.MatchEntity;
import fontys.sem3.chess.persistence.entities.MoveEntity;
import fontys.sem3.chess.persistence.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertNull;

class ConverterTests {

    @ParameterizedTest
    @NullSource
    void testConvertEntityToMove_Null(MoveEntity moveEntity) {
        // Act
        Move result = MoveEntityConverter.convert(moveEntity);

        // Assert
        assertNull(result);
    }

    @ParameterizedTest
    @NullSource
    void testConvertUserEntityToUser_Null(UserEntity userEntity) {
        // Act
        User result = UserEntityConverter.convert(userEntity);

        // Assert
        assertNull(result);
    }

    @ParameterizedTest
    @NullSource
    void testConvertMatchEntityToMatch_Null(MatchEntity matchEntity) {
        // Act
        Match result = MatchEntityConverter.convert(matchEntity);

        // Assert
        assertNull(result);
    }
}
