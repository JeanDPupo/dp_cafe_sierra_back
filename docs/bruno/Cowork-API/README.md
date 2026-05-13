# Bruno Collection

Ubicacion: `mi-backend/docs/bruno/Cowork-API`

## Orden sugerido
1. `Health/01-health`
2. `Auth/01-register`
3. `Auth/02-login`
4. Copiar el `token` del login al environment `local.bru`
5. `Users/03-create-producer-profile`
6. `Users/07-create-farm` si quieres registrar una o mas fincas adicionales
7. Actualizar `producerProfileId` y `farmId` en el environment
8. `Products/01-create-product`
9. Actualizar `productId` en el environment
10. Probar `Comments`, `Cart`, `Orders` y `Payments`

## Flujo con una sola cuenta
- El registro y login son unificados: todos entran como usuarios.
- Un usuario puede comprar de inmediato.
- Cuando quiera vender, primero crea o completa su `producer-profile`.
- Un productor puede tener varias fincas.
- Cada nuevo lote requiere `farmId`, para dejar claro a que finca pertenece.
    
## Sobre el error `Connection refused`
Ese error no es de JWT.

Significa que el backend no esta corriendo en `http://localhost:8080`.

Para levantarlo:

```powershell
cd mi-backend
$env:DB_URL="jdbc:postgresql://localhost:5432/cowork_db"
$env:DB_USERNAME="user_db"
$env:DB_PASSWORD="123456"
$env:JWT_SECRET="change-this-secret-key-change-this-secret-key"
& "$env:TEMP\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

Si no tienes Postgres listo, el siguiente paso natural es que te deje un perfil `dev` con H2 para pruebas rapidas.
