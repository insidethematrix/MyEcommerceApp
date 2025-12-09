# 🛍️ Avora - E-Commerce App

**Avora** is a simulation of an e-commerce application developed using **Java** and **Android Studio**. It is designed to demonstrate the practical application of **Object-Oriented Programming (OOP)** principles and modern Android architecture.

The project showcases core OOP concepts such as **Inheritance**, **Polymorphism**, **Abstraction**, and **Encapsulation** in a real-world scenario. It also integrates **Firebase Authentication** for secure user management.

## 📱 Screenshots

| Login Screen | Register Screen | Showcase & Cart | Profile Screen |
|:---:|:---:|:---:|:---:|
|<img width="108" height="240" alt="login" src="https://github.com/user-attachments/assets/fc605039-532d-4d43-88d3-0f2c24eb3e81" />| <img width="108" height="240" alt="register" src="https://github.com/user-attachments/assets/6fd6046b-c58d-4050-96ce-79d77a8ccd0b" />|<img width="108" height="240" alt="main" src="https://github.com/user-attachments/assets/23a81928-d44d-4399-8566-3394f2ee3a7c" />|<img width="108" height="240" alt="profile" src="https://github.com/user-attachments/assets/1ab57df2-7b29-4117-9af7-3ea334e59f0b" />|



## ✨ Features

* **🔐 User Management (Firebase Auth):**
    * Secure Sign Up and Login with Email/Password.
    * User profile display and Logout functionality.
* **🛒 Dynamic Cart System:**
    * Users can add products from the showcase to their cart.
    * Each user is assigned a private `ShoppingCart` instance.
    * Real-time total price calculation including tax logic.
* **📦 Smart Product Architecture (OOP):**
    * **Abstraction:** All products derive from the abstract `Product` class.
    * **Polymorphism:** The `calculateTax()` method behaves differently for `Electronics` (20% Tax) and `Clothing` (8% Tax).
    * **Interface:** The `Discountable` interface is implemented to give specific products discount capabilities.

## 🛠️ Tech Stack

* **Language:** Java
* **IDE:** Android Studio
* **Database / Auth:** Firebase Authentication
* **UI:** XML Layouts, ConstraintLayout, Custom Image Assets
* **Version Control:** Git & GitHub

## 🏗️ Architecture & OOP Principles

The project adheres to Clean Code principles and demonstrates:

1.  **Encapsulation:** All class fields are `private` and accessed via `Getter/Setter` methods.
2.  **Inheritance:** The `Product` class serves as the parent for `Electronics` and `Clothing`, preventing code duplication.
3.  **Polymorphism:** The cart calculation logic iterates through a list of `Product` objects, where each object executes its own specific tax calculation.
4.  **Interface:** The `Discountable` interface allows for flexible feature implementation (e.g., applying discounts only to specific categories).

## 🚀 Setup & Installation

1.  Clone this repository: `https://github.com/insidethematrix/MyEcommerceApp.git`
2.  Open in Android Studio.
3.  Add your own `google-services.json` file from Firebase Console to the `app/` directory.
4.  Build and Run!

---
**Developer:** Ahmet Zeyt Eroğlu
