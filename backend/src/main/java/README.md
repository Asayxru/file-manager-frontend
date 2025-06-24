# 📘 Інструкція для Frontend-розробника

Цей документ допоможе тобі швидко підключити та використовувати backend у проєкті File Manager. Якщо виникнуть питання — звертайся до мене (бекенд-розробника).

---

## 🚀 Цілі для фронтенду

🔹 Реалізувати UI для взаємодії з API:
- авторизація (через HTTP Basic Auth)
- перегляд / створення / редагування / видалення папок
- завантаження / перегляд / видалення файлів
- перегляд свого профілю

---

## 🧭 План дій

1. 🔗 **Підключись до backend:**
    - Базовий URL: `http://localhost:8080/api`
    - Краще тестувати через Swagger: `http://localhost:8080/swagger-ui/index.html`

2. 👤 **Створи тестового юзера (або запитай готовий):**
    - `POST /auth/register`
    - Використай email + password для HTTP Basic Auth

3. 📁 **Реалізуй логіку для:**
    - `GET /folders` — список папок
    - `POST /folders` — створення
    - `PUT /folders/{id}`, `DELETE /folders/{id}` — редагування/видалення

4. 📄 **Файли:**
    - `GET /files` — список файлів
    - `POST /files/upload` — завантаження через `multipart/form-data`
    - `GET /files/preview/by-id/{id}` — перегляд вмісту
    - `GET /files/download/{filename}` — завантаження
    - `DELETE /files/{id}` — видалення

5. 👤 **Користувач:**
    - `GET /users/me` — показати ім’я, email, роль
    - `PUT /users/me`, `DELETE /users/me` — редагування або видалення себе

6. 🧪 **Для тестування використовуй Swagger або Postman.**

---

## 🛠 API Огляд

### Аутентифікація



POST /auth/register

```json
{
  "username": "user1",
  "email": "user1@example.com",
  "password": "user1"
}

Для захищених запитів використовуй Basic Auth.



📁 Папки
GET /folders

GET /folders/{id}

POST /folders

{
  "name": "My Folder",
  "parentFolderId": 1
}

PUT /folders/{id}

DELETE /folders/{id}



📄 Файли
GET /files

GET /files/{id}

POST /files/upload (Multipart)

GET /files/preview/by-id/{id}

GET /files/download/{filename}

DELETE /files/{id}



👤 Користувач
GET /users/me

PUT /users/me

DELETE /users/me



👥 Ролі
ADMIN — доступ до всього.

USER — доступ лише до власних об'єктів.



🧪 Тестові обліковки
Роль	Username	Пароль
Admin	admin	   admin123
User	user1	   user1

