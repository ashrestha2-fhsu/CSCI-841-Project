package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public class RoleAssignDTO {

    @NotNull
    private Long userId;

    @NotNull
//    @JsonAlias({"role", "roleName"})   // ✅ accept either name
    private RoleType roleName;


    public RoleType getRole() {return roleName;}
    public void setRole(RoleType role) {
        this.roleName = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
