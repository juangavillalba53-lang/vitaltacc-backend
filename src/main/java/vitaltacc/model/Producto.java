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
    private Integer stock;
    private String categoria;

    // Relación: Un producto puede tener muchos lotes
    // 'mappedBy' debe coincidir con el nombre del atributo 'producto' en Lote.java
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("producto") // Evita bucles infinitos al convertir a JSON
    private List<Lote> lotes;
}