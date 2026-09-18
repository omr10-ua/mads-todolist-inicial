# Documentación Técnica: Práctica 2 - MADS Todolist

## 1. Introducción y Arquitectura General

Esta documentación técnica detalla la arquitectura, diseño e implementación de las nuevas características añadidas en la Práctica 2 de la aplicación **MADS Todolist**. El sistema está construido bajo el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** utilizando **Spring Boot (v2.7.14)**, **Spring Data JPA** para la persistencia de datos, **Thymeleaf** como motor de plantillas y **Bootstrap 5.3.3** para la capa visual.

El objetivo principal de esta iteración ha sido la implementación de un rol de usuario administrador, la gestión y listado de usuarios registrados, la protección de rutas administrativas y un sistema de bloqueo de cuentas.

---

## 2. Modelo de Datos y Persistencia

Se han extendido las entidades existentes para soportar los nuevos requisitos de administración y seguridad.

### Modificaciones en la entidad `Usuario`
Se han incorporado dos nuevos atributos booleanos a la entidad mapeada en la tabla `usuarios`:
*   `admin`: Define si el usuario posee privilegios de administración (por defecto `false`).
*   `bloqueado`: Indica si el acceso del usuario ha sido revocado por un administrador (por defecto `false`).

### Repositorios (`UsuarioRepository`)
La interfaz extiende de `CrudRepository` y se han añadido métodos especializados para optimizar consultas de negocio:
*   `Optional<Usuario> findByEmail(String email)`: Utilizado para la autenticación y validación de duplicados.
*   `boolean existsByAdmin(Boolean admin)`: Comprueba de forma eficiente si ya existe un administrador registrado en el sistema, devolviendo un valor booleano sin necesidad de recuperar la entidad completa.

---

## 3. Lógica de Negocio (`UsuarioService`)

La capa de servicio centraliza las reglas de negocio y transaccionalidad de la aplicación mediante la anotación `@Transactional`.

*   **Ampliación de Estados de Login (`LoginStatus`)**: Se ha incorporado el estado `USER_BLOCKED` al enum de control de acceso. Durante el proceso de validación, el sistema evalúa la existencia del usuario y su estado de bloqueo antes de comprobar la validez de la contraseña.
*   **Restricción de Administrador Único**: Mediante el método `existsAdmin()`, el sistema valida si ya se ha configurado un administrador previo.
*   **Gestión de Bloqueos (`cambiarBloqueo`)**: Método transaccional que permite alternar el estado del atributo `bloqueado` de un usuario específico a partir de su identificador.

---

## 4. Controladores y Enrutamiento

La lógica de control se divide principalmente entre `LoginController` (gestión de sesiones, autenticación y registro) y `UsuarioController` (paneles de administración y listados).

*   **Control de Registro Condicional**: Al solicitar la vista de registro (`/registro`), el controlador evalúa si ya existe un administrador. Esta información se inyecta en el modelo (`adminExists`) para condicionar la interfaz visual.
*   **Redirección basada en Roles (`loginSubmit`)**: Tras una autenticación exitosa, el sistema comprueba la propiedad `admin`. Si el usuario es administrador, es redirigido automáticamente al panel de gestión (`/registrados`); de lo contrario, accede a su gestor de tareas habitual.

---

## 5. Seguridad, Autorización y Excepciones

Para garantizar que las rutas de administración (`/registrados` y `/registrados/{id}`) permanezcan inaccesibles para usuarios estándar, se ha implementado una capa de validación explícita en los controladores:

1.  Se valida la existencia de una sesión activa mediante `ManagerUserSession`.
2.  Se recupera el usuario logueado y se verifica la condición de administrador (`admin == true`).
3.  En caso de incumplimiento, se lanza una excepción personalizada (`UnauthorizedException`) anotada con `@ResponseStatus(HttpStatus.UNAUTHORIZED)`, la cual devuelve un código de error HTTP "No autorizado" junto a un mensaje descriptivo de restricción de permisos.

---

## 6. Interfaz de Usuario y Vistas (Thymeleaf)

La capa de presentación reutiliza componentes modulares a través de fragmentos de Thymeleaf (`fragments.html`), garantizando una experiencia visual homogénea con Bootstrap.

*   **Renderizado Condicional (`th:if`)**: Se utiliza para ocultar dinámicamente el checkbox de registro de administrador en el formulario (`formRegistro.html`) una vez que el puesto ha sido ocupado.
*   **Gestión de Navbar Dinámico**: La barra de navegación superior comprueba la existencia de la variable de sesión `usuario` para mostrar u ocultar opciones de forma segura, previniendo errores de evaluación en pantallas públicas como el login.
*   **Panel de Administración y Acciones**: La vista de listado (`registrados.html`) integra una tabla interactiva que muestra identificadores y correos, incorporando enlaces a la vista de detalles y botones de acción dinámica (`Bloquear` / `Desbloquear`) condicionados por clases de color adaptativas de Bootstrap (`btn-success` / `btn-warning`).