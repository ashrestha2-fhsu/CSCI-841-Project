package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.RoleDTO;
import CSCI_841_Project.backend.dto.UserDTO;
import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.repository.RoleRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.security.JwtTokenProvider;
import CSCI_841_Project.backend.service.RoleService;
import CSCI_841_Project.backend.service.UserService;
import CSCI_841_Project.backend.utility.LoginRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    RoleService roleService;
    @Autowired
    RoleRepository roleRepository;


//    // BUILD A REGISTER USER REST API
@PostMapping("/register")
public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(dto));
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Accept username OR email
            User u = userRepository.findByUserName(loginRequest.getUserName())
                    .orElseGet(() -> userRepository.findByEmail(loginRequest.getUserName())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found")));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(u.getUserName(), loginRequest.getPassword())
            );

            String token = jwtTokenProvider.generateToken(u.getUserName());
            var roles = u.getRoles().stream().map(r -> r.getRoleName().name()).toList();
            var perms = u.getRoles().stream().flatMap(r -> r.getPermissions().stream())
                    .map(Enum::name)
                    .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", u.getUserId(),
                    "userName", u.getUserName(),
                    "roles", roles,
                    "permissions", perms
            ));
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        } catch (DisabledException | LockedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));
        if (user.isVerified()) return ResponseEntity.badRequest().body("Account already verified.");

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return ResponseEntity.ok("Account verified successfully!");
    }

    // -------- One-time bootstrap (public, closes after first ADMIN exists) --------

    private boolean adminExists() {
        return userRepository.existsByRoles_RoleName(RoleType.ADMIN);
    }

    /** Create or update a role (idempotent) while no ADMIN exists */
    @PostMapping("/setup/roles")
    @Transactional
    public ResponseEntity<?> setupUpsertRole(@RequestBody RoleDTO dto) {
        if (adminExists()) {
            return ResponseEntity.status(409).body(Map.of("error", "Setup closed", "message", "An ADMIN already exists"));
        }
        RoleType name = dto.getRole();
        Set<RolePermission> perms = (dto.getPermissions()==null || dto.getPermissions().isEmpty())
                ? EnumSet.noneOf(RolePermission.class)
                : EnumSet.copyOf(dto.getPermissions());

        Role role = roleRepository.findByRoleName(name)
                .map(r -> { r.setDescription(dto.getDescription()); r.setPermissions(perms); return roleRepository.save(r); })
                .orElseGet(() -> roleRepository.save(new Role(name, dto.getDescription(), perms)));

        return ResponseEntity.ok(Map.of(
                "role", role.getRoleName(),
                "description", role.getDescription(),
                "permissions", role.getPermissions()
        ));
    }

    /** Promote a user to ADMIN (only while no ADMIN exists) */
    @PostMapping("/setup/promote")
    @Transactional
    public ResponseEntity<?> setupPromoteFirstAdmin(@RequestParam Long userId) {
        if (adminExists()) {
            return ResponseEntity.status(409).body(Map.of("error", "Setup closed", "message", "An ADMIN already exists"));
        }
        User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Role admin = roleRepository.findByRoleName(RoleType.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(
                        RoleType.ADMIN, "System administrator",
                        EnumSet.of(
                                RolePermission.SYSTEM_ADMIN,
                                RolePermission.MANAGE_USERS,
                                RolePermission.READ,
                                RolePermission.CREATE,
                                RolePermission.UPDATE,
                                RolePermission.DELETE
                        ))));
        u.getRoles().add(admin);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{userId}/roles/{role}")
    @Transactional
    public ResponseEntity<Void> assign(@PathVariable Long userId, @PathVariable RoleType role) {
        userService.assignRoleToUser(userId, role);
        return ResponseEntity.noContent().build(); // 204
    }


}
