# BookSwap

BookSwap is a software application developed in Python that facilitates book exchanges between users.  
The project focuses on applying fundamental software engineering principles, including object-oriented programming, modular design, and clean code practices.

## Overview

The application allows users to create and manage user accounts, maintain personal book collections, browse available books, and initiate book exchange requests.  
BookSwap was developed as an academic project to gain practical experience in designing and implementing a structured software system using Python.

## Features

- User account management  
- Book collection management (add, update, remove books)  
- Search and filtering of available books  
- Book exchange request handling  
- Input validation and basic error handling  

## Technologies and Concepts

- Python  
- Object-Oriented Programming (OOP)  
- Data persistence  
- CRUD operations  
- Layered architecture  
- Design Patterns (Observer)  
- Git version control  

## Project Structure

The project follows a modular, layered architecture that separates responsibilities into:

- Models – domain entities (users, books, exchange requests)  
- Business Logic – application rules and workflows  
- Persistence Layer – data storage and retrieval  
- Observer Components – notification of state changes  

This structure improves maintainability, readability, and scalability.

## Installation and Running the Application

### Prerequisites

- Python **3.10 or newer**
- Git

### Installation Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/margiteo/BookSwap.git
2. Open the project in an IDE (e.g., Pycharm).
3. Configure the database connection if required.
4. Build and run the application.

## Motivation
This project was created to strengthen practical programming skills and to demonstrate the ability to design and implement a complete application, from requirements to implementation.

## Future Improvements
- Refactoring toward a REST-based architecture
- Improved exception handling and logging
- Enhanced user interface
- Performance optimizations

