package fontys.sem3.chess.persistence;

import fontys.sem3.chess.persistence.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {
    @Query("SELECT m FROM MatchEntity m WHERE m.player1.id = :userId OR m.player2.id = :userId")
    List<MatchEntity> findMatchesByUserId(Long userId);
    @Query("SELECT COUNT(m) FROM MatchEntity m WHERE m.player1.id = :userId OR m.player2.id = :userId")
    Long countMatchesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MatchEntity m WHERE (m.player1.id = :userId OR m.player2.id = :userId) AND m.result = 'Win'")
    Long countMatchesWonByUserId(@Param("userId") Long userId);

}
