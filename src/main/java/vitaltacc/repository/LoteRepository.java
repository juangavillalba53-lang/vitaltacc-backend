package vitaltacc.repository;

import vitaltacc.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    // Para las alertas de tu anteproyecto
    List<Lote> findByFechaVencimientoBefore(LocalDate fecha);
}