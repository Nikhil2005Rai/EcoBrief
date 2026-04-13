# AI Meeting Transcription & Summarizer

Production-ready full-stack application for uploading meeting audio, transcribing it with Gemini, and generating structured meeting summaries with Spring AI.

## Stack

- Backend: Java 17+, Spring Boot, Spring Web, Spring Data JPA, Spring Security with JWT
- Database: MySQL
- AI:
  - Speech-to-text via Gemini file upload + `generateContent`
  - Summarization via Spring AI `ChatClient`
- Frontend: React + Vite + Axios

## Project Structure

```text
backend/
frontend/
tools/
README.md
```

## How To Run

### 1. Start MySQL

Make sure MySQL is running locally.

Default backend database settings:

```text
database: ai_meeting_db
username: root
password: NikhilRai2005
```

The backend JDBC URL uses `createDatabaseIfNotExist=true`, so MySQL can create the database automatically.

### 2. Configure Backend Keys

Open [backend/.env](c:\Users\lenovo\OneDrive - Noida Institute of Engineering and Technology\Desktop\6th Sem\Advance Java\Project\backend\.env) and set:

```env
GEMINI_API_KEY=your-gemini-api-key
TRANSCRIPTION_API_KEY=your-gemini-api-key
JWT_SECRET=your-base64-secret
```

If you use the same Gemini key for both transcription and summarization, keep both values the same.

### 3. Run Backend

From the project root:

```powershell
cd "c:\Users\lenovo\OneDrive - Noida Institute of Engineering and Technology\Desktop\6th Sem\Advance Java\Project\backend"
..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

### 4. Run Frontend

From the project root:

```powershell
cd "c:\Users\lenovo\OneDrive - Noida Institute of Engineering and Technology\Desktop\6th Sem\Advance Java\Project\frontend"
npm.cmd install
npm.cmd run dev
```

Frontend runs on:

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

- Uploaded files are stored in `backend/uploads/`.
- DTOs are used for API responses.
- Global exception handling and multipart validation are included.
- The backend reads Gemini keys from `backend/.env`.
- Supported audio formats include `mp3`, `wav`, `flac`, `aac`, `ogg`, `webm`, `m4a`, and `mp4`.

## Production Tips

- Replace the default JWT secret with your own secure base64 value.
- Move API keys out of local `.env` and into real environment variables or a secret manager for deployment.
- Do not commit `backend/.env`.
