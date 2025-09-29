package com.fourstory.fourstory_api.model.tglobal;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "TACCOUNT")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dwUserID")
    private Integer id;

    @Column(name = "szUserID", length = 50, nullable = false, unique = true)
    private String userName;

    @Column(length = 254, nullable = false, unique = true)
    private String email;

    @Column(length = 254, nullable = false)
    private String registrationEmail;

    @Column(name = "szPasswd", length = 128, nullable = false)
    private String password;

    @Column(name = "bEmailVerified", nullable = false)
    private Boolean emailVerified;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "bCheck")
    private Byte checkFlag;

    @Column(name = "dFirstLogin")
    private LocalDateTime firstLogin;

    @Column(name = "dLastLogin")
    private LocalDateTime lastLogin;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
