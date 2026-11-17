# 🍽️ FitMealApp

Aplicación web para **planificar comidas semanales y generar automáticamente la lista de la compra**, desarrollada como proyecto final del ciclo formativo de grado superior en **Desarrollo de Aplicaciones Web (DAW)**.

Incluye autenticación con dos roles (`usuario` y `admin`), gestión de recetas, asignación de recetas a usuarios y fechas, y un panel de administración.

---

## ✨ Funcionalidades

- **Autenticación y autorización** con Spring Security y control de acceso por roles.
- **Alta de recetas** con nombre, descripción, instrucciones, imagen e ingredientes con cantidades añadidos dinámicamente desde el navegador.
- **Planificador**: asignar recetas a usuarios y fechas, y consultar las recetas asignadas al usuario autenticado.
- **Lista de la compra** generada a partir de las recetas planificadas, con los ingredientes formateados (`cantidad de ingrediente`).
- **Panel de administración**: crear y eliminar usuarios y recetas, y ver las asignaciones.
- **Interfaz responsive** con Bootstrap 5 y plantillas Thymeleaf.

---

## 🛠️ Tecnologías

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.4.2 (Spring MVC, Spring Security, Spring JDBC) |
| Seguridad | Spring Security + **BCrypt** para contraseñas |
| Plantillas | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Base de datos | MySQL 8 (conector JDBC 8.0.33) |
| Serialización | Jackson (ingredientes en formato JSON) |
| Frontend | Bootstrap 5, CSS propio, JavaScript |
| Build | Maven (wrapper incluido) |
| Despliegue | WAR para Apache Tomcat 10.1+ (Jakarta EE) |
| Monitoreo | Spring Boot Actuator |

---

## 📋 Requisitos previos

- **JDK 17** o superior
- **MySQL 8** en ejecución (puerto `3306` por defecto)
- **Apache Tomcat 10.1+** si se quiere desplegar como WAR (opcional; también funciona con el servidor embebido)

> ℹ️ Se usa Tomcat 10.1 porque el proyecto emplea el namespace `jakarta.*` (Spring Boot 3).

---

## 🚀 Instalación y puesta en marcha

### 1. Clonar el repositorio

```bash
git clone https://github.com/Icemangtr1990/FitMealApp.git
cd FitMealApp
```

### 2. Crear la base de datos

El archivo [`SQL.txt`](SQL.txt) contiene el esquema completo y datos de ejemplo (9 recetas y 2 usuarios).

```bash
mysql -u root -p --default-character-set=utf8mb4 -e "SOURCE C:/ruta/a/FitMealApp/SQL.txt"
```

> ⚠️ El script empieza con `DROP DATABASE IF EXISTS recetas_app;`. **Elimina cualquier base de datos previa con ese nombre**.
>
> ℹ️ Usa `SOURCE` (o `mysql --default-character-set=utf8mb4 < SQL.txt`) para importar. Redirigir el archivo desde PowerShell puede convertir los caracteres UTF-8 y dejar `?` en lugar de tildes y `ñ`. El `SQL.txt` ya crea la base de datos y las tablas con `utf8mb4`.

### 3. Configurar la conexión

Los datos están en `src/main/resources/application.properties` y se pueden
sobrescribir con variables de entorno (ideal para no subir credenciales a GitHub):

```properties
fitmeal.db.host=localhost
fitmeal.db.port=3306
fitmeal.db.name=recetas_app
fitmeal.db.username=root
fitmeal.db.password=1234
```

```bash
set FITMEAL_DB_USER=mi_usuario
set FITMEAL_DB_PASSWORD=mi_password
```

Variables disponibles: `FITMEAL_DB_HOST`, `FITMEAL_DB_PORT`, `FITMEAL_DB_NAME`,
`FITMEAL_DB_USER` y `FITMEAL_DB_PASSWORD`.

### 4. Ejecutar la aplicación

Con el wrapper de Maven (Windows):

```bash
.\mvnw.cmd spring-boot:run
```

O desde un IDE con soporte Spring Boot: ejecutar `FitMealAppApplication.java`.

La aplicación estará disponible en **http://localhost:8080/index**.

### 5. Usuarios de ejemplo

| Usuario | Email | Contraseña | Rol |
|---------|-------|-----------|-----|
| `admin` | admin@admin.es | `1234` | `admin` |
| `pepe` | pepe@gmail.com | `1234` | `usuario` |

> 🔐 En el `SQL.txt` las contraseñas aparecen en texto plano por ser un proyecto académico. **En el primer arranque se migran automáticamente a hashes BCrypt** y se actualizan en la base de datos. Los usuarios creados desde la interfaz ya se guardan cifrados.

### 6. Generar el WAR (opcional)

```bash
.\mvnw.cmd clean package
```

El archivo se genera en `target/FitMealApp-0.0.1-SNAPSHOT.war` y se puede desplegar en Tomcat 10.1+.

---

## 🗄️ Modelo de datos

La base de datos `recetas_app` consta de tres tablas:

```
Usuarios (1) ──< (N) Recetas
Usuarios (1) ──< (N) Planificador >── (1) Recetas

La relación N:M entre Usuarios y Recetas se resuelve con la tabla Planificador.
```

- **`Usuarios`**: `id_usuario`, `nombre`, `email` (único), `password`, `rol` (`usuario` | `admin`).
- **`Recetas`**: `id_receta`, `nombre_receta`, `descripcion`, `instrucciones`, `ingredientes` (JSON), `imagen`, `id_usuario` (FK, `ON DELETE CASCADE`).
- **`Planificador`**: `id_planificador`, `id_usuario` (FK), `fecha`, `id_receta` (FK). Actúa como tabla intermedia entre usuarios y recetas.

Los ingredientes se almacenan como JSON:

```json
[
  { "cantidad": "200g", "ingrediente": "pasta" },
  { "cantidad": "50g", "ingrediente": "queso parmesano" }
]
```

---

## 🧭 Rutas principales

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/index`, `/` | Portada y navegación | Público |
| GET | `/loggin` | Vista de inicio de sesión | Público |
| POST | `/logginprocess` | Procesamiento del login (Spring Security) | Público |
| POST | `/logout` | Cerrar sesión | Autenticado |
| GET | `/recetas` | Listado de recetas y asignaciones | Autenticado |
| POST | `/agregarReceta` | Alta de receta (el autor es el usuario autenticado) | Admin |
| POST | `/admin/eliminar-receta` | Eliminar receta | Admin |
| POST | `/asignarReceta` | Asignar receta a usuario y fecha | Autenticado |
| POST | `/eliminarAsignacion` | Quitar asignación | Autenticado |
| GET | `/planificador` | Recetas planificadas del usuario | Autenticado |
| GET | `/compras` | Lista de la compra | Autenticado |
| GET | `/crearUsuario` | Formulario de alta de usuario | Admin |
| POST | `/admin/crear-usuario` | Crear usuario | Admin |
| GET | `/admin/listar-usuarios` | Listado de usuarios | Admin |
| POST | `/admin/eliminar-usuario/{id}` | Eliminar usuario | Admin |

---

## 📁 Estructura del proyecto

```
src/main/java/es/dsw/
├── app/
│   ├── FitMealAppApplication.java   # Arranque de Spring Boot
│   ├── SecurityConfiguration.java   # Reglas de seguridad y usuarios en memoria
│   └── ServletInitializer.java      # Arranque en contenedor WAR
├── connections/
│   └── MySqlConnection.java         # Capa de conexión JDBC
├── controllers/
│   └── MainController.java          # Controladores MVC (13 rutas)
└── models/
    ├── Users.java                   # Usuario + consultas
    ├── Recetas.java                 # Receta + consultas + parseo JSON
    └── Planificador.java            # Asignaciones y consultas agregadas

src/main/resources/
├── application.properties
├── static/
│   ├── css/                         # estilos propios
│   └── images/                      # imágenes del carrusel
└── templates/                       # vistas Thymeleaf (7 plantillas)

src/test/java/                       # test de carga de contexto
SQL.txt                              # esquema y datos de ejemplo
pom.xml                              # dependencias Maven
```

---

## ✅ Decisiones técnicas destacadas

- **Consultas parametrizadas** con `PreparedStatement` en las operaciones sensibles para evitar inyección SQL.
- **Datos semiestructurados**: los ingredientes se guardan como JSON y se transforman a texto legible con Jackson.
- **Consultas agregadas**: `GROUP_CONCAT` para mostrar todos los usuarios asignados a cada receta en una sola consulta.
- **Integridad referencial**: claves foráneas y `ON DELETE CASCADE` para mantener consistente la base de datos.
- **Control de acceso en dos niveles**: reglas en `SecurityConfiguration` y directivas `sec:authorize` en las plantillas.
- **Seguridad**: contraseñas con **BCrypt** (con migración automática de las del `SQL.txt`), configuración de BD externalizable, **PreparedStatement** en todas las consultas y mutaciones por **POST** con token CSRF de Thymeleaf.
- **Autenticación contra BD**: `DbUserDetailsService` consulta MySQL en cada login, así que los usuarios creados o eliminados desde la interfaz surten efecto **sin reiniciar** la aplicación.

---

## 🔧 Mejoras previstas

Aspectos que se abordarían en un entorno profesional:

- Separar entidades, repositorios y servicios (o migrar a **Spring Data JPA**).
- Sustituir `DriverManager` por un **pool de conexiones** (HikariCP, incluido en Spring Boot).
- Añadir **tests de integración** con H2 para las capas de datos y controladores.
- Agrupar ingredientes repetidos en la lista de la compra y filtrarla por rango de fechas.

---

## 👤 Autor

**Simeón Ivanov Stoev**  
Desarrollador Web Junior · CFGS Desarrollo de Aplicaciones Web · CFGS ASIR  
[GitHub](https://github.com/Icemangtr1990) · [LinkedIn](https://www.linkedin.com/in/sime%C3%B3n-ivanov-steov-91034862)

Proyecto realizado en 2026 como trabajo final de ciclo.
