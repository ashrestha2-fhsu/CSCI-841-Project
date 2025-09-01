package CSCI_841_Project.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDTO {

    private Long userId;
    private String userName;
    private String email;
    private String password; // Should be handled securely, avoid exposing in responses.
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String currency = "USD";
    private String timezone;
    private String profilePicture;
    private String notificationPreferences;
    private String preferredLanguage;
    private boolean isDeleted;
    private boolean isVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;




    // CONSTRUCTOR
    // ✅ Overloaded Constructor (Matches the 4 parameters)
    public UserDTO(Long userId, String userName, String email, String currency) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.currency = currency;
    }

    // GETTER AND SETTER
}
