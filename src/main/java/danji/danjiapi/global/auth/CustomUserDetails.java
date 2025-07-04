package danji.danjiapi.global.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructs a CustomUserDetails instance with the specified user ID, email, password, and authorities.
     *
     * @param userId the unique identifier of the user
     * @param email the user's email address, used as the username
     * @param password the user's password
     * @param authorities the collection of granted authorities or roles assigned to the user
     */
    public CustomUserDetails(Long userId, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Returns the user's email address, which serves as the username for authentication.
     *
     * @return the user's email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
 * Returns the user's password.
 *
 * @return the password associated with this user
 */
@Override public String getPassword() { return password; }
    /**
 * Returns the collection of authorities granted to the user.
 *
 * @return the user's granted authorities
 */
@Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    /**
 * Indicates whether the user's account has expired.
 *
 * @return always {@code true}, as accounts are never considered expired.
 */
@Override public boolean isAccountNonExpired() { return true; }
    /**
 * Indicates whether the user's account is locked.
 *
 * @return always {@code true}, indicating the account is never locked
 */
@Override public boolean isAccountNonLocked() { return true; }
    /**
 * Indicates whether the user's credentials are non-expired.
 *
 * @return always {@code true}, indicating credentials never expire.
 */
@Override public boolean isCredentialsNonExpired() { return true; }
    /**
 * Indicates whether the user account is enabled.
 *
 * @return always {@code true}, indicating the account is enabled.
 */
@Override public boolean isEnabled() { return true; }
}
