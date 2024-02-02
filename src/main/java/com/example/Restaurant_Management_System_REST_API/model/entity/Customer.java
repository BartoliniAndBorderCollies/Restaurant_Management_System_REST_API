package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;


@Entity
@Table(name = "application_users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDateTime creationTime;
    @OneToOne(mappedBy = "customer")
    private Reservation reservation;
    @Embedded
    private ContactDetails contactDetails;

    //Below are fields for security issues
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,}$", message =
            "Password must have at least 5 characters, one small letter, one big letter, and a special character")
    private String password;

//            (?=.*[0-9]) ensures there is at least one digit.
//            (?=.*[a-z]) ensures there is at least one lowercase letter.
//            (?=.*[A-Z]) ensures there is at least one uppercase letter.
//            (?=.*[@#$%^&+=]) ensures there is at least one special character.
//            (?=\\S+$) ensures there are no spaces.
//.           {5,} ensures there are at least 5 characters.
    private Boolean accountNonExpired;//to avoid hardcoding I established these fields
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    @NotBlank(message = "Email is missing!")
    @Email(regexp=".+@.+\\..+", message = "Email address must be as email: example@example.pl")
    private String emailAddress;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "customer_authorities", joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    @NotEmpty(message = "Must have at least one authority (role)!")
    private Set<Authority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
