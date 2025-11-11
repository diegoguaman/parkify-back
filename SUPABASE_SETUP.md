# 🐘 Guía de Configuración PostgreSQL con Supabase (GRATIS)

Esta guía te ayudará a configurar una base de datos PostgreSQL gratuita en Supabase para usar con tu backend en Render.

## ¿Por qué Supabase?

- ✅ **PostgreSQL gratuito para siempre** (500MB de almacenamiento, más que suficiente)
- ✅ **Sin tarjeta de crédito requerida**
- ✅ **UI amigable** para gestionar la base de datos
- ✅ **Backups automáticos**
- ✅ **Muy rápido y confiable**
- ✅ **SSL habilitado por defecto**

---

## 📋 Paso 1: Crear cuenta en Supabase

1. Ve a https://supabase.com
2. Haz clic en **"Start your project"**
3. Regístrate con GitHub, Google o Email
4. Verifica tu email

---

## 🗄️ Paso 2: Crear un nuevo proyecto

1. Una vez en el dashboard, haz clic en **"New Project"**
2. Selecciona tu organización (se crea automáticamente con tu nombre)
3. Configura el proyecto:
   - **Name**: `parkify` (o el nombre que prefieras)
   - **Database Password**: 
     - Haz clic en **"Generate a password"** para generar una segura
     - **⚠️ IMPORTANTE**: Copia y guarda esta contraseña, la necesitarás después
   - **Region**: Selecciona **Europe (Frankfurt)** o la región más cercana a tu backend
   - **Pricing Plan**: Deja seleccionado **Free** (plan gratuito)
4. Haz clic en **"Create new project"**
5. Espera 1-2 minutos mientras se crea la base de datos

---

## 🔗 Paso 3: Obtener la URL de conexión

1. Una vez creado el proyecto, ve a **Settings** (⚙️) en el menú lateral
2. Haz clic en **Database**
3. En la sección **"Connection string"**, selecciona el modo **URI**
4. Copia la cadena de conexión que se muestra:

```
postgresql://postgres:[YOUR-PASSWORD]@db.abcdefghijklmn.supabase.co:5432/postgres
```

5. **IMPORTANTE**: Reemplaza `[YOUR-PASSWORD]` con la contraseña que copiaste en el Paso 2

Ejemplo de URL completa:
```
postgresql://postgres:MiPassword123Super@db.abcdefghijklmn.supabase.co:5432/postgres
```

---

## ⚙️ Paso 4: Configurar variables de entorno en Render

1. Ve a tu Web Service en Render: https://dashboard.render.com
2. Haz clic en tu servicio `parkify-backend`
3. Ve a la pestaña **Environment**
4. **Agrega o actualiza** las siguientes variables de entorno:

### ⚠️ **IMPORTANTE: Formato correcto de DATABASE_URL**

La URL de Supabase debe modificarse para Spring Boot:

**❌ MAL (formato de Supabase):**
```
postgresql://postgres:MiPass!@db.xxx.supabase.co:5432/postgres
```

**✅ BIEN (formato para Spring Boot):**
```
jdbc:postgresql://db.xxx.supabase.co:5432/postgres?user=postgres&password=MiPass!&sslmode=require
```

**Cambios necesarios:**
1. Agregar `jdbc:` al inicio
2. Mover credenciales a parámetros `user` y `password`
3. Agregar `sslmode=require` al final

### 📋 **Variables de entorno:**

| Variable | Valor | Ejemplo |
|----------|-------|---------|
| `SPRING_PROFILES_ACTIVE` | `prod` | `prod` |
| `DATABASE_URL` | Ver formato arriba ⬆️ | `jdbc:postgresql://db.xxx.supabase.co:5432/postgres?user=postgres&password=TU_PASSWORD&sslmode=require` |
| `JWT_SECRET` | Base64 sin espacios | `TXlTdXBlclNlY3VyZUtleUZvclBhcmtpZnk=` |
| `JWT_EXPIRATION` | Tiempo token | `PT10H` |
| `FRONTEND_URL` | URL de tu frontend | `https://parkify-front.vercel.app` |

5. Haz clic en **"Save Changes"**
6. Render redesplegará automáticamente tu aplicación

---

## ✅ Paso 5: Verificar la conexión

1. Ve a la pestaña **"Logs"** de tu Web Service en Render
2. Busca estas líneas para confirmar que la conexión fue exitosa:

```
HikariPool-1 - Start completed.
Initialized JPA EntityManagerFactory for persistence unit 'default'
Started ParkifyApplication in X.XXX seconds
```

3. Si ves estos mensajes, ¡la conexión fue exitosa! 🎉

---

## 📊 Paso 6: Gestionar tu base de datos (Opcional)

Supabase te proporciona herramientas para gestionar tu base de datos:

### Ver tablas y datos:

1. En Supabase, ve a **Table Editor** en el menú lateral
2. Aquí verás todas las tablas creadas por Hibernate:
   - `auth_user`
   - `parking`
   - `review`
   - `reservation` (si existe)

### Ejecutar consultas SQL:

1. Ve a **SQL Editor** en el menú lateral
2. Puedes ejecutar consultas SQL personalizadas:

```sql
-- Ver todos los parkings
SELECT * FROM parking;

-- Ver todos los usuarios
SELECT * FROM auth_user;

-- Ver estadísticas
SELECT 
    (SELECT COUNT(*) FROM auth_user) as total_users,
    (SELECT COUNT(*) FROM parking) as total_parkings;
```

### Ver logs de conexiones:

1. Ve a **Database** → **Logs**
2. Aquí puedes ver todas las conexiones y consultas realizadas

---

## 🔐 Seguridad y Mejores Prácticas

### 1. No expongas tu contraseña de base de datos

- ✅ Usa variables de entorno (como ya hicimos)
- ❌ Nunca la incluyas en el código o en commits de Git

### 2. Limita las conexiones

En Supabase, el plan gratuito permite:
- **60 conexiones simultáneas** (más que suficiente)

Si necesitas optimizar, puedes configurar el pool de conexiones en `application-prod.properties`:

```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
```

### 3. Monitorea el uso

En Supabase, ve a **Settings** → **Usage** para ver:
- Almacenamiento usado
- Número de conexiones
- Tráfico de red

---

## 📝 Resumen de URLs

| Servicio | URL | Propósito |
|----------|-----|-----------|
| Backend (Render) | `https://parkify-backend.onrender.com` | API REST + WebSocket |
| Base de datos (Supabase) | `db.xxx.supabase.co:5432` | PostgreSQL |
| Frontend (Vercel) | `https://parkify-front.vercel.app` | React App |

---

## 🐛 Solución de Problemas

### ❌ Error: "The connection attempt failed"

**Causa**: La URL de conexión es incorrecta o la contraseña no coincide.

**Solución**:
1. Verifica que la contraseña en `DATABASE_URL` sea correcta
2. Asegúrate de que la URL esté completa (sin espacios)
3. Verifica que Supabase esté activo (ve al dashboard)

### ❌ Error: "FATAL: password authentication failed"

**Causa**: La contraseña es incorrecta.

**Solución**:
1. En Supabase, ve a **Settings** → **Database**
2. Haz clic en **"Reset database password"**
3. Genera una nueva contraseña
4. Actualiza `DATABASE_URL` en Render con la nueva contraseña

### ❌ Error: "Connection timeout"

**Causa**: La región de Supabase está muy lejos de la región de Render.

**Solución**:
1. Verifica que tanto Supabase como Render estén en Europa (Frankfurt)
2. Si no es así, crea un nuevo proyecto en Supabase en la misma región

### ❌ El despliegue tarda mucho

**Causa**: La conexión entre Render y Supabase puede ser lenta la primera vez.

**Solución**:
- Es normal que el primer despliegue tarde 5-10 minutos
- Los siguientes despliegues serán más rápidos

---

## 🎉 ¡Listo!

Ahora tienes:
- ✅ Backend desplegado en Render (gratis)
- ✅ PostgreSQL en Supabase (gratis para siempre)
- ✅ Frontend en Vercel (gratis)

**Todo gratis, sin límites de tiempo, sin tarjeta de crédito.**

---

## 🔄 Alternativas si Supabase no te funciona

Si por alguna razón Supabase no te funciona, puedes usar:

1. **Neon**: https://neon.tech (PostgreSQL serverless, 3GB gratis)
2. **ElephantSQL**: https://www.elephantsql.com (20MB gratis, suficiente para desarrollo)
3. **Railway**: https://railway.app ($5 de crédito gratis al mes, incluye PostgreSQL)

El proceso de configuración es muy similar: solo cambias la `DATABASE_URL`.

---

## 📚 Enlaces útiles

- **Dashboard de Supabase**: https://supabase.com/dashboard
- **Documentación de Supabase**: https://supabase.com/docs
- **Dashboard de Render**: https://dashboard.render.com
- **Soporte de Supabase**: https://supabase.com/support

---

### ¿Necesitas ayuda?

Si encuentras algún problema:
1. Revisa los logs en Render (pestaña "Logs")
2. Verifica la configuración en Supabase (Settings → Database)
3. Asegúrate de que todas las variables de entorno estén correctas

