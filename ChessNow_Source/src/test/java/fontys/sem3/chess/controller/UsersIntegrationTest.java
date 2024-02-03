package fontys.sem3.chess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fontys.sem3.chess.business.IUsersCRUD;
import fontys.sem3.chess.business.exception.EmailAlreadyExistsException;
import fontys.sem3.chess.business.exception.UsernameAlreadyExistsException;
import fontys.sem3.chess.configuration.security.token.AccessTokenEncoder;
import fontys.sem3.chess.configuration.security.token.impl.AccessTokenImpl;
import fontys.sem3.chess.controller.dto.userdto.*;
import fontys.sem3.chess.domain.User;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UsersIntegrationTest {

    @LocalServerPort
    private static int port;
    private UsersController usersController;
    private IUsersCRUD usersCRUD;

    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccessTokenEncoder accessTokenEncoder;

    @Before
    public void setUp() {
        usersCRUD = mock(IUsersCRUD.class);
        accessTokenEncoder = mock(AccessTokenEncoder.class);
        usersController = new UsersController(usersCRUD, accessTokenEncoder);
    }

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String URL = createURLWithPort();
    }
    private static String createURLWithPort() {
        return "http://localhost:" + port + "/users";
    }

    @Test
    public void testGetUserWeb_UserExists() {
        // Arrange
        long userId = 1L;
        User mockUser;
        mockUser = new User(userId, "username", "email@example.com", "password",0, true);
        when(usersCRUD.getUser(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<User> response = usersController.getUserWeb(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    public void testGetUserWeb_UserDoesNotExist() {
        // Arrange
        long userId = 1L;
        when(usersCRUD.getUser(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = usersController.getUserWeb(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllUsersWeb() {
        // Arrange
        GetAllUsersRequest request = GetAllUsersRequest.builder().build();
        GetAllUsersResponse mockResponse = new GetAllUsersResponse();
        when(usersCRUD.getUsers()).thenReturn(mockResponse);

        // Act
        ResponseEntity<GetAllUsersResponse> response = usersController.getAllUsersWeb();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    public void testDeleteUserWeb() {
        // Arrange
        int userId = 1;
        doNothing().when(usersCRUD).deleteUser(userId);

        // Act
        ResponseEntity<Void> response = usersController.deleteUserWeb(userId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usersCRUD, times(1)).deleteUser(userId);
    }

    @Test
    public void testCreateUserWeb_Success() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        CreateUserResponse mockResponse = new CreateUserResponse();
        when(usersCRUD.createUser(UserRequestConverter.convertToUser(request))).thenReturn(mockResponse);

        // Act
        ResponseEntity<CreateUserResponse> response = usersController.createUserWeb(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(mockResponse);
    }

    @Test
    public void testCreateUserWeb_EmailAlreadyExists() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        when(usersCRUD.createUser(UserRequestConverter.convertToUser(request))).thenThrow(EmailAlreadyExistsException.class);

        // Act
        ResponseEntity<CreateUserResponse> response = usersController.createUserWeb(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email Already Exists", response.getBody().getErrorMessage());
    }


    @Test
    public void testCreateUserWeb_UsernameAlreadyExists() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        when(usersCRUD.createUser(UserRequestConverter.convertToUser(request))).thenThrow(UsernameAlreadyExistsException.class);

        // Act
        ResponseEntity<CreateUserResponse> response = usersController.createUserWeb(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody().getErrorMessage());
    }



    @Test
    public void testLogin_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("validUsername", "validPassword");
        User authenticatedUser = new User(1L, "validUsername", "email@example.com", "validPassword", 0, true);

        when(usersCRUD.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword()))
                .thenReturn(authenticatedUser);

        when(accessTokenEncoder.encode(any(AccessTokenImpl.class)))
                .thenReturn("mockedAccessToken");

        // Act
        ResponseEntity<String> response = usersController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isEqualTo("mockedAccessToken");

        verify(usersCRUD, times(1)).authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        verify(accessTokenEncoder, times(1)).encode(any(AccessTokenImpl.class));
    }

    @Test
    public void testLogin_Unauthorized() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("invalidUsername", "invalidPassword");

        when(usersCRUD.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword()))
                .thenReturn(null);

        // Act
        ResponseEntity<String> response = usersController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(usersCRUD, times(1)).authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        verify(accessTokenEncoder, never()).encode(any(AccessTokenImpl.class));
    }

    @Test
    public void testLogin_Exception() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("validUsername", "validPassword");

        when(usersCRUD.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword()))
                .thenThrow(new RuntimeException("Simulated exception"));

        // Act
        ResponseEntity<String> response = usersController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(usersCRUD, times(1)).authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        verify(accessTokenEncoder, never()).encode(any(AccessTokenImpl.class));
    }


    @Test
    public void testUpdateUserWeb_UserExistsAndUpdateIsSuccessful() throws Exception {
        // Arrange
        long userId = 1L;
        User user = new User();
        user.setUsername("newUsername");
        user.setEmail("new@example.com");

        UpdateUserRequest request = UpdateUserRequest.builder().username("Changed").email("changed@email.com").build();
        request.setRequesterId(userId);

        User mockUser = new User(userId, "existingUser", "existing@example.com", "password", 0, true);
        when(usersCRUD.getUser(userId)).thenReturn(Optional.of(mockUser));
        doNothing().when(usersCRUD).updateUser(UserRequestConverter.convertToUserUpdated(user, mockUser));

        // Act
        ResponseEntity<String> response = usersController.updateUserWeb(userId, request);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Updated succesfully", response.getBody());

        verify(usersCRUD, times(1)).updateUser(UserRequestConverter.convertToUserUpdated(mockUser, mockUser));
    }

    @Test
    public void testUpdateUserWeb_UserNotFound() throws Exception {
        // Arrange
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRequesterId(userId);

        when(usersCRUD.getUser(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = usersController.updateUserWeb(userId, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Attempted to edit invalid user", response.getBody());

        verify(usersCRUD, never()).updateUser(any());
    }

    @Test
    public void testUpdateUserWeb_EmailAlreadyExists() throws Exception {
        // Arrange
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("existing@example.com"); // Duplicate email
        request.setRequesterId(1L);

        User mockUser = new User(userId, "existingUser", "existing@example.com", "password", 0, true);
        when(usersCRUD.getUser(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<String> response = usersController.updateUserWeb(userId, request);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    }

    @Test
    public void testUpdateUserWeb_UsernameAlreadyExists() throws Exception {
        // Arrange
        long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("existingUser"); // Duplicate username
        request.setRequesterId(userId);

        User mockUser = new User(userId, "existingUser", "existing@example.com", "password", 0, true);
        when(usersCRUD.getUser(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<String> response = usersController.updateUserWeb(userId, request);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    }

    @Test
    public void testUpdateUserWeb_NotOwnDetails(){
        //Arrange
        long userId = 1L;
        long requesterId = 2L;
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setRequesterId(requesterId);

        User mockUser = new User(userId, "existingUser", "existing@example.com", "password", 0, true);
        when(usersCRUD.getUser(userId)).thenReturn(Optional.of(mockUser));

        //Act
        ResponseEntity<String> response = usersController.updateUserWeb(userId, updateUserRequest);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
