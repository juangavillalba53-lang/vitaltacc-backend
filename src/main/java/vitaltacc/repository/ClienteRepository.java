package vitaltacc.repository;

import vitaltacc.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Esto nos va a servir para buscar clientes por DNI antes de vender
    Optional<Cliente> findByDni(String dni);
}
