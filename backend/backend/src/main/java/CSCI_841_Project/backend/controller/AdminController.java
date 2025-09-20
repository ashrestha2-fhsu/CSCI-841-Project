package CSCI_841_Project.backend.controller;

import CSCI_841_Project.backend.dto.RoleAssignDTO;
import CSCI_841_Project.backend.dto.RoleDTO;
import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.repository.RoleRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.RoleService;
import CSCI_841_Project.backend.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // ✅ Only Admins can access
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    @PostMapping("/roles")
    public ResponseEntity<RoleDTO> upsertRole(@Valid @RequestBody RoleDTO dto) {
        Role role = roleService.upsert(dto.getRole(), dto.getDescription(), dto.getPermissions());
        RoleDTO out = new RoleDTO();
        out.setRole(role.getRoleName());
        out.setDescription(role.getDescription());
        out.setPermissions(role.getPermissions());
        return ResponseEntity.ok(out);
    }



    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> listRoles() {
        List<RoleDTO> result = roleRepository.findAll().stream().map(r -> {
            RoleDTO d = new RoleDTO();
            d.setRole(r.getRoleName());
            d.setDescription(r.getDescription());
            d.setPermissions(r.getPermissions());
            return d;
        }).toList();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable RoleType role) {
        roleRepository.findByRoleName(role).ifPresent(roleRepository::delete);
        return ResponseEntity.noContent().build();
    }

    // ---------- User <-> Role assignment (single pattern) ----------

    // ONE canonical assign endpoint (idempotent)
    @PutMapping("/users/{userId}/roles/{roleId}")
    @Transactional
    public ResponseEntity<Void> assignRole(@PathVariable Long userId, @PathVariable RoleType role) {
        userService.assignRoleToUser(userId, role); // centralize logic
        return ResponseEntity.noContent().build();
    }

    // Remove role from user (idempotent)
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    @Transactional
    public ResponseEntity<Void> removeRole(@PathVariable Long userId, @PathVariable RoleType role) {
        userService.removeRoleFromUser(userId, role);
        return ResponseEntity.noContent().build();
    }

    // List roles on a user
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<RoleType>> listUserRoles(@PathVariable Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return ResponseEntity.ok(u.getRoles().stream().map(Role::getRoleName).toList());
    }


    // Create OR update a role (idempotent upsert)
//    @PostMapping("/roles")
//    public ResponseEntity<RoleDTO> upsertRole(@Valid @RequestBody RoleDTO dto) {
//        RoleType name = dto.getRole();
//        Set<RolePermission> perms = (dto.getPermissions()==null || dto.getPermissions().isEmpty())
//                ? EnumSet.noneOf(RolePermission.class)
//                : EnumSet.copyOf(dto.getPermissions());
//
//        Role role = roleRepository.findByRoleName(name)
//                .map(r -> { r.setDescription(dto.getDescription()); r.setPermissions(perms); return roleRepository.save(r); })
//                .orElseGet(() -> roleRepository.save(new Role(name, dto.getDescription(), perms)));
//
//        RoleDTO out = new RoleDTO();
//        out.setRole(role.getRoleName());
//        out.setDescription(role.getDescription());
//        out.setPermissions(role.getPermissions());
//        return ResponseEntity.ok(out); // 200 for upsert
//    }



    }
