package vitaltacc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaltacc.model.Producto;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Si antes decía findByNombre, dejalo igual.
    // Pero si tenías algo con 'Stock', ahora debe ser findByStockMinimo
    Optional<Producto> findByNombre(String nombre);
}