# Pharmacy Order & Prescription Management System

Production-shaped full-stack healthcare operations system.

## Apps

- `backend`: Spring Boot modular monolith with JWT/RBAC, prescription upload, order state machine, MongoDB persistence, atomic inventory reservation, delivery OTP, audit logs, OpenAPI, seed data.
- `frontend`: React + Vite + Tailwind + RTK Query with role-aware patient, pharmacist, and delivery workspaces.

## Local Startup

Backend:

```bash
brew services start mongodb-community
cd backend
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Demo credentials:

- `patient@pharma.local` / `Password123!`
- `pharmacist@pharma.local` / `Password123!`
- `delivery@pharma.local` / `Password123!`

Swagger runs at `http://localhost:8080/swagger-ui.html`.

Set `MONGODB_URI` when using Atlas or a non-default MongoDB instance:

```bash
MONGODB_URI=mongodb://localhost:27017/pharma mvn spring-boot:run
```
