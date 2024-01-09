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



# Proposal for a Restaurant Management System in relation to the presented needs

It will be a REST API with unit and integration tests, based on a MySQL database. The application will use Spring Security to handle roles: Admin, Owner, Manager, Staff. Each of these roles will have an appropriate level of access for themselves. Therefore, DTOs will be used so that critical data does not fall into unauthorized hands. 

Application will include exception handling with appropriate information about what went wrong for the client.

## Application structure

The architecture will follow the MVC (Model View Controller) pattern. At this preliminary stage, I see the need to create the following controllers. Of course, during the creation of the app, it may turn out that more are needed, or in a slightly different form than the one below:

## Controllers:

### MenuController: 
responsible for handling MENU resources in the application. Adding, updating, deleting, reading a single or multiple records in the restaurant MENU. This controller, through the service, will have access to the MenuRepository. Therefore, a Menu entity will be needed with basic fields: name, description, ingredients, price, category, isAvailable

### InventoryController: 
responsible for handling inventory - i.e., what is in stock. Adding, updating, deleting, reading a single or multiple inventory. Inventory affects whether a specific record from the restaurant MENU can be executed, e.g., if potatoes run out, many dishes with these ingredients cannot be made. This controller, through the service, will have access to the InventoryRepository. Therefore, an Inventory entity will be needed with basic fields: name, description, expiry_date, delivery_date, supplier, cost, stock_amount. It is immediately visible here that this table will have a relationship with the Supplier table.

### OrderController: 
responsible for handling orders. Adding, updating, deleting, reading a single or multiple orders placed in the restaurant. Each order will have its status. Possible statuses: PENDING, PREPARING, CANCELLED, DONE, PAID, NOT_PAID. Statuses will be changed automatically. The waiter with a tablet collects orders from the available restaurant MENU*. Marks them on his tablet, clicks send and at this moment this order appears on the tablet in the kitchen and its status automatically changes from PENDING to PREPARING. Both the kitchen and the waiter have the possibility to cancel the order. After the kitchen has completed a given order, it clicks on its tablet that it is ready and the status from PREPARING automatically changes to DONE. At this moment, the waiter receives a notification and on his tablet, he sees that a given order is ready to be picked up from the kitchen and should be served to a specific table. The last level of statuses is PAID - if the client has paid or NOT_PAID if the client pays, e.g., by transfer based on an invoice, or for other reasons did not pay for a given order. Then such an order remains in the database as NOT_PAID and the staff can easily find information about which clients, what orders in what period of time are unpaid.

*I suggest making an application with the option of an editable order, written by hand by the waiter and setting the price manually in case the guest wanted to order something that is not on the restaurant MENU, e.g., a sandwich with peanut butter. Then the waiter, after consulting with the kitchen, could possibly fulfill the guest’s request, without stressing about how to handle it in the application and how to sell it later. In such a non-standard situation, the waiter could manually enter on the tablet “sandwich with peanut butter”, price: 5 USD and such an order could be handled in the system of our application without stress for the staff. In the reporting panel, this type of order will go to the category “non-standard orders”.
The problem that arises here is that a given ingredient will not be reduced from the inventory, therefore the staff (kitchen) will have to mark on their tablet which ingredients they used and how much, in order to update the inventory levels.

OrderController will have access through the OrderService to OrderRepository. The Order entity will have the following basic fields: Menu, Customer, Table, order_time, Status. Therefore, there will be relationships with entities: Menu, Customer, Table. Status is expected to be an enum. Order_time will be assigned automatically at the time of placing the order.

### ReservationController: 
responsible for handling reservations. Adding, updating, deleting, reading a single or multiple reservations with the application of appropriate filters. In the service, logic will be created to monitor table availability, i.e., at what hours which tables are occupied and which are available. This controller, through the service, will have access to ReservationRepository. The Reservation entity will have basic fields: name, description, people_amount, time, Table, Order.
This table will have relationships with the Table and Order tables.

### CustomerController: 
responsible for handling Customer. Adding, updating, deleting, reading a single or multiple customers. Each Customer will have a defined role: Admin, Owner, Manager, Staff. Spring Security will be used to handle roles, through Authorization and Authentication. Customer role will have an impact on what a given customer can do in the application. This controller, through the service, will have access to CustomerRepository. The Customer entity will have basic fields: name, address, telephone number, email address, ROLE, creation_time, Order_history, Reservation, total_spent.

Total_spent will be used for information on how much a given client has spent so far in the restaurant -> this will be useful information for marketing and promotional actions (including the implementation of a possible discount card).

This table will have relationships with the Order and Reservation tables.

### ReportController: 
with access to ReportService, it will be a reporting module. Without a repository, without saving to the database. A Customer with the appropriate role selects parameters and the report is generated automatically, the downloaded .excel file is saved on the hard disk. The reporting module will have access to all repositories.

## Restaurant Management System Schema

![Restaurant_Management_System_Schema drawio (1)](https://github.com/BartoliniAndBorderCollies/Restaurant_Management_System_REST_API/assets/126821059/1a3cbc2a-5990-40c4-9b95-1600c6162638)

## Functionalities - proposal for app version 1.0

Automatically status change.

### Restaurant Menu
- add
- update
- delete
- show all
- show specific
- filter ASC/DESC by price, name

### Inventory
- add
- update
- delete
- show all
- show specific
- show not available (amount = 0)
- filter ASC/DESC by name, price, stock_amount, delivery_date, Supplier, expiry_date

### Order
- add
- update
- delete
- show all
- show specific
- filter ASC/DESC by id, price, customer, status, order_time

### Reservation
- add
- update
- delete
- show all
- show specific
- filter ASC/DESC by id, time, table, name

### Customer
- add
- update
- delete
- show all
- show specific
- filter ASC/DESC by id, name, role, total_spent

### Report
- generate a report using parameters
- download the report to excel file

  
