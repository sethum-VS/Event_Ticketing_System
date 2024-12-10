package threads;

import pool.TicketPool;
import logging.Logger;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerRetrievalInterval; // Time-based interval
    private final int customerId;

    public Customer(TicketPool ticketPool, int customerRetrievalInterval, int customerId) {
        this.ticketPool = ticketPool;
        this.customerRetrievalInterval = customerRetrievalInterval;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        while (true) {
            String ticket = ticketPool.removeTicket();

            if (ticket != null) {
                Logger.log("Customer " + customerId + " retrieved: " + ticket);
            } else if (ticketPool.isComplete()) {
                Logger.log("Customer " + customerId + " finished retrieving tickets. No more tickets available.");
                break;
            }

            try {
                Thread.sleep(customerRetrievalInterval); // Sleep based on interval
            } catch (InterruptedException e) {
                Logger.error("Customer " + customerId + " interrupted.");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
