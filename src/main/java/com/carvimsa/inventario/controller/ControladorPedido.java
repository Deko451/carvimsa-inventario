package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.DetallePedido;
import com.carvimsa.inventario.model.MovimientoInventario;
import com.carvimsa.inventario.model.Pedido;
import com.carvimsa.inventario.model.TipoMovimiento;
import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.DetallePedidoRepository;
import com.carvimsa.inventario.repository.PedidoRepository;
import com.carvimsa.inventario.repository.ProductoRepository;
import com.carvimsa.inventario.repository.UsuarioRepository;
import com.carvimsa.inventario.service.InventarioService;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Registrar Pedido de Despacho (CU-07). Incluye CU-03: todo pedido
 * genera automáticamente una SALIDA de inventario para el producto solicitado.
 */
@Controller
public class ControladorPedido {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    public ControladorPedido(PedidoRepository pedidoRepository,
                              DetallePedidoRepository detallePedidoRepository,
                              ProductoRepository productoRepository,
                              UsuarioRepository usuarioRepository,
                              InventarioService inventarioService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/pedidos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("productos", productoRepository.findByEstado(true));
        model.addAttribute("fechaHoy", LocalDate.now());
        return "pedidos/pedidos";
    }

    @PostMapping("/pedidos/guardar")
    public String guardar(@RequestParam String refCliente,
                           @RequestParam LocalDate fecha,
                           @RequestParam("producto.id") Integer idProducto,
                           @RequestParam Integer cantidad,
                           @RequestParam(required = false) String observaciones,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        var producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        try {
            // CU-07 incluye CU-03: registrar pedido siempre genera una salida
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProducto(producto);
            movimiento.setUsuario(usuario);
            movimiento.setCantidad(cantidad);
            movimiento.setFecha(fecha);
            movimiento.setDestino("Despacho a cliente");
            movimiento.setTipo(TipoMovimiento.SALIDA);
            inventarioService.registrarSalida(movimiento);

            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setMovimiento(movimiento);
            pedido.setRefCliente(refCliente);
            pedido.setFecha(fecha);
            pedido.setObservaciones(observaciones);
            pedidoRepository.save(pedido);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detallePedidoRepository.save(detalle);

            redirectAttributes.addFlashAttribute("mensajeExito", "Pedido registrado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }

        return "redirect:/pedidos/nuevo";
    }
}
