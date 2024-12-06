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
    private final int totalTickets;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition canAddTickets = lock.newCondition();
    private boolean vendorFinished = false;

    public TicketPool(int totalTickets, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.maxTicketCapacity = maxTicketCapacity;

        int initialTickets = Math.min(totalTickets, maxTicketCapacity);
        for (int i = 0; i < initialTickets; i++) {
            tickets.add("Initial-Ticket-" + (i + 1));
        }
        this.totalTicketsAdded = initialTickets;
    }

    public void addTickets(String ticket) {
        lock.lock();
        try {
            while (tickets.size() >= totalTickets) {
                try {
                    canAddTickets.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (totalTicketsAdded < maxTicketCapacity) {
                tickets.add(ticket);
                totalTicketsAdded++;
                notEmpty.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public String removeTicket() {
        lock.lock();
        try {
            while (tickets.isEmpty() && !vendorFinished) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            if (tickets.isEmpty() && vendorFinished) {
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
            this.maxTicketCapacity = newMaxTicketCapacity;
            vendorFinished = false; // Allow vendors to resume operations
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentPoolSize() {
        lock.lock();
        try {
            return tickets.size();
        } finally {
            lock.unlock();
        }
    }

    public int getTotalTickets() {
        return totalTickets; // Return the total tickets allowed at any given time
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
        return maxTicketCapacity;
    }

    public void setVendorFinished() {
        lock.lock();
        try {
            vendorFinished = true;
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isComplete() {
        lock.lock();
        try {
            return vendorFinished && totalTicketsRetrieved >= totalTicketsAdded;
        } finally {
            lock.unlock();
        }
    }
}
