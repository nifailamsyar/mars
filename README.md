# MARS (Music Archive and Repository System)

The Music Archive and Repository System (M.A.R.S) is a web-based application developed as a basic project to address some of the challenges faced by traditional monolithic music score repository systems. The main objective of MARS is to provide a core functionalities of music score repository system which is storing, managing and accessing music scores, but in a more modular and maintainable way.
## Features

### For Clerks
- Upload and catalog musical scores
- Manage score metadata
- Update or delete existing scores
- View and manage uploaded scores
- Profile management

### For Customers
- Browse available scores
- Preview scores before purchase
- Purchase and manage a personal collection of scores
- Search and sort functionality
- Profile management

## Technical Stack

### Backend
- Java Spring Boot
- Spring Security
- JPA/Hibernate
- JWT Authentication
- PostgreSQL Database

### Frontend
- HTML5
- CSS3
- Vanilla JavaScript
- Responsive Design
- Font Awesome Icons

## Project Structure

### Entity Classes
- User.java: User management and authentication
- Score.java: Music score metadata and management
- PurchasedScore.java: Managing customer purchases

### Controllers
- UserController.java: User authentication and profile management
- ScoreController.java: Score upload and management
- PurchaseController.java: Purchase management

### Services
- UserService.java: User-related business logic
- ScoreService.java: Score-related business logic
- PurchaseService.java: Purchase-related operations

### Frontend Pages
- login.html: User authentication
- index.html: Registration page
- clerkhome.html: Clerk dashboard
- upload-score.html: Score upload interface
- catalog-score.html: Score cataloging
- customer-dashboard.html: Customer homepage
- customer-scores.html: Customer's purchased scores
- customer-profile.html: Profile management

## Security Features

- JWT-based authentication
- Password encryption
- Role-based access control
- Secure file handling
- Input validation
- CORS configuration

## Getting Started

1. Clone the repository
2. Configure database settings in application.properties
3. Set up file storage directories
4. Run the Spring Boot application
5. Access the application at http://localhost:8080
