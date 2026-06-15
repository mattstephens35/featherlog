# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FeatherLog is a bird-watching journal app: a React/Vite frontend, a Java 21 /
Spring Boot 3.2 REST API, and a MySQL 8 database (schema + seed data managed by
Flyway). It's intentionally built as a realistic, deterministic full-stack
reference app — useful as a target for UI automation (Playwright/Cypress) and
API/contract testing. There is **no authentication**; every endpoint is open.

See `README.md` for the full API reference, seed data description, and
`data-testid` conventions — it's kept up to date and is the source of truth for
endpoint shapes and frontend routes.

## Commands

### Run everything (Docker)

```bash
docker compose up --build      # frontend :8081, backend :8080, mysql :3306
docker compose down             # stop
docker compose down -v          # stop and wipe the DB volume
```

### Backend (`backend/`)

```bash
mvn spring-boot:run              # run on :8080 (Flyway runs migrations + seed automatically)
mvn test                         # run tests
mvn test -Dtest=ClassName#method # run a single test
mvn package                      # build the jar
```

Requires a running MySQL instance — `docker compose up mysql` starts just the
DB on `localhost:3306` (db/user/pass: `featherlog`/`featherlog`/`featherlog`).
Config is in `backend/src/main/resources/application.yml`, overridable via
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`,
`CORS_ALLOWED_ORIGINS`.

### Frontend (`frontend/`)

```bash
npm install
npm run dev       # dev server on :5173, proxies /api -> http://localhost:8080
npm run build     # production build to dist/
npm run preview   # serve the build on :4173
```

Set `VITE_API_PROXY_TARGET` to point the dev/preview proxy at a non-default
backend host.

There are currently no frontend unit tests or a configured linter.

### End-to-end tests (`e2e/`)

```bash
npm install
npx playwright install chrome     # first time only
npx playwright test                # requires the app stack already running
```

Basic UI flows (add a sighting, sightings list sort order, delete a
sighting) run against `http://localhost:5173` by default — start the
frontend dev server and backend/MySQL first. Set `BASE_URL` to point at the
Docker Compose stack instead (`http://localhost:8081`).

## Architecture

**Three-tier layout, talking only via relative `/api/...` URLs:**

```
React (Vite, :5173 dev / nginx :8081 docker) --/api--> Spring Boot (:8080) --JDBC--> MySQL (:3306)
```

The frontend never hardcodes a backend host: Vite's dev server proxies `/api`
to `VITE_API_PROXY_TARGET` (default `http://localhost:8080`), and in Docker
nginx (`frontend/nginx.conf`) proxies `/api` to the `backend` container. CORS
on the backend (`config/WebConfig.java`, configured via
`featherlog.cors.allowed-origins`) is a fallback for direct calls.

### Backend (`backend/src/main/java/com/featherlog/api/`)

Standard layered structure per resource (Bird, Location, Sighting):

- `controller/` — thin `@RestController`s; validation via `@Valid` on request
  DTOs, pagination via `Pageable`/`@PageableDefault`.
- `service/` — business logic, maps entities <-> DTOs.
- `repository/` — Spring Data JPA repositories plus `*Specifications` classes
  (`BirdSpecifications`, `LocationSpecifications`, `SightingSpecifications`)
  that build dynamic `Specification<T>` filters for search/filter query params.
- `dto/` — request/response records. List endpoints return `PageResponse<T>`.
  Sighting responses embed nested `bird`/`location` summary objects.
- `model/` — JPA entities and enums (`ConservationStatus`, `HabitatType`,
  `SizeCategory`, `WeatherCondition`) — these enums are mirrored on the
  frontend in `frontend/src/data/options.js`.
- `exception/GlobalExceptionHandler` — converts validation errors, 404s
  (`ResourceNotFoundException`), and constraint violations into the consistent
  `ErrorResponse` JSON shape documented in the README.

### Database (`backend/src/main/resources/db/migration/`)

Flyway migrations `V1`-`V4`: `V1` creates the schema, `V2`-`V4` seed locations,
birds, and sightings. `ddl-auto: validate` — schema changes must go through a
new Flyway migration, not Hibernate auto-DDL. Migrations are idempotent on
restart (Flyway tracks applied versions), giving every environment the same
deterministic dataset (18 locations, 25 birds, 32 sightings).

### Frontend (`frontend/src/`)

- `api/` — one Axios module per resource (`birds.js`, `locations.js`,
  `sightings.js`, `stats.js`), all built on the shared `client.js` instance
  (`baseURL: '/api'`).
- `pages/` — route-level components (one per route in `App.jsx`'s router).
- `components/` — shared UI (cards, badges, pagination, toasts via
  `ToastContext`, modals, etc.).
- `data/options.js` — frontend-side enum value lists that must stay in sync
  with the backend enums in `model/`.
- `utils/format.js` — shared formatting helpers (dates, numbers, etc.).

### Cross-cutting conventions

- Enum values are SCREAMING_SNAKE_CASE on the wire (e.g.
  `LEAST_CONCERN`, `WETLAND`, `PARTLY_CLOUDY`) — keep backend enums and
  `frontend/src/data/options.js` in sync when adding/changing values.
- All list endpoints share the same `PageResponse<T>` envelope and
  `page`/`size`/`sort` query params.
- Interactive frontend elements use `data-testid` attributes following the
  conventions documented in the README's "Testing Hooks" table — preserve
  these when modifying components, since they're relied on by automated tests.
