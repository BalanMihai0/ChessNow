package fontys.sem3.chess.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSocial {
    String name;
    Integer rating;
    Long matchesplayed;
    Long matchesWon;
}