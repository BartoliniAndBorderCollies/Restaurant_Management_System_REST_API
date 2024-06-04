package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
import java.util.Objects;
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
    @JsonBackReference(value="customer-reservation")
    private Reservation reservation;
    @Embedded
    private ContactDetails contactDetails;

    //Below are fields for security issues
    private String password; //validation for this field is provided in CustomerDTORequest
    private Boolean accountNonExpired;//to avoid hardcoding I established these fields
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    @NotBlank(message = "Please provide an email address!")
    @Email(regexp=".+@.+\\..+", message = "Please enter a valid email address in the format: yourname@example.com")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(password, customer.password) && Objects.equals(emailAddress, customer.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, emailAddress);
    }
}
