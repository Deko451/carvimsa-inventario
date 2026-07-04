# CARVIMSA — Sistema de Gestión de Inventarios

Proyecto Spring Boot generado según `CARVIMSA_PROYECTO.md` e `INSTRUCCIONES_CLAUDE_CODE.md`
(ADSI, UTP, Sección 18164, Grupo 06).

## Stack

Java 17 · Spring Boot 3.5 · Spring MVC · Thymeleaf · Spring Data JPA · Spring Security · MySQL 8.0 · Maven

## Requisitos previos

- Java 17+
- Maven 3.9+ (o usar el wrapper si se agrega en el futuro)
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

| Usuario    | Contraseña | Rol            |
|------------|------------|----------------|
| admin      | admin123   | Administrador  |
| jefe       | admin123   | Jefe           |
| operario   | admin123   | Operario       |

## Estructura

- `model/` — 9 entidades JPA (Rol, Usuario, Proveedor, Producto, MovimientoInventario, Pedido, DetallePedido, Alerta, Reporte).
- `repository/` — interfaces Spring Data JPA.
- `service/` — lógica de negocio (InventarioService implementa la regla crítica de alerta automática en salidas).
- `config/SecurityConfig.java` — rutas protegidas por rol.
- `controller/` — un controlador por módulo.
- `templates/` — vistas Thymeleaf que replican el prototipo Figma (`diagramas/figma_screens/`).

## Nota sobre los assets de diseño

El archivo `diagramas/figma_screens/07_reportes.png` referenciado en la documentación contiene, en
realidad, una copia del diseño de `03_registrar_ingreso.png` (asset mal referenciado/duplicado). La
pantalla de Reportes (`templates/reportes/reportes.html`) se construyó a partir de la descripción
textual de la Pantalla 7 en `CARVIMSA_PROYECTO.md` (sección 17), no de la imagen. Si tienes la imagen
correcta de Reportes, avísame para ajustar el layout con precisión visual.
