package iccsoft_auth.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iccsoft_auth.model.ERole;
import iccsoft_auth.model.Employe;


@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    Employe findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    Optional<Employe> findByUsername(String username);

    Optional<Employe> findByRole(ERole role);

    List<Employe> findAllByRole(ERole role);

    // Add other query methods as needed
    
}
