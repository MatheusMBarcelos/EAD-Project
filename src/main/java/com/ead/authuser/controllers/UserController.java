package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserDTO;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specification.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpec spec,
                                                       @PageableDefault(sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserModel> userModelPage = userService.findAll(spec, pageable);

        if (!userModelPage.isEmpty()) {
            for (UserModel user : userModelPage.toList()) {
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }
        return ResponseEntity.ok().body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable UUID userId) {
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.ok().body(userModelOptional.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID userId) {
        log.debug("DELETE deleteUser userId received {}", userId);
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        userService.delete(userModelOptional.get());
        log.debug("DELETE deleteUser userId saved {}", userId);
        log.info("User deleted successfully userId {}", userId);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted success.");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId,
                                             @RequestBody @Validated(UserDTO.UserView.UserPut.class)
                                             @JsonView({UserDTO.UserView.UserPut.class}) UserDTO userDTO) {
        log.debug("PUT updateUser userDto received {}", userDTO.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        var userModel = userModelOptional.get();
        userModel.setFullName(userDTO.getFullName());
        userModel.setPhoneNumber(userDTO.getPhoneNumber());
        userModel.setCpf(userDTO.getCpf());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("POST updateUser userId saved {}", userModel.getUserId());
        log.info("User updated successfully userId {}", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable UUID userId,
                                                 @RequestBody @Validated(UserDTO.UserView.PasswordPut.class)
                                                 @JsonView({UserDTO.UserView.PasswordPut.class}) UserDTO userDTO) {
        log.debug("PUT updatePassword userDto received {} ", userDTO.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (!userModelOptional.get().getPassword().equals(userDTO.getOldPassword())) {
            log.warn("Mismatched old password userId {}", userDTO.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Mismatched old password!");
        } else {
            var userModel = userModelOptional.get();
            userModel.setPassword(userDTO.getPassword());
            userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

            userService.save(userModel);
            log.debug("PUT updatePassword userId saved {} ", userModel.getUserId());
            log.info("Password updated successfully userId {} ", userModel.getUserId());
            return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully.");
        }
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateIamge(@PathVariable UUID userId,
                                              @RequestBody @Validated(UserDTO.UserView.ImagePut.class)
                                              @JsonView({UserDTO.UserView.ImagePut.class}) UserDTO userDTO) {
        log.debug("PUT updateImage userDto received {} ", userDTO.toString());
        Optional<UserModel> userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        var userModel = userModelOptional.get();
        userModel.setImageUrl(userDTO.getImageUrl());
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));

        userService.save(userModel);
        log.debug("PUT updateImage userId saved {} ", userModel.getUserId());
        log.info("Image updated successfully userId {} ", userModel.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }
}
