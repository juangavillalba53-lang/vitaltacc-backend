package vitaltacc.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data; // Si usas Lombok, esto crea los setters solos

@Entity
@Table(name = "lote")
@Data // Esta etiqueta de Lombok genera automáticamente el setNumeroLote
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;

    // ASEGURATE QUE ESTA VARIABLE SE LLAME ASÍ:
    private String numeroLote;

    private LocalDate fechaVencimiento;

    // Si NO usas @Data de Lombok, agregá esto a mano:
    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }
}