# 📦 Inventra
> **A modern, localized Point of Sale (POS) and Inventory Management System designed to streamline operations for small-scale physical businesses and wholesalers.**

---

## 🚨 The Problem
Small-scale physical businesses and wholesalers frequently face inefficiencies in **inventory tracking**, **manual billing**, and **localized logistics**. Handwritten transportation receipts ("Chalans") often lead to delivery errors due to illegibility, creating significant logistical hurdles. Furthermore, manually translating daily sales data into accounting software (like Busy 7) is tedious and highly prone to human error for non-accountant shop owners.

## 💡 The Objective
To develop **Inventra**, a robust **Client-Server application** that digitizes stock management, automates billing, generates scannable delivery Chalans, and streamlines accounting exports. 

## ✨ Core Features
* **Centralized Inventory Database:** Full CRUD (Create, Read, Update, Delete) operations to track all physical goods, pricing, SKU codes, and stock levels in real-time.
* **Automated Alert System:** Intelligent triggers that flag low-stock items automatically when the inventory falls below a mathematically calculated 30% reorder threshold, preventing deficits and surpluses.
* **Cashier/POS Interface:** A streamlined billing module to process customer purchases, automatically deduct from inventory, and generate formal **PDF invoices**.
* **Smart 'Chalan' Generation:** Automated creation of delivery receipts containing product details, delivery addresses, and recipient names. **Innovation:** Each Chalan includes an auto-generated **QR code** linking directly to Google Maps coordinates (or a direct map link), assisting delivery personnel with precise navigation.
* **Accounting Software Integration:** A comprehensive "Sales History" tracker with an automated export feature that structures daily purchase/sales data into a formatted **Excel (`.xlsx`) file**, allowing for one-click data import into standardized accounting software.

---

## 🏗️ Architecture & Technology Stack
Inventra utilizes a modern **Client-Server architecture**, decoupling the desktop user interface from the backend business logic and database.

### 🖥️ Frontend (The Client)
* **GUI Framework:** Java Swing
* **UI Theme:** FlatLaf (FlatDarkLaf) for a modern, sleek dark mode experience.
* **API Consumption:** Native Java 11+ `HttpClient` for RESTful communication.
* **JSON Processing:** Google Gson for robust serialization and deserialization of backend data.

### ⚙️ Backend (The Server)
* **Framework:** Java Spring Boot (REST APIs, Spring Data JPA)
* **Database:** MySQL (Relational schema design with entities for `Product` and `Transaction`)

### 🔗 Core Integrations (Maven Dependencies)
* **Apache PDFBox:** For programmatic generation of PDF Bills and Delivery Chalans.
* **ZXing (Zebra Crossing):** For generating dynamic geolocation QR Codes embedded in the PDFs.
* **Apache POI:** For writing structured Excel files for the accounting export.
