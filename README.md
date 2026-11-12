# Student Management System — Java Full Stack CRUD Project

A complete **Create · Read · Update · Delete** web application:

| Layer    | Technology |
|----------|------------|
| Frontend | React 19 + Vite (JavaScript) |
| Backend  | Java 17+, Spring Boot 3.5, Spring Web (REST), Spring Data JPA (Hibernate), Bean Validation |
| Database | H2 in-memory (default, zero setup) **or** MySQL |
| Tools    | Visual Studio Code, Maven, npm |

```
User → React UI (port 5173) → REST API /api/students → Controller → Service → Repository → Database
```

---

## 1. Install the prerequisites (Windows, one time)

1. **JDK 17, 21 or 25** — e.g. Eclipse Temurin from https://adoptium.net (tick "Set JAVA_HOME" during install).
2. **Node.js LTS** — https://nodejs.org (includes npm).
3. **Visual Studio Code** — https://code.visualstudio.com
4. In VS Code, open the Extensions panel (`Ctrl+Shift+X`) and install:
   - **Extension Pack for Java** (Microsoft)
   - **Spring Boot Extension Pack** (VMware/Broadcom) — optional but helpful
   - **REST Client** (Huachao Mao) — optional, to try the API from `api-requests.http`
5. *(Optional)* **Maven** — only needed if you want to run `mvn` commands in a terminal. VS Code's Java extension can build and run the project without it.
6. *(Optional)* **MySQL 8** — only if you want data saved permanently (see section 4).

Check in a terminal:
```
java -version
node -v
npm -v
```

---

## 2. Open and run the project in VS Code

1. Unzip `student-crud.zip`.
2. VS Code → **File → Open Folder…** → select the `student-crud` folder.
3. Wait until the Java extension finishes importing the Maven project (bottom-right status bar shows "Java: Ready"). The first time, it downloads Spring Boot libraries — this needs internet and can take a few minutes.

### Option A — one click (recommended)
Open **Run and Debug** (`Ctrl+Shift+D`), choose **"Full Stack: Backend + Frontend"**, press **F5**.
This starts Spring Boot, runs `npm install` + the React dev server, and opens the app in Microsoft Edge.

### Option B — step by step
1. **Backend:** Run and Debug → **"Backend: Spring Boot (H2 - no setup)"** → F5
   (or open `backend/src/main/java/com/example/studentcrud/StudentCrudApplication.java` and click **Run** above `main`).
   Wait for `Started StudentCrudApplication` in the Debug Console.
2. **Frontend:** open a terminal (`` Ctrl+` ``):
   ```
   cd frontend
   npm install
   npm run dev
   ```
3. Open **http://localhost:5173** in your browser.

You will see 5 sample students. Try **+ Add student**, **Edit**, **Delete** and the search box.

---

## 3. REST API

Base URL: `http://localhost:8080/api/students`

| Operation | Method | URL | Success |
|-----------|--------|-----|---------|
| List all (optional search) | GET | `/api/students?search=ravi` | 200 |
| Get one | GET | `/api/students/{id}` | 200 / 404 |
| Create | POST | `/api/students` | 201 Created |
| Update | PUT | `/api/students/{id}` | 200 |
| Delete | DELETE | `/api/students/{id}` | 204 No Content |

Request body for POST / PUT:
```json
{
  "name": "Kiran Rao",
  "email": "kiran.rao@college.edu",
  "rollNo": "CS2099",
  "department": "Computer Science",
  "yearOfStudy": 2
}
```

Error responses:
- **400** — validation failed, e.g. `{"message":"Validation failed","fieldErrors":{"email":"Email must be valid"}}`
- **404** — student id not found
- **409** — email or roll number already exists

Try them with `api-requests.http` (REST Client extension) or Postman.

**H2 database console:** http://localhost:8080/h2-console
JDBC URL `jdbc:h2:mem:studentdb`, user `sa`, empty password.
Note: H2 data is lost when the backend restarts.

---

## 4. Switch to MySQL (keeps data permanently)

1. Install and start MySQL 8.
2. Edit `backend/src/main/resources/application-mysql.properties` — set your MySQL `username` and `password`.
3. Run **"Backend: Spring Boot (MySQL)"** from Run and Debug.
   The database `studentdb` and the `students` table are created automatically.
   (`database/mysql-setup.sql` shows the equivalent SQL for reference.)

---

## 5. Run the tests

The backend has 7 integration tests (create, read, search, update, delete, validation, duplicate check).

- VS Code: open **Testing** panel (flask icon) → run `StudentControllerTest`, or
- Terminal (needs Maven): `cd backend` then `mvn test`

---

## 6. Project structure

```
student-crud/
├── backend/                         Spring Boot REST API
│   ├── pom.xml                      Maven dependencies
│   └── src/main/java/com/example/studentcrud/
│       ├── StudentCrudApplication.java   main class
│       ├── model/Student.java            @Entity → "students" table
│       ├── dto/StudentRequest.java       request body + validation rules
│       ├── repository/StudentRepository.java   Spring Data JPA
│       ├── service/StudentService.java   business rules (unique email/roll no.)
│       ├── controller/StudentController.java   REST endpoints
│       ├── exception/                    404 / 409 / 400 JSON errors
│       └── config/                       CORS + sample data
├── frontend/                        React app
│   ├── vite.config.js               proxies /api → localhost:8080
│   └── src/
│       ├── api.js                   fetch calls to the backend
│       ├── App.jsx                  page, state, CRUD actions
│       └── components/              StudentTable, StudentForm, ConfirmDialog
├── database/mysql-setup.sql         optional MySQL reference script
├── api-requests.http                ready-made API calls
└── .vscode/                         launch + task configurations
```

---

## 7. Troubleshooting

| Problem | Fix |
|---------|-----|
| "Cannot reach the backend" in the browser | Start the backend first; check it runs on port 8080. |
| `Port 8080 was already in use` | Stop the other app, or change `server.port` in `application.properties` (and the port in `frontend/vite.config.js`). |
| VS Code shows red errors in Java files | Wait for the Maven import to finish; then `Ctrl+Shift+P` → **Java: Clean Java Language Server Workspace**. |
| `java` or `npm` not recognised | Reinstall the JDK / Node.js and restart VS Code so PATH is refreshed. |
| MySQL: `Access denied for user` | Fix username/password in `application-mysql.properties`. |

---

## 8. Ideas to extend it

- Add login with **Spring Security + JWT** (admin vs. student roles).
- Add a **Department** entity with a `@ManyToOne` relationship.
- Add pagination (`Pageable`) and sorting to the table.
- Package with **Docker** and deploy to the cloud.
- Add an AI feature with **Spring AI**, e.g. "summarise this student's progress".
