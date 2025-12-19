# Job Portal Backend - Spring Boot

A comprehensive backend application for a Job and Skill Development Platform built with Spring Boot and MySQL.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Building the Project](#building-the-project)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

This is a Spring Boot-based backend application for a Job Portal and Skill Development Platform. The application provides RESTful APIs for managing job postings, user profiles, and skill development resources.

## ✨ Features

- RESTful API architecture
- MySQL database integration
- JPA/Hibernate for data persistence
- Spring Boot DevTools for development
- Lombok integration for cleaner code
- Automatic database schema management

## 🛠 Technologies

- **Java**: 17
- **Spring Boot**: 3.5.3
- **Spring Web**: For REST API development
- **Spring Data JPA**: For database operations
- **MySQL**: Database
- **Hibernate**: ORM framework
- **Lombok**: To reduce boilerplate code
- **Maven**: Build and dependency management
- **Spring Boot DevTools**: For hot reloading during development

## 📦 Prerequisites

Before running this application, ensure you have the following installed:

- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Git (for version control)

## 🚀 Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/risinsilv/jobportal-backend-spring.git
   cd jobportal-backend-spring
   ```

2. **Set up MySQL database:**
   ```bash
   mysql -u root -p
   ```
   
   Create a database (or let the application create it automatically):
   ```sql
   CREATE DATABASE demo_spring;
   ```

3. **Install dependencies:**
   ```bash
   ./mvnw clean install
   ```
   
   Or if you have Maven installed globally:
   ```bash
   mvn clean install
   ```

## ⚙️ Configuration

The application configuration is located in `src/main/resources/application.properties`.

### Database Configuration

Update the following properties with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demo_spring?createDatabaseIfNotExist=true
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

**Security Note:** Never commit sensitive credentials to version control. Consider using:
- Environment variables for production deployments
- `application-local.properties` (add to `.gitignore`) for local development
- Spring Boot profiles for different environments

**Example using environment variables:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demo_spring?createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME:defaultuser}
spring.datasource.password=${DB_PASSWORD:defaultpassword}
```

### JPA/Hibernate Configuration

The application is configured to:
- Automatically create/update database schema (`spring.jpa.hibernate.ddl-auto=update`)
- Show SQL queries in console (`spring.jpa.show-sql=true`)
- Use MySQL8 dialect

## 🏃 Running the Application

### Using Maven Wrapper (Recommended)

**On Unix/Linux/macOS:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

### Using Maven

If you have Maven installed globally:
```bash
mvn spring-boot:run
```

### Using Java

First, build the project:
```bash
./mvnw clean package
```

Then run the JAR file:
```bash
java -jar target/jobportal-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` by default.

## 🏗️ Building the Project

To build the project without running tests:
```bash
./mvnw clean package -DskipTests
```

To build with tests:
```bash
./mvnw clean package
```

The built JAR file will be located in the `target/` directory.

## 🧪 Testing

Run all tests using Maven:
```bash
./mvnw test
```

Or run a specific test:
```bash
./mvnw test -Dtest=JobportalApplicationTests
```

## 📁 Project Structure

```
jobportal-backend-spring/
├── .mvn/                           # Maven wrapper files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── me/
│   │   │       └── risinu/
│   │   │           └── jobportal/
│   │   │               └── JobportalApplication.java    # Main application class
│   │   └── resources/
│   │       └── application.properties    # Application configuration
│   └── test/
│       └── java/
│           └── me/
│               └── risinu/
│                   └── jobportal/
│                       └── JobportalApplicationTests.java    # Test class
├── target/                         # Compiled files (generated)
├── .gitignore                      # Git ignore file
├── mvnw                           # Maven wrapper script (Unix)
├── mvnw.cmd                       # Maven wrapper script (Windows)
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

### Package Structure

The application follows a standard Spring Boot project structure:
- **Main application**: `me.risinu.jobportal.JobportalApplication`
- **Group ID**: `me.risinu`
- **Artifact ID**: `jobportal`
- **Version**: `0.0.1-SNAPSHOT`

## 📚 API Documentation

The application exposes RESTful APIs at `http://localhost:8080`.

### Base URL
```
http://localhost:8080
```

### Endpoints

*Note: API endpoints will be available as controllers are implemented.*

Common API patterns:
- `GET /api/resource` - Retrieve all resources
- `GET /api/resource/{id}` - Retrieve a specific resource
- `POST /api/resource` - Create a new resource
- `PUT /api/resource/{id}` - Update a resource
- `DELETE /api/resource/{id}` - Delete a resource

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Use Lombok annotations where appropriate to reduce boilerplate code
- Write unit tests for new features
- Ensure all tests pass before submitting a PR
- Follow Spring Boot best practices

## 📄 License

This project is part of a job portal and skill development platform.

## 👥 Authors

- **risinsilv** - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- MySQL for the robust database system
- All contributors who help improve this project

## 📞 Support

For support, please open an issue in the GitHub repository.

---

**Happy Coding!** 🚀
