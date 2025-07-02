package iccsoft_auth.services;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import iccsoft_auth.model.Employe;


public class EmployeDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String email;
    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public EmployeDetailsImpl(Long id, String email, String username, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    // You should implement a method to build EmployeServiceImpl from Employe
    // entity, for example:
    public static EmployeDetailsImpl build(Employe employe) {
        return new EmployeDetailsImpl(
                employe.getId(),
                employe.getEmail(),
                employe.getUsername(),
                employe.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(employe.getRole().toString().strip())));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

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
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EmployeDetailsImpl))
            return false;
        EmployeDetailsImpl that = (EmployeDetailsImpl) o;
        return id.equals(that.id);
    }
}
