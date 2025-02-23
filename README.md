# Challenge App

Este es un servicio de ejemplo desplegado usando Docker y Spring Boot. A continuación se describen los pasos para desplegar el servicio localmente y probar sus endpoints.

## Desplegar el servicio localmente

Para desplegar la aplicación localmente utilizando Docker, sigue estos pasos:

### Prerrequisitos

Asegúrate de tener las siguientes herramientas instaladas:

- [Docker](https://www.docker.com/get-started): Para ejecutar contenedores Docker.
- [Docker Compose](https://docs.docker.com/compose/): Para definir y ejecutar aplicaciones Docker multi-contenedor.
- [JDK 21 o superior](https://adoptium.ne.net/) si deseas construir la aplicación localmente.

### Pasos para desplegar

1. **Clona el repositorio:**:
```bash
    git clone https://github.com/cbarrosc/Challenge.git
    cd challenge
```
2. **Verifica la imagen de Docker:** La imagen de la aplicación está disponible en [Docker Hub](https://hub.docker.com/repository/docker/cbarrosc/challenge-app/general). 
No es necesario construirla, pero si deseas hacerlo localmente puedes ejecutar:
```bash
  ./gradlew bootBuildImage --imageName=cbarrosc/challenge-app:x.x.x
```

Esto creará la imagen cbarrosc/challenge-app:x.x.x que será utilizada en Docker Compose.

3. **Verifica Mockserver: ** Asegúrate de que el directorio mockserver y el archivo [mockserver.json](https://github.com/cbarrosc/Challenge/blob/master/mockserver/mockserver.json) existan:
El directorio mockserver debe contener el archivo mockserver.json. Si no existe, crea el archivo en la ubicación correspondiente.

4. **Inicia los contenedores usando Docker Compose:** Asegúrate de que Docker esté corriendo en tu máquina. 
Luego ejecuta el siguiente comando para iniciar los contenedores:
```bash
  docker-compose up
```
Esto arrancará los siguientes contenedores:

* PostgreSQL: Base de datos relacional.
* Redis: Caché.
* MockServer: Simulador de respuestas para pruebas.
* App La aplicacion del servicio en Spring Boot.

5. **Verifica que los contenedores estén corriendo:** Ejecuta el siguiente comando para asegurarte de que todos los servicios estén activos:
```bash
  docker ps
```
Deberías ver algo similar a esto:

        CONTAINER ID   IMAGE                                COMMAND                  CREATED         STATUS         PORTS                               NAMES
        abcdef123456   cbarrosc/challenge-app:1.0.2         "/bin/sh -c 'java -ja…"   2 seconds ago   Up 1 second    0.0.0.0:8080->8080/tcp              challenge-app
        123456abcdef   redis:latest                         "docker-entrypoint.s…"    2 seconds ago   Up 1 second    0.0.0.0:6379->6379/tcp              redis
        654321abcdef   postgres:latest                      "docker-entrypoint.s…"    2 seconds ago   Up 1 second    0.0.0.0:5432->5432/tcp              postgres

## Endpoints disponibles
Una vez que los contenedores estén corriendo, puedes acceder a los siguientes endpoints de la aplicación:
1. **Swagger UI:**:
* URL: http://localhost:8080/swagger-ui.html
* Puedes acceder a esta URL para explorar la API y probar sus endpoints interactuando con Swagger.

## Detener los contenedores
Cuando hayas terminado, puedes detener todos los contenedores ejecutando:
```bash
  docker-compose down
```

# Análisis técnico

Este proyecto es un servicio desplegado con Docker y Spring Boot que incluye varias decisiones técnicas clave para 
mejorar el rendimiento, la escalabilidad y la mantenibilidad del sistema. A continuación, se detalla un análisis sobre 
las decisiones tomadas durante el desarrollo del servicio.

## Análisis de decisiones técnicas

### Uso de WebFlux para programación reactiva

Se ha optado por usar **Spring WebFlux** para implementar la programación reactiva en lugar de la programación
tradicional basada en hilos. Esto permite manejar un gran número de solicitudes concurrentes de manera más eficiente.
La ventaja principal es la **no bloqueo** de recursos, lo que mejora la capacidad de respuesta del sistema y reduce 
la sobrecarga del servidor cuando se manejan solicitudes largas o I/O intensivo, como consultas a bases de datos o 
servicios externos.

### Uso de Redis como cache

Se ha integrado **Redis** como sistema de almacenamiento en caché para manejar las solicitudes de datos repetidos de
manera más eficiente y soportar múltiples instancias de la aplicación. Esto es especialmente útil cuando se tiene un
servicio con una alta cantidad de usuarios concurrentes, ya que Redis almacena en memoria los resultados de las 
consultas para servir respuestas rápidas sin tener que acceder repetidamente a la base de datos.

La decisión de usar Redis también facilita el manejo de escalabilidad horizontal, ya que permite que varias réplicas
de la aplicación compartan el mismo cache distribuido, lo que asegura que el rendimiento no se vea afectado incluso 
con un mayor número de instancias.

### Uso de Redis para Rate Limiting

Inicialmente, se consideró utilizar **Resilience4j** para implementar el **Rate Limiting**, ya que ofrece una 
solución eficiente para limitar el número de solicitudes a la API. Sin embargo, se descartó esta opción para evitar el
manejo en memoria del estado del rate limiting, lo cual podría no ser adecuado para una arquitectura con múltiples
instancias de la aplicación.

En lugar de Resilience4j, se optó por usar **Redis** como sistema de almacenamiento distribuido para gestionar el

**Rate Limiting**. Esta solución permite que el límite de solicitudes sea mantenido en un sistema centralizado, 
de modo que no importa cuántas réplicas de la aplicación estén ejecutándose, todas compartirán el mismo estado del
rate limiter a través de Redis.

El funcionamiento de este enfoque es simple: Redis se utiliza para contar las solicitudes realizadas por cada usuario 
o cliente en un periodo determinado. Cada vez que un usuario realiza una solicitud, se incrementa un contador en 
Redis asociado a una clave única (que podría ser el ID del usuario o la IP, por ejemplo). Si el número de solicitudes 
excede el límite establecido, la solicitud es rechazada. Además, Redis permite establecer una expiración en las 
claves, lo que garantiza que el contador se reinicie después de un periodo de tiempo determinado.

Esta solución tiene la ventaja de ser **escalable y distribuida**, ya que Redis puede ser compartido entre múltiples
instancias de la aplicación sin necesidad de manejar el estado en memoria en cada una de ellas. De esta forma, se 
asegura que el rate limiting sea coherente y eficaz incluso en un entorno con múltiples réplicas de la aplicación.

**Idealmente**, preferiría extraer estas validaciones de rate limiting y otras lógicas de control de tráfico de la 
aplicación, y manejarlas de forma centralizada en un **API Gateway**. Esto permitiría delegar estas responsabilidades
fuera del servicio de backend, ofreciendo una solución más flexible, desacoplada y escalable, además de facilitar 
el mantenimiento y la implementación de políticas de control de tráfico a nivel de arquitectura.


### Uso de Flyway para scripts de base de datos

Para la gestión de las migraciones de la base de datos se ha utilizado **Flyway**.
Flyway es una herramienta para gestionar el versionado de esquemas de base de datos de forma controlada y reproducible.
A través de Flyway, se puede asegurar que la base de datos esté siempre sincronizada con el código de la aplicación, 
facilitando la automatización de cambios y evitando errores humanos.

La integración con Spring Boot permite que Flyway se ejecute automáticamente al arrancar la aplicación, aplicando las 
migraciones necesarias para mantener la base de datos actualizada.

**Idealmente**, preferiría mover la responsabilidad de la escritura directa en la base de datos a una capa de 
**anticorruption**. Esto se haría a través de un intermediario que se encargue de la adaptación de los modelos 
y de evitar que la lógica del dominio sea directamente influenciada por las estructuras y detalles de la base de datos.
De esta forma, se logra un desacoplamiento más claro entre la capa de persistencia y la lógica de negocio, mejorando
la mantenibilidad y la flexibilidad del sistema.


### Registro de historial asincrónico usando WebFilters

Para el registro del historial de las solicitudes realizadas a la API, se ha optado por hacerlo de forma 
**asincrónica** usando **WebFilters**. Este enfoque evita la necesidad de modificar los endpoints directamente, 
ya que el filtro puede interceptar las solicitudes y procesar el almacenamiento en la base de datos sin bloquear 
el flujo principal de la ejecución.

El uso de WebFilters para registrar el historial ofrece una ventaja significativa en términos de
**separación de preocupaciones** y **rendimiento**. No se requiere agregar lógica adicional a los controladores o
servicios, lo que mantiene el código limpio y enfocado en la lógica de negocio principal.

### Uso de Lombok para reducir el boilerplate

**Lombok** ha sido utilizado para reducir el código repetitivo (boilerplate), como los métodos 
`getters`, `setters`, `equals()`, `hashCode()`, y `toString()`. Usando anotaciones como `@Getter`, `@Setter`, 
`@Builder`, entre otras, Lombok genera automáticamente este código en tiempo de compilación, lo que permite que el 
código sea más limpio y fácil de mantener.

Además de mejorar la legibilidad y la mantenibilidad del código, Lombok contribuye a reducir el tamaño de las clases 
y el esfuerzo necesario para escribir código repetitivo.

### Documentación automática con Springdoc

Para la documentación de la API se ha utilizado **Springdoc OpenAPI**, que permite generar automáticamente la
especificación Swagger a partir de las anotaciones de los controladores de la API. Esto facilita la creación y 
actualización de la documentación de la API sin tener que escribir y mantener manualmente los archivos de documentación.

Springdoc OpenAPI genera una interfaz de usuario interactiva con Swagger UI, que permite probar los endpoints 
directamente desde el navegador. Esto es útil tanto para desarrolladores como para consumidores de la API, ya 
que proporciona un medio rápido para entender y probar la funcionalidad del servicio.


### Conclusión

He pensado en posibles mejoras futuras para el proyecto que podrían implementarse de contar con más tiempo. Algunas de estas mejoras incluyen:

- Automatizar la creación de la imagen Docker mediante **GitHub Actions**.
- Separar el desarrollo en **capas** con responsabilidades bien definidas.
- Implementar un **API Gateway** para el manejo adecuado del **rate limit**.
- Descargar automaticamente la configuración de mockserver usando un contenedor busybox
- Alternativamente a este ultimo punto, generar una version custom de MockServer ya completamente configurada

Además, considero que el manejo de servicios como **Redis**, **MockServer** y **Flyway** dentro del mismo archivo 
**Docker Compose** no es lo ideal, y hubiese preferido tener instancias separadas para cada uno de estos servicios con
el fin de mejorar la escalabilidad y mantenimiento.


A pesar de estas áreas de mejora, se implementaron soluciones robustas que cumplen con los objetivos iniciales y
permiten un despliegue rápido y funcional.


