package danji.danjiapi.domain.user.repository;

import danji.danjiapi.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
 * Retrieves a user by their email address.
 *
 * @param email the email address to search for
 * @return an {@code Optional} containing the user if found, or empty if no user exists with the given email
 */
Optional<User> findByEmail(String email);
}
