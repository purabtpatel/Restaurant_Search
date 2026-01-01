# Restaurant Search Setup Guide

This is a simple full-stack application using a Spring Boot backend and a Vite-based frontend that has a search function on preloaded data. The project uses a single command to install dependencies, build the backend, and run both servers concurrently.

---

## Prerequisites

Before you begin, ensure the following tools are installed on your machine:

* **Java Development Kit (JDK) 17**
* **Node.js** (includes npm)

You can verify your installations with:

```bash
java -version
node -v
npm -v
```

---

## Supported Versions

The following versions are known to work correctly and are recommended:

### Backend

* **Java Version**: 17
* **Spring Boot Parent**: 3.2.0
* **Build Tool**: Maven
* **Server Startup Command**:

  ```bash
  java -jar target/server-0.0.1-SNAPSHOT.jar
  ```

These settings are defined in `server/pom.xml`.

### Frontend

* **Framework**: Vite
* **Vite Version**: 7.2.7

These settings are defined in `client/package.json`.

---

## Project Structure

```
root/
├── server/        # Spring Boot backend
├── client/        # Vite frontend
├── package.json   # Root scripts and concurrently config
└── README.md
```

---

## Installation and Setup

Follow the steps below in order.

### 1. Install Root Dependencies

From the project root directory, install Node.js dependencies. This installs `concurrently`, which is used to run the backend and frontend together.

```bash
npm install
```

---

### 2. Install Frontend Dependencies

Navigate to the client folder and install the frontend dependencies.

```bash
cd client
npm install
```

Return to the project root when finished:

```bash
cd ..
```

### 3. Backend Configuration

Navigate to the backend folder and create a `developer.properties` file for your API keys.

```bash
cd server/src/main/resources
touch developer.properties
```

Add the following line to `developer.properties` (replace `your_api_key_here` with your actual OpenAI API key):

```properties
spring.ai.openai.api-key=your_api_key_here
```

Return to the project root when finished:

```bash
cd ../../../..
```

---

## Running the Application

From the project root, run:

```bash
npm start
```

This command will:

1. Build the Spring Boot backend using Maven
2. Download all required Java dependencies
3. Start the Spring Boot server
4. Start the Vite development server

Both servers run concurrently from a single command.

---

## Accessing the Application

* **Backend API**: Runs on port `http://localhost:8080`
* **Frontend UI**: Available at `http://localhost:5173`

Check the console output for the exact ports if they differ.

---

## Troubleshooting

* Ensure Java 17 is the active Java version on your system
* If dependency issues occur, try deleting `node_modules` and rerunning `npm install`
* For backend issues, verify Maven is correctly downloading dependencies during startup

---

## Notes

* This project is designed for local development
* An OpenAI API key is required for AI-powered features, configured in `developer.properties`

---

## Assumptions & compromises

* I chose to focus on the search functionality and the structure of the spring boot backend
* I did not focus too much on the frontend and allowed claude agent to generate a majority of the code given the GET endpoint

You should now be able to clone the repository, install dependencies, and run the application successfully.
