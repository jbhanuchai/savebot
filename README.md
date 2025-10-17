# SaveBot
AI-powered bill negotiation platform that automates bill tracking, privacy management, and negotiation through intelligent backend APIs.

**Tech Stack:** Java (Spring Boot 3.5+), PostgreSQL, JWT Authentication, React Native (planned), OpenAI API (for AI-driven negotiation).

---

## Structure
- `backend/` – Spring Boot API (fully working JWT-based authentication)
- `frontend/` – React Native mobile app (planned)
- `docs/` – Wireframes, notes, and diagrams

---

## Backend Setup

### 1. Clone the repository
\`\`\`bash
git clone https://github.com/jbhanuchai/savebot.git
cd savebot/backend
\`\`\`

### 2. Configure environment variables
Instead of storing secrets in plain text, create an `.env` file in the backend directory:
\`\`\`bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/savebot
SPRING_DATASOURCE_USERNAME=<your-db-username>
SPRING_DATASOURCE_PASSWORD=<your-db-password>
JWT_SECRET=<your-secret-key>
\`\`\`

Then, reference these in your `application.properties`:
\`\`\`properties
spring.datasource.url=\${SPRING_DATASOURCE_URL}
spring.datasource.username=\${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=\${SPRING_DATASOURCE_PASSWORD}
jwt.secret=\${JWT_SECRET}
\`\`\`

*(The `.env` file should be included in your `.gitignore` — never commit it to GitHub.)*

### 3. Run the backend
\`\`\`bash
mvn clean package -DskipTests
mvn spring-boot:run
\`\`\`

When successful, you should see:
\`\`\`
Tomcat started on port 8080
Started SavebotBackendApplication in X seconds
\`\`\`

---

## API Endpoints

| Endpoint | Method | Description |
|-----------|---------|-------------|
| `/ping` | `GET` | Health check (`pong`) |
| `/auth/signup` | `POST` | Register a new user |
| `/auth/login` | `POST` | Authenticate user and return JWT token |
| `/user/profile` | `GET` | Get logged-in user profile (requires Bearer token) |

---

## Example Usage

\`\`\`bash
# Signup
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"Passw0rd!","name":"Alice"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"Passw0rd!"}'

# Get Profile
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/user/profile
\`\`\`

---

## Next Steps
- 🔒 Improve environment variable management with Spring Boot profiles  
- 🤖 Integrate OpenAI API for intelligent bill analysis  
- 📱 Connect React Native frontend to backend  
- ☁️ Containerize backend with Docker and deploy on AWS  

---

## Author
**Jasti Bhanu Chaitanya**  
📧 [jbhanuchaitanya@gmail.com](mailto:jbhanuchaitanya@gmail.com)  
💻 [GitHub](https://github.com/jbhanuchai)
