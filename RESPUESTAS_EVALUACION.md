# Evaluación Final - OrderFlow

## Ejercicio - Parte 4: Seguridad JWT

### P1. ¿Por qué SecurityConfig no está en domain o application?

SecurityConfig está en la capa de infraestructura porque se encarga de configurar Spring Security y la autenticación con JWT y Keycloak. Como estas herramientas pertenecen al framework y no a la lógica del negocio, no deberían estar en domain ni en application, ya que esas capas deben mantenerse independientes de tecnologías externas.

### P2. ¿Qué pasaría si un usuario envía un JWT válido pero sin ningún rol de Keycloak? ¿Podría acceder a GET /api/orders/{id}?

Sí. En este proyecto la regla para ese endpoint usa authenticated(), lo que significa que solo se verifica que el usuario tenga un JWT válido. Como no se pide un rol específico para consultar una orden, el usuario podría acceder aunque no tenga los roles ADMIN o USER.

### P3. ¿Qué función cumple KeycloakRoleConverter y qué sucedería si no existiera?

KeycloakRoleConverter toma los roles que vienen dentro del token de Keycloak y los adapta para que Spring Security pueda reconocerlos. Si no existiera, Spring no identificaría correctamente esos roles y las reglas que usan hasRole(...) podrían dejar de funcionar, provocando que se niegue el acceso aunque el usuario tenga un token válido.

### P4. Explica la diferencia entre 401 Unauthorized y 403 Forbidden en el contexto de este proyecto.

Un error 401 Unauthorized ocurre cuando el usuario no envía un token, el token ya expiró o no es válido, por lo que la aplicación no puede autenticarlo. En cambio, un 403 Forbidden significa que el usuario sí está autenticado, pero no tiene permisos para realizar esa acción. Por ejemplo, en este proyecto un usuario con un JWT válido pero sin el rol ADMIN recibiría un 403 al intentar crear una orden.