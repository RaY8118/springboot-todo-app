# ✅ Spring Boot To-Do App

A simple **To-Do application** built with Spring Boot. It provides a REST API for managing tasks with full CRUD functionality.

---

## 🚀 Features

* Create, Read, Update, and Delete (CRUD) operations for tasks.
* Filter tasks by **completion status**.
* Search for tasks by **title**.
* Mark tasks as **complete**.
* PostgreSQL persistence with Spring Data JPA.

---

## 🛠️ Tech Stack

* **Java 21**
* **Spring Boot 3.5.6**
* **Spring Web**
* **Spring Data JPA**
* **PostgreSQL**
* **Maven**

---

## ⚡ Getting Started

Follow these steps to get the app running locally.

### ✅ Prerequisites

Make sure you have installed:

* [Java 21+](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [PostgreSQL](https://www.postgresql.org/)

---

### 📦 Installation

1. **Clone the repository**

   ```sh
   git clone https://github.com/your_username/springboot-todo-app.git
   ```

2. **Navigate into the project directory**

   ```sh
   cd springboot-todo-app
   ```

3. **Install dependencies**

   ```sh
   mvn install
   ```

4. **Create the database**

   ```sql
   CREATE DATABASE todo_db;
   ```

5. **Update database configuration** in
   `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/todo_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

6. **Run the application**

   ```sh
   mvn spring-boot:run
   ```

👉 The app will be running at: **[http://localhost:8080](http://localhost:8080)**

---

## 📡 API Endpoints

| Method   | Endpoint                     | Description                        |
| -------- | ---------------------------- | ---------------------------------- |
| `GET`    | `/tasks`                     | Get all tasks.                     |
| `POST`   | `/tasks`                     | Create a new task.                 |
| `GET`    | `/tasks/{id}`                | Get a task by ID.                  |
| `GET`    | `/tasks/filter?completed={}` | Filter tasks by completion status. |
| `GET`    | `/tasks/search?title={}`     | Search tasks by title.             |
| `PUT`    | `/tasks/{id}`                | Update a task.                     |
| `PATCH`  | `/tasks/{id}/complete`       | Mark a task as complete.           |
| `DELETE` | `/tasks/{id}`                | Delete a task.                     |

---

## 📋 Request & Response Format

### 📝 Task Object

```json
{
  "todo_id": 1,
  "title": "My Task",
  "description": "Task details here",
  "completed": false,
  "dueDate": "2025-12-31"
}
```

### ➕ Create Task Request

```json
{
  "title": "My New Task",
  "description": "This is a description of my new task.",
  "dueDate": "2025-12-31"
}
```

### 🔄 Update Task Request

```json
{
  "title": "My Updated Task",
  "description": "This is an updated description.",
  "dueDate": "2026-01-15"
}
```

---

## 🗄️ Database Configuration

This project uses **PostgreSQL**.
By default, it connects to:

* **Database:** `todo_db`
* **Host:** `localhost`
* **Port:** `5432`

Update your credentials in `application.properties` before running.

---

## 🤝 Contributing

Contributions are welcome!
Feel free to fork this repo and submit a pull request with your improvements.

---

## 📜 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

---
