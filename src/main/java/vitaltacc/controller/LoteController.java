package vitaltacc.controller;

import vitaltacc.model.Lote;
import vitaltacc.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lotes")
@CrossOrigin(origins = "*")
public class LoteController {

    @Autowired
    private LoteRepository loteRepository;

    // Este es el método que usaremos para "Reponer Stock"
    @PostMapping("/guardar")
    public ResponseEntity<?> guardarNuevoLote(@RequestBody Lote lote) {
        try {
            // Guardamos el nuevo lote (se vincula al producto por ID)
            Lote guardado = loteRepository.save(lote);
            return ResponseEntity.ok("Lote cargado correctamente. Stock actualizado.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cargar lote: " + e.getMessage());
        }
    }
}
