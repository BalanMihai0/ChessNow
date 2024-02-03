package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.domain.User;
import fontys.sem3.chess.persistence.entities.UserEntity;

public final class ReverseUserEntityConverter {

    private ReverseUserEntityConverter(){}
    public static UserEntity convert(User user) {
        if(user == null) return null;
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .isAdmin(user.isAdmin())
                .rating(user.getRating())
                .build();
    }
}
