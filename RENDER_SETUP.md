# 🚀 Guía de Despliegue en Render

Esta guía te ayudará a desplegar correctamente la aplicación Parkify Backend en Render.

## 📋 Prerequisitos

1. Cuenta en [Render.com](https://render.com)
2. Repositorio del proyecto en GitHub conectado a Render

## 🗄️ Paso 1: Crear la Base de Datos PostgreSQL

### 1.1 Crear PostgreSQL Database en Render

1. En el dashboard de Render, haz clic en **"New +"** → **"PostgreSQL"**
2. Configura los siguientes campos:
   - **Name**: `parkify-db` (o el nombre que prefieras)
   - **Database**: `db_parkify`
   - **User**: Se genera automáticamente
   - **Region**: Elige la región más cercana (ej: Frankfurt - EU)
   - **PostgreSQL Version**: 15 o superior
   - **Plan**: Free (para desarrollo) o Starter (para producción)
3. Haz clic en **"Create Database"**
4. **IMPORTANTE**: Copia y guarda la **Internal Database URL** que aparece en la página de la base de datos creada. La necesitarás en el siguiente paso.

### 1.2 Verificar la Base de Datos

Una vez creada, verás información como:
```
Internal Database URL: postgresql://parkify_db_user:xxxxx@dpg-xxxxx-a.frankfurt-postgres.render.com/parkify_db
External Database URL: postgresql://parkify_db_user:xxxxx@dpg-xxxxx-a.frankfurt-postgres.render.com/parkify_db
```

**Usa la Internal Database URL** para mejor rendimiento (conexiones internas en Render son más rápidas).

---

## 🐳 Paso 2: Crear el Web Service

### 2.1 Crear nuevo Web Service

1. En el dashboard de Render, haz clic en **"New +"** → **"Web Service"**
2. Conecta tu repositorio de GitHub
3. Configura los siguientes campos:
   - **Name**: `parkify-backend`
   - **Region**: **Misma región que la base de datos** (ej: Frankfurt)
   - **Branch**: `main` (o la rama que uses)
   - **Root Directory**: (dejar vacío)
   - **Runtime**: `Docker`
   - **Instance Type**: Free (para desarrollo) o Starter

### 2.2 Configurar Build Settings

Render detectará automáticamente el `Dockerfile` y usará estos comandos:
- **Build Command**: (automático desde Dockerfile)
- **Start Command**: (automático desde Dockerfile)

---

## 🔑 Paso 3: Configurar Variables de Entorno

En la sección **"Environment Variables"** del Web Service, agrega las siguientes variables:

### Variables Obligatorias:

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Activa el perfil de producción |
| `DATABASE_URL` | `[Tu Internal Database URL]` | URL de la base de datos PostgreSQL creada en Paso 1 |
| `JWT_SECRET` | `[Tu clave secreta]` | Clave para firmar tokens JWT (mínimo 32 caracteres) |
| `JWT_EXPIRATION` | `PT10H` | Tiempo de expiración del token (10 horas) |
| `FRONTEND_URL` | `https://parkify-front.vercel.app` | URL de tu frontend en Vercel |
| `PORT` | `8080` | Puerto donde corre la app (Render lo sobrescribe automáticamente) |

### Ejemplo de JWT_SECRET seguro:

Genera una clave aleatoria segura con este comando (en Linux/Mac/WSL):
```bash
openssl rand -base64 32
```

O usa este valor temporal para desarrollo:
```
MiClaveSecretaSuperSeguraParaProduccion123456789ABC
```

### 3.1 Configurar DATABASE_URL

⚠️ **IMPORTANTE**: Usa la **Internal Database URL** copiada en el Paso 1.1

Ejemplo:
```
postgresql://parkify_db_user:xxxxpasswordxxxx@dpg-xxxxx-a.frankfurt-postgres.render.com/parkify_db
```

---

## 🎯 Paso 4: Desplegar la Aplicación

1. Una vez configuradas todas las variables de entorno, haz clic en **"Create Web Service"**
2. Render comenzará a construir y desplegar tu aplicación automáticamente
3. El proceso puede tomar 5-10 minutos la primera vez

### 4.1 Monitorear el despliegue

En la pestaña **"Logs"** podrás ver:
- Build logs (construcción de la imagen Docker)
- Deploy logs (inicio de la aplicación Spring Boot)

Busca este mensaje para confirmar que arrancó correctamente:
```
Started ParkifyApplication in X.XXX seconds
```

---

## ✅ Paso 5: Verificar el Despliegue

### 5.1 Verificar que la aplicación está corriendo

Una vez desplegado, tu backend estará disponible en:
```
https://parkify-backend.onrender.com
```

### 5.2 Probar endpoints

#### Endpoint de prueba:
```bash
curl https://parkify-backend.onrender.com/api/v1/auth/health
```

#### WebSocket endpoint:
```
wss://parkify-backend.onrender.com/ws
```

---

## 🔄 Redesplegar (Deploy Manual)

Si necesitas redesplegar manualmente:
1. Ve a tu Web Service en Render
2. Haz clic en **"Manual Deploy"** → **"Deploy latest commit"**
3. O simplemente haz push a tu rama `main` y Render desplegará automáticamente

---

## 🐛 Solución de Problemas Comunes

### ❌ Error: "Connection refused" o "Connection reset"

**Causa**: La base de datos no está configurada o la `DATABASE_URL` es incorrecta.

**Solución**:
1. Verifica que la base de datos PostgreSQL esté creada y corriendo en Render
2. Verifica que `DATABASE_URL` tenga la URL completa correcta
3. Asegúrate de usar la **Internal Database URL**, no la Externa

### ❌ Error: "Unable to start web server"

**Causa**: Faltan variables de entorno obligatorias.

**Solución**:
- Verifica que `SPRING_PROFILES_ACTIVE=prod` esté configurado
- Verifica que `DATABASE_URL` esté configurado
- Revisa los logs para identificar qué variable falta

### ❌ La aplicación se reinicia constantemente

**Causa**: La aplicación no puede conectarse a la base de datos.

**Solución**:
1. Verifica que la base de datos esté en la **misma región** que el Web Service
2. Verifica que la base de datos esté "Available" (no suspended)
3. En planes Free, la base de datos puede suspenderse después de 90 días de inactividad

### ❌ Error 403 en endpoints

**Causa**: Problemas con Spring Security o CORS.

**Solución**:
- Verifica que `FRONTEND_URL` apunte correctamente a tu frontend
- Revisa que el token JWT sea válido
- Verifica en los logs si hay errores de autenticación

---

## 📊 Monitoreo y Logs

### Ver logs en tiempo real:
1. Ve a tu Web Service en Render
2. Haz clic en la pestaña **"Logs"**
3. Los logs se actualizan en tiempo real

### Filtrar logs:
```bash
# En la barra de búsqueda de logs, puedes buscar:
ERROR        # Ver solo errores
Started      # Ver cuando arranca la app
Broadcasting # Ver mensajes de WebSocket
```

---

## 🔐 Seguridad en Producción

### Recomendaciones:

1. **JWT_SECRET**: Usa una clave aleatoria y segura, nunca uses la clave de desarrollo
2. **Database backups**: Configura backups automáticos en Render (solo en planes de pago)
3. **HTTPS**: Render proporciona HTTPS automáticamente
4. **Variables de entorno**: Nunca las expongas en logs o commits

---

## 📝 Checklist de Despliegue

- [ ] Base de datos PostgreSQL creada en Render
- [ ] Internal Database URL copiada
- [ ] Web Service creado con Docker runtime
- [ ] Región del Web Service = Región de la base de datos
- [ ] Variable `SPRING_PROFILES_ACTIVE=prod` configurada
- [ ] Variable `DATABASE_URL` configurada con Internal Database URL
- [ ] Variable `JWT_SECRET` configurada con clave segura
- [ ] Variable `FRONTEND_URL` configurada
- [ ] Despliegue exitoso (logs muestran "Started ParkifyApplication")
- [ ] Endpoint de health check responde correctamente
- [ ] WebSocket funciona desde el frontend

---

## 🎉 ¡Listo!

Si todos los pasos se completaron correctamente, tu backend de Parkify está ahora desplegado en Render y listo para conectarse con tu frontend en Vercel.

### Enlaces útiles:

- **Dashboard de Render**: https://dashboard.render.com
- **Documentación de Render**: https://render.com/docs
- **PostgreSQL en Render**: https://render.com/docs/databases
- **Docker en Render**: https://render.com/docs/docker

---

### Soporte

Si encuentras algún problema:
1. Revisa los logs en la pestaña "Logs" de Render
2. Verifica que todas las variables de entorno estén configuradas
3. Asegúrate de que la base de datos esté corriendo y en la misma región

**Nota**: En el plan Free de Render, los servicios pueden "dormir" después de 15 minutos de inactividad. La primera solicitud después de que "despierte" puede tomar 30-60 segundos.

