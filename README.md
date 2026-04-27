# Smart-Lost-and-Found-Management-System-Console-based-

The Lost & Found Management System is a console-based application developed in Java to manage lost and found items efficiently. It allows users to report items, search records, and manage item status through a menu-driven interface.

---

## Features

- Add Lost/Found Items
- View All Items
- Search Items by Keyword
- Update Item Status (Lost / Found / Claimed / Returned)
- Delete Item Records
- Menu-driven Console Interface
- File Handling for Data Storage

---

## System Architecture

The application follows a layered structure:

Main (Menu) → Service Layer → Data Layer → File Storage

- Presentation Layer: Console (CLI)
- Business Logic: Java Classes
- Data Storage: File Handling (Text/CSV)

---

## Modules

1. Item Management Module  
Handles adding, viewing, updating, and deleting items.

2. Search Module  
Allows users to search items using keywords.

3. Status Management Module  
Manages lifecycle states such as LOST, FOUND, CLAIMED, and RETURNED.

4. File Handling Module  
Stores and retrieves item data from files.

---

## How to Run

1. Compile the program:
javac Main.java

2. Run the program:
java Main

---

## Sample Menu

1. Add Item  
2. View Items  
3. Search Item  
4. Update Status  
5. Delete Item  
6. Exit  

---

## Tech Stack

- Java (Core Java)
- OOP Concepts (Classes, Objects, Inheritance)
- File Handling (FileReader, FileWriter)

---

## Project Structure

- Main.java → Entry point with menu
- Item.java → Item model class
- Service classes → Business logic
- File handling classes → Data storage

---

## Limitations

- No graphical user interface
- Data stored in files (no database)
- No authentication system

---

## Future Enhancements

- Convert to GUI or Web Application
- Integrate database (MySQL)
- Add user authentication
- Add email notification system

---

## Author

Sowndarya  
Lost & Found Management System  

---

## License

This project is developed for academic purposes.
