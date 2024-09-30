# README #

Este proyecto es una API REST para la gestión de operaciones CRUD de Comitentes, Mercados y Países. 
Proporciona funcionalidades para obtener estadísticas de comitentes distribuidos por mercados y países.

## Características

- Gestión de recursos `Comitente`, `Mercado` y `País`.
- Manejo de excepciones y errores con respuestas claras.
- Cumplimiento de buenas prácticas de REST.

## Tecnologías Utilizadas

- **Java 1.8**
- **Spring Boot**
- **Spring Data JPA** (para acceso a base de datos)
- **MySQL** (base de datos)
- **JUnit5** y **Mockito** (para pruebas)

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

- [Java 8](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL](https://dev.mysql.com/downloads/installer/)

## Instalación

### 1. Clonar el Repositorio

```bash```
git clone https://github.com/Agustinusaurou/decrypto-operacionescrud.git


### 2. Configurar la Base de Datos
Asegúrate de tener MySQL configurado y corriendo.
Luego, actualiza las credenciales en el archivo ```application.properties```:

spring.datasource.url=jdbc:mysql://localhost:3306/tu_basededatos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

### 3. Instalar Dependencias
```bash```
mvn clean install

### 4. Ejecutar la Aplicación
```bash```
mvn spring-boot:run

### 5. Se pueden ver los endpoint disponibles con swagger
http://localhost:8080/swagger-ui/index.html
