# Smart Water Usage Monitoring and Automated Billing Management Platform

**Team:** Code_Crew\
**Stack:** Spring Boot + React.js + PostgreSQL\
**Roadmap:** 8 weeks

## 1. Overview

The Smart Water Usage Monitoring and Automated Billing Management
Platform is a full-stack web application for apartment communities. It
records household water consumption, tracks bulk water purchases,
calculates bills using configurable tiered tariffs, allocates shared
water costs, identifies abnormal usage, and sends notifications.

Residents can view daily/monthly usage charts, billing previews, invoice
history, household comparisons, and water-saving tips. Administrators
can manage apartments, households, meter readings, tariff plans, bulk
purchases, billing cycles, alerts, and downloadable PDF invoices.

## 2. Problem Statement

Apartment communities need a transparent way to monitor household
consumption, fairly distribute shared water costs, and identify
potential leaks or abnormal usage. This platform centralizes usage
records, billing, cost allocation, alerts, and invoices to help
residents understand their bills and help administrators manage water
use and expenditure.

## 3. Required Modules

### Module 1 --- Apartment & Household Schema, Water Usage Logging & Core REST APIs

-   PostgreSQL schema for apartments, households, users, water usage
    logs, billing cycles, and tariff plans.
-   JPA entity relationships and Flyway migrations.
-   Spring Security 6 and JWT authentication with Admin and Resident
    roles.
-   Registration, login, profile management, apartment onboarding,
    household registration, resident assignment, and meter configuration
    APIs.
-   Jakarta Bean Validation for incoming data.
-   Manual daily meter readings and bulk CSV upload with parsing,
    validation, and duplicate detection.
-   JUnit 5, Mockito, `@SpringBootTest`, and MockMvc tests.

### Module 2 --- Billing Engine, Consumption Distribution & Alert System

-   Configurable tiered tariff engine. The specification gives an
    example of a base rate for the first 10 kL and a higher rate beyond
    that.
-   Bulk water purchase tracking for tanker deliveries and municipal
    supply billing, including volume and unit cost.
-   Consumption-proportional household cost allocation, with flat-area
    allocation as a fallback for households without meters.
-   Scheduled email and in-app notifications using Spring `@Scheduled`.
-   Statistical anomaly detection for usage more than two standard
    deviations above a household average (`> 2σ`), flagged as a
    potential leak indicator.
-   Billing-cycle opening, finalization, archiving, and itemized invoice
    records containing base charges, shared-area allocation, and
    adjustments.

### Module 3 --- React.js Resident Dashboard, Admin Panel & Invoice Generation

-   Resident dashboard with Recharts daily/monthly trends, billing
    summary, invoice history, and water-saving tips.
-   Admin panel with household comparisons, meter upload, billing-cycle
    controls, tariff configuration, and bulk purchase entry.
-   Backend-generated downloadable PDF invoices using Apache PDFBox or
    iText.
-   Household comparison against apartment average and similar-sized
    households.
-   JavaMail or SendGrid for bill notifications, overuse alerts, and
    anomaly reports.

### Module 4 --- System Integration, Testing & Project Finalization

-   End-to-end integration from usage logging through billing,
    allocation, alert scheduling, and dashboard.
-   Edge-case fixes, JMeter or k6 load/stress tests, and
    Chrome/Firefox/Edge validation.
-   Responsive mobile/tablet UI.
-   Architecture and ER diagrams, Swagger/OpenAPI documentation via
    springdoc-openapi, deployment guide using Docker Compose, Flyway
    migration documentation, known limitations, final PPT, and rehearsed
    demo.

## 4. Technology Stack

  Layer                 Required technology
  --------------------- ---------------------------------------------
  Frontend              React.js
  Charts                Recharts
  Backend               Java, Spring Boot
  Security              Spring Security 6, JWT
  Database              PostgreSQL
  ORM / migrations      Spring Data JPA, Hibernate, Flyway
  Validation            Jakarta Bean Validation
  Tests                 JUnit 5, Mockito, Spring Boot Test, MockMvc
  PDF invoices          Apache PDFBox or iText
  Scheduling            Spring `@Scheduled`
  Email                 JavaMail or SendGrid
  API documentation     springdoc-openapi / Swagger UI
  Performance testing   Apache JMeter or k6
  Deployment            Docker Compose

**Architecture:** React frontend → Spring Boot REST API → PostgreSQL.
The browser must not connect directly to PostgreSQL.

## 5. High-Level Architecture

``` text
Resident / Administrator
          |
          v
    React.js Frontend
    - Resident dashboard
    - Admin panel
    - Charts, alerts, invoices
          |
       HTTP/JSON
          |
          v
   Spring Boot REST API
   - JWT authentication / roles
   - Apartment and household APIs
   - Usage logging and CSV import
   - Billing and cost allocation
   - Alert / anomaly services
   - PDF invoice and email services
          |
          v
       PostgreSQL
   - Users, apartments, households
   - Meters and usage logs
   - Tariffs and purchases
   - Billing cycles, bills, alerts
```

## 6. Suggested Database Entities

Finalize these with an ER diagram during Weeks 1--2.

-   **Apartment:** community details and configuration.
-   **User:** profile, credentials, and role.
-   **Household:** apartment, flat identifier, area, occupancy if
    available, and resident association.
-   **WaterMeter:** meter identifier, household association, and
    status/configuration.
-   **WaterUsageLog:** meter/household, reading date, value or
    consumption, source (manual/CSV), and audit metadata.
-   **TariffPlan:** apartment, tier thresholds, rates, and effective
    dates if needed.
-   **BulkWaterPurchase:** source/vendor, volume, unit cost, purchase
    date, and billing-cycle association.
-   **BillingCycle:** apartment, period, state
    (open/finalized/archived), timestamps.
-   **HouseholdBill / Invoice:** cycle, household, consumption charge,
    shared allocation, adjustments, total, and invoice reference.
-   **Alert / Notification:** apartment or household, type, message,
    trigger value, status, and timestamps.

Use foreign keys, appropriate uniqueness constraints/indexes, and
decimal types for currency. Define duplicate-reading rules. Decide
whether meter input is a cumulative reading or a period consumption
amount; document and test that choice.

## 7. Core Business Rules

### Water readings

-   Accept manual readings and CSV imports.
-   Validate apartment, household, meter, date, and numeric values.
-   Reject malformed, negative, or duplicate data according to
    documented rules.
-   If cumulative meter readings are used, calculate period consumption
    from successive readings and handle resets/decreasing values
    explicitly.

### Tiered tariff

-   Store configurable tier thresholds/rates per apartment.
-   Implement the example of a base rate for the first 10 kL and a
    higher rate beyond it, while keeping the threshold configurable.
-   Test below, exactly at, and above each tier boundary.
-   Store a charge breakdown so the bill is explainable.

### Bulk purchase and shared allocation

-   Record purchased volume and unit cost.
-   Link purchases and bills to a billing cycle.
-   Allocate costs proportionally to metered household consumption.
-   Use flat-area distribution as the fallback for households without
    meters.
-   Document rounding and reconciliation rules.

### Alerts and anomaly detection

-   Support configurable usage thresholds.
-   Alert when a household exceeds its threshold.
-   Flag usage `> 2σ` above the household average as a potential
    anomaly.
-   Define the historical baseline window and minimum data needed; avoid
    claiming a leak is confirmed.
-   Prevent repeated notifications for the same event where practical.

### Billing cycles

-   Support open, finalize, and archive operations.
-   Define what can change after finalization.
-   Preserve invoice breakdowns and adjustments; do not silently
    recalculate finalized bills.

## 8. Suggested REST API Groups

These are suggested resource groups, not existing implemented endpoints.
Final routes/payloads must be documented in Swagger/OpenAPI.

  Group            Responsibility
  ---------------- -----------------------------------------------------
  Authentication   Register/login, JWT, profile
  Apartments       Apartment/community management
  Households       Registration and resident assignment
  Meters           Meter configuration
  Usage logs       Add/list readings, CSV import, duplicate checks
  Tariffs          View/configure tier thresholds and rates
  Bulk purchases   Record/list water purchases
  Billing cycles   Open, finalize, archive, inspect
  Bills/invoices   Preview, itemized bill, PDF download
  Allocations      Explain shared-cost calculations
  Alerts           List, acknowledge, resolve, inspect anomaly details
  Dashboard        Resident/admin metrics and chart data

Use consistent JSON, HTTP status codes, validation errors, pagination
where needed, and server-side role checks. Residents must not be
authorized to perform admin operations.

## 9. Eight-Week Milestone Plan

### Weeks 1--2 --- Database, security, core APIs

1.  Create repository, backend/frontend skeletons, environment
    configuration, and coding conventions.
2.  Create PostgreSQL schema, JPA entities, relationships, and Flyway
    migrations.
3.  Implement JWT authentication and Admin/Resident authorization.
4.  Implement apartment, household, resident, and meter APIs.
5.  Implement manual readings and CSV import with validation/duplicate
    detection.
6.  Add JUnit/Mockito/MockMvc tests. **Evidence:** ER diagram, login
    demo, documented APIs, and working usage-entry flow.

### Weeks 3--4 --- Billing, allocation, alerts

1.  Implement configurable tiered tariffs.
2.  Implement bulk purchase tracking.
3.  Implement proportional cost allocation and flat-area fallback.
4.  Implement billing-cycle states and itemized bills.
5.  Implement scheduled thresholds and `> 2σ` anomaly flags.
6.  Test tariff boundaries, missing meters, rounding, duplicate
    readings, and insufficient anomaly history. **Evidence:** repeatable
    sample bill calculation, allocation breakdown, and alert demo.

### Weeks 5--6 --- React UI, invoices, email

1.  Add React routing, login, and role-specific navigation.
2.  Build resident charts, billing summary, invoice history, and tips.
3.  Build admin usage comparisons, uploads, tariff/purchase forms, and
    cycle controls.
4.  Add household benchmarking.
5.  Generate backend PDF invoices.
6.  Integrate email notifications. **Evidence:** resident/admin journeys
    connected to the actual API and a downloadable invoice.

### Weeks 7--8 --- Integration and finalization

1.  Test usage → storage → billing → allocation → alert → dashboard →
    invoice.
2.  Fix integration bugs and edge cases.
3.  Run JMeter/k6 tests and document results.
4.  Validate browsers and responsive layouts.
5.  Finish OpenAPI, ERD, setup guide, Docker Compose guide, migrations,
    and known limitations.
6.  Prepare PPT and rehearse the complete demo. **Evidence:**
    reproducible deployment, test evidence, integrated demo, and
    complete documentation.

## 10. Recommended Build Order

1.  Repository and project skeletons.
2.  Database schema and migrations.
3.  Authentication and roles.
4.  Apartment/household/resident/meter management.
5.  Usage logging and CSV validation.
6.  Tariff engine and unit-tested calculations.
7.  Bulk purchases and shared allocation.
8.  Billing-cycle management and invoice records.
9.  Threshold alerts and anomaly detection.
10. React layout, login, and role-specific dashboards.
11. Connect forms/charts to real APIs.
12. PDF invoices and email.
13. Integration, performance, browser, and responsive tests.
14. Documentation, Docker Compose, slides, and demo rehearsal.

Build one vertical slice at a time. A feature is not complete until its
database, backend, UI, validation, and tests work together.

## 11. Suggested Five-Member Team Allocation

-   **Member 1 --- Backend/API lead:** Spring Boot structure, REST
    conventions, authentication integration, API contracts.
-   **Member 2 --- Database/billing:** PostgreSQL, JPA, Flyway, tariff
    engine, billing cycles, purchases, allocations.
-   **Member 3 --- Resident frontend:** React resident dashboard,
    Recharts, bill summary, history, comparisons, tips.
-   **Member 4 --- Admin/notifications:** Admin UI, CSV upload,
    tariff/purchase forms, alerts, email, invoice-download integration.
-   **Member 5 --- QA/integration/docs:** test strategy, end-to-end and
    performance tests, Docker, documentation, demo coordination.

This is a suggested split. Assign named owners, review API contracts
early, and integrate continuously rather than waiting until the end.

## 12. Optional Differentiators

These are recommendations, not mandatory requirements. Implement them
only after the required flow works.

1.  **Explainable anomaly alerts:** show the baseline, recent usage,
    deviation, and reason for the alert.
2.  **Apartment water-balance view:** compare purchased water, metered
    household consumption, and common-area allocation; flag unexplained
    differences for investigation.
3.  **Bill what-if preview:** estimate how reduced consumption could
    affect the next bill under the configured tariff.
4.  **Water-efficiency indicator:** show trends and comparisons with
    suitable peer households.
5.  **IoT-ready ingestion:** keep the usage API ready for future sensor
    integration while supporting manual/CSV entry now.

Do not describe an optional feature as completed unless it is
implemented and tested.

## 13. Testing Strategy

-   **Authentication:** valid/invalid login, invalid/expired JWT, role
    restrictions.
-   **Usage:** valid/invalid values, duplicates, unknown meters, CSV
    errors, dates.
-   **Billing:** zero usage, tariff boundaries, high usage, rounding,
    adjustments.
-   **Allocation:** all metered, some unmetered, zero-consumption cases,
    reconciliation.
-   **Anomalies:** normal values, spikes, insufficient history, repeated
    alerts.
-   **Cycles:** open/finalize/archive and post-finalization changes.
-   **Invoices:** line items, totals, PDF response, authorization.
-   **Integration:** usage input through invoice download.
-   **Frontend:** loading, empty, validation, success/error states,
    responsive screens.
-   **Performance:** record setup, load, response results, and
    bottlenecks. Never claim results that have not been measured.

## 14. Security and Configuration

-   Never commit passwords, JWT secrets, API keys, or real resident
    data.
-   Use secure password hashing supported by Spring Security.
-   Enforce authorization on the backend; hiding UI buttons is not
    security.
-   Validate input on the server and configure CORS for the frontend
    origin.
-   Avoid logging credentials or JWTs.
-   Use separate local and production configurations.
-   Keep `.env.example` limited to placeholder values.

## 15. Repository Layout

``` text
smart-water-platform/
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/
│   ├── src/
│   ├── package.json
│   └── ...
├── docs/
│   ├── architecture.md
│   ├── database-erd.md
│   ├── api-guide.md
│   └── demo-checklist.md
├── .env.example
├── .gitignore
├── docker-compose.yml
└── README.md
```

This is the recommended structure; create files as implementation
progresses.

## 16. Local Setup

### Prerequisites

-   Git
-   Java version compatible with the selected Spring Boot version
-   Maven or Maven Wrapper
-   Node.js and npm
-   PostgreSQL
-   Optional: Docker and Docker Compose

### Setup sequence

1.  Clone the repository.
2.  Create a local PostgreSQL database and configure backend connection
    variables.
3.  Start the backend so Flyway migrations run.
4.  Install frontend dependencies with `npm install` inside `frontend/`.
5.  Configure the frontend API base URL to point to the backend.
6.  Start the React development server.
7.  Open Swagger UI and test the endpoints.

Add exact commands and tested URLs after the actual build files, ports,
and scripts are created. Do not guess commands that have not been
tested.

### Illustrative environment variables

``` dotenv
DB_HOST=localhost
DB_PORT=5432
DB_NAME=smart_water_db
DB_USERNAME=your_local_username
DB_PASSWORD=replace_with_local_password

JWT_SECRET=replace_with_a_secure_local_secret
JWT_EXPIRATION_MINUTES=60

# For a Vite frontend; adapt if another React setup is chosen
VITE_API_BASE_URL=http://localhost:8080

# Configure only when email is implemented
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=
```

These names are examples and must be aligned with the actual application
configuration. Never commit real secrets.

## 17. Judge Demo (5--7 Minutes)

1.  Explain the apartment water-use and shared-billing problem.
2.  Log in as a resident and show usage trends, bill summary, and
    invoice history.
3.  Log in as an admin and add/upload a reading.
4.  Show validation and the saved reading.
5.  Explain the configured tariff and bill calculation.
6.  Show shared-cost allocation and its fallback rule if relevant.
7.  Trigger a prepared overuse/anomaly example.
8.  Download an itemized household PDF invoice.
9.  Present one implemented differentiator and the evidence for it.

Use prepared demo data and rehearse the flow. If an external email
service is unavailable during the presentation, show recorded/test-mode
evidence honestly rather than claiming a live email was delivered.

## 18. Definition of Done

A feature is complete when: - Code is committed to the shared
repository. - Validation and expected error cases are handled. - Backend
authorization is enforced where required. - Automated tests cover core
logic. - The UI uses the real backend endpoint. - The integrated flow
has been tested. - Documentation is updated. - The team can demonstrate
it without manual database edits.

## 19. Project Status

This README documents planned scope and implementation guidance. It does
**not** claim that the application is already implemented. Update
status, screenshots, test results, deployment URLs, and completed
features only after verifying them in the repository.

**Team:** Code_Crew\
**Project:** Development of a Smart Water Usage Monitoring and Automated
Billing Management Platform

## 20. Future Scope

Possible extensions include IoT water-meter integration, more advanced
leak detection, apartment water-balance analytics, conservation
programmes, and additional reporting. These are future possibilities,
not substitutes for completing the required modules.

## 21. Requirements Basis

This README follows the supplied project specification: four
implementation modules, an eight-week roadmap, Spring Boot REST backend,
React.js frontend, PostgreSQL, tiered tariffs, bulk purchase tracking,
shared-cost allocation, scheduled alerts and `> 2σ` anomaly flags,
dashboards, PDF invoices, email notifications, testing, documentation,
and final demonstration.
