package fontys.sem3.chess.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialEntity {
    String name;
    Integer rating;
    Long matchesplayed;
    Long matchesWon;
}