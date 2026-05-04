# 🎓 Smart Interview Preparation & Skill Evaluation Platform

A full-stack web application for conducting online MCQ tests and skill evaluation with performance analytics.

🌐 **Live Demo**: [Frontend on Netlify](https://intervieww-prepp.netlify.app/)
⚙️ **Backend API**: [Deployed on Render](https://smart-interview-k8g6.onrender.com)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18 + Vite |
| Routing | React Router v6 |
| Backend | Java 17 + Spring Boot 3.2.2 |
| ORM | Spring Data JPA (Hibernate) |
| Database | PostgreSQL (Neon hosted) |
| Auth | JWT (jjwt 0.11.5) + Spring Security |
| Password | BCryptPasswordEncoder |
| Deployment | Netlify (frontend) + Render (backend) |

---

## ✅ Features

### 🔐 1. User Authentication
- Separate **Student Login** (`/login/student`) and **Admin Login** (`/login/admin`) pages
- Common **Register** page with role selection (Student / Admin)
- Role-based redirect — students go to `/dashboard`, admins go to `/admin`
- Access denied if wrong role tries to log in
- JWT token stored in `localStorage`
- Protected routes via `ProtectedRoute` component

### 🧠 2. Online MCQ Test Module
- 5 topic-wise test categories: **DSA, OS, DBMS, CN, Aptitude**
- 10 questions per test fetched from PostgreSQL via backend
- 10-minute countdown timer with red warning when ≤ 60 seconds remain
- Auto-submit when timer hits zero
- Previous / Next navigation between questions
- **Question Navigator** panel — click any number to jump to that question
- Answered questions highlighted in navigator

### ✅ 3. Auto Evaluation & Results
- Score calculated server-side against correct answers in DB
- Score shown as `X / 10` and percentage
- Performance message: Excellent (≥80%), Good Job (≥60%), Keep Practicing (<60%)
- Latest result shown prominently with option to retake
- Full test history listed with date, time, score, pass/fail badge

### 📊 4. Performance Analytics (History Page)
- Total tests taken, overall average %, areas to improve — shown as stat cards
- **Topic-wise breakdown**: tests taken, average score, best score per category
- **Recent test scores** grid with pass/fail color coding
- **Weak areas panel** (categories with average < 60%) with direct "Improve Now" link

### 👨💻 5. Admin Panel
- **Questions Summary** card showing count per category
- **Category filter** dropdown to view questions by topic
- Add new question: question text, 4 options, correct answer selector, category, difficulty (Easy / Medium / Hard)
- Edit existing questions inline
- Delete questions with confirmation dialog
- **View All Results** tab: table of all student submissions with name, email, category, score, percentage, date
- Seed questions via `POST /api/admin/seed-questions`

### 🧭 6. Navbar
- Shows **Home, Login, Register** when logged out
- Shows **Dashboard, My Results, History** for students
- Shows **Admin Panel** link for admins
- Displays logged-in user's name and role
- Logout clears `localStorage` and redirects to home

---

## 📁 Project Structure

```
SMI-P/
├── backend-springboot/
│   ├── src/main/java/com/smartinterview/
│   │   ├── config/
│   │   │   ├── DataSourceConfig.java   # Parses DATABASE_URL for PostgreSQL
│   │   │   └── SecurityConfig.java     # Spring Security + CORS config
│   │   ├── controller/
│   │   │   ├── AuthController.java     # POST /register, POST /login
│   │   │   ├── QuestionController.java # GET /questions/:category
│   │   │   ├── ResultController.java   # POST /submit, GET /, GET /analytics
│   │   │   ├── AdminController.java    # CRUD questions + GET all results
│   │   │   └── HealthController.java   # GET /health
│   │   ├── model/
│   │   │   ├── User.java               # users entity
│   │   │   ├── Question.java           # questions entity
│   │   │   ├── ResultRecord.java       # results entity
│   │   │   └── JsonListConverter.java  # JSONB options converter
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── QuestionRepository.java
│   │   │   └── ResultRepository.java
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java   # JWT generation & validation
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── service/
│   │   │   └── SeedDataService.java    # Seeds 50 questions on startup
│   │   └── SmartInterviewApplication.java
│   ├── src/main/resources/
│   │   └── application.properties      # server.port, jpa, jwt config
│   └── pom.xml
│
├── frontend/
│   └── vite-project/
│       ├── public/
│       │   └── _redirects              # Netlify SPA routing fallback
│       ├── src/
│       │   ├── components/
│       │   │   ├── Navbar.jsx          # Role-aware top navigation
│       │   │   └── ProtectedRoute.jsx  # Redirects to /login if no token
│       │   ├── pages/
│       │   │   ├── Home.jsx            # Landing page with features & CTA
│       │   │   ├── Login.jsx           # General login
│       │   │   ├── StudentLogin.jsx    # Student-specific login
│       │   │   ├── AdminLogin.jsx      # Admin-specific login
│       │   │   ├── Register.jsx        # Registration with role selector
│       │   │   ├── Dashboard.jsx       # Student dashboard + admin control panel
│       │   │   ├── Test.jsx            # MCQ test with timer & navigator
│       │   │   ├── Result.jsx          # Latest result + full test history
│       │   │   ├── History.jsx         # Analytics, topic performance, weak areas
│       │   │   └── AdminDashboard.jsx  # Question CRUD + all student results
│       │   └── services/
│       │       ├── api.js              # fetch-based service calls to backend
│       │       └── mockData.js         # (unused in production) local question data
│       ├── .env                        # VITE_API_URL=https://smart-interview-k8g6.onrender.com
│       ├── netlify.toml                # Netlify build config + SPA redirects
│       └── package.json
└── README.md
```

---

## 🚀 Local Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (or use Neon hosted DB)

### 1. Backend

```bash
cd backend-springboot
```

Set environment variables:
```
DATABASE_URL=postgresql://<user>:<password>@<host>/<db>?sslmode=require
JWT_SECRET=your_secret_key
```

Build and run:
```bash
mvn spring-boot:run
```

Or run the pre-built jar:
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

> Tables are auto-created via `spring.jpa.hibernate.ddl-auto=update`. Questions are seeded automatically on first startup via `SeedDataService`.

Backend runs at: **http://localhost:5000**

### 2. Frontend

```bash
cd frontend/vite-project
npm install
npm run dev
```

Create `.env`:
```
VITE_API_URL=http://localhost:5000
```

Frontend runs at: **http://localhost:5173**

---

## ☁️ Deployment

### Backend → Render
1. Push to GitHub
2. Go to https://render.com → **New Web Service** → connect repo
3. Set:
   - **Root directory**: `backend-springboot`
   - **Build command**: `mvn clean package -DskipTests`
   - **Start command**: `java -jar target/backend-0.0.1-SNAPSHOT.jar`
4. Add environment variables: `DATABASE_URL`, `JWT_SECRET`
5. Deploy

### Frontend → Netlify
1. Go to https://app.netlify.com → **Add new site** → **Import from GitHub**
2. Set:
   - **Base directory**: `frontend/vite-project`
   - **Build command**: `npm run build`
   - **Publish directory**: `dist`
3. Add environment variable: `VITE_API_URL=https://smart-interview-k8g6.onrender.com`
4. Deploy

---

## 🔗 API Endpoints

| Method | Route | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register user |
| POST | `/api/auth/login` | Public | Login user |
| GET | `/api/questions/:category` | Bearer | Get 10 questions by category |
| POST | `/api/results/submit` | Bearer | Submit test answers |
| GET | `/api/results` | Bearer | Get user's test history |
| GET | `/api/results/analytics` | Bearer | Get performance analytics |
| GET | `/api/admin/questions` | Admin | Get all questions |
| POST | `/api/admin/questions` | Admin | Add new question |
| PUT | `/api/admin/questions/:id` | Admin | Edit question |
| DELETE | `/api/admin/questions/:id` | Admin | Delete question |
| GET | `/api/admin/results` | Admin | Get all student results |
| POST | `/api/admin/seed-questions` | Admin | Seed 50 default questions |

---

## 🗄 Database Schema

### users
| Column | Type |
|---|---|
| id | SERIAL PRIMARY KEY |
| name | TEXT |
| email | TEXT UNIQUE |
| password_hash | TEXT |
| role | TEXT (student / admin) |
| created_at | TIMESTAMP |

### questions
| Column | Type |
|---|---|
| id | SERIAL PRIMARY KEY |
| category | TEXT |
| question | TEXT |
| options | JSONB |
| correct_answer | TEXT |
| difficulty | TEXT (Easy / Medium / Hard) |
| created_at | TIMESTAMP |

### results
| Column | Type |
|---|---|
| id | SERIAL PRIMARY KEY |
| user_id | INTEGER (FK → users) |
| category | TEXT |
| score | INTEGER |
| total_questions | INTEGER |
| percentage | NUMERIC(5,2) |
| created_at | TIMESTAMP |

---

## 📦 Pre-loaded Question Bank

50 questions across 5 categories (10 each) seeded automatically via `SeedDataService.java`:

| Category | Topics Covered |
|---|---|
| DSA | Binary search, sorting, trees, BFS/DFS, hash tables, complexity |
| OS | Processes, scheduling, deadlocks, semaphores, virtual memory |
| DBMS | Normalization, ACID, SQL commands, keys, views, indexing |
| CN | OSI model, TCP/UDP, DNS, IP addressing, MAC, HTTP ports |
| Aptitude | Percentages, speed, simple interest, ratios, LCM/HCF, algebra |

---

## 👤 Usage Guide

### For Students
1. Register with role **Student**
2. Login via `/login` or `/login/student`
3. Dashboard shows stats + 5 test category cards
4. Pick a category → take the timed MCQ test
5. View score on Results page
6. Track progress on History page

### For Admins
1. Register with role **Admin**
2. Login via `/login` or `/login/admin`
3. Dashboard shows Admin Control Panel links
4. Go to Admin Panel → manage questions (add/edit/delete)
5. Switch to "View Results" tab to see all student submissions

---

## 📄 License

This project is for educational purposes.
