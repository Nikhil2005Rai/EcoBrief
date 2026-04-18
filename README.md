# ECHOBRIEF

EchoBrief is a full-stack app for uploading meeting audio, transcribing it with Gemini, and generating structured summaries with Spring AI.

## Stack

- Backend: Java 17, Spring Boot, Spring Web, Spring Data JPA, Spring Security, JWT
- Database: MySQL
- AI: Gemini transcription and Spring AI summarization
- Frontend: React, Vite, Axios

## Prerequisites

Make sure you have installed:

- Java 17 or newer
- Maven 3.9+ or the Maven wrapper
- Node.js 18+ and npm
- MySQL 8+
- A Gemini API key

## Project Layout

```text
backend/
frontend/
README.md
```

## Configuration

### 1. Backend env file

Create `backend/.env` manually, or copy it from `backend/.env.sample` if that file is present, and set:

```env
GEMINI_API_KEY=your-gemini-api-key
TRANSCRIPTION_API_KEY=your-gemini-api-key
JWT_SECRET=your-base64-secret
```

If you want to use the same key for transcription and summarization, keep both API key values the same.

### 2. MySQL settings

The backend connects to MySQL using the settings in `backend/src/main/resources/application.yml`.

Default local dev values are:

```text
database: ai_meeting_db
username: root
password: rootpasscode
```

If your local MySQL credentials are different, update the datasource section in `application.yml` before starting the backend.

## Run The Project

### 1. Start MySQL

Make sure your MySQL server is running and accessible on `localhost:3306`.

### 2. Start the backend

From the project root:

```bash
cd backend
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

### 3. Start the frontend

Open a second terminal from the project root:

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## API Endpoints

- `POST /auth/signup`
- `POST /auth/login`
- `POST /meeting/upload`
- `GET /meeting/all`
- `GET /meeting/{id}`

## Notes

- The frontend points to `http://localhost:8080` by default.
- Uploaded files are stored in `backend/uploads/`.
- Supported audio formats include `mp3`, `wav`, `flac`, `aac`, `ogg`, `webm`, `m4a`, and `mp4`.
- Do not commit `backend/.env`.

## Troubleshooting

- If login or upload fails, confirm the backend is running and your JWT secret is set.
- If the backend cannot connect to MySQL, verify the username and password in `application.yml`.
- If uploads fail with an AI-related error, confirm your Gemini key is valid.
