# Kaiju — Gestión y trazabilidad de inventario

Aplicación móvil Android para consultar el inventario y registrar movimientos de productos. Este proyecto es un avance del Examen Final Transversal (EFT) e integra una aplicación móvil, un microservicio propio y una API externa.

## Integrantes

- Aaron Hernandez
- Vicente Jaramillo

## Contexto y problema

En un negocio pequeño, controlar el inventario manualmente puede producir diferencias entre el stock registrado y el disponible. También dificulta detectar productos que necesitan reposición y mantener un historial claro de las entradas y salidas.

Kaiju permite consultar productos desde una aplicación móvil, registrar movimientos con su motivo y mantener la trazabilidad del inventario en una base de datos central.

## Funcionalidades

- Consulta y búsqueda de productos del inventario.
- Visualización del stock y stock mínimo.
- Detalle de producto con atributos como talla, color y modelo cuando corresponda.
- Registro de productos.
- Registro de movimientos de tipo entrada, salida o ajuste, con motivo.
- Historial de movimientos.
- Resumen diario con visualización gráfica.
- Copia local de productos y movimientos mediante Room para consultar los últimos datos sincronizados.

## Arquitectura

```text
Aplicación Android
  ├── Retrofit y Gson → API REST propia en Spring Boot
  │                       └── Spring Data JPA / Hibernate → PostgreSQL
  ├── Room → copia local de productos y movimientos
  └── Retrofit → QuickChart para generar gráficos del resumen
```

La aplicación Android consume la API REST de Spring Boot. El backend valida las solicitudes y persiste productos y movimientos en PostgreSQL. Room mantiene una copia local de la información sincronizada. QuickChart se usa para generar el gráfico del resumen diario.

## Tecnologías

**Aplicación móvil:** Kotlin, Jetpack Compose, Material 3, Retrofit, Gson y Room. Requiere Android 10 (API 29) o superior.

**Microservicio:** Java 21, Spring Boot, Spring Web MVC, Spring Data JPA, Hibernate, Bean Validation y Maven.

**Persistencia y servicios externos:** PostgreSQL para los datos del proyecto, H2 para las pruebas del backend y [QuickChart](https://quickchart.io/ ) para generar gráficos.

**Despliegue de prueba:** AWS EC2. PM2 administra el microservicio Spring Boot; Node.js se conserva como respaldo durante la validación de la migración.

## API REST

La API propia usa el prefijo `/api` y expone estos endpoints:

- `GET /api/health`: comprueba el estado del servicio.
- `GET /api/productos`: lista los productos.
- `GET /api/productos/{codigo}`: consulta un producto por código.
- `POST /api/productos`: registra un producto.
- `GET /api/movimientos`: lista los movimientos.
- `POST /api/movimientos`: registra un movimiento y actualiza el stock.

Ejemplo de solicitud para registrar un producto:

```json
{
  "codigo": "KJ-100",
  "nombre": "Polera de ejemplo",
  "categoria": "Ropa",
  "precio": 12990,
  "stock": 10,
  "stockMinimo": 3,
  "talla": "M",
  "color": "Negro",
  "modelo": "Clásica"
}
```

Ejemplo de solicitud para registrar un movimiento:

```json
{
  "codigoProducto": "KJ-100",
  "tipo": "Salida",
  "cantidad": 1,
  "motivo": "Venta"
}
```

Los tipos de movimiento aceptados son `Entrada`, `Salida` y `Ajuste`. La API valida los datos recibidos, comprueba que el producto exista y evita que una salida deje el stock en un valor negativo.

## Base de datos y configuración

PostgreSQL almacena los productos y sus movimientos. Spring Boot valida el esquema existente al iniciar; la aplicación no crea ni modifica automáticamente las tablas.

El backend usa estas variables de entorno:

- `PORT`: puerto HTTP; por defecto, `3001`.
- `DB_HOST`: dirección del servidor PostgreSQL.
- `DB_PORT`: puerto de PostgreSQL; por defecto, `5432`.
- `DB_NAME`: nombre de la base de datos.
- `DB_USER`: usuario de la base de datos.
- `DB_PASSWORD`: contraseña de la base de datos.

No guardes credenciales reales en el repositorio. No subas archivos `.env`, contraseñas ni la clave privada `.jks` a GitHub.

## Ejecución y pruebas

### Aplicación Android

Clona la rama de migración:

```bash
git clone --branch migracion-springboot --single-branch https://github.com/Vicente1920/App_kaiju.git
```

Abre la carpeta del proyecto en Android Studio y espera que termine la sincronización de Gradle. Luego conecta un teléfono Android o inicia un emulador.

Desde PowerShell, en la carpeta raíz del proyecto, ejecuta las pruebas:

```powershell
.\gradlew.bat test
```

La dirección de la API usada por Android se configura en:

```text
app/src/main/java/com/example/kaiju/red/ClienteRed.kt
```

Para usar otro servidor, actualiza allí el host y el puerto de la API.

### Microservicio Spring Boot

Se requiere Java 21. Desde PowerShell, abre la carpeta `api-spring\kaiju-api-spring` y ejecuta las pruebas:

```powershell
.\mvnw.cmd test
```

Para generar el paquete ejecutable:

```powershell
.\mvnw.cmd package
```

El archivo `.jar` se genera en:

```text
target/kaiju-api-spring-0.0.1-SNAPSHOT.jar
```

Para iniciar Spring Boot localmente, configura primero las variables de entorno con los datos de una base PostgreSQL disponible. Ejemplo en PowerShell:

```powershell
$env:PORT = "3001"
$env:DB_HOST = "127.0.0.1"
$env:DB_PORT = "5432"
$env:DB_NAME = "kaiju_db"
$env:DB_USER = "<usuario-postgres>"
$env:DB_PASSWORD = "<contraseña-postgres>"
.\mvnw.cmd spring-boot:run
```

Reemplaza los valores entre `< >` por los de tu propio entorno. No compartas ni publiques credenciales.

## APK firmada

La APK release se genera desde Android Studio con **Build → Generate Signed Bundle / APK → APK → release**.

Para que la APK nueva pueda actualizar una versión anterior instalada, se debe firmar con el mismo archivo `.jks`, alias y credenciales de firma. El archivo generado normalmente queda en:

```text
app/release/app-release.apk
```

La clave `.jks` y sus contraseñas son privadas y no deben compartirse ni subirse al repositorio.

## Despliegue y seguridad

En el entorno de prueba, Spring Boot escucha en el puerto `3001` y Node.js permanece en `3000` como respaldo. El acceso al puerto de la API se restringe en el Security Group de AWS a las IP autorizadas para las pruebas o demostraciones.

El puerto `5432` de PostgreSQL no debe exponerse a Internet. El despliegue académico usa HTTP y no incluye autenticación de usuarios; no debe considerarse una configuración de producción.

## Validación realizada

- Las pruebas del backend Spring Boot finalizaron correctamente con Maven.
- Las pruebas de Android finalizaron correctamente con Gradle.
- Los endpoints de salud de Spring Boot y Node.js respondieron correctamente.
- Se comprobó la lectura de productos y movimientos desde Spring Boot.
- La APK release firmada se instaló y mostró el inventario conectándose a Spring Boot.

## Tablero de trabajo

El seguimiento de tareas del proyecto está en Trello: [Tablero Kaiju](https://trello.com/b/PKMUumSI/appkaiju ).

## Repositorio

Código fuente: [App_kaiju — rama migracion-springboot](https://github.com/Vicente1920/App_kaiju/tree/migracion-springboot ).