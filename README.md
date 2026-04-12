# 📦 Inventra
> **A modern, localized Point of Sale (POS) and Inventory Management System designed to streamline operations for small-scale physical businesses and wholesalers.**

---

## 🚨 The Problem
Small-scale physical businesses and wholesalers frequently face inefficiencies in **inventory tracking**, **manual billing**, and **localized logistics**. Handwritten transportation receipts ("Chalans") often lead to delivery errors due to illegibility, creating significant logistical hurdles. Furthermore, manually translating daily sales data into accounting software (like Busy 7) is tedious and highly prone to human error for non-accountant shop owners.

## 💡 The Objective
To develop **Inventra**, a robust **Client-Server application** that digitizes stock management, automates billing, generates scannable delivery Chalans, and streamlines accounting exports. 

## ✨ Core Features
* **Centralized Inventory Database:** Full CRUD (Create, Read, Update, Delete) operations to track all physical goods, pricing, and stock levels in real-time.
* **Automated Alert System:** Mathematical threshold triggers that flag low-stock items based on customizable reorder formulas, preventing deficits and surpluses.
* **Cashier/POS Interface:** A streamlined billing module to process customer purchases, automatically deduct from inventory, and generate formal **PDF invoices**.
* **Smart 'Chalan' Generation:** Automated creation of delivery receipts containing product details, delivery addresses, and the amount due. **Innovation:** Each Chalan includes an auto-generated **QR code** linking directly to Google Maps coordinates, assisting delivery personnel with precise navigation.
* **Accounting Software Integration:** An automated "Export to Busy 7" feature that structures daily purchase/sales data into a formatted **Excel (`.xlsx`) file**, allowing for one-click data import into standardized accounting software.

---

## 🏗️ Architecture & Technology Stack
Inventra utilizes a modern **Client-Server architecture**, decoupling the desktop user interface from the backend business logic and database.

### 🖥️ Frontend (The Client)
* **GUI Framework:** JavaFX (UI designed via SceneBuilder)
* **API Consumption:** Java `HttpClient` for RESTful communication.

### ⚙️ Backend (The Server)
* **Framework:** Java Spring Boot (REST APIs, Spring Data JPA)
* **Database:** MySQL (Relational schema design)

### 🔗 Core Integrations (Maven Dependencies)
* **Apache PDFBox:** For programmatic generation of PDF Bills and Chalans.
* **ZXing:** For generating dynamic geolocation QR Codes.
* **Apache POI:** For writing structured Excel files for the accounting export.
