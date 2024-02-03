package fontys.sem3.chess.business.exception;

public class InvalidEmailFormatException extends Exception {
    public InvalidEmailFormatException(){
        super("Email format is not valid");
    }
}
