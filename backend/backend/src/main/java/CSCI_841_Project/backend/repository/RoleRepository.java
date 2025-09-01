package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find Role by name method
    Optional<Role> findByRoleName(RoleType roleName);
}
