package fontys.sem3.chess.business.impl;

import fontys.sem3.chess.business.exception.UsernameAlreadyExistsException;
import fontys.sem3.chess.domain.*;

import fontys.sem3.chess.business.IUsersCRUD;
import fontys.sem3.chess.business.exception.EmailAlreadyExistsException;
import fontys.sem3.chess.business.exception.InvalidUserException;
import fontys.sem3.chess.controller.dto.userdto.*;
import fontys.sem3.chess.persistence.MatchRepository;
import fontys.sem3.chess.persistence.UserRepository;
import fontys.sem3.chess.persistence.entities.UserEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UsersCRUDImpl implements IUsersCRUD {

    @Autowired
    private final UserRepository userRepository;

    private final MatchRepository matchRepository;

    @Override
    public CreateUserResponse createUser(User toAdd) throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        try {
            User savedUser = this.saveUser(toAdd);
            return CreateUserResponse.builder().id(savedUser.getId()).build();
        } catch (Exception e) {
            if (e instanceof ConstraintViolationException && e.getMessage().contains("unique_email")) {
                throw new EmailAlreadyExistsException();
            } else if (e instanceof ConstraintViolationException && e.getMessage().contains("unique_username")) {
                throw new UsernameAlreadyExistsException();
            }
            return null;
        }
    }

    private User saveUser(User request) {
        //ctor hashes password
        User newUser = new User(request.getEmail(), request.getUsername(), request.getPassword(), request.isAdmin(), request.getRating());

        UserEntity newUserEntity = ReverseUserEntityConverter.convert(newUser);
        UserEntity savedUserEntity = this.userRepository.save(newUserEntity);

        return UserEntityConverter.convert(savedUserEntity);
    }

    @Override
    public void deleteUser(long id) {
        this.userRepository.deleteById(id);
    }
    @Override
    @Transactional
    public Optional<User> getUser(long id) {
        return UserEntityConverter.convertOptional(this.userRepository.findById(id));
    }
    @Override
    public GetAllUsersResponse getUsers() {
        List<User> result = this.userRepository.findAll().stream().map(UserEntityConverter::convert).toList();
        GetAllUsersResponse response = new GetAllUsersResponse();
        List<User> users = result.stream().toList();
        response.setUsers(users);
        return response;
    }
    @Override
    public void updateUser(User toUpdate) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Optional<UserEntity> userEntityOptional = this.userRepository.findById(toUpdate.getId());
        if (userEntityOptional.isEmpty()) {
            throw new InvalidUserException("USER_ID_INVALID");
        }else {
            if(this.userRepository.existsByUsername(toUpdate.getUsername()) && this.userRepository.findByUsername(toUpdate.getUsername()).getId() != toUpdate.getId()){
                throw new UsernameAlreadyExistsException();
            } else if(this.userRepository.existsByEmail(toUpdate.getEmail()) && this.userRepository.findByEmail(toUpdate.getEmail()).getId() != toUpdate.getId()){
                throw new EmailAlreadyExistsException();
            }
        }

        UserEntity userEntity = userEntityOptional.get();

        // Copy the updated fields
        userEntity.setEmail(toUpdate.getEmail());
        userEntity.setUsername(toUpdate.getUsername());
        userEntity.setRating(toUpdate.getRating());
        // Not modify id
        this.userRepository.save(userEntity);
    }

    @Override
    public User authenticateUser(String username, String password) {
        User user = UserEntityConverter.convert(userRepository.findByUsername(username));
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username){
        return UserEntityConverter.convert(userRepository.findByUsername(username));
    }

    @Override
    public List<UserSocial> getSortedUsers(boolean ascending) {
        Comparator<UserSocial> ratingComparator = Comparator.comparingInt(UserSocial::getRating);
        if (!ascending) {
            ratingComparator = ratingComparator.reversed();
        }

        return this.userRepository.getUsersSocialData()
                .stream()
                .map(UserSocialEntityConverter::convert)
                .sorted(ratingComparator)
                .toList();
    }

    @Override
    public Long getNumberOfMatchesPlayed(Long userId) {
        return this.matchRepository.countMatchesByUserId(userId);
    }

    @Override
    public Long getNumberOfMatchesWon(Long userId) {
        return this.matchRepository.countMatchesWonByUserId(userId);
    }

    @Override
    public List<UserSocial> getUserSocial() {
        return this.userRepository.getUsersSocialData().stream().map(UserSocialEntityConverter :: convert).toList();
    }


    public UsersCRUDImpl(final UserRepository userRepository, MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }
}
