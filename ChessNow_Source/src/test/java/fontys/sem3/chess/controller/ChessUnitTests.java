package fontys.sem3.chess.controller;

import fontys.sem3.chess.business.exception.MatchNotFoundException;
import fontys.sem3.chess.business.impl.ChessCRUDImpl;
import fontys.sem3.chess.domain.Match;
import fontys.sem3.chess.domain.Move;

import fontys.sem3.chess.persistence.MatchRepository;
import fontys.sem3.chess.persistence.MoveRepository;
import fontys.sem3.chess.persistence.UserRepository;
import fontys.sem3.chess.persistence.entities.MatchEntity;
import fontys.sem3.chess.persistence.entities.MoveEntity;
import fontys.sem3.chess.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ChessUnitTests {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MoveRepository moveRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChessCRUDImpl chessCRUD;

    @BeforeEach
    void setup() {
        // Initialize
        userRepository = mock(UserRepository.class);
        matchRepository = mock(MatchRepository.class);
        moveRepository = mock(MoveRepository.class);
        // Inject
        chessCRUD = new ChessCRUDImpl(matchRepository, moveRepository, userRepository);
    }
//    @Test
//    public void storeMoveInExistingMatch_shouldStoreMove() throws MatchNotFoundException {
//        // Arrange
//        Move move = new Move();
//        move.setMoveId(1L);
//
//        User user = new User();
//        user.setId(1L);
//        move.setPlayer(user);
//
//        Match match = new Match();
//        match.setMatchId(1L);
//        move.setMatch(match);
//
//        MatchEntity matchEntity = new MatchEntity();
//        matchEntity.setMatchId(1L);
//
//        when(matchRepository.findById(anyLong())).thenReturn(Optional.of(matchEntity));
//
//        MoveEntity expectedMoveEntity = new MoveEntity();
//        expectedMoveEntity.setMoveId(1L);
//        expectedMoveEntity.setMatch(matchEntity);
//
//        //when(moveRepository.save(expectedMoveEntity)).thenReturn(expectedMoveEntity);
//
////        when(moveRepository.save(any(MoveEntity.class))).thenAnswer(invocation -> {
////            MoveEntity moveEntityArg = invocation.getArgument(0);
////            if (moveEntityArg == null) {
////                return expectedMoveEntity;
////            } else {
////                if (moveEntityArg.getMoveId() != null) {
////                    return moveEntityArg;
////                } else {
////                    return expectedMoveEntity;
////                }
////            }
////        });
//
//        // Act
//        Long result = chessCRUD.storeMoveInExistingMatch(move);
//
//        // Assert
//        assertEquals(expectedMoveEntity.getMoveId(), result);
//        verify(matchRepository, times(1)).findById(anyLong());
//        verify(moveRepository, times(1)).save(expectedMoveEntity);
//    }

    @Test
    void storeMoveInExistingMatch_MatchNotFoundException() {
        // Arrange
        Move move = new Move(); // Provide necessary details for move
        move.setMatch(new Match()); // Provide necessary details for match in the move

        when(matchRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MatchNotFoundException.class, () -> chessCRUD.storeMoveInExistingMatch(move));
    }

    @Test
    void getMatchById_Success() throws MatchNotFoundException {
        // Arrange
        long matchId = 1L;
        MatchEntity matchEntity = new MatchEntity(); // Provide necessary details for match entity
        matchEntity.setMatchId(matchId);
        matchEntity.setPlayer1(UserEntity.builder().id(1L).build());
        matchEntity.setPlayer2(UserEntity.builder().id(2L).build());

        when(matchRepository.findById(anyLong())).thenReturn(Optional.of(matchEntity));

        // Act
        Match result = chessCRUD.getMatchById(matchId);

        // Assert
        assertNotNull(result);
        assertEquals(matchId, result.getMatchId());
    }

    @Test
    void getMatchById_MatchNotFoundException() {
        // Arrange
        long matchId = 1L;

        when(matchRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MatchNotFoundException.class, () -> chessCRUD.getMatchById(matchId));
    }

    @Test
    void getMovesByMatchId_Success() {
        // Arrange
        long matchId = 1L;

        UserEntity player1 = new UserEntity();
        player1.setId(1L);
        player1.setUsername("testPlayer1");

        UserEntity player2 = new UserEntity();
        player2.setId(2L);
        player2.setUsername("testPlayer2");

        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setPlayer1(player1);
        matchEntity.setPlayer2(player2);
        matchEntity.setMatchId(1L);

        MoveEntity moveEntity = new MoveEntity();
        moveEntity.setMatch(matchEntity);
        moveEntity.setMoveId(1L);

        when(moveRepository.getMovesByMatchId(anyLong())).thenReturn(Collections.singletonList(moveEntity));

        // Act
        List<Move> result = chessCRUD.getMovesByMatchId(matchId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getMatchHistory_Success() {
        // Arrange
        MatchEntity matchEntity = new MatchEntity();

        when(matchRepository.findAll()).thenReturn(Collections.singletonList(matchEntity));

        List<Match> result = chessCRUD.getMatchHistory(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void startMatch_Success() throws MatchNotFoundException {
        // Arrange
        String player1 = "player1";
        String player2 = "player2";
        Long matchId = 1L;

        UserEntity userEntity1 = new UserEntity();
        userEntity1.setUsername(player1);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setUsername(player2);

        when(userRepository.findByUsername(player1)).thenReturn(userEntity1);
        when(userRepository.findByUsername(player2)).thenReturn(userEntity2);

        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setMatchId(matchId);

        when(matchRepository.save(ArgumentMatchers.any())).thenReturn(matchEntity);

        // Act
        Long result = chessCRUD.startMatch(player1, player2, matchId);

        // Assert
        assertNotNull(result);
        assertEquals(matchId, result);

        // Verify that userRepository.findByUsername was called with the correct parameters
        verify(userRepository, times(1)).findByUsername(player1);
        verify(userRepository, times(1)).findByUsername(player2);
    }


    @Test
    void endMatch_Success() throws MatchNotFoundException {
        // Arrange
        long matchId = 1L;
        String matchResult = "Win";

        MatchEntity existingMatchEntity = new MatchEntity();
        existingMatchEntity.setMatchId(matchId);

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(existingMatchEntity));

        // Act
        chessCRUD.endMatch(matchId, matchResult);

        // Assert
        verify(matchRepository, times(1)).findById(matchId);
        verify(matchRepository, times(1)).save(existingMatchEntity);

        assertNotNull(existingMatchEntity.getEndTimeStamp());
        assertEquals(matchResult, existingMatchEntity.getResult());
    }

    @Test
    void endMatch_MatchNotFound() {
        // Arrange
        long nonExistingMatchId = 2L;
        String matchResult = "Win";

        when(matchRepository.findById(nonExistingMatchId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(MatchNotFoundException.class, () -> chessCRUD.endMatch(nonExistingMatchId, matchResult));
        verify(matchRepository, times(1)).findById(nonExistingMatchId);
    }
}
