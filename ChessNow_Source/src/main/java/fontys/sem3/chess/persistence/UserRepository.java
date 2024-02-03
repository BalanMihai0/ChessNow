package fontys.sem3.chess.persistence;

import fontys.sem3.chess.persistence.entities.UserEntity;
import fontys.sem3.chess.persistence.entities.UserSocialEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
    UserEntity findByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.email = :email, u.username = :username, u.rating = :rating WHERE u.id = :id")
    void updateUserFields(@Param("id") Long id, @Param("email") String email, @Param("username") String username, @Param("rating") Integer rating);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.friends = :friends WHERE u.id = :userId")
    void addFriend(@Param("userId") Long userId, @Param("friends") Set<UserEntity> friends);

    @Query("SELECT NEW fontys.sem3.chess.persistence.entities.UserSocialEntity(" +
            "u.username, " +
            "u.rating, " +
            "COUNT(m), " + // Total matches
            "SUM(CASE WHEN m.result = 'Win' THEN 1 ELSE 0 END)" + // Count of wins
            ") " +
            "FROM UserEntity u " +
            "LEFT JOIN MatchEntity m ON u.id = m.player1.id OR u.id = m.player2.id " +
            "GROUP BY u.username, u.rating")
    List<UserSocialEntity> getUsersSocialData();


    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}