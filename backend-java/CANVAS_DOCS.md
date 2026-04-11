# 🎓 Canvas API Wrapper

This Spring Boot service acts as a secure middleware for interacting with the **Canvas LMS**. It handles authentication internally, allowing the frontend or terminal users to fetch course and assignment data without needing to manage sensitive API tokens.

---

## 🛠 Setup & Requirements

* **Environment**: Java 17+ and Spring Boot.
* **Configuration**: Requires a `.env` file or environment variables for `canvas.api.token` and `canvas.api.domain`.
* **Base URL**: `http://localhost:8080/api/canvas`.

---

## 🚀 API Endpoints

### 1. User & Courses
* **Get User Profile**
  * **Endpoint**: `GET /profile`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/profile"
    ```

* **Get All Active Courses**
  * **Endpoint**: `GET /courses`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/courses"
    ``` 

### 2. Assignments
* **Get All Assignments for a Course**
  * **Endpoint**: `GET /assignments/{courseId}`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/assignments/{courseId}"
    ``` 

* **Get Specific Assignment Details**
  * Fetches metadata for a single assignment, including processed description fields.
  * **Endpoint**: `GET /courses/{courseId}/assignments/{assignmentId}`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/courses/60000000000030087/assignments/60000000001040467"
    ``` 

### 3. Modules & Items
* **Get Course Modules (Categorized)**
  * Fetches all modules including nested items like Quizzes, Files, and Pages.
  * **Endpoint**: `GET /courses/{courseId}/modules`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/courses/{courseId}/modules"
    ``` 

### 4. Quizzes
* **Get All Quizzes for a Course**
  * **Endpoint**: `GET /courses/{courseId}/quizzes`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/courses/{courseId}/quizzes"
    ``` 

* **Get Specific Quiz Details**
  * **Endpoint**: `GET /courses/{courseId}/quizzes/{quizId}`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/courses/{courseId}/quizzes/{quizId}"
    ``` 

* **Get Quiz Submissions**
  * **Endpoint**: `GET /courses/{courseId}/quizzes/{quizId}/submissions`
  * **Command**:
    ```zsh
    curl -i "http://localhost:8080/api/canvas/courses/{courseId}/quizzes/{quizId}/submissions"
    ``` 

---

## 📄 Data Models

### CanvasUserProfile
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique User ID |
| `name` | String | Full name of the user |
| `email` | String | Primary email address |
| `avatarUrl` | String | URL to the user's profile picture |
| `bio` | String | User's biography text |

### CanvasCourse
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique Course ID |
| `name` | String | Full name of the course |
| `courseCode` | String | Short course code (e.g., CS101) |
| `termId` | Long | ID of the enrollment term |

### CanvasModule
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique Module ID |
| `name` | String | Title of the module (e.g., "Week 01") |
| `items` | List\<CanvasModuleItem\> | List of nested items within the module |

### CanvasModuleItem
| Field | Type | Description |
| :--- | :--- | :--- |
| `title` | String | Display name of the item |
| `type` | String | Type (e.g., "Quiz", "File", "Assignment") |
| `contentId` | Long | The ID used to fetch specific details for this item |

### CanvasAssignment
Processed with `JSoup` to provide cleaner data formats:

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique Assignment ID |
| `name` | String | Title of the assignment |
| `dueAt` | OffsetDateTime | Date and time due |
| `description` | String | Raw HTML instructions |
| `plain_text_description` | String | HTML tags stripped out for clean text |
| `canvas_file_links` | List\<String\> | Direct URLs to internal Canvas files |
| `external_links` | List\<String\> | URLs to external sites (YouTube, etc.) |

### CanvasQuiz
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique Quiz ID |
| `title` | String | Title of the quiz |
| `quizType` | String | Category (e.g., assignment, practice_quiz) |
| `timeLimit` | Integer | Time limit in minutes |
| `allowedAttempts` | Integer | Number of attempts permitted |
| `questionCount` | Integer | Total number of questions |
| `published` | boolean | Whether the quiz is currently visible to students |

### CanvasQuizSubmission
| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | Long | Unique Submission ID |
| `attempt` | Integer | Which attempt number this is |
| `score` | Double | Score achieved |
| `timeSpent` | Integer | Time taken in **seconds** |
| `finishedAt` | OffsetDateTime | Completion timestamp |

---

## 🧪 Performance & Error Handling
The application uses URI-based requests to prevent double-encoding of special characters. Responses are returned as empty lists `[]` instead of `null` errors when no data is found for modules, quizzes, or submissions to ensure frontend stability.