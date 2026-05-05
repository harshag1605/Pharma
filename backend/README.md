# Pharmacy Backend

Spring Boot modular monolith for pharmacy ordering, prescription verification, inventory, delivery, JWT/RBAC, auditing, and notification hooks. Persistence uses MongoDB.

## Run

```bash
brew services start mongodb-community
mvn spring-boot:run
```

Default API: `http://localhost:8080`  
Swagger: `http://localhost:8080/swagger-ui.html`
Default MongoDB: `mongodb://localhost:27017/pharma`

Demo users are seeded on startup:

- `patient@pharma.local` / `Password123!`
- `pharmacist@pharma.local` / `Password123!`
- `delivery@pharma.local` / `Password123!`

## Production Notes

- Replace `CryptoService` with envelope encryption backed by KMS or Vault.
- Replace local `StorageService` with S3/GCS and short-lived signed URLs.
- Move `NotificationService` to Kafka/RabbitMQ-backed outbox processing for guaranteed delivery.
- Use MongoDB Atlas or a managed replica set in production via `MONGODB_URI`.
- Enforce scoped CORS origins, TLS, malware scanning for uploads, and immutable audit retention.
