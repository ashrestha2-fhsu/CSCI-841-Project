package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.dto.UserDTO;
import CSCI_841_Project.backend.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    /**
     * Creates a new user account with validation checks.
     * @param userDTO User registration data.
     * @return Created UserDTO with user details.
     */
    UserDTO register(UserDTO userDTO);

    /**
     * Updates user profile information.
     * @param userId ID of the user.
     * @param userDTO Updated user data.
     * @return Updated UserDTO.
     */
    UserDTO updateUser(Long userId, UserDTO userDTO);



    /**
     * Retrieves user details by user ID.
     * @param userId ID of the user.
     * @return UserDTO containing user information.
     */
    UserDTO getUserById(Long userId);


    /**
     * Retrieves a paginated list of users.
     * @param pageable Pagination and sorting parameters.
     * @return Page<UserDTO> containing paginated user data.
     */
    Page<UserDTO> getAllUsers(Pageable pageable);


    /**
     * Marks a user account as deleted (soft delete).
     * @param userId ID of the user to delete.
     */
    void deleteUser(Long userId);

    /**
     * Restores a soft-deleted user account.
     * @param userId ID of the user to restore.
     */
    void restoreDeletedUser(Long userId);



    String authenticate(String username, String password);

    void assignRoleToUser(Long userId, RoleType roleName);

    void removeUser(Long userId);

    UserDTO getUserProfile(Authentication authentication);

    UserDTO updateUserProfile(Authentication authentication, UserDTO userDTO);


    String uploadProfilePicture(MultipartFile file, Authentication authentication);

    void removeRoleFromUser(Long userId, RoleType roleName);
}
