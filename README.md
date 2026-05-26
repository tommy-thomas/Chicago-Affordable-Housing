# Chicago Affordable Housing

## General Info
This app consumes data from the [Chicago Data Portal](https://data.cityofchicago.org/Community-Economic-Development/Affordable-Rental-Housing-Developments/s6ha-ppgi/about_data) and presents the data in an accessible way to help Chicago residents discover affordable housing options across the city.

## Overview

The Chicago Affordable Housing application provides a comprehensive resource for finding and accessing information about affordable rental housing developments throughout Chicago. It integrates with the City of Chicago's official data portal to deliver up-to-date, reliable housing information.

## Features

- **Housing Search**: Browse and filter available affordable housing developments
- **Location Mapping**: View properties across Chicago neighborhoods
- **Eligibility Information**: Learn about income requirements and household eligibility
- **Detailed Listings**: Access comprehensive information about each development including:
  - Rent prices and unit sizes
  - Amenities and services
  - Contact information and application processes
  - Accessibility features
- **Real-Time Data**: Information sourced directly from the Chicago Data Portal

## Technologies

Project is created with:
* **Java**: Core application language
* **[Room](https://developer.android.com/jetpack/androidx/releases/room#2.6.1)**: 2.6.0 (Local data persistence and caching)
* **[Retrofit](https://square.github.io/retrofit/)**: REST client for API communication with Chicago Data Portal

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven or Gradle
- Git

### Installation

1. Clone the repository:
```bash
git clone https://github.com/tommy-thomas/Chicago-Affordable-Housing.git
```

2. Navigate to the project directory:
```bash
cd Chicago-Affordable-Housing
```

3. Build the project:
```bash
# Using Maven
mvn clean install

# Using Gradle
gradle build
```

4. Run the application:
```bash
# Using Maven
mvn spring-boot:run

# Using Gradle
gradle run
```

## Architecture

This application uses modern Android/Java architectural patterns:
- **Room Database**: Manages local caching of housing data
- **Retrofit**: Handles API calls to the Chicago Data Portal
- **MVVM Pattern**: Separates UI logic from business logic

## Data Source

Housing data is sourced from:
- [Chicago Data Portal - Affordable Rental Housing Developments](https://data.cityofchicago.org/Community-Economic-Development/Affordable-Rental-Housing-Developments/s6ha-ppgi/about_data)

## API Integration

The app connects to Chicago's official APIs to fetch property information and locations.

## Project Structure

```
Chicago-Affordable-Housing/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── models/          # Data models
│   │   │   ├── api/             # Retrofit API interfaces
│   │   │   ├── database/        # Room database components
│   │   │   └── ui/              # UI components
│   │   └── resources/
│   └── test/
├── pom.xml (or build.gradle)
└── README.md
```

## Usage


## Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

[Specify your license here]

## Contact & Support

For questions, feedback, or issues, please:
- Open an issue on GitHub
- Contact the maintainers

## Acknowledgments

- City of Chicago Data Portal
- Chicago Department of Housing
- Community partners and housing organizations
- Open source community

## Resources

- [City of Chicago Housing Authority](https://www.chalgha.org/)
- [Chicago Data Portal](https://data.cityofchicago.org/)
- [Department of Housing and Economic Development](https://www.chicago.gov/city/en/depts/dcd/supp_info/affordable_housing/apartment_listings.html)

---

**Last Updated**: May 2026
