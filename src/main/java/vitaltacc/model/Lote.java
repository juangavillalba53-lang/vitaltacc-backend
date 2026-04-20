package vitaltacc.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "lote")
@Data
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroLote;
    private Integer cantidad;
    private LocalDate fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JsonIgnoreProperties("lotes")
    private Producto producto;
}