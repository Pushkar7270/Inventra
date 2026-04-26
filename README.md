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

> 💡 **How to add images to this README:**
>
> 1. Create a folder called `docs/images/` in the root of your repository.
> 2. Take a screenshot and save it there (e.g. `docs/images/dashboard.png`).
> 3. Push the folder to GitHub.
> 4. Replace each placeholder `img` tag below by swapping `docs/images/YOUR_FILENAME.png` with your actual filename. The `alt` text and caption beneath can also be updated to match.
>
> Alternatively, you can drag-and-drop an image directly into a GitHub Issue or PR comment box, copy the generated URL it gives you, and paste that URL into the `src=""` attribute below.

---

### 🖥️ Main Inventory Dashboard

<!-- 
  SCREENSHOT GUIDE — Inventory Dashboard:
  Recommended: Take a screenshot of the main "My Shop Inventory" window
  showing a few items in the table, ideally with one row highlighted red
  to demonstrate the low-stock alert feature.
  Save as: docs/images/dashboard.png
-->

<div align="center">
  <img src="docs/images/dashboard.png" alt="Inventra — Main Inventory Dashboard showing items with low-stock alert highlighted in red" width="800"/>
  <br/>
  <em>Main inventory table — low-stock items are automatically highlighted in red</em>
</div>

---

### 🛒 Sell an Item / POS Interface

<!-- 
  SCREENSHOT GUIDE — POS / Sell Dialog:
  Recommended: Take a screenshot of the "Sell an Item" dialog box
  open on screen, showing the System Number input and quantity spinner.
  Save as: docs/images/sell_dialog.png
-->

<div align="center">
  <img src="docs/images/sell_dialog.png" alt="Inventra — Sell an Item dialog with quantity spinner" width="500"/>
  <br/>
  <em>Cashier interface — select an item, set quantity, and process the sale in seconds</em>
</div>

---

### 📜 Sales History & Chalan Generation

<!-- 
  SCREENSHOT GUIDE — Sales History:
  Recommended: Take a screenshot of the Sales History dialog window
  showing the transaction table with the "Generate Chalan" button visible
  in the Action column.
  Save as: docs/images/sales_history.png
-->

<div align="center">
  <img src="docs/images/sales_history.png" alt="Inventra — Sales History window with Generate Chalan button" width="800"/>
  <br/>
  <em>Sales history log — every transaction is recorded with a one-click Chalan generator</em>
</div>

---

### 📦 Generated Delivery Chalan (PDF)

<!-- 
  SCREENSHOT GUIDE — Chalan PDF:
  Recommended: Take a screenshot of the generated Delivery_Chalan.pdf
  open in a PDF viewer, showing the recipient details, item list,
  and the embedded QR code.
  Save as: docs/images/chalan_pdf.png
-->

<div align="center">
  <img src="docs/images/chalan_pdf.png" alt="Inventra — Generated Delivery Chalan PDF with embedded GPS QR code" width="500"/>
  <br/>
  <em>Auto-generated Chalan PDF — includes recipient info, item details, and a scannable Google Maps QR code</em>
</div>

---

### 📊 Excel Sales Export

<!-- 
  SCREENSHOT GUIDE — Excel Export:
  Recommended: Take a screenshot of the My_Sales.xlsx file open in
  Excel or LibreOffice, showing the Sales Data sheet with columns
  and a few rows of transaction data.
  Save as: docs/images/excel_export.png
-->

<div align="center">
  <img src="docs/images/excel_export.png" alt="Inventra — Excel sales export with Product Name, Price, Quantity, Total, and Date columns" width="800"/>
  <br/>
  <em>One-click Excel export — structured and ready for direct import into accounting software</em>
</div>

---

## 📁 Project Structure

```
Inventra/
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
