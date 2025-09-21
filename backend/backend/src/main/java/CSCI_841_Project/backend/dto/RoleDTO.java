package CSCI_841_Project.backend.dto;

import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO {

    @NotNull
//    @JsonAlias({"role", "roleName"})   // ✅ accept either "role" or "roleName"
    private RoleType roleName;
    private String description;
    private Set<RolePermission> permissions;


    public RoleType getRole() { return roleName; }
    public void setRole(RoleType role) { this.roleName = role; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<RolePermission> permissions) {
        this.permissions = permissions;
    }
}
