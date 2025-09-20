package CSCI_841_Project.backend.mapper;

import CSCI_841_Project.backend.dto.UserDTO;
import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RoleType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.repository.RoleRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    private RoleRepository roleRepository;

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
