package vitaltacc.controller;

import vitaltacc.model.Lote;
import vitaltacc.model.Producto;
import vitaltacc.repository.LoteRepository;
import vitaltacc.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public Producto guardarProductoConLote(@RequestBody Map<String, Object> datos) {
        String nombre = (String) datos.get("nombre");
        String categoria = (String) datos.get("categoria");
        String descripcion = (String) datos.get("descripcion"); // Nuevo: captura la descripción
        Double precio = Double.parseDouble(datos.get("precio").toString());
        Integer cantidad = Integer.parseInt(datos.get("stock").toString());
        String fechaVencRaw = (String) datos.get("fechaVencimiento");
        String numLote = (String) datos.get("lote");

        // 1. Buscamos si existe o creamos nuevo
        Producto producto = productoRepository.findByNombre(nombre)
                .orElse(new Producto());

        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setDescripcion(descripcion); // Nuevo: guarda la descripción
        producto.setPrecio(precio);

        // Actualizamos stock total
        int stockActual = (producto.getStock() != null) ? producto.getStock() : 0;
        producto.setStock(stockActual + cantidad);

        Producto productoGuardado = productoRepository.save(producto);

        // 2. Creamos el Lote vinculado
        Lote nuevoLote = new Lote();
        nuevoLote.setProducto(productoGuardado);
        nuevoLote.setCantidad(cantidad);
        nuevoLote.setNumeroLote(numLote);
        nuevoLote.setFechaVencimiento(LocalDate.parse(fechaVencRaw));

        loteRepository.save(nuevoLote);

        return productoGuardado;
    }

    // NUEVO: Método para aplicar descuento manual (%)
    @PatchMapping("/{id}/descuento")
    public Producto aplicarDescuento(@PathVariable Long id, @RequestBody Map<String, Double> datos) {
        Double porcentaje = datos.get("porcentaje");
        Producto p = productoRepository.findById(id).orElseThrow();

        // Calculamos el nuevo precio bajando el X% que vos ingreses
        Double nuevoPrecio = p.getPrecio() * (1 - (porcentaje / 100));

        // Redondeamos a 2 decimales para que no quede un número largo
        p.setPrecio(Math.round(nuevoPrecio * 100.0) / 100.0);

        return productoRepository.save(p);
    }

    @DeleteMapping("/{id}")
    public void borrarProducto(@PathVariable Long id) {
        // Al borrar el producto, se borran sus lotes por la cascada (cascade =
        // CascadeType.ALL)
        productoRepository.deleteById(id);
    }
}