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
2. **Verifica la imagen de Docker:** La imagen de la aplicación está disponible en Docker Hub. 
No es necesario construirla, pero si deseas hacerlo localmente puedes ejecutar:
```bash
  ./gradlew bootBuildImage --imageName=cbarrosc/challenge-app:x.x.x
```
Esto creará la imagen cbarrosc/challenge-app:x.x.x que será utilizada en Docker Compose.

3. **Inicia los contenedores usando Docker Compose:** Asegúrate de que Docker esté corriendo en tu máquina. 
Luego ejecuta el siguiente comando para iniciar los contenedores:
```bash
  docker-compose up
```
Esto arrancará los siguientes contenedores:

* PostgreSQL: Base de datos relacional.
* Redis: Caché.
* MockServer: Simulador de respuestas para pruebas.
* App La aplicacion del servicio en Spring Boot.

4. **Verifica que los contenedores estén corriendo:** Ejecuta el siguiente comando para asegurarte de que todos los servicios estén activos:
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