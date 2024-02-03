package fontys.sem3.chess.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "`match`")
@AllArgsConstructor
@NoArgsConstructor
public class MatchEntity {
    @Id
    @Column(name = "match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "player1_id", nullable = false)
    private UserEntity player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", nullable = false)
    private UserEntity player2;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTimestamp;

    @Column(name = "end_time")
    private LocalDateTime endTimeStamp;

    @Column(name = "result", nullable = false)
    private String result;
}
