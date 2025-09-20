package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.dto.UserDTO;
import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RoleType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.UserMapper;
import CSCI_841_Project.backend.repository.RoleRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.security.JwtTokenProvider;
import CSCI_841_Project.backend.service.UserService;
import CSCI_841_Project.backend.utility.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private FileStorageService fileStorageService;


    // METHOD TO CREATE / REGISTER USER    ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    @Transactional
    public UserDTO register(UserDTO dto) {
        System.out.println("Registering user: " + dto.getUserName());

        // ADMIN calls this method to create a user and assign roles in one shot
        if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
            throw new IllegalArgumentException("At least one role must be provided.");
        }

        userRepository.findByUserName(dto.getUserName().trim())
                .ifPresent(u -> { throw new NotFoundException.ConflictException("Username already taken"); });
        userRepository.findByEmail(dto.getEmail().trim())
                .ifPresent(u -> { throw new NotFoundException.ConflictException("Email already registered"); });

        User user = new User();
        user.setUserName(dto.getUserName().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());

        user.setCurrency(dto.getCurrency() != null ? dto.getCurrency().toUpperCase() : "USD");
        user.setTimezone(dto.getTimezone() != null ? dto.getTimezone() : "UTC");
        user.setPreferredLanguage(dto.getPreferredLanguage() != null ? dto.getPreferredLanguage() : "en");

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setDeleted(false);
        user.setVerified(true);
        user.setLastLogin(LocalDateTime.now());
        user.setDateCreated(LocalDateTime.now());
        user.setDateUpdated(LocalDateTime.now());

        // Resolve roles (all must exist)
        Set<Role> roles = dto.getRoles().stream()
                .map(rt -> roleRepository.findByRoleName(rt)
                        .orElseThrow(() -> new NotFoundException("Role not found: " + rt)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        user.setRoles(roles);

        user = userRepository.save(user);
        return userMapper.toDTO(user);
    }


    // METHOD TO UPDATE USER DATA  ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        // First verify that the user exists and login
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User Not Found" + userId));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setCurrency(userDTO.getCurrency());
        user.setTimezone(userDTO.getTimezone());
        user.setPreferredLanguage(userDTO.getPreferredLanguage());
        user.setProfilePicture(userDTO.getProfilePicture());
        user.setNotificationPreferences(userDTO.getNotificationPreferences());

        // Save update
        userRepository.save(user);
        return userMapper.toDTO(user);
    }

    // METHOD TO GET USER BY ID +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new NotFoundException("User not found with ID: " + userId));
        return userMapper.toDTO(user);
    }

    // METHOD TO GET ALL USERS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Retrieves a paginated list of users.
     * @param pageable Pagination and sorting parameters.
     * @return Paginated list of UserDTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findByIsDeletedFalse(pageable)
                .map(userMapper::toDTO);
    }

    // METHOD TO DELETE USER +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // PERMANENTLY DELETE USER FROM DATABASE
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User Not Found"));
        userRepository.deleteById(userId);

    }

    // METHOD TO DELETE USER BUT STILL SAVE IN DATABASE USING IS_DELETE  +++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void removeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        // Soft Delete
        user.setDeleted(true); // ✅ Mark as deleted
        userRepository.save(user);
    }

    // METHOD TO RESTORE DELETED/REMOVED USER +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void restoreDeletedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setDeleted(false);
        userRepository.save(user);
    }


    @Override
    public String authenticate(String username, String password) {
        // 1) Load canonical user (username OR email)
        User user = userRepository.findByUserName(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new NotFoundException("User Not Found: " + username)));
        if (user.isDeleted()) {
            throw new RuntimeException("Your account has been deleted. Contact support for help.");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), password)
        );
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        String principal = auth.getName(); // equals user.getUserName()
        return jwtTokenProvider.generateToken(principal);
    }

    // ASSIGN ROLE TO USER  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    @Transactional
    public void assignRoleToUser(Long userId, RoleType roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(()-> new NotFoundException("Role Not Found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }

        userRepository.save(user);

    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, RoleType roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        if (user.getRoles().remove(role)) {
            userRepository.save(user);
        }
    }

    /**
     * ✅ Get user profile by authenticated user (Ensures only the logged-in user accesses their profile)
     */
    @Override
    public UserDTO getUserProfile(Authentication authentication) {
        String username = authentication.getName(); // Get username from JWT token

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        UserDTO userDTO = userMapper.toDTO(user); // Convert user entity to DTO
        // ✅ Ensure profile picture is never null
        if (userDTO.getProfilePicture() == null || userDTO.getProfilePicture().isEmpty()) {
            userDTO.setProfilePicture("http://localhost:8080/uploads/default-profile.png");
        }
        return userDTO;
    }


    /**
     * ✅ Update user profile (Only editable fields can be changed)
     */
    public UserDTO updateUserProfile(Authentication authentication, UserDTO userDTO) {
        String username = authentication.getName(); // Get logged-in username

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        // ✅ Update only provided fields
        Optional.ofNullable(userDTO.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userDTO.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(userDTO.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(userDTO.getAddress()).ifPresent(user::setAddress);
        Optional.ofNullable(userDTO.getPreferredLanguage()).ifPresent(user::setPreferredLanguage);
        Optional.ofNullable(userDTO.getTimezone()).ifPresent(user::setTimezone);
        Optional.ofNullable(userDTO.getCurrency()).ifPresent(user::setCurrency);

        userRepository.save(user);
        return userMapper.toDTO(user); // Return updated user data
    }

    /**
     * ✅ Upload and update user's profile picture
     */
    @Override
    public String uploadProfilePicture(MultipartFile file, Authentication authentication) {
        String username = authentication.getName(); // Get logged-in user's username

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        try {
            // ✅ Store image and get file URL
            String fileUrl = "http://localhost:8080/uploads/" + fileStorageService.storeFile(file);
            // ✅ Update user's profile picture URL
            user.setProfilePicture(fileUrl);
            userRepository.save(user);

            return fileUrl; // ✅ Return the file URL for frontend display
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }


//    public void sendVerificationEmail(User user) {
//        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + user.getVerificationToken();
//        String emailBody = "Click the link to verify your account: " + verificationLink;
//        try {
//            emailService.sendEmail(user.getEmail(), "Verify Your Account", emailBody); // ✅ Send email
//            System.out.println("✅ Verification email sent to: " + user.getEmail());
//        } catch (Exception e) {
//            System.err.println("🚨 Error in sendVerificationEmail: " + e.getMessage()); // 🚨 Log email error
//        }
//    }


}
