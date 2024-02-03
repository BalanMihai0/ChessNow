package fontys.sem3.chess.configuration.security.token.impl;

import fontys.sem3.chess.configuration.security.token.AccessToken;
import fontys.sem3.chess.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;

@EqualsAndHashCode
@Getter
public class AccessTokenImpl implements AccessToken {
    private final String subject;
    private final String role;
    private final String username;
    private final Long rating;
    private final String email;

    public AccessTokenImpl(String subject, String role, String username, Long rating, String email) {
        this.subject = subject;
        this.role = role;
        this.rating = rating;
        this.username = username;
        this.email = email;
    }

    public AccessTokenImpl(User user) {
        String role1;
        subject = String.valueOf(user.getId());
        email = String.valueOf(user.getEmail());
        rating = Long.valueOf(user.getRating());
        username = String.valueOf(user.getUsername());
        if (user.isAdmin()) {
            role1 = "ADMIN";
        } else {
            role1 = "USER";
        }
        role = role1;
    }

    @Override
    public String getRole() {
        return role;
    }
}
