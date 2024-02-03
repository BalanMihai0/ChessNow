package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.business.IChessCRUD;
import fontys.sem3.chess.business.exception.MatchNotFoundException;
import fontys.sem3.chess.domain.Match;
import fontys.sem3.chess.domain.Move;
import fontys.sem3.chess.persistence.MatchRepository;
import fontys.sem3.chess.persistence.MoveRepository;
import fontys.sem3.chess.persistence.UserRepository;
import fontys.sem3.chess.persistence.entities.MatchEntity;
import fontys.sem3.chess.persistence.entities.MoveEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChessCRUDImpl implements IChessCRUD {
    @Autowired
    private final MatchRepository matchRepository;
    @Autowired
    private final MoveRepository moveRepository;
    @Autowired
    private final UserRepository userRepository;

    public ChessCRUDImpl(MatchRepository matchRepository, MoveRepository moveRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.moveRepository = moveRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Long storeMoveInExistingMatch( Move move) throws MatchNotFoundException {
        MoveEntity moveEntity = new MoveEntity();
        moveEntity.setMoveId(move.getMoveId());
        Optional<MatchEntity> matchEntityOptional = this.matchRepository.findById(move.getMatch().getMatchId());
        if(!matchEntityOptional.isPresent()) throw new MatchNotFoundException();
        moveEntity.setMatch(matchEntityOptional.get());
        moveEntity.setPlayer(ReverseUserEntityConverter.convert(move.getPlayer()));
        moveEntity.setFenString(move.getFenString());
        moveEntity.setMove(move.getMoveNotation());

        MoveEntity savedMove = moveRepository.save(moveEntity);

        return savedMove.getMoveId();
    }

    @Override
    public Match getMatchById(@NotNull long matchId) throws MatchNotFoundException {
        Optional<MatchEntity> matchEntityOptional = this.matchRepository.findById(matchId);

        if (matchEntityOptional.isPresent()) {
            return MatchEntityConverter.convert(matchEntityOptional.get());
        } else {
            throw new MatchNotFoundException();
        }
    }

    @Override
    public List<Move> getMovesByMatchId(long id) {
        return this.moveRepository.getMovesByMatchId(id).stream().map(MoveEntityConverter :: convert).toList();
    }

    @Override
    public List<Match> getMatchHistory(Long userId){
        return this.matchRepository.findMatchesByUserId(userId).stream().map(MatchEntityConverter :: convert).toList();
    }

    @Override
    public void endMatch(Long matchId, String matchResult) throws MatchNotFoundException {
        Optional<MatchEntity> matchEntityOptional = this.matchRepository.findById(matchId);
        if(matchEntityOptional.isPresent()){
            MatchEntity matchEntity = matchEntityOptional.get();
            matchEntity.setEndTimeStamp(LocalDateTime.now());
            matchEntity.setResult(matchResult);
            matchRepository.save(matchEntity);
        }
        else throw new MatchNotFoundException();

    }

    @Override
    public Long startMatch(String player1, String player2, Long matchId) throws MatchNotFoundException {
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setPlayer1(userRepository.findByUsername(player1));
        matchEntity.setPlayer2(userRepository.findByUsername(player2));
        matchEntity.setMatchId(matchId);
        matchEntity.setStartTimestamp((LocalDateTime.now()));
        matchEntity.setResult("In Progress");

        MatchEntity savedMatch = matchRepository.save(matchEntity);


        return savedMatch.getMatchId();
    }
}
