package CSCI_841_Project.backend.security;

import CSCI_841_Project.backend.entity.Role;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUserNameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));

        Set<GrantedAuthority> auths = new HashSet<>();
        if (u.getRoles() != null) {
            for (Role r : u.getRoles()) {
                // Works for enum RoleType; yields ROLE_ADMIN for ADMIN
                String roleKey = String.valueOf(r.getRoleName()).toUpperCase();
                auths.add(new SimpleGrantedAuthority("ROLE_" + roleKey));
                if (r.getPermissions() != null) {
                    r.getPermissions().forEach(p ->
                            auths.add(new SimpleGrantedAuthority(String.valueOf(p))));
                }
            }
        }

        return new org.springframework.security.core.userdetails.User(
                u.getUserName(), u.getPassword(), auths
        );
    }


}