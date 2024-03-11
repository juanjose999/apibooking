package com.bookings.service.controller.user;

import com.bookings.service.dto.user.UserDto;
import com.bookings.service.dto.user.UserResponseDto;
import com.bookings.service.service.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static com.bookings.service.utils.Constants.ADMIN_ROLE;


@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        //loadSampleUsers();
    }

    public void loadSampleUsers() {
        LocalDate fecha = LocalDate.of(2024, 2, 22);
        UserDto userEntity = new UserDto("Ada", " Lovelace", fecha, "ada@mail.com", "passw0rd");
        userService.createUser(userEntity);
        UserDto adminUserEntity = new UserDto("Ada", "Admin", fecha, "admin@mail.com", "passw0rd");
        UserResponseDto userCreated = userService.createUser(adminUserEntity);
        createUserAdmin(new UserDto("Super", "admin", fecha, "super@mail.com", "wer99"));
    }


    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUser(){
        try{
            return ResponseEntity.ok(userService.getAllUsers());
        }catch (Exception e){
            return new ResponseEntity("Error in getAllUsers controller: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserResponseDto> findUserById (@PathVariable String idUser){
        try{
            return new ResponseEntity<>(userService.findUserById(idUser),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity("the user " + idUser + " doesn't in the data base.", HttpStatus.NOT_FOUND);
        }
    }
    @RolesAllowed(ADMIN_ROLE)
    @PutMapping("/{idUser}")
    public ResponseEntity<Boolean> updateUser(@PathVariable String idUser,@RequestBody UserDto userDto){
        try{
            Boolean isUpdateUser = userService.updateUser(idUser, userDto);
            if(isUpdateUser){
                return new ResponseEntity("The user is update", HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (NoSuchElementException e){
            return new ResponseEntity("the user " + idUser + " doesn't in the data base." , HttpStatus.NOT_FOUND);
        }
    }
    @RolesAllowed(ADMIN_ROLE)
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserDto userDto){
        try{
            return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
        }catch (RuntimeException e){
            return new ResponseEntity("An error has occurred while retrieving users", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RolesAllowed(ADMIN_ROLE)
    @PostMapping("/createAdmin")
    public ResponseEntity<UserResponseDto> createUserAdmin(@RequestBody UserDto userDto){
        return new ResponseEntity<>(userService.createUserAdmin(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{idUser}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable  String idUser){
        return new ResponseEntity<>(userService.deleteUser(idUser),HttpStatus.OK);
    }

}