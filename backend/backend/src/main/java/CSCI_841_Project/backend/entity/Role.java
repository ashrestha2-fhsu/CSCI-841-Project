package CSCI_841_Project.backend.entity;

import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", unique = true, nullable = false, length = 30)
    private RoleType roleName;  // Example: "ADMIN", "USER", "MANAGER"

    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)  // ✅ Permissions stored separately
    @Enumerated(EnumType.STRING)
    private Set<RolePermission> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)  // Many roles per user
    private Set<User> users = new HashSet<>();


    @Override
    public String toString(){
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Long getRoleId() {
        return roleId;
    }


    // CONSTRUCTOR

    // GETTER AND SETTER
}
