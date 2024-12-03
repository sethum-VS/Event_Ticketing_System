# CLI: Real-Time Event Ticketing System

This directory contains the Command-Line Interface (CLI) implementation for the Real-Time Event Ticketing System. The CLI is used to configure system parameters and run simulations.

---

## Features
1. **Initial Configuration**:
    - Input system parameters (e.g., total tickets, release rates).
    - Save configurations persistently to the backend database.

2. **Simulation**:
    - Simulates interactions between vendors (producers) and customers (consumers).
    - Displays real-time transaction details in the console.

3. **API Communication**:
    - Uses **OkHttp** for making REST API calls to the backend for:
        - Saving configurations.
        - Triggering simulations.

---


