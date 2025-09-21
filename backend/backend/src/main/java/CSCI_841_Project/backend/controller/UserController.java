package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.UserDTO;
import CSCI_841_Project.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        System.out.println("Received registration request for: " + userDTO.getUserName()); // ✅ Log request
        UserDTO registeredUser = userService.register(userDTO);
        return ResponseEntity.ok(registeredUser);
    }

    // BUILD AN UPDATE USER REST API
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUpdate(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    // BUILD A GET USER BY ID REST API
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // BUILD A GET ALL USERS REST API
    @GetMapping
    public Page<UserDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(PageRequest.of(page, size));
    }


    // BUILD REST API THAT DELETE USER PERMANENTLY
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User has been deleted successfully.");
    }

    // BUILD A SOFT DELETE USER REST API
    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<String> removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
        return ResponseEntity.ok("User has been deleted successfully.");
    }


    /**
     * Restores a previously soft-deleted user account.
     */
    @PutMapping("/{userId}/restore")
    public ResponseEntity<String> restoreDeletedUser(@PathVariable Long userId) {
        userService.restoreDeletedUser(userId);
        return ResponseEntity.ok("User has been restored successfully.");
    }


    /**
     * ✅ Get the logged-in user's profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        UserDTO userDTO = userService.getUserProfile(authentication);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * ✅ Update the logged-in user's profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestBody UserDTO userDTO, Authentication authentication) {
        UserDTO updatedUser = userService.updateUserProfile(authentication, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * ✅ Upload a profile picture
     */
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file, Authentication authentication) {
        String fileUrl = userService.uploadProfilePicture(file, authentication);
        return ResponseEntity.ok().body(Map.of("url", fileUrl));
    }
}
