package CSCI_841_Project.backend.service;

import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;

import java.util.Set;

public interface RoleService {

    Role createRole(RoleType roleName, String description, Set<RolePermission> permissions);

    Role updateRole(RoleType roleName, String newDescription, Set<RolePermission> newPermissions);

    Role findByRoleName(RoleType roleName);

    Role upsert(RoleType roleName, String description, Set<RolePermission> permissions);

}
