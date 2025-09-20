package CSCI_841_Project.backend.utility;

import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import CSCI_841_Project.backend.repository.RoleRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap-admin.email:admin@gmail.com}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:Admin123}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        // If any user already has ADMIN, skip
        if (userRepository.existsByRoles_RoleName(RoleType.ADMIN)) {
            log.info("[bootstrap] ADMIN already exists — skipping seed.");
            return;
        }

        // Ensure ADMIN role exists (create if missing)
        Role adminRole = roleRepository.findByRoleName(RoleType.ADMIN).orElseGet(() -> {
            Role r = new Role(
                    RoleType.ADMIN,
                    "System administrator",
                    EnumSet.of(
                            RolePermission.SYSTEM_ADMIN,
                            RolePermission.MANAGE_USERS,
                            RolePermission.READ,
                            RolePermission.CREATE,
                            RolePermission.UPDATE,
                            RolePermission.DELETE
                    )
            );
            r = roleRepository.save(r);
            log.info("[bootstrap] Created ADMIN role");
            return r;
        });

        // Create or reuse the admin user (by username/email)
        User admin = userRepository.findByUserName(adminUsername)
                .orElseGet(() -> userRepository.findByEmail(adminEmail).orElse(null));

        if (admin == null) {
            admin = new User();
            admin.setUserName(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));

            admin.setFirstName("System");
            admin.setLastName("Administrator");

            admin.setCurrency("USD");
            admin.setTimezone("UTC");
            admin.setPreferredLanguage("en");

            admin.setVerified(true);
            admin.setDeleted(false);
            admin.setLastLogin(LocalDateTime.now());
            admin.setDateCreated(LocalDateTime.now());
            admin.setDateUpdated(LocalDateTime.now());

            admin.setRoles(new LinkedHashSet<>());
            // If you have primaryRole column/field:
            try { admin.setPrimaryRole(adminRole); } catch (Exception ignored) {}

            log.info("[bootstrap] Created admin user '{}'", adminUsername);
        } else {
            log.info("[bootstrap] Reusing existing user '{}' for admin promotion", admin.getUserName());
        }

        // Ensure ADMIN role is assigned
        if (!admin.getRoles().contains(adminRole)) {
            admin.getRoles().add(adminRole);
        }

        userRepository.save(admin);
        log.info("[bootstrap] Admin '{}' is ready with ADMIN role", admin.getUserName());
    }
}