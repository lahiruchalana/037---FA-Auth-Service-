package com.cloudofgoods.auth.entity;

import com.cloudofgoods.auth.enumpackage.AppUserRoleEnum;
import com.cloudofgoods.auth.enumpackage.AuthenticationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "auth_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements SuperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "user_name")
    private String username;
    @Column(name = "password", length = 1000)
    private String password;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "account_non_expired")
    private boolean accountNonExpired;
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    private int failedAttempt;
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private AuthenticationType authType;


    private Date lockTime;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_user", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles;

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.accountNonExpired = user.isAccountNonExpired();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.roles = user.getRoles();
    }

    public User(String givenName, String name, String username, AppUserRoleEnum roleUser, AuthenticationType auth) {

       Role role = new Role();
       role.setId(4);
       role.setName("ROLE_user");
        this.enabled = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
        this.username = name;
        this.email = username;
        this.authType = (auth);
        //this.roles.add(role);

    }

    public void eraseCredentials(){
        this.password=null;
    }
}
