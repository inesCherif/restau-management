# Restaurant Management System

A Java Swing-based desktop application for restaurant order management.

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── restaurant/
│               ├── Main.java
│               ├── models/       # Data models (User, Order, MenuItem, etc.)
│               ├── views/        # UI components and screens
│               ├── controllers/  # Business logic and control flow
│               ├── utils/        # Utility classes and helpers
│               └── data/         # File-based data storage handlers
resources/
├── images/     # Icons and images
└── data/       # Data storage files
```


## Setup Instructions

1. Make sure you have Java JDK 8 or above installed.
2. Clone this repository.
3. Compile and run the project using your favorite IDE or command line.

---

## 🔧 Tech Rules (as per course requirements)

- Built only with **core Java**
- UI built using **Java Swing** (JFrame, JPanel, JButton, etc.)
- No drag-and-drop UI tools
- **No external libraries**
- **No database** — All data is stored in binary files 

---

## 👥 User Roles

### 🧑‍💼 Manager (Admin)

Can:

- **Create customer accounts**
  - Fields: login, password, first name, last name, date of birth, address, phone number
  - Stored in file
- **View all customers**
  - Reads data from file
- **Delete customer accounts**
  - Removes customer info from file
- **Add menu items**
  - Fields: item name, description, price
  - Categories: dish, sandwich, salad, dessert, pastry, drink
  - Stored in file
- **View menu items**
  - Loads from file
- **View all customer orders**
  - Orders are stored in either per-customer files or one global file
- **Update order status**
  - Statuses: Not processed, In preparation, Ready, Out for delivery
  - Updates status in file

### 👤 Customer (Client)

Can:

- **View the menu**
  - Loaded from file
- **Place an order**
  - Choose delivery type:
    - Home delivery (with address)
    - On-site (eat at restaurant)
    - Take-away
  - Saved to file
- **Track order status**
  - Read status from file

---

## ✨ Features

- Login system with Manager and Client roles
- Full customer account management
- Menu creation and viewing
- Order creation with delivery type options
- Real-time order tracking with status updates
- Clean and intuitive Swing-based UI
- File-based data persistence (no databases used)

---

## 🚀 Project Status

✅ Java Desktop App completed  
📦 Now adding polish and structure

---

## 🛠 Want to Contribute or Follow Along?

Here’s how we’re building it:

1. First, define a clear class structure
2. Then, we build each feature step by step:
   - Complete Java class
   - Related Swing GUI
   - File read/write logic
   - All explained simply and clearly
3. Wait for feedback before moving on

---

