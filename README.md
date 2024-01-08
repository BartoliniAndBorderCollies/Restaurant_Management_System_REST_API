This project is under development.

# Restaurant Management System – Backend Service

## Main objective
The goal of the project is to develop a backend service to facilitate various operations of the restaurant or a similar place. Said service will handle the management of menu
items, orders, reservations, customer information and other essential aspects necessary for a modern, coordinated restaurant.

## Features and functionalities

`Menu related:`
1. Create, modify and delete menu items with details such as name, description,
price, category and availability.
2. Avoid selling items that are unavailable in the inventory.
   
`Inventory related:`
1. Create and maintain a database of available ingredients, supplies and stock items.
2. Track quantities, units and details for each inventory item (e.g., name, description,
supplier, cost, current stock level).
3. Record and manage inventory transactions such as receiving new stock, usage in food
preparation and waste/spoilage. Track it with timestamps.
4. Keep record of cooperating suppliers and their details (e.g., name, address, category).
   
`Order related:`
1. Create, modify and delete orders placed by customers.
2. Handle order status (pending, preparing, done, paid).
3. Calculate order totals and manage item quantities in inventory.
4. Automatically deduct used ingredients from the inventory when an order is fulfilled.
   
`Reservation handling related:`
1. Enable customers to make, modify, or cancel reservations.
2. Manage table availability and scheduling based on reservations.
   
`Customer related:`
1. Store and manage customer information, including contact details and previous orders.
2. Support customer authentication and authorization for placing orders and viewing
reservation status.
3. Support various types of employee accounts (e.g., admin, owner, manager, staff).
   
`Reporting and analytics:`
1. Generate reports such as sale summaries, inventory usage, popular dishes and revenue
analysis.

## Technical requirements

Tech stack:
- Java 21 for the backend part of the project.
- Spring Boot framework for building the RESTful API.
- Latest version of MySQL database.
- Spring Security for user authentication and authorization.

## Project structure:
- Implement adequate error handling and logging mechanisms.
- Write unit and integration tests to ensure code reliability.
- Use DTOs for exchange between layers.
- Implement RESTful endpoints for CRUD operations on various entities.
- Keep best practices and conventions in mind regarding naming, documentation,
commits, branches and codebase in general
