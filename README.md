# Ecommerce Shadow Coders Backend

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/ShadowCoders/EcommerceShadowCodersBack)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

## Tabla de Contenido

- [Descripción](#descripción)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Instalación y configuración](#instalación-y-configuración)
- [Variables de entorno](#variables-de-entorno)
- [Cómo correr el proyecto](#cómo-correr-el-proyecto)
- [Endpoints principales](#endpoints-principales)
- [Ejemplos de uso](#ejemplos-de-uso)
- [Logs y monitoreo](#logs-y-monitoreo)
- [Tests](#tests)
- [Contribución](#contribución)
- [Licencia](#licencia)

## Descripción

Backend desarrollado en Java Spring Boot para gestión de productos, usuarios, pagos y órdenes de compra. Incluye integración con Mercado Pago, seguridad JWT, manejo global de errores y estructura modular.

## Estructura del proyecto

```
src/main/java/com/dresscode/api_dresscode/
├── controllers/        # Controladores REST
├── services/           # Lógica de negocio
├── repositories/       # Acceso a datos
├── entities/           # Modelos JPA
├── dtos/               # Data Transfer Objects
├── config/             # Configuración
├── Jwt/                # Autenticación JWT
├── Auth/               # Autenticación/autorización
```

## Instalación y configuración

1. Clona el repositorio:
   ```bash
   git clone https://github.com/ShadowCoders/EcommerceShadowCodersBack.git
   ```
2. Instala Java 17 y Gradle.
3. Define las variables de entorno (usa el script `set-env.ps1`).

## Variables de entorno

- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET
- MP_ACCESS_TOKEN
- MP_WEBHOOK_SECRET

## Cómo correr el proyecto

```powershell
.\set-env.ps1
./gradlew bootRun
```

## Endpoints principales

| Método | Ruta                     | Descripción               |
| ------ | ------------------------ | ------------------------- |
| GET    | /api/usuarios            | Listar usuarios           |
| POST   | /api/usuarios            | Crear usuario             |
| GET    | /api/productos           | Listar productos          |
| POST   | /api/productos           | Crear producto            |
| GET    | /api/categorias          | Listar categorías         |
| POST   | /api/categorias          | Crear categoría           |
| GET    | /api/ordenes             | Listar órdenes de compra  |
| POST   | /api/ordenes             | Crear orden de compra     |
| POST   | /api/mercadopago/pago    | Iniciar pago Mercado Pago |
| POST   | /api/mercadopago/webhook | Webhook Mercado Pago      |

> Para ver todos los endpoints, revisa los controladores en `src/main/java/com/dresscode/api_dresscode/controllers/`

## Ejemplos de uso

### Crear usuario

```json
POST /api/usuarios
{
  "username": "juan",
  "email": "juan@email.com",
  "password": "123456"
}
```

### Listar productos

```json
GET /api/productos
Response:
[
  {
    "id": 1,
    "nombre": "Remera",
    "precio": 1200
  },
  ...
]
```

### Iniciar pago Mercado Pago

```json
POST /api/mercadopago/pago
{
  "orderId": 123,
  "amount": 5000
}
```

## Logs y monitoreo

- Los logs aparecen en la consola.
- Monitoreo disponible en `/actuator/health`, `/actuator/metrics`, `/actuator/env`, etc.

## Tests

Ejecuta los tests con:

```powershell
./gradlew test
```

## Contribución

¡Las contribuciones son bienvenidas! Por favor, abre un issue o pull request para sugerencias o mejoras.

## Licencia

MIT

## Autor

Shadow Coders
