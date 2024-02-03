package fontys.sem3.chess.configuration.security.token;

public interface AccessTokenEncoder {
    String encode(AccessToken accessToken);
}
