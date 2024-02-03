package fontys.sem3.chess.business.exception;
public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(){
        super("Email already exists");
    }
}
