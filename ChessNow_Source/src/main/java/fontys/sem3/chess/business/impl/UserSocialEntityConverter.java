package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.domain.UserSocial;
import fontys.sem3.chess.persistence.entities.UserSocialEntity;

public class UserSocialEntityConverter {
    private UserSocialEntityConverter(){

    }

    static UserSocial convert(UserSocialEntity userSocialEntity){
        if(userSocialEntity == null) return null;
        return UserSocial.builder()
                .name(userSocialEntity.getName())
                .rating(userSocialEntity.getRating())
                .matchesplayed(userSocialEntity.getMatchesplayed())
                .matchesWon(userSocialEntity.getMatchesWon())
                .build();
    }
}
