<div align="center">

# 📦 Inventra

### *A Modern, Localized Point of Sale & Inventory Management System*

> Built for small businesses. Designed for real-world logistics.

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Apache POI](https://img.shields.io/badge/Apache_POI-5.2-D22128?style=for-the-badge&logo=apache&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

</div>

---

## 🧭 About the Project

### The Problem

Small-scale physical businesses and wholesalers face a trio of persistent, costly inefficiencies:

- 📋 **Manual inventory tracking** is time-consuming, imprecise, and constantly falling out of sync with reality.
- 🗒️ **Handwritten Chalans** (transportation receipts) are illegible, error-prone, and regularly cause logistical failures — wrong deliveries, wrong addresses, wrong quantities.
- 📊 **Manual accounting entry** — copying daily sales into software like *Busy 7* — is a tedious, human-error-prone task that no shop owner should be spending hours on.

### The Solution

**Inventra** is a robust **Client-Server desktop application** that directly tackles each of these problems:

| Problem | Inventra's Solution |
|---|---|
| Disorganized stock tracking | Centralized real-time inventory database with CRUD operations |
| Illegible handwritten Chalans | Auto-generated PDF Chalans with embedded GPS QR codes |
| Manual accounting data entry | One-click Excel export formatted for direct import into accounting software |

---

## ✨ Key Features

### 🗄️ Centralized Inventory Database
Full **Create, Read, Update, Delete (CRUD)** operations to manage all physical goods. Track item names, barcodes/SKU codes, pricing, and real-time stock levels — all from a clean desktop interface.

### 🚨 Automated Low-Stock Alert System
Inventra calculates a **30% reorder threshold** automatically for every item added. When an item's stock falls at or below this threshold, it is **instantly highlighted in red** in the inventory table — no manual monitoring required.

### 🧾 Cashier / POS Interface
A streamlined billing module for processing customer purchases. Each sale:
- **Automatically deducts** the sold quantity from live inventory
- **Generates a formal PDF invoice** for the customer
- Logs the transaction with a timestamp to the sales history

### 📦 Smart Chalan Generation *(Flagship Feature)*
Generate a professional delivery receipt (Chalan) for any transaction. Each Chalan includes:
- Recipient name and delivery address
- Itemized product details and quantities
- 🗺️ **An auto-generated QR code** that links directly to Google Maps via the delivery address — enabling delivery personnel to navigate with one scan.

### 📊 One-Click Accounting Export
The Sales History module exports all transaction records into a structured **`.xlsx` Excel file**, formatted with clean business-friendly columns (`Product Name`, `Price per Item`, `Quantity Sold`, `Total Price`, `Date`) — ready for direct import into standard accounting software.

---

## 🏗️ Architecture & Tech Stack

Inventra uses a decoupled **Client-Server architecture**, separating the desktop UI from the backend business logic and database.

```
┌─────────────────────────┐         HTTP/REST         ┌──────────────────────────────┐
│   Java Swing Frontend   │  ◄──────────────────────► │   Spring Boot Backend        │
│   (Desktop Client)      │      localhost:8080        │   + MySQL Database           │
└─────────────────────────┘                            └──────────────────────────────┘
```

### 🖥️ Frontend — Desktop Client

| Technology | Purpose |
|---|---|
| **Java Swing** | Core GUI framework for the desktop application |
| **FlatLaf (FlatDarkLaf)** | Modern dark-mode UI theme for a polished, professional look |
| **Java 11+ `HttpClient`** | Native HTTP client for consuming the REST API |
| **Google Gson** | JSON serialization and deserialization of API responses |

### ⚙️ Backend — Server

| Technology | Purpose |
|---|---|
| **Java Spring Boot** | REST API framework, dependency injection, and application structure |
| **Spring Data JPA** | ORM layer for database interaction (`Product` & `Transaction` entities) |
| **MySQL** | Relational database for persistent storage of inventory and sales data |
| **Apache PDFBox** | Programmatic generation of Customer Bills and Delivery Chalans |
| **ZXing (Zebra Crossing)** | Dynamic geolocation QR code generation embedded in Chalan PDFs |
| **Apache POI** | Structured Excel (`.xlsx`) file generation for accounting exports |

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Apache Maven 3.9+](https://maven.apache.org/download.cgi)
- [MySQL Server 8.0+](https://dev.mysql.com/downloads/mysql/)

---

### ⚙️ Step 1 — Clone the Repository

```bash
git clone https://github.com/Pushkar7270/Inventra.git
cd Inventra
```

---

### 🗄️ Step 2 — Configure the Database

Open the backend configuration file at:

```
backend/backend/src/main/resources/application.properties
```

Update the credentials to match your local MySQL setup:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventra_db?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

> **Note:** The `createDatabaseIfNotExist=true` parameter means you do **not** need to manually create the database. Hibernate will handle schema creation automatically on first run.

---

### ▶️ Step 3 — Start the Backend Server

Navigate to the backend module and run the Spring Boot application:

```bash
cd backend/backend
./mvnw spring-boot:run
```

The server will start on **`http://localhost:8080`**. Wait for the console to show a successful startup message before proceeding.

---

### 🖥️ Step 4 — Launch the Desktop Frontend

Open a new terminal, navigate to the Swing frontend module, and run:

```bash
cd swing-frontend
mvn compile exec:java -Dexec.mainClass="org.example.InventraApp"
```

> Alternatively, open the `swing-frontend` module in IntelliJ IDEA and run `InventraApp.java` directly.

The **My Shop Inventory** window will launch. You're all set!

---

## 📸 Screenshots

---

### 🖥️ Main Inventory Dashboard

<div align="center">
  <img src="docs/images/dashboard.jpeg" alt="Inventra — Main Inventory Dashboard showing items with low-stock alert highlighted in red" width="800"/>
  <br/>
  <em>Main inventory table — items with critically low stock are automatically highlighted in red</em>
</div>

> 📌 **To add this image:** Save your screenshot of the main app window as `docs/images/dashboard.jpeg` and push the `docs/` folder to your GitHub repository. The image will appear automatically.

---

### 🛒 Sell an Item / POS Interface

<!-- IMAGE NOT YET ADDED
     Steps to add:
       1. Open Inventra and click "Sell an Item"
       2. Screenshot the dialog showing the System Number field and +/- quantity spinner
       3. Save as: docs/images/sell_dialog.jpeg
       4. Push docs/ folder to GitHub — the image below will render automatically
-->

<div align="center">
  <img src="docs/images/sell_dialog.jpeg" alt="Inventra — Sell an Item dialog with quantity spinner" width="500"/>
  <br/>
  <em>Cashier interface — select an item, set quantity, and process the sale in seconds</em>
</div>

> 📌 **To add this image:** Screenshot the "Sell an Item" dialog (with the +/− quantity spinner visible), save as `docs/images/sell_dialog.jpeg`, and push to your repo.

---

### 📜 Sales History & Chalan Generation

<!-- IMAGE NOT YET ADDED
     Steps to add:
       1. Open Inventra and click "Sales History"
       2. Screenshot the transaction table showing at least one row
          and the blue "Generate Chalan" button in the Action column
       3. Save as: docs/images/sales_history.jpeg
       4. Push docs/ folder to GitHub — the image below will render automatically
-->

<div align="center">
  <img src="docs/images/sales_history.jpeg" alt="Inventra — Sales History window with Generate Chalan button" width="800"/>
  <br/>
  <em>Sales history log — every transaction is recorded with a one-click Chalan generator</em>
</div>

> 📌 **To add this image:** Screenshot the Sales History dialog showing the transaction table with the blue "Generate Chalan" button, save as `docs/images/sales_history.jpeg`, and push to your repo.

---

### 📦 Generated Delivery Chalan (PDF)

<!-- IMAGE NOT YET ADDED
     Steps to add:
       1. Open Delivery_Chalan.pdf in any PDF viewer
       2. Screenshot the full page — make sure the QR code and recipient
          details are clearly visible
       3. Save as: docs/images/chalan_pdf.jpeg
       4. Push docs/ folder to GitHub — the image below will render automatically
-->

<div align="center">
  <img src="docs/images/chalan_pdf.jpeg" alt="Inventra — Generated Delivery Chalan PDF with embedded GPS QR code" width="500"/>
  <br/>
  <em>Auto-generated Chalan PDF — includes recipient info, item details, and a scannable Google Maps QR code</em>
</div>

> 📌 **To add this image:** Open `Delivery_Chalan.pdf`, screenshot the full page with the QR code visible, save as `docs/images/chalan_pdf.jpeg`, and push to your repo.

---

### 🧾 Customer Bill (PDF)

<!-- IMAGE NOT YET ADDED
     Steps to add:
       1. Open Customer_Bill_16.pdf in any PDF viewer
       2. Screenshot the full bill page
       3. Save as: docs/images/customer_bill.jpeg
       4. Push docs/ folder to GitHub — the image below will render automatically
-->

<div align="center">
  <img src="docs/images/customer_bill.jpeg" alt="Inventra — Official Customer Bill PDF generated after a sale" width="500"/>
  <br/>
  <em>Formal customer bill — auto-generated as a PDF after every completed sale</em>
</div>

> 📌 **To add this image:** Open `Customer_Bill_16.pdf`, take a screenshot, save as `docs/images/customer_bill.jpeg`, and push to your repo.

---

### 📊 Excel Sales Export

<div align="center">
  <img src="docs/images/excel_export.jpeg" alt="Inventra — Excel sales export showing Product Name, Price per Item, Quantity Sold, Total Price, and Date columns" width="800"/>
  <br/>
  <em>One-click Excel export — structured and ready for direct import into accounting software</em>
</div>

> 📌 **To add this image:** Screenshot `My_Sales.xlsx` open in Excel showing the Sales Data sheet, save as `docs/images/excel_export.jpeg`, and push to your repo.

---

## 📁 Project Structure

```
Inventra/
├── docs/
│   └── images/                  # ← Place all README screenshots here
│       ├── dashboard.jpeg        # ✅ Ready to add
│       ├── sell_dialog.jpeg      # ⏳ Screenshot needed
│       ├── sales_history.jpeg    # ⏳ Screenshot needed
│       ├── chalan_pdf.jpeg       # ⏳ Screenshot needed
│       ├── customer_bill.jpeg    # ⏳ Screenshot needed
│       └── excel_export.jpeg     # ✅ Ready to add
├── backend/
│   └── backend/
│       └── src/main/java/com/Inventra/backend/
│           ├── Entity/          # JPA Entities (Product, Transaction)
│           ├── Service/         # Business logic (ProductService)
│           ├── controller/      # REST Controllers (ProductController, TransactionController)
│           ├── repository/      # Spring Data JPA Repositories
│           └── Util/            # PDF, QR Code, and Excel generators
└── swing-frontend/
    └── src/main/java/org/example/
        ├── InventraApp.java     # Main Swing application window
        ├── ApiClient.java       # HTTP client for backend communication
        └── Product.java         # Client-side data model
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

```
MIT License — Copyright (c) 2026 Pushkar7270
```

You are free to use, modify, and distribute this software for any purpose, with or without modification, as long as the original copyright notice is retained.

---

<div align="center">

Built with ☕ Java, 🍃 Spring Boot, and a genuine need to fix broken logistics.

**[⭐ Star this repo](https://github.com/Pushkar7270/Inventra)** if Inventra helped you or inspired your own project!

</div>
