# StackOverflow

A full-stack Q&A platform built with **Java Spring Boot**, **PostgreSQL**, and **Thymeleaf**.

## 🚀 Prerequisites
Before running the app, ensure you have:
1.  **Java JDK 17** or higher (JDK 21 recommended)
2.  **Maven** (or use the included `mvnw` wrapper)
3. **IntelliJ** as the IDE

---

## ⚡ Quickstart: Choose Your Method

### Option A: Connect to the Online Database (Fastest)
*Use this if you just want to run the app without installing PostgreSQL locally.*

1.  **Get the Credentials:** Check the group chat / pinned message for the **Database Credentials** (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
2.  **Configure Environment Variables:**
    * **IntelliJ:** Click "Edit Configurations" -> "Environment variables".
    * **Terminal:** Export the variables (see below).
3.  **Set the Variables:**
    ```text
    DB_URL=jdbc:postgresql://<online-host>:5432/<db-name>
    DB_USERNAME=<online-username>
    DB_PASSWORD=<online-password>
    JWT_SECRET=<jwt_secret>
    CLOUDINARY_NAME=demo
    CLOUDINARY_KEY=12345
    CLOUDINARY_SECRET=abcde
    ```
4.  **Run the App.**

---

### Option B: Run with Local Database (Best for Dev)
*Use this if you want full control or are developing offline.*

1.  **Install PostgreSQL** and open pgAdmin.
2.  **Create a Database:**
    ```postgresql
    CREATE DATABASE stackoverflow_db;
    ```
3.  **Set Environment Variables:**
    Point the app to your local machine.

    **Windows (PowerShell):**
    ```powershell
    $env:DB_URL="jdbc:postgresql://localhost:5432/stackoverflow_db"
    $env:DB_USERNAME="postgres"
    $env:DB_PASSWORD="YOUR_LOCAL_PASSWORD"
    $env:JWT_SECRET="jwt_secret"
    ./mvnw spring-boot:run
    ```

---

## 🔑 Important: Account Activation
Since we do not have a live email server yet, **account activation is simulated in the console**.

1.  Go to [http://localhost:8080/register](http://localhost:8080/register) and sign up.
2.  **Check your Terminal / IntelliJ Logs.** You will see a block like this:
    ```text
    ---------- EMAIL SIMULATION ----------
    To: user@example.com
    Subject: Activate your SolveIt Account
    Body: Click the link below...
    http://localhost:8080/activate?token=...
    --------------------------------------
    ```
3.  **Copy that link** and paste it into your browser to activate your account.
4.  Login at [http://localhost:8080/login](http://localhost:8080/login).

---

## 🛠️ Tech Stack
* **Backend:** Java Spring Boot 3
* **Database:** PostgreSQL
* **Frontend:** Thymeleaf + Bootstrap 5
* **Security:** Spring Security (Form Login)