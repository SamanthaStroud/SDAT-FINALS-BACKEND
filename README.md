# SDAT Finals — Backend

Spring Boot API for the Software Study Scripts project — an authenticated study-notes app where users browse topics/concepts, take personal notes, and bookmark topics, with an admin panel for user management.

## Stack

- Java 21, Spring Boot 4.1
- Spring Data JPA + PostgreSQL
- Spring Security (session/cookie-based auth, BCrypt password hashing)
- Docker / Docker Compose
- JUnit 5, Mockito, MockMvc

## Running locally

```bash
docker compose up -d db      # start Postgres only
./mvnw spring-boot:run       # run the app
```

Or run the whole stack (app + DB) in Docker:

```bash
docker compose up --build
```

The API runs on `http://localhost:8080`.

## Running tests

```bash
./mvnw test
```

## Entities

- **Topic** — a subject area (e.g. Databases), has many Concepts
- **Concept** — a study item belonging to a Topic
- **User** — a registered account (name, email, hashed password, role)
- **Note** — a personal note a User writes on a Topic
- **Bookmark** — a User's saved/favorited Topic

## Auth

Session-cookie based. `POST /api/auth/register` and `POST /api/auth/login` create an authenticated session; `GET /api/auth/me` reports the current user (or `null`); `POST /api/auth/logout` ends it. Browsing topics/concepts is public; notes, bookmarks, and admin endpoints require a logged-in session; `/api/admin/**` additionally requires the `ADMIN` role.

## User Stories (Manual Test Scenarios)

- As a new user, I can register an account with a name, email, and password.
- As a registered user, I can log in and stay logged in as I navigate the site.
- As a logged-in user, I can create, edit, and delete my own notes on a topic.
- As a logged-in user, I cannot edit or delete another user's notes, even if I know the note's id.
- As a logged-in user, I can bookmark and unbookmark topics, and see my bookmarked topics later.
- As a logged-in user, I can log out, which ends my session.
- As an admin, I can view a list of all registered users.
- As an admin, I can promote a regular user to admin.
- As an admin, I can delete a non-admin user's account.
- As a non-admin, I cannot access the admin endpoints, even if I'm logged in.
- As anyone, an admin account can't be deleted or accidentally demoted through the delete endpoint.

## Docker

```bash
docker compose up --build
```

Builds the API image (multi-stage: Maven build, then a slim JRE runtime) and starts it alongside a Postgres container. Data persists in a named volume across restarts.
