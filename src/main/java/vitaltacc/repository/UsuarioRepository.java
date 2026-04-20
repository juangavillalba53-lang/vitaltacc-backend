package vitaltacc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaltacc.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscamos por el email que es el campo único
    Optional<Usuario> findByEmail(String email);
}