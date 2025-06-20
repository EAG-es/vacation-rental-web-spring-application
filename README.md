# VacationStay Rental Application

This application provides a platform for users to browse, book, and manage vacation rental properties.

## Features

- Browse vacation rentals with search and filtering
- View detailed property information with photos, amenities, and availability
- User authentication (login/signup) to access booking features
- Booking system to reserve properties for specific dates
- User dashboard to manage bookings and profile
- Internationalization support for English and Spanish

## Technology Stack

### Frontend
- React
- Vite
- Tailwind CSS
- shadcn/ui components
- i18next for internationalization

### Backend (Java Spring Boot)
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring MVC
- Thymeleaf (for server-side templates)
- H2 Database (for development)

## Getting Started

### Prerequisites
- Node.js (v16+)
- npm or yarn
- Java 17+
- Maven

### Running the Frontend
```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

### Running the Backend
```bash
# Navigate to the backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## Testing

### Frontend Tests
```bash
# Run tests
npm test
```

### Backend Tests
```bash
# Run tests
mvn test
```

## Generating Javadoc

To generate Javadoc documentation for the Java backend:

```bash
# Generate Javadoc
mvn javadoc:javadoc
```

The generated documentation will be available in the `target/site/apidocs` directory.

### Javadoc Configuration

The project is configured to generate comprehensive Javadoc with the following features:
- Package, class, method, and field documentation
- Navigation links between related classes
- Inheritance diagrams
- Index of all classes and members

## Internationalization

The application supports multiple languages:
- English (default)
- Spanish

To switch languages, use the language selector in the application header.

### Adding New Languages

To add support for a new language:
1. Create a new translation file in `src/locales/{language}/translation.json`
2. Add the language to the language options in `src/components/LanguageSwitcher.tsx`

## License

This project is licensed under the MIT License - see the LICENSE file for details.