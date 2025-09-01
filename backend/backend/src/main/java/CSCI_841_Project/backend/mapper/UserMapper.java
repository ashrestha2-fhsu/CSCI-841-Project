package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.UserDTO;
import CSCI_841_Project.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert User entity to UserDTO.
     * @param user The User entity to be converted.
     * @return UserDTO representing the User entity.
     */
    public UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setCurrency(user.getCurrency());
        dto.setTimezone(user.getTimezone());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setNotificationPreferences(user.getNotificationPreferences());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setDeleted(user.isDeleted());
        dto.setVerified(user.isVerified());
        dto.setDateCreated(user.getDateCreated());
        dto.setDateUpdated(user.getDateUpdated());
        return dto;
    }

    /**
     * Convert UserDTO to User entity.
     * @param userDTO The UserDTO to be converted.
     * @return User entity representing the UserDTO.
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // ⚠️ Ensure password is hashed before saving.
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setCurrency(userDTO.getCurrency());
        user.setTimezone(userDTO.getTimezone());
        user.setProfilePicture(userDTO.getProfilePicture());
        user.setNotificationPreferences(userDTO.getNotificationPreferences());
        user.setPreferredLanguage(userDTO.getPreferredLanguage());
        user.setDeleted(userDTO.isDeleted());
        user.setVerified(userDTO.isVerified());
        user.setDateCreated(userDTO.getDateCreated());
        user.setDateUpdated(userDTO.getDateUpdated());
        return user;
    }
}
