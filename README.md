# School Management System

A school management platform: student and teacher records, courses and
promotions, class scheduling, Edusign-style attendance signing, grades and
rankings, exams with virtual proctoring / anti-cheat, and year-end archiving —
a Spring Boot REST API plus an Angular web client.

Java 25 · Spring Boot 4.1 · PostgreSQL · Flyway · Angular · Tailwind CSS · Docker.

## Contents

- [What it does](#what-it-does)
- [Architecture](#architecture)
- [Running the app](#running-the-app)
- [Configuration](#configuration)
- [API docs](#api-docs)
- [Frontend (web/)](#frontend-web)
- [Testing](#testing)
- [Project layout](#project-layout)

## What it does

The system is organized into bounded contexts, each owning its own aggregates:

| Context       | Aggregates                          | Responsibility                                    |
|---------------|--------------------------------------|----------------------------------------------------|
| `enrollment`  | Student, Promotion, Teacher, Course | Who and where — reference data                     |
| `iam`         | User                                 | Authentication and role-based authorization         |
| `scheduling`  | Session                              | Scheduled class occurrences, signing windows        |
| `attendance`  | AttendanceRecord                     | Edusign-style student attendance signing            |
| `grading`     | Grade (+ read models)                | Grades, weighted averages, promotion rankings       |
| `examination` | Exam, ExamAttempt                    | Exam scheduling and virtual proctoring / integrity  |
| `reporting`   | (read models only)                   | Cross-context statistics and at-risk dashboards     |
| `archiving`   | (capability on Promotion)            | Year-end archiving of promotions and students       |

Notable behavior:
- **Attendance is event-driven.** Closing a session's signing window publishes
  `SessionSigningClosed`; the attendance context reacts and marks every
  enrolled non-signer `ABSENT`, decoupled from the scheduling context.
- **Exam integrity.** Proctoring events (tab switches, window blur, copy/paste,
  multiple faces, ...) accumulate on an attempt; crossing
  `app.exam.integrity-threshold` auto-flags it for review.
- **At-risk students** are surfaced by `reporting` when attendance rate and
  overall average both drop below configured thresholds.
- **Archiving** marks a promotion and its students archived; archived
  aggregates reject further mutation and are excluded from active queries.

## Architecture

Each bounded context follows the same layered structure, with dependencies
pointing inward only (`domain ← application ← {infrastructure, presentation}`):

```
com.schoolmanagement.<context>/
  domain/            pure Java — no Spring, no JPA, no web, no identity/security types
  application/       use-case handlers; depends only on domain (+ @Service/@Transactional)
  infrastructure/    JPA entities, mappers, Spring Data repos, Flyway migrations, event listeners
  presentation/      REST controllers, request/response DTOs, @PreAuthorize
```

Key rules enforced across the codebase:
- **Aggregates reference each other by id**, never by object reference.
- **Authorization is layered**: `@PreAuthorize` on controller methods checks
  role/identity (security concern); domain methods like
  `Course.isTaughtBy(teacherId)` express business facts; policy beans
  (`CourseAccessPolicy`, `SessionAccessPolicy`, `GradeAccessPolicy`,
  `ExamAccessPolicy`) combine the two inside the `@PreAuthorize` expression.
  Identity never enters an aggregate.
- **Cross-context coordination** uses Spring domain events
  (`ApplicationEventPublisher`, published after commit) instead of direct
  cross-context calls, so contexts stay decoupled and each transaction
  touches one aggregate.
- **Persistence**: Flyway owns the schema (`ddl-auto=none`); JPA entities are
  separate from domain aggregates and rehydrated through a
  `reconstitute(...)` factory; migrations live in
  `src/main/resources/db/migration/`.

## Running the app

Requires Docker and Docker Compose.

```bash
cp .env.example .env
# edit .env — at minimum change JWT_SECRET before anything but local dev

docker compose up
```

This starts Postgres (`db`), the API (`app`), and the Angular dev server
(`web`) together. The `app` image runs `mvn spring-boot:run` against the
mounted source, applying Flyway migrations on boot — the API is served on
`http://localhost:${APP_HOST_PORT:-8080}`. The `web` container runs
`npm install && npm start`, serving the UI on
`http://localhost:${WEB_HOST_PORT:-4200}`. Run `docker compose up app` alone
to start only the API.

To seed an initial admin user on startup, set `SEED_ADMIN_ENABLED=true` plus
`SEED_ADMIN_USERNAME` / `SEED_ADMIN_PASSWORD` in `.env` — dev/local only.

Useful one-offs:

```bash
docker compose run --rm --no-deps app mvn compile   # compile only
docker compose run --rm --no-deps app mvn test      # unit tests
docker compose run --rm --no-deps app mvn verify     # unit + integration tests (Testcontainers)
docker compose down -v                                # reset the database volume
```

## Configuration

All configuration is environment-variable driven (`app.*` custom properties,
`${VAR:default}` dev defaults — see `src/main/resources/application.yaml`).
Real values go in a git-ignored `.env`; every variable is mirrored in
`.env.example`:

| Variable | Purpose |
|---|---|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | Database credentials, shared by `db` and `app` |
| `DB_HOST`, `DB_HOST_PORT`, `APP_HOST_PORT` | Networking / port mapping |
| `CORS_ALLOWED_ORIGINS` | Allowed origins for the security filter chain's CORS source |
| `JWT_SECRET`, `JWT_TTL_SECONDS` | JWT signing secret and token lifetime |
| `SEED_ADMIN_ENABLED`, `SEED_ADMIN_USERNAME`, `SEED_ADMIN_PASSWORD` | Env-gated startup admin seeder |
| `EXAM_INTEGRITY_THRESHOLD` | Integrity events before an attempt auto-flags (default `5`) |
| `REPORTING_ATTENDANCE_RISK_THRESHOLD`, `REPORTING_GRADE_RISK_THRESHOLD` | At-risk thresholds (default `0.75` attendance rate, `10.0`/20 average) |

## API docs

Interactive Swagger UI: `http://localhost:8080/swagger-ui.html`
Raw OpenAPI spec: `http://localhost:8080/v3/api-docs`

Auth is stateless JWT (`Authorization: Bearer <token>`), obtained via
`POST /api/v1/auth/login`. Roles are `ADMIN`, `TEACHER`, `STUDENT`.

| Method & path | Roles | Notes |
|---|---|---|
| `POST /api/v1/auth/login` | public | issues a JWT |
| `POST /api/v1/auth/register` | ADMIN | create a user |
| `POST /api/v1/students` | ADMIN | enroll a student |
| `GET /api/v1/students/{id}` | authenticated | |
| `POST /api/v1/students/{id}/archive` | ADMIN | |
| `POST /api/v1/teachers` | ADMIN | hire a teacher |
| `GET /api/v1/teachers`, `GET /api/v1/teachers/{id}` | authenticated | |
| `POST /api/v1/teachers/{id}/archive` | ADMIN | |
| `POST /api/v1/courses` | ADMIN | |
| `GET /api/v1/courses`, `GET /api/v1/courses/{id}` | authenticated | filter with `?promotionId=` |
| `POST /api/v1/courses/{id}/assign-teacher` | ADMIN | |
| `POST /api/v1/promotions` | ADMIN | |
| `GET /api/v1/promotions` | authenticated | list active promotions |
| `GET /api/v1/promotions/{id}`, `GET /api/v1/promotions/{id}/students` | authenticated | |
| `POST /api/v1/promotions/{id}/students` | ADMIN | admit a student |
| `POST /api/v1/promotions/{id}/archive` | ADMIN | year-end archiving |
| `GET /api/v1/archive/promotions`, `GET /api/v1/archive/promotions/{id}` | authenticated | |
| `POST /api/v1/sessions` | ADMIN | schedule a class session |
| `GET /api/v1/sessions`, `GET /api/v1/sessions/{id}` | authenticated | filter with `?promotionId=&date=` |
| `POST /api/v1/sessions/{id}/open` \| `/close` \| `/cancel` | ADMIN, or owning TEACHER | via `SessionAccessPolicy` |
| `POST /api/v1/sessions/{sessionId}/attendance/sign` | STUDENT | signs self in |
| `POST /api/v1/sessions/{sessionId}/attendance/{studentId}/justify` | ADMIN, or owning TEACHER | mark `EXCUSED` |
| `GET /api/v1/sessions/{sessionId}/attendance` | authenticated | |
| `GET /api/v1/students/{studentId}/attendance` | authenticated | |
| `POST /api/v1/courses/{courseId}/grades` | ADMIN, or owning TEACHER | via `CourseAccessPolicy` |
| `GET /api/v1/students/{studentId}/grades`, `/averages` | authenticated | |
| `POST /api/v1/grades/{id}/correct` | ADMIN, or owning TEACHER | via `GradeAccessPolicy` |
| `GET /api/v1/promotions/{promotionId}/rankings` | authenticated | |
| `POST /api/v1/exams` | ADMIN | |
| `GET /api/v1/exams`, `GET /api/v1/exams/{id}` | authenticated | filter with `?promotionId=` |
| `POST /api/v1/exams/{id}/open` \| `/close` | ADMIN, or owning TEACHER | via `ExamAccessPolicy` |
| `POST /api/v1/exams/{examId}/attempts/start` | STUDENT | |
| `POST /api/v1/attempts/{id}/events` | STUDENT | record a proctoring event |
| `POST /api/v1/attempts/{id}/submit` | STUDENT | |
| `GET /api/v1/attempts/{id}` | authenticated | |
| `GET /api/v1/exams/{examId}/attempts` | ADMIN, or owning TEACHER | filter with `?flagged=true` |
| `GET /api/v1/reporting/students/{studentId}/summary` | authenticated | |
| `GET /api/v1/reporting/promotions/{promotionId}/summary` | authenticated | |
| `GET /api/v1/reporting/promotions/{promotionId}/at-risk` | ADMIN, TEACHER | |

Standard error responses use a shared `ApiError` body: 400 for bean-validation
and invalid value objects, 404 for not-found, 409 for invariant/conflict
violations, 401/403 for auth failures.

## Frontend (web/)

An Angular (standalone components, signals) + Tailwind CSS client lives in
`web/`, covering the day-to-day flows for each role:

- **Auth** — JWT login, stored client-side; an `HttpInterceptor` attaches the
  token to API calls and logs out on 401. The JWT carries `personId` (the
  linked student/teacher id) alongside `username`/`role` so the app knows
  "who am I" without an extra round trip.
- **Student** — sign attendance for today's open sessions, view attendance
  history, view grades and averages.
- **Teacher** — manage sessions in a promotion (open/close/cancel signing),
  view a session's attendance and justify absences, record and correct
  grades for own courses.
- **Admin** — create promotions/teachers/courses/sessions, enroll and admit
  students to a promotion, assign teachers to courses, register user
  accounts, and the reporting dashboard.
- **Reporting** — promotion summary, at-risk student list, and a per-student
  lookup (shared by ADMIN and TEACHER).

Routing is guarded per role (`authGuard` / `roleGuard`) and the nav in the
shell adapts to the signed-in role. The API base URL is a constant in
`web/src/app/core/config.ts` (`http://localhost:8080/api/v1` by default).

Run it via `docker compose up web` (starts `app` and `db` as dependencies
too), or standalone:

```bash
cd web
npm install
npm start          # serves on http://localhost:4200
```

`CORS_ALLOWED_ORIGINS` in `.env` must include the web app's origin
(`http://localhost:4200` by default) — the API enforces CORS inside the
Spring Security filter chain, not at the web-server level, so preflight
(`OPTIONS`) requests are permitted before the auth check runs.

## Testing

- **Unit tests** — domain invariants and application handlers against
  in-memory fakes, no Spring context.
- **Integration tests** (`*IT`) — Testcontainers-backed Postgres: repository
  adapters, controllers, and event listeners.

```bash
docker compose run --rm --no-deps app mvn test      # unit only
docker compose run --rm --no-deps app mvn verify     # unit + integration
```

CI runs `mvn -B verify` on every PR into `integration` or `main`
(`.github/workflows/ci.yml`).

## Project layout

```
src/main/java/com/schoolmanagement/
  <context>/domain/           aggregates, value objects, domain events, repository ports
  <context>/application/      command/query handlers
  <context>/infrastructure/   JPA entities, adapters, config, event listeners
  <context>/presentation/     REST controllers, DTOs
  shared/                     DomainException, ApiError, FullName, EmailAddress,
                               TimeConfiguration (Clock bean), security/CORS config
src/main/resources/
  application.yaml
  db/migration/                Flyway migrations, V<n>__<desc>.sql

web/src/app/
  core/                        auth (JWT service, guard, interceptor), API services, config
  shared/                      cross-cutting UI helpers (API error parsing)
  features/
    auth/login/
    shell/                     app shell, role-based nav, role home redirect
    student/                   attendance, grades
    teacher/                   sessions, session attendance, grading
    admin/                     promotions, promotion detail, teachers, users
    reporting/
```
