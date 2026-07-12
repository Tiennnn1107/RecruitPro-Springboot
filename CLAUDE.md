# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RecruitBox is a Spring Boot recruitment platform with MySQL, Thymeleaf (server-side rendering), and Bootstrap 5 frontend. It supports three user roles: CANDIDATE, RECRUITER, and ADMIN.

## Build Commands

```bash
# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ApplicationServiceTest

# Package the application
./mvnw package

# Clean and build
./mvnw clean package
```

## Architecture

**Layered Structure:**
- `controller/` - REST controllers and view controllers handling HTTP requests
- `service/` - Business logic layer
- `repository/` - Data access layer (Spring Data JPA)
- `entity/` - JPA entities (database tables)
- `dto/` - Data transfer objects (request/response models)
- `enums/` - Enumerations (Role, JobStatus, ApplicationStatus, etc.)
- `security/` - JWT authentication and Spring Security configuration
- `config/` - Application configuration and exception handling
- `util/` - Utility classes (JWT handling)

**User Roles:**
- CANDIDATE - Can browse jobs, apply, manage profile and CV
- RECRUITER - Can post jobs, manage applications, schedule interviews
- ADMIN - Full access

**Key Entities:** User, CandidateProfile, Company, JobPosting, JobField, Application, Interview, MyCV, LinkCV, Notification

## Database

- MySQL 8.0 required
- Database: `recruitment_db` (utf8mb4_unicode_ci)
- Hibernate auto-creates tables (`spring.jpa.hibernate.ddl-auto=update`)
- Sample data loaded from `src/main/resources/sql/data.sql` on startup

## Configuration

- `src/main/resources/application.properties` - Main configuration
- Database credentials, JWT secret, mail settings, file upload limits (5MB max)
- Environment variables: `MAIL_USERNAME`, `MAIL_PASSWORD`, `JWT_SECRET`

## Sample Accounts

| Email | Password | Role |
|-------|----------|------|
| recruiter@techviet.com | password123 | RECRUITER |
| candidate@email.com | password123 | CANDIDATE |
| admin@recruitpro.com | password123 | ADMIN |

## Frontend

Thymeleaf templates in `src/main/resources/templates/` organized by role:
- `candidate/` - Candidate-facing pages
- `recruiter/` - Recruiter dashboard (jobs, applications, interviews subfolders)
- `auth/` - Login/register pages
- `layout/` - Base layouts with header/footer
- `common/` - Shared components
