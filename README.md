# ✅ CheckIt - Cloud-Synced To-Do App

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Material Design](https://img.shields.io/badge/Material_Design_3-757575?style=for-the-badge&logo=material-design&logoColor=white)

CheckIt is a premium, feature-rich task management application built natively for Android. It features a pixel-perfect Material Design 3 interface based on a custom Figma prototype, backed by a real-time cloud database and an advanced multi-user authentication system.

## 📱 Download the App
**[📥 Download CheckIt v1.0.0 APK Here](https://github.com/MovinVinusandha/CheckIt/releases/latest)**

<br>

<p align="center">
  <img width="150" alt="Screenshot_20260517_032515" src="https://github.com/user-attachments/assets/87b58e6c-2c0b-4781-9bcb-f8f5d3723a10" />
  <img width="150" alt="Screenshot_20260517_032444" src="https://github.com/user-attachments/assets/3676b538-336c-4e4e-8a91-385c8ad1d2a3" />
  <img width="150" alt="Screenshot_20260517_032749" src="https://github.com/user-attachments/assets/9daf2cc6-d063-41bc-9287-5cb699f0311a" />
  <img width="150" alt="Screenshot_20260517_033202" src="https://github.com/user-attachments/assets/d1342a79-91da-4932-8caf-f675da409524" />
</p>

## ✨ Key Features

*   ☁️ **Real-Time Cloud Sync:** Tasks update instantly across all devices using **Firebase Firestore** snapshot listeners.
*   🔐 **Flexible Authentication:** Users can log in using *either* their Email or a custom unique Username. Includes full password reset functionality via **Firebase Auth**.
*   👥 **Multi-Account Switcher:** An advanced Instagram-style seamless account switcher utilizing `Gson` and `SharedPreferences` to securely store and swap sessions locally.
*   🎨 **Material Design 3 UI:** Includes pill-shaped input fields, edge-to-edge screens that respect camera notches, custom fonts, SVG vector assets, and frosted glass bottom sheets.
*   ✅ **Dynamic Task States:** Features a live active-task counter, beautiful empty-state illustrations, and dynamic UI changes (strikethroughs, fading cards, icon swaps) when tasks are completed.
*   👤 **Profile Management:** Fully editable user profiles stored locally and globally, with timezone selection and automatic display name extraction.

## 🛠️ Tech Stack & Architecture

*   **Language:** Java & XML
*   **Architecture:** MVC (Model-View-Controller) utilizing custom Adapters and Interfaces.
*   **Database:** Firebase Firestore (NoSQL)
*   **Authentication:** Firebase Authentication
*   **Local Storage:** SharedPreferences & Gson (for multi-account state management)
*   **UI Components:** `RecyclerView`, `BottomSheetDialog`, `MaterialCardView`, `TextInputLayout`

## 📂 Code Overview

*   `MainActivity.java` - Core task manager handling Firestore real-time snapshots, dynamic active-task counting, and BottomSheet task creation.
*   `TaskAdapter.java` - Manages the `RecyclerView`, binding dynamic visual states to tasks and routing click events via a custom `OnTaskClickListener` interface.
*   `LoginActivity.java` & `SignupActivity.java` - Handles complex queries to enforce unique usernames and resolve username-to-email logins.
*   `AccountMenuActivity.java` - Dynamically inflates saved user profiles and securely swaps Firebase authentication tokens in the background without requiring re-login.

## 🚀 Getting Started

To run this project locally on your own machine:

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/CheckIt.git
