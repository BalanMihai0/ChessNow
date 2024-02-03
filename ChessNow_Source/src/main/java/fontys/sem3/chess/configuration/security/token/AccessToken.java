package fontys.sem3.chess.configuration.security.token;

public interface AccessToken {
    String getSubject();
    String getRole();
    String getUsername();
    Long getRating();
    String getEmail();


}
