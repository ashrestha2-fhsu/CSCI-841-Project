package CSCI_841_Project.backend.enums;

public enum RolePermission {
    READ,
    CREATE,
    WRITE,
    UPDATE,
    DELETE,
    MANAGE_USERS,  // Managers & Admins can manage users
    SYSTEM_ADMIN,   // Full control over everything
    ROLE_ASSIGN
}

