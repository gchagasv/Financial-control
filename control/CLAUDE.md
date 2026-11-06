# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project layout

The Maven project lives in `financialcontrol/control/control/` (nested folders; the git repo root is `financialcontrol/control/`). Run all Maven commands from the directory containing `pom.xml`.

Spring Boot 4.1.1, Java 21, Spring Data JPA, Bean Validation, Spring WebMVC, PostgreSQL, Lombok (wired as an annotation processor explicitly in `maven-compiler-plugin` in `pom.xml`).

## Commands

- Run: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)
- Build / package: `./mvnw clean package`
- All tests: `./mvnw test`
- Single test: `./mvnw test -Dtest=ControlApplicationTests#methodName`

No linter is configured. The only existing test is the default `contextLoads` test, which needs a reachable PostgreSQL.

## Configuration

- `application.properties` sets `spring.profiles.active=local` and `ddl-auto=update`, so the schema is generated from the entities. There are no migrations.
- `application-local.properties` holds the datasource: PostgreSQL at `localhost:5432/financial_control`, with the credentials committed in plain text. The database must exist before startup.

## Architecture

Layered package structure under `com.financial.control`: `controllers` → `service` → `repositories` → `models`, with `dtos` (Java records, e.g. `CategoryRequest`/`CategoryResponse`) at the API boundary and `exceptions` for errors.

- Only the Category feature is implemented end to end (`POST /categories`). `Transaction` and `Asset` entities exist but have no repository, service or controller yet.
- Domain model: `Transaction` has a `TransactionType` (EXPENSE / INCOME / INVESTMENT) and optionally references a `Category` (for expenses and income) or an `Asset` (for investments, with `quantity` and `unitPrice`). Monetary amounts use `BigDecimal`.
- Business rules live in services, not controllers. For example, `CategoryService` rejects `INVESTMENT` categories (`InvalidCategoryException`, 400) and case-insensitive duplicate names (`CategoryAlreadyExistsException`, 409).
- `GlobalExceptionHandler` (`@RestControllerAdvice`) maps custom exceptions to a JSON body of `{message, status}`. Add a new `@ExceptionHandler` there for each new domain exception.
- Dependency injection uses Lombok `@RequiredArgsConstructor` with `private final` fields.
