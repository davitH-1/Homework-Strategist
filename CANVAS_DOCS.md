# 🎓 Canvas API Wrapper

This Spring Boot service acts as a secure middleware for interacting with the **Canvas LMS**. It handles authentication internally, allowing the frontend or terminal users to fetch course and assignment data without needing to manage sensitive API tokens.

---

## 🛠 Setup & Requirements

* **Environment**: Java 17+ and Spring Boot.
* **Configuration**: Requires a `.env` file in the root directory with your `CANVAS_ACCESS_TOKEN`.
* **Base URL**: `http://localhost:8080/api/canvas`.

---

## 🚀 API Endpoints

### 1. Get All Active Courses
Returns a list of courses where the user is currently active.
* **Endpoint**: `GET /courses`
* **Command**:
    ```zsh
    curl -i http://localhost:8080/api/canvas/courses
    ```

### 2. Get All Assignments for a Course
Retrieves all assignments for a specific course ID.
* **Endpoint**: `GET /assignments/{courseId}`
* **Command**:
    ```zsh
    curl -i http://localhost:8080/api/canvas/assignments/60000000000030087
    ```

### 3. Get Specific Assignment Details
Fetches metadata for a single assignment, including processed description fields.
* **Endpoint**: `GET /courses/{courseId}/assignments/{assignmentId}`
* **Command**:
    ```zsh
    curl -i http://localhost:8080/api/canvas/courses/60000000000030087/assignments/60000000001040467
    ```

---

## 📦 Data Models

### CanvasCourse
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique Canvas ID |
| `name` | String | Full course name |
| `courseCode` | String | Short code (e.g., "CS 1D") |

### CanvasAssignment
The service now automatically parses the raw HTML description from Canvas into multiple helpful formats:

| Field | Type | Description                                    |
| :--- | :--- |:-----------------------------------------------|
| `id` | Long | Unique Assignment ID                           |
| `name` | String | Title of the assignment                        |
| `dueAt` | OffsetDateTime | Date and time due                              |
| `description` | String | Raw HTML assignment instructions               |
| `plain_text_description` | String | Instructions with all HTML tags stripped out   |
| `canvas_file_links` | List\<String\> | Direct URLs to hosted files (PDFs, docs)       |
| `external_links` | List\<String\> | URLs to external resources (YouTube, IDEs, etc.)|

---

## 🧪 Performance & Caching
The application uses `@EnableCaching` to store API responses locally. Repeated calls to the same IDs will return data significantly faster and reduce hits to the Canvas servers.