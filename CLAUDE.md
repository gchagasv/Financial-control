# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project layout

The git repo root and the Maven project are the same folder, `financialcontrol/control/` (it contains `pom.xml`). Run all Maven commands from there.

Spring Boot 4.1.1, Java 21, Spring Data JPA, Bean Validation, Spring WebMVC, PostgreSQL, Lombok (wired as an annotation processor explicitly in `maven-compiler-plugin` in `pom.xml`).

## Commands

- Run: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)
- Build / package: `./mvnw clean package`
- All tests: `./mvnw test`
- Single test: `./mvnw test -Dtest=ControlApplicationTests#methodName`

No linter is configured. The only existing test is the default `contextLoads` test, which needs a reachable PostgreSQL.

## Configuration

- `application.properties` sets `spring.profiles.active=local` and `ddl-auto=update`, so the schema is generated from the entities. There are no migrations.
- `ddl-auto=update` only adds tables and columns. It never changes an existing column's type. After changing a mapping (for example an enum from ORDINAL to `EnumType.STRING`), the old column must be dropped or altered by hand, otherwise inserts fail with a type error.
- `application-local.properties` holds the datasource: PostgreSQL at `localhost:5432/financial_control`. The database must exist before startup. The credentials are still committed in plain text in this public repo; moving them to environment variables and git-ignoring the file is planned. Never print or copy this file's contents.

## Architecture

Layered package structure under `com.financial.control`: `controllers` → `service` → `repositories` → `models`, with `dtos` (Java records, e.g. `CategoryRequest`/`CategoryResponse`) at the API boundary and `exceptions` for errors.

- Category has full CRUD on `/categories`: `POST` (201), `GET` (list), `GET /{id}`, `PUT /{id}`, `DELETE /{id}` (204). `Transaction` and `Asset` entities exist but have no repository, service or controller yet.
- Domain model: `Transaction` has a `TransactionType` (EXPENSE / INCOME / INVESTMENT) and optionally references a `Category` (for expenses and income) or an `Asset` (for investments, with `quantity` and `unitPrice`). Monetary amounts use `BigDecimal`.
- Business rules live in services, not controllers. For example, `CategoryService` rejects `INVESTMENT` categories (`InvalidCategoryException`, 400), case-insensitive duplicate names (`CategoryAlreadyExistsException`, 409; on update it uses `existsByNameIgnoreCaseAndIdNot` so a category can keep its own name) and unknown ids (`CategoryNotFoundException`, 404). Shared logic sits in private helpers (`getOrThrow`, `validateTransactionType`, `toResponse`).
- `GlobalExceptionHandler` (`@RestControllerAdvice`) maps custom exceptions to a JSON body of `{message, status}`. Add a new `@ExceptionHandler` there for each new domain exception. Bean Validation failures (`MethodArgumentNotValidException`) return 400 with a message joined from the field errors, as `field: message, field: message`. Custom `message` values on the DTO annotations keep these in English regardless of the server locale.
- Dependency injection uses Lombok `@RequiredArgsConstructor` with `private final` fields.

## Planned direction

- Asset CRUD next: one `Asset` entity with the `AssetType` enum (no subclass per type), unique by ticker (case-insensitive, stored trimmed and uppercase).
- Market data from the brapi API (`https://brapi.dev/api/v2/stocks/quote` and `/historical`), behind a provider interface so it can be swapped. The token comes from an environment variable and is sent as `Authorization: Bearer`. The free plan allows at most 90 days per historical request and returns previous-day data, so daily closes will be stored in our own database (backfilled in chunks, then a daily job).
- P/VP from CVM open data. A React + TypeScript frontend will draw the charts.
