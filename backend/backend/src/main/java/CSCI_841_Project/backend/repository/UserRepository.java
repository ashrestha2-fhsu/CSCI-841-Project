package CSCI_841_Project.backend.repository;

import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find user by username */
    Optional<User> findByUserName(String userName);

    Optional<User> findByUserIdAndIsDeletedFalse(Long userId); // ✅ Exclude deleted users

    Page<User> findByIsDeletedFalse(Pageable pageable); // ✅ Fetch only non-deleted users

    Optional<User> findByUserNameOrEmail(String username, String email);

    Optional<User> findByVerificationToken(String verificationToken); // ✅ Add this method

    boolean existsByRoles_RoleName(RoleType roleName);

    /** Check if a username already exists */
    boolean existsByUserName(String userName);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);



    // ✅ Load roles and permissions in one query so authorities aren't empty
    @Query("""
           select distinct u
           from User u
           left join fetch u.roles r
           left join fetch r.permissions
           where u.userName = :username
           """)
    Optional<User> findByUserNameWithRoles(@Param("username") String username);

}
