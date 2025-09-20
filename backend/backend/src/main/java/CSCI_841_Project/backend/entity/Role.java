package CSCI_841_Project.backend.entity;

import CSCI_841_Project.backend.enums.RolePermission;
import CSCI_841_Project.backend.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter

@Entity
@Table(name = "roles", uniqueConstraints=@UniqueConstraint(name="uk_role_name", columnNames="role_name"))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", unique = true, nullable = false, length = 30)
    private RoleType roleName;  // Example: "ADMIN", "USER", "MANAGER"

    @Column(name = "description")
    private String description;

    @ElementCollection(targetClass = RolePermission.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<RolePermission> permissions = EnumSet.noneOf(RolePermission.class);

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)  // Many roles per user
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    // CONSTRUCTOR
    protected Role() {} // JPA needs this

    public Role(RoleType roleName, String description, Set<RolePermission> permissions) {
        this.roleName = roleName;
        this.description = description;
        this.permissions = permissions;
    }


    // equals/hashCode by business key so Set<Role> de-dupes properly
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Role r)) return false;
        return roleName == r.roleName;
    }

    @Override
    public int hashCode(){ return Objects.hash(roleName); }


    @Override
    public String toString(){
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


    // GETTER AND SETTER





}
