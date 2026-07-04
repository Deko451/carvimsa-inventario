# CARVIMSA — Sistema de Gestión de Inventarios

Sistema web de gestión de inventarios para CARVIMSA (Cartones Villa Marina S.A.), una empresa
peruana del sector manufacturero de cartones y empaques. Controla el inventario de materias primas,
productos en proceso y productos terminados, con roles diferenciados, registro de movimientos y
alertas automáticas por stock mínimo.

Proyecto desarrollado para el curso de Análisis y Diseño de Sistemas de Información (ADSI) —
Universidad Tecnológica del Perú (UTP), Sección 18164, Grupo 06.

## Stack

Java 17 · Spring Boot 3.5 · Spring MVC · Thymeleaf · Spring Data JPA · Spring Security · MySQL 8.0 · Maven

## Funcionalidades

| Módulo | Descripción | Rol requerido |
|---|---|---|
| Login | Autenticación por usuario/contraseña (BCrypt) | Todos |
| Dashboard | KPIs (productos activos, movimientos del día, alertas, pedidos pendientes) y últimos movimientos | Todos |
| Registrar Ingreso | Alta de stock por compra/recepción de proveedor | Operario, Jefe, Administrador |
| Registrar Salida | Baja de stock por producción o despacho a cliente | Operario, Jefe, Administrador |
| Consultar Stock | Listado filtrable por tipo (MP/PP/PT) con estado OK/BAJO | Operario, Jefe, Administrador |
| Registrar Pedido | Pedido de despacho a cliente (genera automáticamente una salida) | Operario, Jefe, Administrador |
| Gestionar Alertas | Alertas activas por stock bajo mínimo, con resolución manual | Jefe, Administrador |
| Generar Reportes | Reporte de movimientos por producto (entradas, salidas, saldo neto) en un rango de fechas | Jefe, Administrador |
| Gestionar Productos/Proveedores/Usuarios | Alta de catálogos maestros | Administrador |

**Regla de negocio crítica:** al registrar una salida, si el stock resultante cae por debajo del
stock mínimo del producto, el sistema crea automáticamente una alerta en estado `ACTIVA`
(`InventarioService.registrarSalida`).

## Requisitos previos

- Java 17+
- Maven 3.9+
- MySQL 8.0 corriendo localmente (o accesible por red)

## Puesta en marcha

1. Clonar el repositorio.
2. Crear el esquema ejecutando el script contra tu servidor MySQL:
   ```
   mysql -u root -p < src/main/resources/db/carvimsa_inventario.sql
   ```
   Esto crea la base `carvimsa_inventario`, las tablas y carga roles, usuarios y productos de prueba.
3. Configurar credenciales de base de datos. Por defecto la app asume `root/root` en
   `localhost:3306` (ver `application.properties`). Si tu instalación usa otras credenciales,
   **no edites el archivo** — sobreescribe con variables de entorno antes de ejecutar:

   | Variable | Default | Descripción |
   |---|---|---|
   | `DB_URL` | `jdbc:mysql://localhost:3306/carvimsa_inventario?useSSL=false&serverTimezone=America/Lima` | URL JDBC completa |
   | `DB_USERNAME` | `root` | Usuario de MySQL |
   | `DB_PASSWORD` | `root` | Contraseña de MySQL |

   Ejemplo (Windows PowerShell):
   ```powershell
   $env:DB_USERNAME = "mi_usuario"
   $env:DB_PASSWORD = "mi_password"
   mvn spring-boot:run
   ```
   Ejemplo (Linux/macOS):
   ```bash
   DB_USERNAME=mi_usuario DB_PASSWORD=mi_password mvn spring-boot:run
   ```
4. Sin variables de entorno, simplemente:
   ```
   mvn spring-boot:run
   ```
5. Abrir `http://localhost:8080/login`.

## Usuarios de prueba

| Usuario  | Contraseña | Rol           |
|----------|------------|---------------|
| admin    | admin123   | Administrador |
| jefe     | admin123   | Jefe          |
| operario | admin123   | Operario      |

## Estructura del proyecto

```
src/main/java/com/carvimsa/inventario/
├── controller/   Controladores Spring MVC (uno por módulo)
├── service/      Lógica de negocio (InventarioService, AlertaService, ReporteService, SeguridadService)
├── repository/   Interfaces Spring Data JPA
├── model/        Entidades JPA: Rol, Usuario, Proveedor, Producto, MovimientoInventario,
│                 Pedido, DetallePedido, Alerta, Reporte
└── config/       SecurityConfig (rutas protegidas por rol)

src/main/resources/
├── application.properties
├── db/carvimsa_inventario.sql   Script DDL + datos de prueba
├── static/css/                  Hoja de estilos compartida
└── templates/                   Vistas Thymeleaf (login, dashboard, inventario/, alertas/, reportes/, pedidos/, admin/)
```

## Rutas principales

| URL | Método | Rol |
|---|---|---|
| `/login` | GET/POST | Todos |
| `/dashboard` | GET | Todos |
| `/ingresos/nuevo`, `/ingresos/guardar` | GET, POST | Operario+ |
| `/salidas/nuevo`, `/salidas/guardar` | GET, POST | Operario+ |
| `/stock` | GET | Operario+ |
| `/pedidos/nuevo`, `/pedidos/guardar` | GET, POST | Operario+ |
| `/alertas`, `/alertas/resolver/{id}` | GET, POST | Jefe+ |
| `/reportes`, `/reportes/generar` | GET, POST | Jefe+ |
| `/admin/productos`, `/admin/proveedores`, `/admin/usuarios` | GET | Administrador |

## Notas de desarrollo

- El diseño visual de las pantallas se basó en un prototipo Figma (no incluido en este repositorio).
  La pantalla de Reportes se construyó a partir de la especificación textual del documento de diseño
  original, ya que el asset de imagen correspondiente estaba duplicado/mal referenciado.
- Las fechas en formularios usan `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)` para ser compatibles
  con el input nativo `type="date"` de HTML5, independientemente del locale del servidor.
