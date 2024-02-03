package fontys.sem3.chess.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "move")
@AllArgsConstructor
@NoArgsConstructor
public class MoveEntity {
    @Id
    @Column(name = "move_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moveId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity match;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private UserEntity player;

    @Column(name = "fenstring", nullable = false)
    private String fenString;

    @Column(name = "move", nullable = false)
    private String move;
}
