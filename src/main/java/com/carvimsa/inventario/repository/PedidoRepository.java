package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.EstadoPedido;
import com.carvimsa.inventario.model.Pedido;
import com.carvimsa.inventario.model.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByUsuario(Usuario usuario);
}
