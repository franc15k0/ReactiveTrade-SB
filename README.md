# 🛒 ReactiveTrade-SB

**Arquitectura de microservicios reactiva para un sistema e-commerce basada en eventos coreografiados.**

---

## 🧱 Tecnologías principales

- ⚙️ Spring Boot
- 🔁 Spring WebFlux (reactivo)
- 🐘 PostgreSQL
- 📩 Apache Kafka
- 📦 Avro (contratos)
- 🧠 Saga Pattern
- 🧪 Testcontainers, JUnit 5
- 🐳 Docker & Docker Compose
- 🚀 CI/CD ready

---

## 📦 Módulos del proyecto

| Módulo         | Descripción                                  |
|----------------|----------------------------------------------|
| `order-service`  | Gestión de órdenes, inicia y coordina la Saga |
| `payment-service`| Proceso de pagos y compensaciones           |
| `product-service`| Catálogo y validación de stock              |
| `common`         | Schemas Avro compartidos entre servicios    |

---

## 📊 Arquitectura

- **Microservicios desacoplados** mediante **Kafka**
- Comunicación basada en **eventos Avro**
- Patrón **Saga Coreografiado**
- Persistencia reactiva con **Spring Data R2DBC**

---

## 🚀 Cómo ejecutar

docker-compose up -d

## 📌 Autor

Desarrollado por **Francisco Bazan**

- 💼 [LinkedIn](https://www.linkedin.com/in/francisco-bazan-286abb7a/)
- 💻 [GitHub](https://github.com/juanperezdev)
  - ✉️ fbazanm@gmail.com
