# App_kaiju

# Kaiju · Gestión y trazabilidad de inventario

Aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose** para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC, CITT San Bernardo). Corresponde a la **Evaluación Parcial 2 (encargo en parejas)**.

> Es un MVP académico. Todos los datos son **ficticios**; no se usan credenciales ni información real.

## Integrantes

- Aaron Hernandez
- Vicente Jaramillo

**Planificación (Trello):** [pegar aquí el enlace del tablero]

## Descripción del proyecto

Kaiju administra productos, materiales e insumos que se reciben, almacenan, usan o venden. Hoy el control de inventario se hace con registros manuales, lo que genera diferencias entre el stock registrado y el real, entradas y salidas sin anotar, y poca visibilidad de los productos que se están agotando.

La app permite conocer el stock disponible, registrar cada movimiento dejando trazabilidad y detectar a tiempo qué productos hay que reponer.

## Funcionalidades implementadas

- **Inicio:** resumen general con cantidad de productos, productos con stock bajo y lista de productos por reponer.
- **Inventario:** listado de productos con búsqueda por nombre o código y filtros por estado (Todos, Stock bajo, Agotado). Cada producto muestra su estado con color y etiqueta (Disponible, Stock bajo, Agotado).
- **Detalle de producto:** stock actual y mínimo, atributos opcionales (talla, color, modelo), precio e historial de movimientos del producto.
- **Registro de movimientos:** entradas, salidas y ajustes, con motivo obligatorio. El formulario valida cada campo y muestra mensajes de error con ícono.
- **Validaciones desacopladas de la interfaz:** las reglas viven en la carpeta `validacion/` (por ejemplo, no se puede sacar más stock del disponible).
- **Persistencia local:** productos y movimientos se guardan en el dispositivo (JSON en `SharedPreferences`) y se conservan al cerrar la app.
- **Ventas:** pantalla de indicadores comerciales (más y menos vendidos).
- **Recurso nativo – notificaciones:** aviso cuando un producto llega al stock mínimo. En Android 13 o superior se solicita el permiso en tiempo de ejecución.

## En desarrollo

- Escáner de códigos QR y de barras con la cámara (recurso nativo n.º 2).
- ViewModel (MVVM) para separar el estado de la interfaz.
- Formulario para crear y editar productos.
- Animaciones de transición entre pantallas.

## Estructura del proyecto

```
app/src/main/java/com/example/kaiju/
├── model/           Producto y Movimiento
├── data/            Datos de ejemplo y persistencia local
├── validacion/      Reglas de validación de movimientos
├── notificaciones/  Aviso de stock bajo
├── ui/              Pantallas (Compose) y navegación
│   └── theme/       Colores, tipografía y tema
└── MainActivity.kt
```

## Cómo ejecutar el proyecto

**Requisitos:** Android Studio actualizado, conexión a internet (la primera sincronización descarga dependencias) y un emulador o un teléfono Android con API 29 o superior.

1. Clona el repositorio:
   ```
   git clone https://github.com/Vicente1920/App_kaiju.git
   ```
2. Abre la carpeta en Android Studio (**File → Open**).
3. Espera a que termine la sincronización de Gradle (barra de progreso inferior).
4. Crea o elige un emulador en **Device Manager** (o conecta un teléfono con depuración USB).
5. Presiona **Run** (▶) para instalar y abrir la app.

**Pruebas unitarias:** clic derecho sobre la carpeta `app/src/test` → **Run Tests**.

**Para empezar con los datos de ejemplo:** en el emulador, borra los datos de la app (Info de la app → Almacenamiento → Borrar datos) o desinstálala.

## Reglas de negocio

- Un producto está en **stock bajo** cuando su stock es menor o igual a su stock mínimo.
- Un movimiento no puede dejar el stock en negativo.
- Todo movimiento queda registrado con fecha, tipo, cantidad y motivo.

## Tecnologías

Kotlin · Jetpack Compose · Material 3 · SharedPreferences (JSON) · Notificaciones de Android · Git y GitHub 
