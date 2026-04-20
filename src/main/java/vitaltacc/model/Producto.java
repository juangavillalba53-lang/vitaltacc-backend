package vitaltacc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "productos")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stockMinimo;
    private String categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("producto")
    private List<Lote> lotes;
}