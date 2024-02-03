package fontys.sem3.chess.business.exception;


public class MatchNotFoundException extends Exception {
    public MatchNotFoundException() {
        super("match was not found");
    }
}
