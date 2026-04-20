package vitaltacc.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios") // El nombre en tu captura es 'usuarios' (en plural)
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "email", unique = true)
    private String email; // Usaremos este para el login

    @Column(name = "contrasena")
    private String contrasena; // Tal cual está en tu SQL

    private String rol; // admin o empleado
}