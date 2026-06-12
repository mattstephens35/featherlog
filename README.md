# 🪶 FeatherLog

FeatherLog is a bird-watching journal application. Track sightings, browse a field
guide of bird species, and explore birding hotspots around the world — complete
with country flags, species illustrations (emoji + color theming), conservation
status, and a personal sighting log.

It's built as a small but realistic full-stack reference app: a React UI, a Java
Spring Boot REST API, and a MySQL database, all wired together with Docker
Compose. It's intentionally seeded with a non-trivial, predictable dataset so it
can be used as a target for UI automation (Playwright, Cypress) and API/contract
testing.

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite, React Router, Axios, `flag-icons` |
| Backend | Java 21, Spring Boot 3.2 (Web, Data JPA, Validation, Actuator) |
| Database | MySQL 8, Flyway migrations |
| Containers | Docker, Docker Compose, multi-stage builds, nginx (frontend) |

## Architecture

```
┌────────────────┐     /api/*      ┌──────────────────┐     JDBC      ┌──────────────┐
│    Frontend     │ ──────────────►│   Backend API     │ ─────────────►│    MySQL     │
│  React + Vite    │ ◄──────────────│   Spring Boot      │ ◄─────────────│   Database    │
│  served by nginx │      JSON       │   port 8080        │               │  port 3306    │
└────────────────┘                  └──────────────────┘                └──────────────┘
   port 8081 (docker)
   port 5173 (dev server)
```

In both Docker and local dev, the frontend talks to the backend via relative
`/api/...` URLs:

- In **dev** (`npm run dev`), Vite's dev server proxies `/api` → `http://localhost:8080`.
- In **Docker**, nginx proxies `/api` → the `backend` container on port 8080.

This means the frontend never needs to know the backend's host/port, and there
are no CORS issues in either mode. The backend also has CORS enabled for
`http://localhost:5173`, `http://localhost:3000`, and `http://localhost:4173` in
case you want to call it directly (e.g. from API tests or a separately-hosted
frontend).

---

## Quick Start (Docker — recommended)

**Requirements:** Docker Desktop (with Compose v2)

```bash
docker compose up --build
```

This builds and starts three containers:

| Service | URL | Notes |
|---|---|---|
| Frontend | http://localhost:8081 | Served by nginx |
| Backend API | http://localhost:8080/api | Spring Boot |
| Health check | http://localhost:8080/actuator/health | `{"status":"UP"}` once ready |
| MySQL | `localhost:3306` | db/user/password: `featherlog` / `featherlog` / `featherlog` |

On first boot, MySQL initializes, then the backend runs Flyway migrations to
create the schema and load seed data (18 locations, 25 birds, 32 sightings).
This typically takes 30–60 seconds. The backend container's healthcheck will
report healthy once `/actuator/health` returns `UP`.

To stop everything:

```bash
docker compose down
```

To stop and wipe the database volume (start fresh next time):

```bash
docker compose down -v
```

---

## Local Development (without Docker)

**Requirements:** Java 21+, Maven 3.9+, Node.js 18+, and a MySQL 8 instance.

### 1. Start MySQL only

The easiest way is to use the database service from Compose:

```bash
docker compose up mysql
```

This exposes MySQL on `localhost:3306` with database `featherlog`, user
`featherlog`, password `featherlog`.

### 2. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**. Flyway automatically creates
the schema and seeds data on first run (it's idempotent — it won't re-seed on
subsequent runs).

Configuration is in `backend/src/main/resources/application.yml` and can be
overridden with environment variables: `DB_HOST`, `DB_PORT`, `DB_NAME`,
`DB_USERNAME`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`.

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The dev server starts on **http://localhost:5173** with `/api` proxied to
`http://localhost:8080`. If your backend runs elsewhere, set
`VITE_API_PROXY_TARGET` before starting the dev server.

---

## Seed Data

Flyway loads a realistic starter dataset on first startup:

- **18 birding locations** across 6 continents — Everglades, Yellowstone,
  Central Park, the Pantanal, Galápagos, Serengeti, Okavango Delta, and more —
  each with country, region, habitat type, coordinates, best season, and a flag
  (via `flag-icons`, keyed by `countryCode`).
- **25 bird species** spanning raptors, owls, waterfowl, songbirds, parrots,
  and more — each with scientific name, family, conservation status, size,
  average length/weight, habitat, diet, an emoji icon, and a theme color.
- **32 sample sightings** logged between May and June 2026 by six different
  observers, with quantities, weather conditions, notes, and 12 marked as
  favorites.

This gives you a non-empty, deterministic dataset to assert against immediately
— useful for both UI snapshot-style tests and API contract tests.

---

## API Reference

Base URL: `http://localhost:8080/api` (direct) or `http://localhost:8081/api`
(through the dockerized frontend's nginx proxy).

All list endpoints return a `PageResponse<T>`:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 12,
  "totalElements": 25,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

### Birds — `/api/birds`

| Method | Path | Description |
|---|---|---|
| GET | `/api/birds` | Paginated list. Query params: `search`, `family`, `conservationStatus`, `page`, `size`, `sort` (default: `size=12`, `sort=commonName,asc`) |
| GET | `/api/birds/families` | List of distinct family names (for filter dropdowns) |
| GET | `/api/birds/{id}` | Get a single bird |
| POST | `/api/birds` | Create a bird (201) |
| PUT | `/api/birds/{id}` | Update a bird |
| DELETE | `/api/birds/{id}` | Delete a bird (204) |

**Bird object:**

```json
{
  "id": 1,
  "commonName": "Bald Eagle",
  "scientificName": "Haliaeetus leucocephalus",
  "family": "Accipitridae",
  "conservationStatus": "LEAST_CONCERN",
  "sizeCategory": "LARGE",
  "averageLengthCm": 90.0,
  "averageWeightGrams": 4500.0,
  "habitat": "Near large bodies of open water with abundant fish",
  "diet": "Fish, waterfowl, carrion",
  "migratory": true,
  "description": "...",
  "icon": "🦅",
  "colorHex": "#4A6B8A"
}
```

`conservationStatus` ∈ `LEAST_CONCERN | NEAR_THREATENED | VULNERABLE | ENDANGERED | CRITICALLY_ENDANGERED`
`sizeCategory` ∈ `SMALL | MEDIUM | LARGE`

### Locations — `/api/locations`

| Method | Path | Description |
|---|---|---|
| GET | `/api/locations` | Paginated list. Query params: `search`, `country`, `habitatType`, `page`, `size`, `sort` (default: `size=12`, `sort=name,asc`) |
| GET | `/api/locations/countries` | Distinct `{country, countryCode}` pairs (for filter dropdowns) |
| GET | `/api/locations/{id}` | Get a single location |
| POST | `/api/locations` | Create a location (201) |
| PUT | `/api/locations/{id}` | Update a location |
| DELETE | `/api/locations/{id}` | Delete a location (204) |

**Location object:**

```json
{
  "id": 1,
  "name": "Everglades National Park",
  "country": "United States",
  "countryCode": "us",
  "region": "Florida",
  "habitatType": "WETLAND",
  "latitude": 25.286700,
  "longitude": -80.898000,
  "bestSeason": "December to April",
  "description": "...",
  "icon": "💧"
}
```

`habitatType` ∈ `WETLAND | FOREST | GRASSLAND | COASTAL | MOUNTAIN | URBAN_PARK | DESERT | TUNDRA`
`countryCode` is a lowercase ISO 3166-1 alpha-2 code, used directly as a
`flag-icons` class (`fi fi-us`, `fi fi-br`, etc.) on the frontend.

### Sightings — `/api/sightings`

| Method | Path | Description |
|---|---|---|
| GET | `/api/sightings` | Paginated list. Query params: `birdId`, `locationId`, `fromDate`, `toDate` (ISO `yyyy-MM-dd`), `favorite`, `search`, `page`, `size`, `sort` (default: `size=10`, `sort=sightingDate,desc`) |
| GET | `/api/sightings/{id}` | Get a single sighting |
| POST | `/api/sightings` | Create a sighting (201) |
| PUT | `/api/sightings/{id}` | Update a sighting |
| PATCH | `/api/sightings/{id}/favorite` | Toggle the `favorite` flag |
| DELETE | `/api/sightings/{id}` | Delete a sighting (204) |

**Sighting object (response):**

```json
{
  "id": 1,
  "bird": { "id": 7, "commonName": "Mallard", "scientificName": "Anas platyrhynchos", "icon": "🦆", "colorHex": "#2E7D5B" },
  "location": { "id": 1, "name": "Everglades National Park", "country": "United States", "countryCode": "us", "icon": "💧" },
  "sightingDate": "2026-06-09",
  "sightingTime": "07:30:00",
  "quantity": 4,
  "observerName": "Jordan Avery",
  "weatherCondition": "SUNNY",
  "notes": "Feeding near the boardwalk.",
  "favorite": true,
  "createdAt": "2026-06-09T12:01:00Z",
  "updatedAt": "2026-06-09T12:01:00Z"
}
```

**Sighting create/update request body:**

```json
{
  "birdId": 7,
  "locationId": 1,
  "sightingDate": "2026-06-09",
  "sightingTime": "07:30:00",
  "quantity": 4,
  "observerName": "Jordan Avery",
  "weatherCondition": "SUNNY",
  "notes": "Feeding near the boardwalk.",
  "favorite": true
}
```

`weatherCondition` ∈ `SUNNY | PARTLY_CLOUDY | CLOUDY | OVERCAST | RAINY | FOGGY | SNOWY | WINDY` (nullable).
`sightingTime` and `notes` are nullable. `sightingDate` cannot be in the future
(`PastOrPresent`). `quantity` must be between 1 and 10000. An invalid `birdId`
or `locationId` returns **404**.

### Stats — `/api/stats`

| Method | Path | Description |
|---|---|---|
| GET | `/api/stats/summary` | Dashboard summary (see below) |
| GET | `/api/stats/timeline` | Monthly sighting counts, oldest first |
| GET | `/api/stats/conservation` | Species/sighting counts by conservation status |
| GET | `/api/stats/lifers` | First-ever sighting of each species ("lifers"), oldest first |
| GET | `/api/stats/activity` | Per-day sighting counts plus the longest sighting streak |

```json
{
  "totalSightings": 32,
  "speciesRecorded": 18,
  "locationsVisited": 12,
  "totalSpeciesCatalog": 25,
  "totalLocationsCatalog": 18,
  "sightingsThisMonth": 9,
  "recentSightings": [ /* up to 5 SightingResponse, most recent first */ ],
  "topBirds": [ { "birdId": 7, "commonName": "Mallard", "icon": "🦆", "colorHex": "#2E7D5B", "sightingCount": 4 } ],
  "topLocations": [ { "locationId": 1, "name": "Everglades National Park", "countryCode": "us", "icon": "💧", "sightingCount": 5 } ]
}
```

`GET /api/stats/timeline`:

```json
{
  "months": [
    { "yearMonth": "2026-05", "sightingCount": 18 },
    { "yearMonth": "2026-06", "sightingCount": 14 }
  ]
}
```

`GET /api/stats/conservation` — one entry per `ConservationStatus` value
(always 5 entries, including statuses with zero birds/sightings):

```json
{
  "statuses": [
    { "status": "LEAST_CONCERN", "speciesCount": 18, "sightingCount": 24 },
    { "status": "NEAR_THREATENED", "speciesCount": 3, "sightingCount": 4 },
    { "status": "VULNERABLE", "speciesCount": 2, "sightingCount": 2 },
    { "status": "ENDANGERED", "speciesCount": 1, "sightingCount": 1 },
    { "status": "CRITICALLY_ENDANGERED", "speciesCount": 1, "sightingCount": 1 }
  ]
}
```

`GET /api/stats/lifers` — `bird` and `firstSeenLocation` are the same
`BirdSummary`/`LocationSummary` shapes embedded in `SightingResponse`:

```json
{
  "lifers": [
    {
      "bird": { "id": 7, "commonName": "Mallard", "scientificName": "Anas platyrhynchos", "icon": "🦆", "colorHex": "#2E7D5B" },
      "firstSeenDate": "2026-05-02",
      "firstSeenLocation": { "id": 1, "name": "Everglades National Park", "country": "United States", "countryCode": "us", "icon": "💧" }
    }
  ]
}
```

`GET /api/stats/activity` — `days` is sparse (only dates with at least one
sighting); `longestStreak*` fields are `null`/`0` if there are no sightings:

```json
{
  "days": [
    { "date": "2026-05-02", "sightingCount": 2 },
    { "date": "2026-05-03", "sightingCount": 1 }
  ],
  "longestStreak": 4,
  "longestStreakStart": "2026-05-02",
  "longestStreakEnd": "2026-05-05"
}
```

### Errors

All errors return a consistent JSON shape:

```json
{
  "timestamp": "2026-06-09T12:01:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/sightings",
  "fieldErrors": {
    "observerName": "must not be blank",
    "quantity": "must be greater than or equal to 1"
  }
}
```

| Status | When |
|---|---|
| 400 | Validation failure (`fieldErrors` populated), malformed JSON, or bad query param type |
| 404 | Entity not found, or a sighting references a non-existent `birdId`/`locationId` |
| 409 | Database constraint violation (e.g. duplicate `scientificName`) |
| 500 | Unexpected server error |

### Health

| Method | Path | Description |
|---|---|---|
| GET | `/actuator/health` | `{"status":"UP"}` when the app and DB connection are healthy |

---

## Frontend Routes

| Route | Page | Notes |
|---|---|---|
| `/` | Dashboard | Stats, recent sightings, top species/locations |
| `/birds` | Bird Field Guide | Searchable, filterable, paginated grid |
| `/birds/:id` | Bird Detail | Species details + recent sightings of this bird |
| `/locations` | Birding Locations | Searchable, filterable, paginated grid |
| `/locations/:id` | Location Detail | Location details + recent sightings here |
| `/map` | Map | Leaflet/OpenStreetMap view of all locations |
| `/sightings` | Sighting Journal | Filterable list with edit/delete/favorite |
| `/sightings/new` | Log a Sighting | Create form |
| `/sightings/:id/edit` | Edit Sighting | Edit form, pre-populated |
| `/stats` | Statistics | Sighting timeline, conservation breakdown, activity heatmap, lifer log |
| `*` | Not Found | 404 page |

---

## Testing Hooks (`data-testid` conventions)

Interactive elements throughout the UI carry `data-testid` attributes to give
automated tests stable selectors that don't depend on visual styling or copy.

| Pattern | Example | Used for |
|---|---|---|
| `nav-*` | `nav-dashboard`, `nav-birds`, `nav-locations`, `nav-map`, `nav-sightings`, `nav-stats`, `nav-log-sighting`, `nav-theme-toggle`, `nav-toggle` | Top navigation links/buttons |
| `*-page` | `dashboard-page`, `birds-page`, `bird-detail-page`, `sightings-page`, `sighting-form-page`, `map-page`, `stats-page` | Top-level page containers |
| `bird-card-{id}` | `bird-card-7` | Bird grid card (links to detail page) |
| `location-card-{id}` | `location-card-1` | Location grid card |
| `sighting-row-{id}` | `sighting-row-12` | A row in the sighting journal |
| `*-search-input`, `*-filter`, `*-clear-filters` | `birds-search-input`, `birds-family-filter`, `sightings-favorites-filter` | Filter bar controls on list pages |
| `pagination`, `pagination-prev`, `pagination-next`, `pagination-info` | — | Pagination controls |
| `add-sighting-button` | — | "+ Log Sighting" button on the journal page |
| `favorite-toggle-{id}` | `favorite-toggle-12` | Star button to toggle favorite on a sighting |
| `edit-sighting-{id}`, `delete-sighting-{id}` | — | Row actions in the sighting journal |
| `confirm-modal`, `confirm-modal-confirm`, `confirm-modal-cancel` | — | Delete confirmation dialog |
| `sighting-form`, `field-*` | `field-bird`, `field-location`, `field-date`, `field-time`, `field-quantity`, `field-observer`, `field-weather`, `field-notes`, `field-favorite` | Sighting create/edit form inputs |
| `error-{fieldName}` | `error-observerName` | Inline field validation errors (client- and server-side) |
| `submit-sighting`, `cancel-sighting` | — | Form submit/cancel buttons |
| `toast-success`, `toast-error` | — | Toast notifications after create/update/delete |
| `loading-state`, `empty-state`, `error-banner` | — | Async/empty/error UI states |
| `map-container`, `map-popup-{id}` | `map-popup-1` | Map view container and per-location popup |
| `timeline-chart`, `conservation-chart` | — | Stats page charts (sightings over time, conservation breakdown) |
| `activity-heatmap`, `longest-streak`, `activity-cell-{date}` | `activity-cell-2026-05-02` | Stats page activity calendar and streak callout |
| `lifer-list`, `lifer-{id}` | `lifer-7` | Stats page lifer log |
| `lifer-badge` | — | "🏅 Lifer" badge on the bird detail page |

---

## Environment Variables (Backend)

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `featherlog` | MySQL database name |
| `DB_USERNAME` | `featherlog` | MySQL username |
| `DB_PASSWORD` | `featherlog` | MySQL password |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,http://localhost:3000,http://localhost:4173` | Comma-separated list of allowed CORS origins for `/api/**` |

## Environment Variables (Frontend)

| Variable | Default | Description |
|---|---|---|
| `VITE_API_PROXY_TARGET` | `http://localhost:8080` | Backend target for the Vite dev server's `/api` proxy |

---

## Project Structure

```
featherlog/
├── backend/                  # Spring Boot API
│   ├── src/main/java/com/featherlog/api/
│   │   ├── config/            # CORS / web config
│   │   ├── controller/         # REST controllers
│   │   ├── dto/                # Request/response records
│   │   ├── exception/          # Global exception handling
│   │   ├── model/               # JPA entities + enums
│   │   ├── repository/          # Spring Data repositories + specifications
│   │   └── service/              # Business logic
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/        # Flyway schema + seed data (V1-V4)
│   └── Dockerfile
├── frontend/                  # React + Vite SPA
│   ├── src/
│   │   ├── api/                  # Axios API client modules
│   │   ├── components/            # Reusable UI components
│   │   ├── pages/                  # Route-level pages
│   │   ├── data/                    # Frontend enum option lists
│   │   └── utils/                    # Formatting helpers
│   ├── nginx.conf
│   └── Dockerfile
└── docker-compose.yml
```

---

## Notes

- There is no authentication — every endpoint is open. This keeps the app
  focused on its purpose as a UI/API testing target; auth could be layered on
  top as a future exercise.
- All data lives in MySQL via Flyway-managed migrations, so the dataset is
  identical across fresh environments — handy for deterministic test
  assertions.
