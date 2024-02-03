package fontys.sem3.chess.controller.dto.userdto;

import fontys.sem3.chess.domain.User;
import fontys.sem3.chess.domain.UserSocial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersSocial {
    private List<UserSocial> users;
}

