package vitaltacc.controller;

import vitaltacc.model.*;
import vitaltacc.repository.*;
import vitaltacc.service.EmailService; // IMPORTAMOS EL SERVICIO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    // INYECTAMOS EL SERVICIO DE EMAIL
    @Autowired
    private EmailService emailService;

    @PostMapping("/realizar")
    @Transactional
    public ResponseEntity<?> realizarVenta(@RequestBody Venta venta) {
        try {
            // 1. Lógica de Cliente: Buscar por DNI o crear uno nuevo
            if (venta.getCliente() != null && venta.getCliente().getDni() != null) {
                String dni = venta.getCliente().getDni();
                Optional<Cliente> clienteExistente = clienteRepository.findByDni(dni);

                if (clienteExistente.isPresent()) {
                    venta.setCliente(clienteExistente.get());
                } else {
                    Cliente nuevoCliente = venta.getCliente();
                    if (nuevoCliente.getNombre() == null) {
                        nuevoCliente.setNombre("Consumidor Final");
                    }
                    // IMPORTANTE: Aquí podrías setear un rol por defecto si es nuevo
                    nuevoCliente.setRol("CLIENTE");
                    Cliente clienteGuardado = clienteRepository.save(nuevoCliente);
                    venta.setCliente(clienteGuardado);
                }
            } else {
                venta.setCliente(null);
            }

            // 2. Preparar cabecera de la venta
            venta.setFecha(LocalDate.now());

            double acumuladorTotal = 0;
            for (DetalleVenta d : venta.getDetalles()) {
                Producto p = productoRepository.findById(d.getProducto().getId())
                        .orElseThrow(
                                () -> new RuntimeException("Producto no encontrado ID: " + d.getProducto().getId()));
                acumuladorTotal += (p.getPrecio() * d.getCantidad());
            }
            venta.setTotal(acumuladorTotal);

            Venta ventaGuardada = ventaRepository.save(venta);

            // 3. Procesar cada producto (Lógica FIFO)
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                int cantidadAVender = detalle.getCantidad();
                List<Lote> lotesDisponibles = loteRepository.findByProductoOrderByFechaVencimientoAsc(producto);

                for (Lote lote : lotesDisponibles) {
                    if (cantidadAVender <= 0)
                        break;
                    int stockEnLote = lote.getCantidad();
                    if (stockEnLote > 0) {
                        int aRestar = Math.min(stockEnLote, cantidadAVender);
                        lote.setCantidad(stockEnLote - aRestar);
                        cantidadAVender -= aRestar;
                        loteRepository.save(lote);
                    }
                }

                if (cantidadAVender > 0) {
                    throw new RuntimeException("No hay stock suficiente para: " + producto.getNombre());
                }

                detalle.setVenta(ventaGuardada);
                detalle.setProducto(producto);
                detalle.setPrecioUnitario(producto.getPrecio());
                detalleVentaRepository.save(detalle);
            }

            // --- ENVÍO DE EMAIL ---
            // Solo mandamos mail si el cliente tiene un correo cargado
            if (ventaGuardada.getCliente() != null && ventaGuardada.getCliente().getEmail() != null) {
                try {
                    emailService.enviarCorreoConfirmacion(
                            ventaGuardada.getCliente().getEmail(),
                            ventaGuardada.getCliente().getNombre(),
                            ventaGuardada.getCliente().getDni(), // Pasamos el DNI como referencia o ID
                            ventaGuardada.getTotal());
                } catch (Exception mailError) {
                    // Si falla el mail, no queremos que se cancele la venta (rollback),
                    // solo avisamos por consola.
                    System.err.println("Error al enviar el mail: " + mailError.getMessage());
                }
            }

            return ResponseEntity.ok("Venta registrada con éxito. Pedido #" + ventaGuardada.getId());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en la venta: " + e.getMessage());
        }
    }
}