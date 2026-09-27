# Gym Application

The main objective of this project is to provide a complete gym management application. It enables gym trainees and 
trainers to register their profiles, allowing trainees to select trainings. Users can log their activities and view 
them based on their role (trainer or trainee). 

The application provides functionality to modify profile information and activate or deactivate profiles. Access to 
the application (excluding the Registration page) requires login credentials for both trainers and trainees. 
Additionally, the system calculates and provides weekly training duration statistics for each trainer.

## Prerequisites

- Docker and Docker Compose must be installed.
- Commands should be executed from the root directory of the repository.

## Setup Git Hooks

Before starting development, configure Git to use the shared repository hooks. This ensures that the test pipeline 
automatically runs before every push:

```bash
git config core.hooksPath .githooks
```

## Running the Application

This project is fully containerized. A single `./run` script is provided to manage the infrastructure, application, 
and test lifecycles.

### Infrastructure

To start the persistent background infrastructure (PostgreSQL database, pgAdmin, and SonarQube), run:

```bash
./run infra
```

### Start the Application

To compile and start the Java application in detached mode, run:

```bash
./run start
```
To view the live logs of a specific service, use: `docker logs -f <container_name>`

### Run the Tests

To execute the fully isolated test pipeline, run:

```bash
./run test
```
This command compiles the code, runs all tests, generates JaCoCo test coverage reports, and pushes the analysis to the 
SonarQube container. If a test fails, the pre-push hook will block your commit, and the script will preserve the failed 
container so you can inspect its logs.

### Clean the Database

If you need to wipe all data from your database and start completely fresh (without losing SonarQube history or Maven caches), run:

```bash
./run clean-db
```

### Shut Down and Clean Up

To safely stop all running containers and networks while preserving your data (database storage, SonarQube history, 
and Maven caches), run:

```bash
./run down
```

To permanently delete all persistent volumes and start completely fresh, run:

```bash
./run down -v
```

## Configuration

All environment configuration is centralized in the `env/.env` file. You can modify credentials and other values in 
this file, also checkout the `env/.env.example` with default development values.
