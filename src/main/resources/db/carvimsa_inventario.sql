-- =====================================================
-- CARVIMSA - Sistema de Gestion de Inventarios
-- Script DDL + datos de prueba (CARVIMSA_PROYECTO.md secc. 5
-- e INSTRUCCIONES_CLAUDE_CODE.md Paso 3.2 / Paso 7)
-- Ejecutar en MySQL 8.0 ANTES de arrancar la aplicacion.
-- spring.jpa.hibernate.ddl-auto=validate -> Hibernate NO crea el esquema.
-- =====================================================

CREATE DATABASE IF NOT EXISTS carvimsa_inventario
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE carvimsa_inventario;

-- Tablas maestras
CREATE TABLE rol (
  id_rol       INT          NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(50)  NOT NULL,
  descripcion  VARCHAR(150)     NULL,
  CONSTRAINT pk_rol PRIMARY KEY (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuario (
  id_usuario   INT          NOT NULL AUTO_INCREMENT,
  id_rol       INT          NOT NULL,
  nombre       VARCHAR(100) NOT NULL,
  apellido     VARCHAR(100) NOT NULL,
  username     VARCHAR(50)  NOT NULL,
  password     VARCHAR(255) NOT NULL,
  estado       TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_usuario  PRIMARY KEY (id_usuario),
  CONSTRAINT uq_username UNIQUE (username),
  CONSTRAINT fk_usu_rol  FOREIGN KEY (id_rol)
    REFERENCES rol(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE proveedor (
  id_proveedor INT          NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(150) NOT NULL,
  ruc          VARCHAR(11)  NOT NULL,
  contacto     VARCHAR(100)     NULL,
  direccion    VARCHAR(200)     NULL,
  estado       TINYINT(1)   NOT NULL DEFAULT 1,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_proveedor PRIMARY KEY (id_proveedor),
  CONSTRAINT uq_ruc       UNIQUE (ruc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE producto (
  id_producto   INT           NOT NULL AUTO_INCREMENT,
  nombre        VARCHAR(150)  NOT NULL,
  codigo        VARCHAR(30)   NOT NULL,
  tipo          ENUM('MP','PP','PT') NOT NULL,
  unidad_medida VARCHAR(20)   NOT NULL,
  stock_actual  INT           NOT NULL DEFAULT 0,
  stock_minimo  INT           NOT NULL DEFAULT 0,
  estado        TINYINT(1)    NOT NULL DEFAULT 1,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_producto  PRIMARY KEY (id_producto),
  CONSTRAINT uq_codigo    UNIQUE (codigo),
  CONSTRAINT ck_stock_act CHECK (stock_actual >= 0),
  CONSTRAINT ck_stock_min CHECK (stock_minimo >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tablas transaccionales
CREATE TABLE movimiento_inventario (
  id_movimiento INT           NOT NULL AUTO_INCREMENT,
  id_producto   INT           NOT NULL,
  id_usuario    INT           NOT NULL,
  id_proveedor  INT               NULL,
  tipo          ENUM('ENTRADA','SALIDA') NOT NULL,
  cantidad      INT           NOT NULL,
  fecha         DATE          NOT NULL,
  destino       VARCHAR(100)      NULL,
  observaciones TEXT              NULL,
  created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_movimiento  PRIMARY KEY (id_movimiento),
  CONSTRAINT ck_cantidad    CHECK (cantidad > 0),
  CONSTRAINT fk_mov_prod    FOREIGN KEY (id_producto)
    REFERENCES producto(id_producto) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_mov_usu     FOREIGN KEY (id_usuario)
    REFERENCES usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_mov_prov    FOREIGN KEY (id_proveedor)
    REFERENCES proveedor(id_proveedor) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pedido (
  id_pedido         INT          NOT NULL AUTO_INCREMENT,
  id_usuario        INT          NOT NULL,
  id_movimiento     INT              NULL,
  ref_cliente       VARCHAR(100) NOT NULL,
  fecha             DATE         NOT NULL,
  estado            ENUM('PENDIENTE','DESPACHADO') NOT NULL DEFAULT 'PENDIENTE',
  observaciones     TEXT             NULL,
  created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_pedido   PRIMARY KEY (id_pedido),
  CONSTRAINT fk_ped_usu  FOREIGN KEY (id_usuario)
    REFERENCES usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_ped_mov  FOREIGN KEY (id_movimiento)
    REFERENCES movimiento_inventario(id_movimiento) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE detalle_pedido (
  id_detalle  INT NOT NULL AUTO_INCREMENT,
  id_pedido   INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad    INT NOT NULL,
  CONSTRAINT pk_detalle  PRIMARY KEY (id_detalle),
  CONSTRAINT ck_cant_det CHECK (cantidad > 0),
  CONSTRAINT fk_det_ped  FOREIGN KEY (id_pedido)
    REFERENCES pedido(id_pedido) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_det_prod FOREIGN KEY (id_producto)
    REFERENCES producto(id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE alerta (
  id_alerta        INT      NOT NULL AUTO_INCREMENT,
  id_producto      INT      NOT NULL,
  stock_al_momento INT      NOT NULL,
  stock_minimo     INT      NOT NULL,
  fecha_activacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado           ENUM('ACTIVA','RESUELTA') NOT NULL DEFAULT 'ACTIVA',
  CONSTRAINT pk_alerta   PRIMARY KEY (id_alerta),
  CONSTRAINT fk_ale_prod FOREIGN KEY (id_producto)
    REFERENCES producto(id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE reporte (
  id_reporte       INT         NOT NULL AUTO_INCREMENT,
  id_usuario       INT         NOT NULL,
  tipo             ENUM('MOVIMIENTOS','STOCK','ROTACION') NOT NULL,
  fecha_inicio     DATE        NOT NULL,
  fecha_fin        DATE        NOT NULL,
  fecha_generacion DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado           ENUM('GENERADO','ERROR') NOT NULL DEFAULT 'GENERADO',
  CONSTRAINT pk_reporte PRIMARY KEY (id_reporte),
  CONSTRAINT fk_rep_usu FOREIGN KEY (id_usuario)
    REFERENCES usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indices de rendimiento
CREATE INDEX idx_mov_producto  ON movimiento_inventario (id_producto);
CREATE INDEX idx_mov_fecha     ON movimiento_inventario (fecha);
CREATE INDEX idx_alerta_prod   ON alerta (id_producto, estado);
CREATE INDEX idx_pedido_estado ON pedido (estado);

-- =====================================================
-- Datos iniciales
-- =====================================================

-- Roles
INSERT INTO rol (nombre, descripcion) VALUES
  ('Administrador', 'Gestión total del sistema'),
  ('Jefe',          'Supervisión, reportes y alertas'),
  ('Operario',      'Registro de movimientos y pedidos');

-- Usuarios de prueba (password para los 3: admin123 -> hash BCrypt verificado con BCryptPasswordEncoder.matches)
INSERT INTO usuario (id_rol, nombre, apellido, username, password, estado) VALUES
(1, 'Admin', 'Sistema', 'admin', '$2a$10$NdPOd6u7ySIMhc.c/pbXzursQuxvBlMaK03yym0o2Yp1BIH7ybS.W', 1),
(2, 'Jefe', 'Almacen', 'jefe', '$2a$10$NdPOd6u7ySIMhc.c/pbXzursQuxvBlMaK03yym0o2Yp1BIH7ybS.W', 1),
(3, 'Paolo', 'Caycho', 'operario', '$2a$10$NdPOd6u7ySIMhc.c/pbXzursQuxvBlMaK03yym0o2Yp1BIH7ybS.W', 1);

-- Productos de prueba
INSERT INTO producto (nombre, codigo, tipo, unidad_medida, stock_actual, stock_minimo) VALUES
('Cartón Kraft 500g', 'C-001', 'MP', 'kg', 320, 150),
('Film Stretch', 'C-002', 'MP', 'rollos', 80, 200),
('Bolsa Poliet. #40', 'C-003', 'PP', 'unidades', 1200, 500),
('Caja corrugada E30', 'C-004', 'PT', 'unidades', 45, 100),
('Cinta adhesiva', 'C-005', 'MP', 'rollos', 600, 100);

-- Proveedor de prueba
INSERT INTO proveedor (nombre, ruc, contacto, direccion) VALUES
('Papeles del Sur SAC', '20512345678', '987654321', 'Av. Industrial 234, Lima');
