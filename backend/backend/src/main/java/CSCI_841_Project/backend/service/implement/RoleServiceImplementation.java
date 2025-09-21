package CSCI_841_Project.backend.service.implement;

import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.repository.RoleRepository;
import CSCI_841_Project.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImplementation implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role createRole(RoleType roleName, String description, Set<RolePermission> permissions) {
        if (roleName == null) throw new IllegalArgumentException("role is required");
        if (roleRepository.existsByRoleName(roleName)) {
            throw new NotFoundException.ConflictException("Role already exists: " + roleName);
        }
        Set<RolePermission> perms = (permissions == null || permissions.isEmpty())
                ? EnumSet.noneOf(RolePermission.class)
                : EnumSet.copyOf(permissions);
        return roleRepository.save(new Role(roleName, description, perms));
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByRoleName(RoleType roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
    }


    @Override
    public Role updateRole(RoleType roleName, String newDescription, Set<RolePermission> newPermissions) {
        Role role = findByRoleName(roleName);
        if (newDescription != null) role.setDescription(newDescription);
        if (newPermissions != null) {
            role.setPermissions(newPermissions.isEmpty()
                    ? EnumSet.noneOf(RolePermission.class)
                    : EnumSet.copyOf(newPermissions));
        }
        return roleRepository.save(role);
    }


    @Override
    public Role upsert(RoleType roleName, String description, Set<RolePermission> permissions) {
        if (roleName == null) throw new IllegalArgumentException("role is required");
        Set<RolePermission> perms = (permissions == null || permissions.isEmpty())
                ? EnumSet.noneOf(RolePermission.class)
                : EnumSet.copyOf(permissions);

        return roleRepository.findByRoleName(roleName)
                .map(r -> {
                    r.setDescription(description);
                    r.setPermissions(perms);
                    return roleRepository.save(r);
                })
                .orElseGet(() -> roleRepository.save(new Role(roleName, description, perms)));
    }



}
