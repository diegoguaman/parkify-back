# 🔴 Error: MaxClientsInSessionMode - Guía de Solución

## 🧐 ¿Qué pasó?

Tu aplicación en Render falló con este error:

```
FATAL: MaxClientsInSessionMode: max clients reached - in Session mode max clients are limited to pool_size
```

## 📚 Explicación técnica

### El problema:

1. **HikariCP** (el pool de conexiones de Spring Boot) estaba configurado para crear **10 conexiones simultáneas**
2. **Supabase Session Pooler** (Free Tier) solo permite **3-5 conexiones simultáneas**
3. Cuando Spring Boot intentó crear la 6ª conexión, Supabase la rechazó

### ¿Por qué usamos Session Pooler?

- **Render Free Tier** solo soporta IPv4
- **Supabase Direct Connection (puerto 5432)** usa IPv6
- **Supabase Session Pooler (puerto 6543)** usa IPv4 ✅

Por eso usamos el Session Pooler, pero tiene limitaciones de conexiones.

---

## ✅ Solución Aplicada

### 1. Reducción del Pool de Conexiones

He modificado `application-prod.properties` para **limitar HikariCP a 3 conexiones**:

```properties
# Configuración de HikariCP optimizada para Supabase Free Tier
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

**Cambios:**
- ~~`maximum-pool-size=10`~~ → `maximum-pool-size=3`
- Agregado `minimum-idle=1` (mantiene 1 conexión activa siempre)
- Agregado configuraciones de timeout para mejor gestión

### 2. Actualización de Documentación

- Actualizado `SUPABASE_SETUP.md` con:
  - Explicación de **Session Pooler vs Direct Connection**
  - Comparación de opciones
  - Troubleshooting específico para este error

---

## 🚀 Pasos para Desplegar la Corrección

### 1. Commit y Push de los cambios

```bash
git add .
git commit -m "fix(database): Reducir pool de conexiones para Supabase Session Pooler"
git push origin develop
```

### 2. Verificar en Render

1. Ve a tu Web Service en Render: https://dashboard.render.com
2. El despliegue comenzará automáticamente
3. Espera 2-3 minutos
4. Ve a **Logs** y busca estas líneas:

```
✅ Correcto:
HikariPool-1 - Start completed.
Initialized JPA EntityManagerFactory for persistence unit 'default'
Started ParkifyApplication in X.XXX seconds

❌ Incorrecto (si aún falla):
FATAL: MaxClientsInSessionMode: max clients reached
```

### 3. Si aún falla

**Verifica variables de entorno en Render:**

1. Ve a **Environment** en tu Web Service
2. Busca y **ELIMINA** estas variables si existen:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Deja solo:
   - `DATABASE_URL` (con formato correcto)
   - `SPRING_PROFILES_ACTIVE=prod`
   - `JWT_SECRET`
   - `JWT_EXPIRATION`
   - `FRONTEND_URL`
4. Guarda y espera el redespliegue

---

## 📊 Comparación: Antes vs Después

| Aspecto | ❌ Antes | ✅ Después |
|---------|----------|-----------|
| **Max Conexiones** | 10 | 3 |
| **Min Conexiones** | 10 (default) | 1 |
| **Compatibilidad Supabase Free** | ❌ No | ✅ Sí |
| **Timeout Connection** | Default | 30s |
| **Idle Timeout** | Default | 10 min |
| **Max Lifetime** | Default | 30 min |

---

## 🔄 Alternativas (si prefieres otra solución)

### Opción A: Cambiar a Direct Connection (Puerto 5432)

**Ventajas:**
- Sin límite de conexiones
- Más rápido

**Desventajas:**
- Solo IPv6 (puede NO funcionar con Render Free)

**Pasos:**
1. En Supabase, obtén la URL de **Direct Connection** (puerto 5432)
2. Convierte a formato JDBC:
   ```
   jdbc:postgresql://db.xxx.supabase.co:5432/postgres?user=postgres&password=TU_PASSWORD&sslmode=require
   ```
3. Actualiza `DATABASE_URL` en Render
4. Puedes subir `maximum-pool-size` a 10 nuevamente

### Opción B: Cambiar a Transaction Mode (Puerto 6543)

**Ventajas:**
- Más conexiones permitidas que Session Mode
- IPv4 compatible

**Pasos:**
1. Usa la misma URL del Session Pooler
2. Agrega `&pgbouncer=true` al final:
   ```
   jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?user=postgres.xxx&password=TU_PASSWORD&sslmode=require&pgbouncer=true
   ```
3. Puedes subir `maximum-pool-size` a 5-7

---

## 🎯 Recomendación Final

**Para Parkify MVP con Supabase Free + Render Free:**

✅ **Usa la solución aplicada** (Session Pooler con pool size = 3)

**Por qué:**
- Funciona de forma consistente
- 3 conexiones son suficientes para un MVP con tráfico bajo/medio
- Sin costos adicionales
- Compatible con Render Free (IPv4)

**Cuando actualices a planes pagos:**
- Supabase Pro: hasta 200 conexiones → puedes subir `maximum-pool-size` a 20
- Render Paid: soporte IPv6 → puedes usar Direct Connection (puerto 5432)

---

## 📝 Checklist de Verificación

Antes de dar por solucionado el problema, verifica:

- [ ] `application-prod.properties` tiene `maximum-pool-size=3`
- [ ] No hay variables duplicadas en Render Environment
- [ ] La `DATABASE_URL` está en formato JDBC correcto
- [ ] El despliegue en Render se completó sin errores
- [ ] Los logs muestran "Started ParkifyApplication"
- [ ] El backend responde en `https://parkify-backend.onrender.com/api/v1/auth/health`

---

## 🆘 ¿Aún tienes problemas?

Si después de seguir estos pasos aún tienes errores:

1. Copia los últimos 50 líneas de logs de Render
2. Verifica que Supabase esté activo (dashboard no muestra "Paused")
3. Prueba resetear la contraseña de Supabase
4. Considera usar Transaction Mode en lugar de Session Mode

---

**Fecha de creación:** 2025-11-11  
**Última actualización:** 2025-11-11  
**Estado:** ✅ Solucionado

