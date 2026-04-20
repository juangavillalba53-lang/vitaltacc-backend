package vitaltacc.controller;

import vitaltacc.model.*;
import vitaltacc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private LoteRepository loteRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/realizar")
    @Transactional // Esto asegura que si algo falla, no se reste el stock por la mitad
    public ResponseEntity<?> realizarVenta(@RequestBody Venta venta) {
        try {
            // 1. Guardar la cabecera de la venta
            venta.setFecha(LocalDate.now());
            Venta ventaGuardada = ventaRepository.save(venta);

            // 2. Procesar cada producto del carrito
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                int cantidadAVender = detalle.getCantidad();

                // --- LÓGICA FIFO (First In, First Out) ---
                // Buscamos lotes del producto que tengan stock, ordenados por fecha de
                // vencimiento
                List<Lote> lotesDisponibles = loteRepository.findByProductoOrderByFechaVencimientoAsc(producto);

                for (Lote lote : lotesDisponibles) {
                    if (cantidadAVender <= 0)
                        break;

                    int stockEnLote = lote.getCantidad();
                    if (stockEnLote > 0) {
                        int aRestar = Math.min(stockEnLote, cantidadAVender);
                        lote.setCantidad(stockEnLote - aRestar);
                        cantidadAVender -= aRestar;
                        loteRepository.save(lote); // Actualizamos el lote
                    }
                }

                if (cantidadAVender > 0) {
                    throw new RuntimeException("No hay stock suficiente para: " + producto.getNombre());
                }

                // 3. Guardar el detalle del renglón
                detalle.setVenta(ventaGuardada);
                detalle.setProducto(producto);
                detalle.setPrecioUnitario(producto.getPrecio());
                detalleVentaRepository.save(detalle);
            }

            return ResponseEntity.ok("Venta realizada con éxito y stock actualizado (FIFO)");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en la venta: " + e.getMessage());
        }
    }
}
