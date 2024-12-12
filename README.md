# Ticket Simulation System - Command-Line Interface (CLI)

The **Ticket Simulation System CLI** provides a simple and interactive way to configure and run real-time ticket simulations. It simulates ticket producers (vendors) and consumers (customers) interacting with a shared ticket pool using core multi-threading principles.

---

## Features

- **Simulation Parameters:**
  - Input key simulation parameters, such as:
    - **Total Tickets**
    - **Ticket Release Rate (ms)**
    - **Customer Retrieval Rate (ms)**
    - **Max Ticket Capacity**
- **Multi-Threaded Simulation:**
  - Simulates vendors adding tickets and customers retrieving them in real time.
  - Uses synchronized ticket pool management for thread safety.
- **Logging and Data Persistence:**
  - Logs simulation activities such as ticket additions and purchases.
  - Saves configurations and logs in MongoDB for reuse and analysis.
- **Import and Export Configurations:**
  - Retrieve saved configurations from the database to run simulations.
  - Save new configurations for future use.

---

## Setup

### Prerequisites
1. **Java 17+** installed on your system.
2. **Maven** for building the CLI project.
3. **MongoDB Atlas or Local MongoDB Instance** for database integration.

### Steps
1. Clone the repository:
   ```bash
   git clone <repository_url>
