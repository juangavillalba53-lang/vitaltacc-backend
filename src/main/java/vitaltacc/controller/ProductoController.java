package vitaltacc.controller;

import vitaltacc.model.Lote;
import vitaltacc.model.Producto;
import vitaltacc.repository.LoteRepository;
import vitaltacc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @PostMapping("/con-lote")
    public ResponseEntity<?> guardarProductoConLote(@RequestBody Map<String, Object> datos) {
        try {
            // 1. Extraemos los datos del JSON
            String nombre = (String) datos.get("nombre");
            String categoria = (String) datos.get("categoria");
            String descripcion = (String) datos.get("descripcion");
            Double precio = Double.parseDouble(datos.get("precio").toString());

            // Sincronizado con admin.html: usamos 'cantidad' y 'numeroLote'
            Integer cantidad = Integer.parseInt(datos.get("cantidad").toString());
            String numLote = (String) datos.get("numeroLote");
            String fechaVencRaw = (String) datos.get("fechaVencimiento");

            // 2. Buscamos si existe el producto por nombre o creamos uno nuevo
            Producto producto = productoRepository.findByNombre(nombre)
                    .orElse(new Producto());

            producto.setNombre(nombre);
            producto.setCategoria(categoria);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);

            // Verificamos el stock mínimo (por defecto 5 si es nuevo)
            if (producto.getStockMinimo() == null) {
                producto.setStockMinimo(5);
            }

            Producto productoGuardado = productoRepository.save(producto);

            // 3. Creamos y vinculamos el Lote
            Lote nuevoLote = new Lote();
            nuevoLote.setProducto(productoGuardado);
            nuevoLote.setCantidad(cantidad);
            nuevoLote.setNumeroLote(numLote);
            nuevoLote.setFechaVencimiento(LocalDate.parse(fechaVencRaw));

            loteRepository.save(nuevoLote);

            return ResponseEntity.ok(productoGuardado);

        } catch (Exception e) {
            // Esto nos avisará en el navegador si algo sale mal internamente
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/descuento")
    public ResponseEntity<?> aplicarDescuento(@PathVariable Long id, @RequestBody Map<String, Double> datos) {
        try {
            Double porcentaje = datos.get("porcentaje");
            Producto p = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Double nuevoPrecio = p.getPrecio() * (1 - (porcentaje / 100));
            p.setPrecio(Math.round(nuevoPrecio * 100.0) / 100.0);

            Producto actualizado = productoRepository.save(p);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al aplicar descuento");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> borrarProducto(@PathVariable Long id) {
        try {
            productoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar producto");
        }
    }
}