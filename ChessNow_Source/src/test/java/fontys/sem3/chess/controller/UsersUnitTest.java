package fontys.sem3.chess.controller;

import fontys.sem3.chess.business.exception.EmailAlreadyExistsException;
import fontys.sem3.chess.business.exception.InvalidUserException;
import fontys.sem3.chess.business.exception.UsernameAlreadyExistsException;
import fontys.sem3.chess.business.impl.UsersCRUDImpl;
import fontys.sem3.chess.controller.dto.userdto.CreateUserResponse;
import fontys.sem3.chess.controller.dto.userdto.GetAllUsersResponse;
import fontys.sem3.chess.domain.User;
import fontys.sem3.chess.domain.UserSocial;
import fontys.sem3.chess.persistence.MatchRepository;
import fontys.sem3.chess.persistence.UserRepository;
import fontys.sem3.chess.persistence.entities.UserSocialEntity;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import fontys.sem3.chess.persistence.entities.UserEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class UsersUnitTest {

    private UserRepository userRepository;

    private UsersCRUDImpl usersCRUD;
    private MatchRepository matchRepository;

    @BeforeEach
     void setup() {
        userRepository =  mock(UserRepository.class);
        matchRepository = mock(MatchRepository.class);
        usersCRUD = new UsersCRUDImpl(userRepository, matchRepository);
    }

    @Test
     void testCreateUser() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        // Arrange
        User testUser = new User("test@test.com", "testuser", "password", false, 800);
        UserEntity testEntity = UserEntity.builder()
                .email("test@test.com")
                .username("testuser")
                .password("password")
                .isAdmin(false)
                .build();
        // Act
        Mockito.when(userRepository.save(any())).thenReturn(testEntity);
        CreateUserResponse response = usersCRUD.createUser(testUser);

        //Assert
        assertNotNull(response);
    }

    @Test
     void testCreateUserWithExistingEmail() {
        // Arrange
        User testUser = new User("existing@test.com", "testuser", "password", false, 800);

        // Act
        Mockito.when(userRepository.save(any())).thenThrow(new ConstraintViolationException("unique_email", new HashSet<>()));

        // Assert
        assertThrows(EmailAlreadyExistsException.class, () -> usersCRUD.createUser(testUser));
    }

    @Test
     void testCreateUserWithExistingUsername()   {
        //Arrange
        User testUser = new User("test@test.com", "existinguser", "password", false,800);

        // ACt
        Mockito.when(userRepository.save(any())).thenThrow(new ConstraintViolationException("unique_username", new HashSet<>()));

        // Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> usersCRUD.createUser(testUser));
    }

    @Test
    void testCreateUserWithUnexpectedException() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        // Arrange
        User testUser = new User("test@test.com", "testuser", "password", false, 800);

        // Act
        Mockito.when(userRepository.save(any())).thenThrow(new RuntimeException("Unexpected exception"));

        // Assert
        assertNull(usersCRUD.createUser(testUser));
    }


    @Test
     void testDeleteUser() {
        long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        usersCRUD.deleteUser(userId);

        verify(userRepository, Mockito.times(1)).deleteById(userId);
    }


    @Test
     void testGetUser() {
        long userId = 1L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        Optional<User> userOptional = usersCRUD.getUser(userId);

        assertTrue(userOptional.isPresent());
    }

    @Test
    void testGetNonExistentUser() {
        long userId = 0L;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> userOptional = usersCRUD.getUser(userId);

        assertTrue(userOptional.isEmpty());
    }


    @Test
     void testGetUsers() {

        List<UserEntity> userEntities = List.of(new UserEntity(), new UserEntity());
        Mockito.when(userRepository.findAll()).thenReturn(userEntities);

        GetAllUsersResponse response = usersCRUD.getUsers();

        assertNotNull(response);
        assertNotNull(response.getUsers());
        assertEquals(userEntities.size(), response.getUsers().size());
    }

    @Test
     void testUpdateUser() throws UsernameAlreadyExistsException, EmailAlreadyExistsException  {

        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setUsername("newUsername");
        userToUpdate.setEmail("newEmail");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("oldUsername");
        userEntity.setEmail("oldEmail");

        Mockito.when(userRepository.findById(userToUpdate.getId())).thenReturn(Optional.of(userEntity));

        // Call the updateUser method
        usersCRUD.updateUser(userToUpdate);

        verify(userRepository).save(userEntity);
        assertEquals(userToUpdate.getUsername(), userEntity.getUsername());
        assertEquals(userToUpdate.getEmail(), userEntity.getEmail());
    }


    @Test
     void testUpdateNonExistentUser() {
        long userId = 1L;
        User updatedUser = new User("updated@test.com", "updateduser", "newpassword", false, 800);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidUserException.class, () -> usersCRUD.updateUser(updatedUser));
    }

    @Test
    void testAuthenticateUser_ValidCredentials() {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .username(username)
                .password(hashedPassword)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        // Act
        User authenticatedUser = usersCRUD.authenticateUser(username, password);

        // Assert
        assertNotNull(authenticatedUser);
        assertEquals(username, authenticatedUser.getUsername());
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String invalidPassword = "wrongpassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .username(username)
                .password(hashedPassword)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        // Act
        User authenticatedUser = usersCRUD.authenticateUser(username, invalidPassword);

        // Assert
        assertNull(authenticatedUser);
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        // Arrange
        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenThrow(NullPointerException.class);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> usersCRUD.authenticateUser(username, "password"));
    }

    @Test
    void testGetUserByUsername_WithExistingUser() {
        // Arrange
        String username = "testuser";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername(username);

        UsersCRUDImpl usersCRUD = new UsersCRUDImpl(userRepository, matchRepository);

        // Mock the repository behavior
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        // Act
        User result = usersCRUD.getUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(username, result.getUsername());
    }

    @Test
    void testGetUserByUsername_WithNonExistingUser() {
        // Arrange
        String username = "nonexistentuser";

        UsersCRUDImpl usersCRUD = new UsersCRUDImpl(userRepository, matchRepository);

        // Mock the repository behavior
        when(userRepository.findByUsername(username)).thenThrow(InvalidUserException.class);

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> usersCRUD.getUserByUsername(username));
    }

    @Test
    void testGetSortedUsers_AscendingOrder() {
        // Arrange
        UsersCRUDImpl usersCRUD = new UsersCRUDImpl(userRepository, matchRepository);

        List<UserSocialEntity> userEntities = Arrays.asList(
                UserSocialEntity.builder().name("user1").rating(100).build(),
                UserSocialEntity.builder().name("user2").rating(80).build(),
                UserSocialEntity.builder().name("user3").rating(120).build()
        );

        // Mock the repository behavior
        when(userRepository.getUsersSocialData()).thenReturn(userEntities);

        // Act
        List<UserSocial> result = usersCRUD.getSortedUsers(true);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("user2", result.get(0).getName());
        assertEquals("user1", result.get(1).getName());
        assertEquals("user3", result.get(2).getName());
    }

    @Test
    void testGetSortedUsers_DescendingOrder() {
        // Arrange
        UsersCRUDImpl usersCRUD = new UsersCRUDImpl(userRepository, matchRepository);

        List<UserSocialEntity> userEntities = Arrays.asList(
                UserSocialEntity.builder().name("user1").rating(100).build(),
                UserSocialEntity.builder().name("user2").rating(80).build(),
                UserSocialEntity.builder().name("user3").rating(120).build()
        );

        // Mock the repository behavior
        when(userRepository.getUsersSocialData()).thenReturn(userEntities);

        // Act
        List<UserSocial> result = usersCRUD.getSortedUsers(false);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("user3", result.get(0).getName());
        assertEquals("user1", result.get(1).getName());
        assertEquals("user2", result.get(2).getName());
    }

    @Test
    void testGetNumberOfMatchesPlayed() {
        // Arrange
        Long userId = 1L;
        when(matchRepository.countMatchesByUserId(userId)).thenReturn(5L);

        // Act
        Long result = usersCRUD.getNumberOfMatchesPlayed(userId);

        // Assert
        assertEquals(5L, result);
        verify(matchRepository, times(1)).countMatchesByUserId(userId);
    }

    @Test
    void testGetNumberOfMatchesWon() {
        // Arrange
        Long userId = 1L;
        when(matchRepository.countMatchesWonByUserId(userId)).thenReturn(3L);

        // Act
        Long result = usersCRUD.getNumberOfMatchesWon(userId);

        // Assert
        assertEquals(3L, result);
        verify(matchRepository, times(1)).countMatchesWonByUserId(userId);
    }
}
