# CafeDirecto Sacramento Backend

Backend Spring Boot para la plataforma de comercializacion directa de cafe.

## Modelo funcional

- Una sola cuenta por usuario.
- La misma cuenta puede comprar y vender.
- Vender requiere activar `producer-profile`.
- Un productor puede registrar varias fincas.
- Cada lote publicado debe pertenecer a una finca.

## Ejecutar local

Configura variables de entorno o usa los valores por defecto definidos en `application.properties`.

```bash
DB_URL=jdbc:postgresql://localhost:5432/cowork_db
DB_USERNAME=user_db
DB_PASSWORD=123456
JWT_SECRET=change-this-secret-key-change-this-secret-key
FRONTEND_URL=http://localhost:3000
```

Luego ejecuta:

```bash
mvn test
mvn spring-boot:run
```

## Variables para Render

Variables recomendadas:

```bash
DB_URL=jdbc:postgresql://...
DB_USERNAME=...
DB_PASSWORD=...
JWT_SECRET=...
FRONTEND_URL=https://TU-FRONTEND.onrender.com
JWT_EXPIRATION_MS=86400000
DDL_AUTO=update
SHOW_SQL=false
MP_ACCESS_TOKEN=...
MP_WEBHOOK_SECRET=...
```

`FRONTEND_URL` puede recibir varias URLs separadas por coma si necesitas permitir localhost y Render al mismo tiempo.

## Render

Este repo ya incluye `render.yaml`.

Configuracion equivalente:

```bash
Build Command: mvn clean package -DskipTests
Start Command: java -jar target/cowork-0.0.1-SNAPSHOT.jar
```

## Estado actual

- JWT y seguridad stateless.
- Auth unificado con registro e inicio de sesion.
- Perfil productor.
- Multiples fincas por productor.
- Catalogo y detalle de productos.
- Publicacion y gestion basica de lotes.
- Carrito, ordenes, comentarios y pagos ya estructurados en la API.
## Arranque en Windows

Si `mvn spring-boot:run` falla con `ClassNotFoundException` en esta ruta del proyecto, usa el script:

```powershell
.\scripts\start-backend.ps1
```

Para detenerlo:

```powershell
.\scripts\stop-backend.ps1
```

El endpoint de prueba recomendado es:

```text
http://127.0.0.1:8080/api/v1/health
```
