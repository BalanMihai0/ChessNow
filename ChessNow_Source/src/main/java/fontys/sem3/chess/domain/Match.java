package fontys.sem3.chess.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Long matchId;
    private String player1;
    private String player2;
    private LocalDateTime startTime;
    private String result;
}

