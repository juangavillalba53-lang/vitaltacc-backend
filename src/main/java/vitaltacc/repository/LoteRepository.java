package vitaltacc.repository;

import vitaltacc.model.Lote;
import vitaltacc.model.Producto; // Importante para que reconozca el objeto Producto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    // 1. Para las alertas de vencimiento (tu anteproyecto)
    List<Lote> findByFechaVencimientoBefore(LocalDate fecha);

    // 2. Para la venta inteligente (FIFO): busca lotes de un producto y los ordena
    // por fecha
    List<Lote> findByProductoOrderByFechaVencimientoAsc(Producto producto);
}