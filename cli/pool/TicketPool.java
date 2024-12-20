package pool;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private final List<String> tickets = Collections.synchronizedList(new LinkedList<>());
    private int totalTicketsAdded;
    private int totalTicketsRetrieved = 0;
    private int maxTicketCapacity;
    private final int totalTickets; // Real-time pool capacity
    private final int vendorCount;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition canAddTickets = lock.newCondition();
    private int vendorsFinished = 0;

    public TicketPool(int totalTickets, int maxTicketCapacity, int vendorCount) {
        this.totalTickets = totalTickets;
        this.maxTicketCapacity = maxTicketCapacity;
        this.vendorCount = vendorCount;

        int initialTickets = Math.min(totalTickets, maxTicketCapacity);
        for (int i = 0; i < initialTickets; i++) {
            int vendorId = (i % vendorCount) + 1; // Distribute tickets among vendors
            tickets.add("Initial-Ticket- no " + (i + 1) + " (Vendor-" + vendorId + ")");
        }
        this.totalTicketsAdded = initialTickets;
    }


    public void addTickets(String ticket, int vendorId) {
        lock.lock();
        try {
            while ((tickets.size() >= totalTickets || totalTicketsAdded >= maxTicketCapacity)
                    && totalTicketsRetrieved < maxTicketCapacity) {
                canAddTickets.await();
            }

            if (totalTicketsAdded < maxTicketCapacity && totalTicketsRetrieved < maxTicketCapacity) {
                tickets.add(ticket + " (Vendor-" + vendorId + ")");
                totalTicketsAdded++;
                notEmpty.signalAll();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public String removeTicket() {
        lock.lock();
        try {
            while (tickets.isEmpty() && vendorsFinished < vendorCount) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            if (tickets.isEmpty() && vendorsFinished == vendorCount) {
                return null;
            }

            totalTicketsRetrieved++;
            String ticket = tickets.remove(0);
            canAddTickets.signalAll();
            return ticket;
        } finally {
            lock.unlock();
        }
    }

    public void updateMaxTicketCapacity(int newMaxTicketCapacity) {
        lock.lock();
        try {
            this.maxTicketCapacity += newMaxTicketCapacity;
            vendorsFinished = 0; // Reset for the next run
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getTotalTicketsRetrieved() {
        lock.lock();
        try {
            return totalTicketsRetrieved;
        } finally {
            lock.unlock();
        }
    }

    public int getTotalTicketsAdded() {
        lock.lock();
        try {
            return totalTicketsAdded;
        } finally {
            lock.unlock();
        }
    }


    public int getMaxTicketCapacity() {
        lock.lock();
        try {
            return maxTicketCapacity;
        } finally {
            lock.unlock();
        }
    }

    public boolean isComplete() {
        lock.lock();
        try {
            return (vendorsFinished == vendorCount || totalTicketsRetrieved >= maxTicketCapacity) && tickets.isEmpty();
        } finally {
            lock.unlock();
        }
    }


    public void setVendorFinished() {
        lock.lock();
        try {
            vendorsFinished++;
            if (vendorsFinished == vendorCount) {
                notEmpty.signalAll(); // Notify customers that no more tickets will be added
            }
        } finally {
            lock.unlock();
        }
    }
}
