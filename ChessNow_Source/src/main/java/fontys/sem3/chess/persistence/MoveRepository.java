package fontys.sem3.chess.persistence;

import fontys.sem3.chess.persistence.entities.MoveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MoveRepository extends JpaRepository<MoveEntity, Long> {
    @Query("SELECT m FROM MoveEntity m WHERE m.match.matchId = :matchId")
    List<MoveEntity> getMovesByMatchId(@Param("matchId") Long matchId);
}
