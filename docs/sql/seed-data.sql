-- Datos demo para CafeDirecto Sacramento
-- Ejecutar en el SQL Editor de Supabase despues de crear el schema

-- Password: password123 (bcrypt)
-- En produccion, registra usuarios desde la app para que el hash se genere correctamente

-- Usa el endpoint de registro desde la app o el siguiente curl:
-- curl -X POST https://TU_BACKEND.onrender.com/api/v1/auth/register \
--   -H "Content-Type: application/json" \
--   -d '{"fullName":"Jheymer Navarro","email":"jheymer@cafe.com","password":"password123","phone":"+573001234567","whatsappNumber":"+573001234567"}'

-- Nota: los datos de productores, fincas y productos se crean desde la app.
-- Entra como productor, completa tu perfil y publica tus lotes desde el panel de venta.
