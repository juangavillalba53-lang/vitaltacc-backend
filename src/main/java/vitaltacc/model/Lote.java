package vitaltacc.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "lote")
@Data
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_lote")
    private String numeroLote;

    private Integer cantidad;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @JsonBackReference // Esto evita errores de bucle infinito al convertir a JSON
    private Producto producto;
}