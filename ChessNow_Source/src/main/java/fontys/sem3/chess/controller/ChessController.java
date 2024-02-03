package fontys.sem3.chess.controller;

import fontys.sem3.chess.business.IChessCRUD;
import fontys.sem3.chess.business.IUsersCRUD;
import fontys.sem3.chess.business.exception.MatchNotFoundException;
import fontys.sem3.chess.controller.dto.chessdto.*;
import fontys.sem3.chess.domain.Match;
import fontys.sem3.chess.domain.Move;
import fontys.sem3.chess.domain.User;

import java.util.List;
import java.util.Random;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chess")
public class ChessController {
    private final IChessCRUD chessCRUD;
    private final IUsersCRUD usersCRUD;

    public ChessController(IChessCRUD chessCRUD, IUsersCRUD usersCRUD) {
        this.chessCRUD = chessCRUD;
        this.usersCRUD = usersCRUD;
    }

    @RolesAllowed({"USER"})
    @PostMapping("/move")
    public ResponseEntity<String> makeMove(@RequestBody MakeMoveRequest moveRequest) {
        try {
            Match currentMatch = this.chessCRUD.getMatchById(moveRequest.getMatchId());
            User player1 = usersCRUD.getUserByUsername(moveRequest.getPlayer());
            Move move = new Move(currentMatch, player1, moveRequest.getAfter(), moveRequest.getLan());
            this.chessCRUD.storeMoveInExistingMatch(move);
            return ResponseEntity.ok("Move successful");
        }
        catch(MatchNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Match not found");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid move attempt");
        }
    }
    @RolesAllowed({"USER"})
    @PostMapping("/startmatch")
    public StartMatchResponse startMatch(@RequestBody StartMatchRequest matchRequest) {
        try {
            Random random = new Random();
            long randomId = 100_000_000L + random.nextInt(900_000_000);
            Long id = this.chessCRUD.startMatch(matchRequest.getPlayer(), matchRequest.getOpponent(), randomId);
            return new StartMatchResponse(id);
        }
        catch(Exception e){
            return new StartMatchResponse();

        }
    }

    @RolesAllowed({"USER"})
    @GetMapping("/matchHistory/{userId}")
    public MatchHistoryResponse getMatchHistory(@PathVariable("userId") Long userId){
        try{
            List<Match> matches = this.chessCRUD.getMatchHistory(userId);
            return MatchHistoryResponse.builder().matches(matches).build();
        }
        catch(Exception e){
            return new MatchHistoryResponse();
        }
    }


    @GetMapping("/matchMoves/{matchId}")
    public MatchMovesResponse getMatchMoves(@PathVariable("matchId") Long matchId){
        try{
            List<Move> movesOfMatch = this.chessCRUD.getMovesByMatchId(matchId);
            return  MatchMovesResponse.builder().moves(movesOfMatch).build();
        } catch (Exception e) {
            return new MatchMovesResponse();
        }
    }

    @GetMapping("/lastMatchPosition/{matchId}")
    public ResponseEntity<String> getLastPosition(@PathVariable("matchId") Long matchId){
        try {
            List<Move> movesOfMatch = this.chessCRUD.getMovesByMatchId(matchId);

            if (!movesOfMatch.isEmpty()) {
                Move lastMove = movesOfMatch.get(movesOfMatch.size() - 1);
                String fenString = lastMove.getFenString();
                return ResponseEntity.ok(fenString);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/endMatch")
    public EndMatchResponse endMatch(@RequestBody EndMatchRequest endMatchRequest){
        try {
            this.chessCRUD.endMatch(endMatchRequest.getMatchId(), endMatchRequest.getMatchResult());
            return EndMatchResponse.builder().response("Match ended").build();
        }catch(MatchNotFoundException e){
            return EndMatchResponse.builder().response(e.getMessage()).build();
        } catch(Exception e){
            return EndMatchResponse.builder().response("Could not end match").build();
        }
    }

}
