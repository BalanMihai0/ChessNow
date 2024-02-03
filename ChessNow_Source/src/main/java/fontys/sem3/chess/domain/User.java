package fontys.sem3.chess.domain;

import fontys.sem3.chess.business.exception.InvalidEmailFormatException;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    private Long id;
    private String email;
    private String username;
    private String password;
    @Builder.Default
    private Integer rating = 800;
    @Builder.Default
    private boolean isAdmin = false;

    public User(String email, String username, String password, boolean isAdmin, Integer Rating)  {
        try {
            checkEmail(email);
            this.email = email;
            this.username = username;
            this.password = hashPassword(password);
            this.isAdmin = isAdmin;
            this.rating = Rating;
        }
        catch(InvalidEmailFormatException e){
        }

    }
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public void checkEmail(String email) throws InvalidEmailFormatException {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new InvalidEmailFormatException();
        }
    }

}
