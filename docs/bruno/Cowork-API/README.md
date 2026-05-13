# Bruno Collection

Ubicacion: `mi-backend/docs/bruno/Cowork-API`

## Orden sugerido
1. `Health/01-health`
2. `Auth/01-register`
3. `Auth/02-login`
4. Copiar el `token` del login al environment `local.bru`
5. `Users/03-create-producer-profile`
6. `Products/01-create-product`
7. Actualizar `producerProfileId` y `productId` en el environment
8. Probar `Comments`, `Cart`, `Orders` y `Payments`
    
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
