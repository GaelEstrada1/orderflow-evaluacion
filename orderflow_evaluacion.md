# Evaluación Final - OrderFlow

**Proyecto:** OrderFlow  
**Modalidad:** Individual  
**Duración sugerida:** 4 horas  
**Fecha:** Junio de 2026

---

## Objetivo

Demostrar que el alumno comprende y es capaz de aplicar de forma autónoma los conceptos de **Arquitectura Hexagonal**, **Spring Boot**, **testing**, **seguridad con JWT** y **despliegue en la nube**, extendiendo un proyecto real con un nuevo caso de uso completo que atraviese todas las capas de la arquitectura.

---

## Contexto del proyecto

OrderFlow es una API REST que gestiona el ciclo de vida de órdenes de compra. Actualmente implementa cuatro casos de uso:

| Caso de uso | Puerto de entrada | Servicio | Endpoint |
|---|---|---|---|
| Crear orden | `CreateOrderUseCase` | `CreateOrderService` | `POST /api/orders` |
| Añadir ítem | `AddItemToOrderUseCase` | `AddItemToOrderService` | `POST /api/orders/{id}/items` |
| Pagar orden | `PayOrderUseCase` | `PayOrderService` | `POST /api/orders/{id}/pay` |
| Cancelar orden | `CancelOrderUseCase` | `CancelOrderService` | `POST /api/orders/{id}/cancel` |

La arquitectura está organizada en cuatro módulos Maven:

```
domain/           → Entidades, Value Objects, excepciones (sin dependencias externas)
application/      → Puertos (in/out), servicios de aplicación, commands
infraestructure/  → Adaptadores REST (in), JPA (out), seguridad, DTOs
boot/             → Configuración Spring, ensamblaje, tests E2E e integración
```

---

## Ejercicio - Parte 1: Nuevo caso de uso `GetOrderById` (30%)

El alumno debe implementar el caso de uso **consultar una orden por su ID** (`GET /api/orders/{id}`), creando todos los archivos necesarios que atraviesen las capas de la arquitectura hexagonal.

### Archivos a crear/modificar

El alumno debe identificar qué archivos necesita crear y en qué módulo ubicarlos. Como guía mínima (no exhaustiva), se espera:

#### Módulo `application` (capa de aplicación)

| Archivo | Ubicación | Descripción |
|---|---|---|
| `GetOrderByIdUseCase.java` | `application/.../port/in/` | Puerto de entrada (interfaz) del nuevo caso de uso |
| `GetOrderByIdService.java` | `application/.../service/` | Servicio de aplicación que implementa el puerto |

#### Módulo `infraestructure` (capa de infraestructura)

| Archivo | Ubicación | Descripción |
|---|---|---|
| `OrderController.java` | `infraestructure/.../adapter/in/rest/` | **Modificar**: añadir endpoint `@GetMapping("/{id}")` |

#### Módulo `boot` (ensamblaje)

| Archivo | Ubicación | Descripción |
|---|---|---|
| `UseCaseConfig.java` | `boot/.../config/` | **Modificar**: registrar el bean del nuevo caso de uso |

### Criterios de evaluación - Parte 1

| Criterio | Puntos | Detalle |
|---|---|---|
| Puerto de entrada correcto | 5 | Interfaz en `port/in/` con la firma adecuada. Retorna `Order` |
| Servicio de aplicación | 8 | Implementa el puerto, usa `FindOrderByIdPort`, lanza `OrderNotFoundException` si no existe |
| Endpoint REST | 7 | `@GetMapping("/{id}")`, devuelve `200 OK` con `OrderResponse`, o `404` si no existe |
| Registro del bean | 3 | Nuevo `@Bean` en `UseCaseConfig` con las dependencias correctas |
| Inyección en el controller | 3 | El controller recibe el nuevo use case por constructor |
| Respeta la arquitectura | 4 | No hay dependencias invertidas; el dominio no importa nada de infraestructura ni de Spring |

> **Nota:** El alumno tiene la libertad de elegir si crea un command/query object o si pasa directamente el `String orderId` al servicio. Ambas opciones son válidas si se justifican.

---

## Ejercicio - Parte 2: Nuevo adaptador externo (30%)

El alumno debe integrar un **nuevo adaptador de salida** que represente un servicio externo. Debe elegir **una** de las siguientes opciones:

### Opción A - Servicio de Notificaciones

Implementar un puerto de salida `NotificationService` que se invoque cuando una orden cambie de estado (por ejemplo, al pagarse o cancelarse).

| Archivo | Módulo | Descripción |
|---|---|---|
| `NotificationService.java` | `application/.../port/out/` | Puerto de salida (interfaz): `void notifyOrderStatusChange(String orderId, OrderStatus status)` |
| `SimulatedNotificationService.java` | `infraestructure/.../adapter/out/notification/` | Adaptador simulado que loguea por consola (`@Component`) |
| Servicio existente | `application/.../service/` | **Modificar** `PayOrderService` y/o `CancelOrderService` para invocar el nuevo puerto |
| `UseCaseConfig.java` | `boot/.../config/` | **Modificar**: inyectar el nuevo puerto en los servicios que lo usen |

### Opción B - Servicio de Auditoría / Logging externo

Implementar un puerto de salida `AuditService` que registre cada operación realizada sobre una orden.

| Archivo | Módulo | Descripción |
|---|---|---|
| `AuditService.java` | `application/.../port/out/` | Puerto de salida (interfaz): `void logEvent(String orderId, String eventType, String details)` |
| `SimulatedAuditService.java` | `infraestructure/.../adapter/out/audit/` | Adaptador simulado que loguea por consola (`@Component`) |
| Servicio(s) existente(s) | `application/.../service/` | **Modificar** al menos dos servicios para registrar eventos |
| `UseCaseConfig.java` | `boot/.../config/` | **Modificar**: inyectar el nuevo puerto |

### Criterios de evaluación - Parte 2

| Criterio | Puntos | Detalle |
|---|---|---|
| Puerto de salida correcto | 10 | Interfaz definida en `application/port/out/`, sin dependencias de infraestructura |
| Adaptador simulado | 8 | Clase en `infraestructure/adapter/out/`, anotada con `@Component`, usa `Logger` (SLF4J) |
| Integración en servicios | 7 | Al menos un servicio de aplicación invoca el nuevo puerto correctamente |
| Cableado en `UseCaseConfig` | 5 | El bean se inyecta correctamente y la aplicación arranca sin errores |

> **Importante:** El adaptador puede ser **simulado**. No se requiere una implementación real con un servicio externo.

---

## Ejercicio - Parte 3: Tests (25%)

El alumno debe escribir tests que validen el nuevo código creado en las Partes 1 y 2.

### Tests mínimos requeridos

| Test | Tipo | Módulo | Descripción |
|---|---|---|---|
| `GetOrderByIdServiceTest.java` | Unitario (Mockito) | `application` | Test del servicio: caso feliz (orden encontrada) y caso de error (`OrderNotFoundException`) |
| Test del adaptador simulado | Unitario | `infraestructure` | Test del nuevo adaptador externo (Parte 2): verificar que no lanza excepciones |
| Test del endpoint GET | Unitario o `@WebMvcTest` | `infraestructure` | Verificar que `GET /api/orders/{id}` devuelve `200` con datos correctos y `404` cuando no existe |

### Criterios de evaluación - Parte 3

| Criterio | Puntos | Detalle |
|---|--------|---|
| Test unitario del servicio | 10     | Usa `@ExtendWith(MockitoExtension.class)`, mockea los puertos de salida, verifica caso feliz y caso de error |
| Test del adaptador externo | 5      | Verifica el comportamiento del adaptador simulado de la Parte 2 |
| Test del endpoint REST | 6      | Verifica status HTTP, cuerpo de respuesta JSON y manejo de errores |
| Buenas prácticas | 4      | Patrón AAA (Arrange-Act-Assert), nombres descriptivos, sin lógica en los asserts |

---

## Ejercicio - Parte 4: Seguridad JWT (15%)

El alumno debe proteger el nuevo endpoint `GET /api/orders/{id}` con las reglas de seguridad del proyecto.

### Tareas

1. **Verificar** que la regla existente en `SecurityConfig.java` cubre el nuevo endpoint:
   ```java
   .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()
   ```
2. **Responder con sus propias palabras** las siguientes preguntas teóricas (máximo 3-4 líneas cada una):

### Preguntas teóricas

| # | Pregunta |
|---|---|
| P1 | ¿Por qué `SecurityConfig` no está en `domain` o `application`? |
| P2 | ¿Qué pasaría si un usuario envía un JWT válido pero sin ningún rol de Keycloak? ¿Podría acceder a `GET /api/orders/{id}`? Justifica tu respuesta con la configuración actual. |
| P3 | ¿Qué función cumple `KeycloakRoleConverter` y qué sucedería si no existiera? |
| P4 | Explica la diferencia entre `401 Unauthorized` y `403 Forbidden` en el contexto de este proyecto. Da un ejemplo concreto de cuándo ocurre cada uno. |

### Criterios de evaluación - Parte 4

| Criterio | Puntos | Detalle |
|---|---|---|
| Endpoint protegido correctamente | 5 | El endpoint requiere autenticación. El alumno identifica y explica la regla que aplica |
| Preguntas teóricas P1-P2 | 5 | Respuestas correctas que demuestran comprensión de la arquitectura y la seguridad |
| Preguntas teóricas P3-P4 | 5 | Respuestas correctas sobre el flujo JWT y los códigos HTTP de seguridad |

---

## Entregables

El alumno debe entregar:

1. **Repositorio Git (o fork)** con todos los cambios en una rama `feature/evaluacion-final` (o similar).
2. **Todos los tests pasan**: ejecutar `mvn clean verify` sin errores.
3.  **Commits atómicos** con mensajes descriptivos, no un solo commit con todo). Pueden crear un commit por cada parte de la evaluación.
4. **Respuestas teóricas** de la Parte 4 en un archivo MD o PDF.
5. **Todos los tests pasan**: ejecutar `mvn clean verify` sin errores.
6. Se debe enviar el nombre del repo/fork a `cursos@mitocodenetwork.com` Asunto: Trabajo final curso Arq. Clean y Hexagonal, Nombre de estudiante (hasta el 31 de julio de 2026).
