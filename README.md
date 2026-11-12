# Jetstore

Tienda virtual de libros - Backend API REST con Spring Boot.

## Tecnologías

- Java 17
- Spring Boot 4.0.7-SNAPSHOT
- Spring Data JPA + Hibernate
- PostgreSQL
- Maven
- Lombok

## Estructura del proyecto

```
src/main/java/com/idasta/jetstore/
├── JetstoreApplication.java          # Entry point
├── controller/                       # Controladores REST
│   ├── LibroController.java
│   ├── UsuarioController.java
│   ├── CarritoController.java
│   ├── PagoController.java
│   ├── RolController.java
│   └── PasswordRecoveryController.java
├── service/                          # Lógica de negocio
│   ├── LibroService.java / Impl
│   ├── UsuarioService.java / Impl
│   ├── CarritoService.java / Impl
│   ├── PagoService.java / Impl
│   ├── PasswordRecoveryService.java / Impl
│   └── CorreoService.java / Impl
├── repository/                       # Acceso a datos (JPA)
│   ├── LibroRepo.java (+ Custom / Impl)
│   ├── UsuarioRepo.java
│   ├── CarritoRepo.java
│   ├── PagoRepo.java
│   ├── VentaRepo.java
│   ├── RolRepo.java
│   ├── SesionRepo.java
│   └── RecuperacionRepo.java
├── model/                            # Entidades JPA
│   ├── Libro.java
│   ├── Categoria.java
│   ├── Carrito.java / CarritoItem.java
│   ├── Pago.java
│   ├── Venta.java / DetalleVenta.java
│   ├── Usuario.java
│   ├── Rol.java
│   ├── Sesion.java
│   ├── Recuperacion.java
│   └── Cliente.java
├── dto/                              # DTOs (record)
│   ├── GuardarLibroDTO.java
│   ├── VerLibroDTO.java
│   ├── FiltroLibroDTO.java
│   ├── AgregarStockDTO.java
│   ├── GuardarCategoriaDTO.java
│   ├── AgregarAlCarritoDTO.java
│   ├── CarritoResponseDTO.java / ItemCarritoDTO.java
│   ├── PagarRequestDTO.java
│   ├── ConfirmarPagoDTO.java
│   ├── PagoResponseDTO.java / VentaResponseDTO.java
│   ├── RegistrarUsuarioDTO.java
│   ├── LoginDTO.java / LoginResponseDTO.java
│   ├── UsuarioPerfilDTO.java
│   ├── SolicitarRecuperacionDTO.java
│   └── RestablecerPasswordDTO.java
└── helper/
    ├── RespuestaApi.java             # Envoltorio de respuesta {ok, mensaje, payload}
    ├── GlobalExceptionHandler.java   # Manejo global de errores
    ├── Mapear.java                   # Mapeo DTO -> Entidad
    ├── PasswordUtil.java             # Hash SHA-256 + salt
    └── DataSeeder.java               # Crea roles ADMIN y CLIENTE al iniciar
```

## Requisitos

- JDK 17+
- PostgreSQL corriendo en `localhost:5432`
- Base de datos `jetstore` creada

## Configuración

Edita `src/main/resources/application.properties` si tu conexión es distinta:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jetstore
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Recuperación de contraseña

Variables de entorno (valores por defecto entre paréntesis):

| Variable | Descripción |
|----------|-------------|
| `FRONTEND_URL` (`http://localhost:3000`) | Base del enlace `/reset-password?token=...` enviado al correo |
| `RECUPERACION_EXPIRACION_HORAS` (`1`) | Horas de validez del enlace |
| `MAIL_HABILITADO` (`true`) | Habilita/deshabilita el envío de correos |
| `MAIL_REMITENTE` (`no-responder@jetstore.pe`) | Remitente de los correos |
| `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | Configuración SMTP (opcional) |

> Sin SMTP configurado, el enlace de recuperación se muestra en la consola del backend. El token es de un solo uso; solicitar uno nuevo invalida los anteriores y restablecer la contraseña cierra las sesiones activas del usuario.

## Ejecutar

```bash
./mvnw spring-boot:run
```

O desde tu IDE: ejecutar `JetstoreApplication.java`

Las tablas se crean automáticamente (`ddl-auto=create-drop`). Los roles `ADMIN` y `CLIENTE` se crean al iniciar.

## Endpoints

### Rol

| Método | Ruta | Body |
|--------|------|------|
| POST | `/rol/crear` | `{"nombre": "VENDEDOR"}` |
| GET | `/rol/todos` | - |

### Usuario

| Método | Ruta | Body / Header |
|--------|------|---------------|
| POST | `/usuario/registrar` | `{"nombreUsuario": "...", "correo": "...", "password": "...", "rol": "CLIENTE"}` |
| POST | `/usuario/login` | `{"nombreUsuario": "...", "password": "..."}` |
| GET | `/usuario/perfil/{id}` | - |
| POST | `/usuario/cerrar-sesion` | Header: `Authorization: <token>` |
| POST | `/usuario/recuperar` | `{"correo": "..."}` |
| POST | `/usuario/restablecer` | `{"token": "...", "password": "...", "confirmPassword": "..."}` |

### Libro

| Método | Ruta | Body / Params |
|--------|------|---------------|
| POST | `/libro/guardar` | `{"titulo": "...", "autor": "...", "categoria": {"nombre": "..."}, "precio": 0, "formato": "FISICO", "stock": 0}` |
| POST | `/libro/agregar-stock` | `{"libroId": 1, "cantidad": 5}` |
| DELETE | `/libro/eliminar/{id}` | - |
| GET | `/libro/todos` | - |
| GET | `/libro/buscar?q=keyword` | - |
| POST | `/libro/filtrar` | `{"categoria": "...", "formato": "...", "precioDesde": 0, "precioHasta": 0}` |
| GET | `/libro/ver/{id}` | - |
| GET | `/libro/categorias` | - |
| GET | `/libro/categoria/{nombre}` | - |
| GET | `/libro/recientes` | - |

### Carrito

| Método | Ruta | Body |
|--------|------|------|
| POST | `/carrito/agregar` | `{"usuarioId": 1, "libroId": 1, "cantidad": 2}` |
| GET | `/carrito/ver/{usuarioId}` | - |
| DELETE | `/carrito/eliminar/{itemId}` | - |

### Pago

| Método | Ruta | Body |
|--------|------|------|
| POST | `/pago/procesar` | `{"usuarioId": 1, "metodoPago": "YAPE"}` |
| POST | `/pago/confirmar` | `{"pagoId": 1}` |

## Respuesta

Todos los endpoints devuelven:

```json
{
  "ok": true,
  "mensaje": "Mensaje descriptivo",
  "payload": { ... }
}
```

En caso de error, `ok` es `false` y el mensaje describe el problema.

## Colección Postman

Importa el archivo `jetstore.postman_collection.json` (pendiente de exportar).
