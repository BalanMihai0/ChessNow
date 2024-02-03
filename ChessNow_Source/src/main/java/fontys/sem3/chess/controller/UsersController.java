package fontys.sem3.chess.controller;
import fontys.sem3.chess.business.*;
import fontys.sem3.chess.business.exception.EmailAlreadyExistsException;
import fontys.sem3.chess.business.exception.UsernameAlreadyExistsException;
import fontys.sem3.chess.configuration.security.token.AccessTokenEncoder;
import fontys.sem3.chess.configuration.security.token.impl.AccessTokenImpl;
import fontys.sem3.chess.controller.dto.userdto.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fontys.sem3.chess.domain.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping({"/users"})
public class UsersController {
    private final IUsersCRUD usersCRUD;
    private final AccessTokenEncoder accessTokenEncoder;

    @GetMapping({"{id}"})
    public ResponseEntity<User> getUserWeb(@PathVariable("id") final long id) {
        Optional<User> userOptional = this.usersCRUD.getUser(id);
        return userOptional.map(user -> ResponseEntity.ok().body(user)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RolesAllowed({"ADMIN","USER"})
    @GetMapping
    public ResponseEntity<GetAllUsersResponse> getAllUsersWeb() {
        GetAllUsersResponse response = this.usersCRUD.getUsers();
        return ResponseEntity.ok(response);
    }

    @RolesAllowed({"ADMIN","USER"})
    @GetMapping("/social")
    public GetAllUsersSocial getAllUsersSocial() {
        GetAllUsersSocial response = GetAllUsersSocial.builder().users(this.usersCRUD.getUserSocial()).build();
        return response;
    }

    @RolesAllowed({"ADMIN"})
    @DeleteMapping({"/delete/{id}"})
    public ResponseEntity<Void> deleteUserWeb(@PathVariable int id) {
        this.usersCRUD.deleteUser((id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUserWeb(@RequestBody @Valid CreateUserRequest request) {

        CreateUserResponse response;
        User toAdd = UserRequestConverter.convertToUser(request);
        try {
            response = this.usersCRUD.createUser(toAdd);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CreateUserResponse.builder().errorMessage("Email Already Exists").build());
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CreateUserResponse.builder().errorMessage("Username already exists").build());
        }

    }

    @RolesAllowed({"USER"})
    @PutMapping("{id}")
    public ResponseEntity<String> updateUserWeb(@PathVariable("id") long id, @RequestBody  UpdateUserRequest request) {
        try {
            if(request.getRequesterId() != id)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attempted to edit another user's data ");
            Optional<User> userOptional = this.usersCRUD.getUser(id);
            if(userOptional.isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attempted to edit invalid user");
            User user = userOptional.get();
            User updatedUser = user;
            updatedUser.setEmail(request.getEmail());
            updatedUser.setUsername(request.getUsername());
            //user now contains all fields which cannot be updated
            //request is filled with the new object, which has to override user in the db
            this.usersCRUD.updateUser(UserRequestConverter.convertToUserUpdated(updatedUser, user));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Updated succesfully");
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Already Exists");
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody  LoginRequest loginRequest) {
        try {
            User authenticatedUser = usersCRUD.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            if (authenticatedUser != null) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(accessTokenEncoder.encode(new AccessTokenImpl(authenticatedUser)));
            } else {
                //401
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @RolesAllowed({"USER"})
    @GetMapping("/sortRating/{ascending}")
    public SortByRatingResponse getSorted(@PathVariable("ascending") boolean ascending ){
        try{

            List<UserSocial> sortedUsers = usersCRUD.getSortedUsers(ascending);
           return SortByRatingResponse.builder().sortedUsers(sortedUsers).build();
        }catch(Exception e){
            return new SortByRatingResponse();
        }
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/totalMatches/{username}")
    public TotalMatchResponse getTotalMatches(@PathVariable("username") String username){
        try{
            Long userId = (this.usersCRUD.getUserByUsername(username)).getId();
            Long number = this.usersCRUD.getNumberOfMatchesPlayed(userId);
            return TotalMatchResponse.builder().number(number).build();
        }catch(Exception e){
            return new TotalMatchResponse();
        }
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/totalMatchesWon/{username}")
    public TotalMatchResponse getTotalMatchesWon(@PathVariable("username") String username){
        try{
            Long userId = (this.usersCRUD.getUserByUsername(username)).getId();
            Long number = this.usersCRUD.getNumberOfMatchesWon(userId);
            return TotalMatchResponse.builder().number(number).build();
        }catch(Exception e){
            return new TotalMatchResponse();
        }
    }

    public UsersController(final IUsersCRUD usersCRUD, final AccessTokenEncoder accessTokenEncoder) {
        this.usersCRUD = usersCRUD;
        this.accessTokenEncoder = accessTokenEncoder;
    }
}
