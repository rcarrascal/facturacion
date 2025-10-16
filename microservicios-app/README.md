# Proyecto Microservicios - Sistema de Gestión de Productos

Sistema completo de microservicios desarrollado con **Spring Boot** y **Java 17** que implementa autenticación, gestión de productos con persistencia dual (PostgreSQL y MongoDB) y comunicación asíncrona mediante ActiveMQ.

## 🏗️ Arquitectura

El proyecto consta de dos microservicios:

### 1. **API Service** (Puerto 8080)
- **Funcionalidad**: Autenticación (login/registro) y gestión de productos
- **Base de datos**: PostgreSQL
- **Características**:
  - Autenticación JWT
  - CRUD completo de productos
  - Al crear un producto, lo guarda en PostgreSQL y envía mensaje a ActiveMQ

### 2. **Worker Service**
- **Funcionalidad**: Procesar mensajes de ActiveMQ y sincronizar con MongoDB
- **Base de datos**: MongoDB
- **Características**:
  - Escucha mensajes de la cola `product.queue`
  - Persiste productos en MongoDB de forma asíncrona

### Infraestructura
- **PostgreSQL**: Base de datos relacional para el API Service
- **MongoDB**: Base de datos NoSQL para sincronización
- **ActiveMQ**: Message broker para comunicación asíncrona

## 📋 Requisitos Previos

- Docker Desktop instalado
- Docker Compose instalado
- Puertos disponibles: 8080, 5432, 27017, 61616, 8161

## 🚀 Instalación y Ejecución

### 1. Clonar o ubicarse en el directorio del proyecto

```bash
cd microservicios-app
```

### 2. Construir y levantar todos los servicios

```bash
docker-compose up --build
```

Este comando:
- Construirá las imágenes de ambos microservicios
- Levantará PostgreSQL, MongoDB y ActiveMQ
- Iniciará los microservicios

### 3. Verificar que todos los servicios estén corriendo

```bash
docker-compose ps
```

Deberías ver 5 contenedores corriendo:
- `postgres_db`
- `mongo_db`
- `activemq`
- `api_service`
- `worker_service`

## 📡 Endpoints del API Service

### Autenticación

#### Registrar Usuario
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "usuario1",
  "password": "password123",
  "email": "usuario1@example.com"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "usuario1",
  "email": "usuario1@example.com"
}
```

#### Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "usuario1",
  "password": "password123"
}
```

### Gestión de Productos (Requiere autenticación)

#### Crear Producto
```http
POST http://localhost:8080/api/products
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Laptop Dell XPS 15",
  "description": "Laptop de alto rendimiento",
  "price": 1500.00,
  "stock": 10,
  "category": "Electrónica"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "name": "Laptop Dell XPS 15",
  "description": "Laptop de alto rendimiento",
  "price": 1500.00,
  "stock": 10,
  "category": "Electrónica",
  "createdAt": "2024-10-16T13:30:00",
  "updatedAt": "2024-10-16T13:30:00"
}
```

#### Obtener Todos los Productos
```http
GET http://localhost:8080/api/products
Authorization: Bearer {token}
```

#### Obtener Producto por ID
```http
GET http://localhost:8080/api/products/{id}
Authorization: Bearer {token}
```

#### Buscar Productos por Categoría
```http
GET http://localhost:8080/api/products/category/{category}
Authorization: Bearer {token}
```

#### Buscar Productos por Nombre
```http
GET http://localhost:8080/api/products/search?name={nombre}
Authorization: Bearer {token}
```

#### Actualizar Producto
```http
PUT http://localhost:8080/api/products/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Laptop Dell XPS 15 (Actualizado)",
  "description": "Laptop de alto rendimiento con 32GB RAM",
  "price": 1600.00,
  "stock": 8,
  "category": "Electrónica"
}
```

#### Eliminar Producto
```http
DELETE http://localhost:8080/api/products/{id}
Authorization: Bearer {token}
```

## 🔍 Verificación del Flujo Completo

### 1. Verificar PostgreSQL
Conectarse a PostgreSQL:
```bash
docker exec -it postgres_db psql -U admin -d products_db
```

Ver productos:
```sql
SELECT * FROM products;
```

### 2. Verificar MongoDB
Conectarse a MongoDB:
```bash
docker exec -it mongo_db mongosh -u admin -p admin123 --authenticationDatabase admin
```

Ver productos sincronizados:
```javascript
use products_mongo
db.products.find().pretty()
```

### 3. Verificar ActiveMQ
Acceder a la consola web de ActiveMQ:
```
http://localhost:8161
Usuario: admin
Password: admin
```

Navegar a "Queues" para ver la cola `product.queue` y los mensajes procesados.

## 📊 Flujo de Datos

1. **Usuario se registra/loguea** → Recibe token JWT
2. **Usuario crea producto** → 
   - Se guarda en PostgreSQL
   - Se envía mensaje a ActiveMQ (cola `product.queue`)
3. **Worker Service escucha la cola** →
   - Recibe el mensaje del producto
   - Lo guarda en MongoDB

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **Spring Data MongoDB**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **Spring JMS**

### Bases de Datos
- **PostgreSQL 15**
- **MongoDB 7**

### Message Broker
- **ActiveMQ 5.15.9**

### Contenedores
- **Docker**
- **Docker Compose**

## 📁 Estructura del Proyecto

```
microservicios-app/
├── api-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/microservicios/api/
│   │       │   ├── config/          # Configuraciones (Security, JMS)
│   │       │   ├── controller/      # Controladores REST
│   │       │   ├── dto/             # Data Transfer Objects
│   │       │   ├── model/           # Entidades JPA
│   │       │   ├── repository/      # Repositorios JPA
│   │       │   ├── security/        # JWT y filtros de seguridad
│   │       │   └── service/         # Lógica de negocio
│   │       └── resources/
│   │           └── application.yml  # Configuración Spring Boot
│   ├── Dockerfile
│   └── pom.xml
├── worker-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/microservicios/worker/
│   │       │   ├── config/          # Configuración JMS
│   │       │   ├── dto/             # Data Transfer Objects
│   │       │   ├── listener/        # Listeners de mensajes
│   │       │   ├── model/           # Documentos MongoDB
│   │       │   ├── repository/      # Repositorios MongoDB
│   │       │   └── service/         # Lógica de sincronización
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── README.md
```

## 🔧 Configuración

### Variables de Entorno (API Service)

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/products_db` | URL de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `admin` | Usuario de PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | `admin123` | Contraseña de PostgreSQL |
| `ACTIVEMQ_BROKER_URL` | `tcp://activemq:61616` | URL de ActiveMQ |
| `JWT_SECRET` | `mi_clave_secreta_super_segura_para_jwt_token_2024` | Clave secreta JWT |

### Variables de Entorno (Worker Service)

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `SPRING_DATA_MONGODB_HOST` | `mongodb` | Host de MongoDB |
| `SPRING_DATA_MONGODB_PORT` | `27017` | Puerto de MongoDB |
| `SPRING_DATA_MONGODB_DATABASE` | `products_mongo` | Base de datos MongoDB |
| `SPRING_DATA_MONGODB_USERNAME` | `admin` | Usuario de MongoDB |
| `SPRING_DATA_MONGODB_PASSWORD` | `admin123` | Contraseña de MongoDB |
| `ACTIVEMQ_BROKER_URL` | `tcp://activemq:61616` | URL de ActiveMQ |

## 🧪 Pruebas con cURL

### 1. Registrar usuario
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "email": "test@example.com"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'
```

### 3. Crear producto (reemplazar {TOKEN})
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "name": "Producto Test",
    "description": "Descripción del producto",
    "price": 99.99,
    "stock": 50,
    "category": "Test"
  }'
```

## 🐛 Troubleshooting

### Los servicios no inician
```bash
docker-compose down -v
docker-compose up --build
```

### Ver logs de un servicio específico
```bash
docker-compose logs -f api-service
docker-compose logs -f worker-service
```

### Reiniciar un servicio específico
```bash
docker-compose restart api-service
```

### Limpiar todo y empezar de cero
```bash
docker-compose down -v
docker system prune -a
docker-compose up --build
```

## 📝 Notas Importantes

1. **Seguridad**: En producción, cambiar todas las contraseñas y el JWT_SECRET
2. **Persistencia**: Los datos se mantienen en volúmenes de Docker
3. **Escalabilidad**: El worker service puede escalarse horizontalmente
4. **Logs**: Revisar logs para debugging con `docker-compose logs`

## 👨‍💻 Desarrollo

Para desarrollo local sin Docker:

1. Instalar PostgreSQL, MongoDB y ActiveMQ localmente
2. Actualizar `application.yml` con las URLs locales
3. Ejecutar cada servicio con Maven:
```bash
cd api-service
mvn spring-boot:run

cd worker-service
mvn spring-boot:run
```

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia ISC.

---

**¡Listo para usar!** 🎉
