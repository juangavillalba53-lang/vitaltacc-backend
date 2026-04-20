package vitaltacc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vitaltacc.repository.UsuarioRepository;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        // Normalizamos el email a minúsculas para que no haya errores de tipeo
        String emailIngresado = datos.get("email") != null ? datos.get("email").toLowerCase() : "";
        String claveIngresada = datos.get("contrasena");

        return usuarioRepository.findByEmail(emailIngresado)
                .filter(u -> u.getContrasena().equals(claveIngresada))
                .map(u -> ResponseEntity.ok(Map.of(
                        // Normalizamos el rol a mayúsculas para facilitar la lógica en el Frontend
                        "rol", u.getRol().toUpperCase(),
                        "nombre", u.getNombre(),
                        "email", u.getEmail())))
                .orElse(ResponseEntity.status(401).build());
    }
}