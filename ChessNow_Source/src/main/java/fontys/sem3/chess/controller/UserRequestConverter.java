package fontys.sem3.chess.controller;

import fontys.sem3.chess.controller.dto.userdto.CreateUserRequest;
import fontys.sem3.chess.controller.dto.userdto.UpdateUserRequest;
import fontys.sem3.chess.domain.User;

import java.util.Optional;


final class UserRequestConverter {

    private UserRequestConverter(){} //hide implicit public constructor
    public static User convertToUser(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .isAdmin(request.isAdmin())
                .build();
    }

    public static User convertToUserUpdated(User request, User user) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .id(request.getId())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(user.getPassword())
                .isAdmin(user.isAdmin())
                .build();
    }
}
