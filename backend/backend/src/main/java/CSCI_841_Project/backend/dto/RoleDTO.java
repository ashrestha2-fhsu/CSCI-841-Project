package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.RoleType;

public class RoleDTO {
    private Long userId;
    private RoleType role;
    private String description;

    // ✅ Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
