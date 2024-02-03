package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.persistence.entities.UserEntity;
import fontys.sem3.chess.domain.*;

import java.util.Optional;

public final class UserEntityConverter {

    private UserEntityConverter(){}
    public static User convert(UserEntity user) {
        if(user == null) return null;
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .isAdmin(user.isAdmin())
                .rating(user.getRating())
                .build();
    }

    public static Optional<User> convertOptional(Optional<UserEntity> userOptional) {
        return userOptional.map(UserEntityConverter::convert);
    }

}

