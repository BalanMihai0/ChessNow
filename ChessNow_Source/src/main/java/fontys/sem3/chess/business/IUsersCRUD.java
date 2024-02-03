package fontys.sem3.chess.business;

import fontys.sem3.chess.business.exception.EmailAlreadyExistsException;
import fontys.sem3.chess.business.exception.UsernameAlreadyExistsException;
import fontys.sem3.chess.controller.dto.userdto.*;

import java.util.List;
import java.util.Optional;
import fontys.sem3.chess.domain.*;

public interface IUsersCRUD {
    void updateUser(User toUpdate) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    Optional<User> getUser(long id);

    GetAllUsersResponse getUsers();

    void deleteUser(long id);

    CreateUserResponse createUser(User request) throws EmailAlreadyExistsException, UsernameAlreadyExistsException;

    User authenticateUser(String username, String password);
    User getUserByUsername(String username);

    List<UserSocial> getSortedUsers(boolean ascending);

    Long getNumberOfMatchesPlayed(Long userId);

    Long getNumberOfMatchesWon(Long userId);

    List<UserSocial> getUserSocial();
}
