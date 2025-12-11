public class CheeseCounter {
    static class Ticket {
        private final int maxTicket;
        private int next = 1;

        Ticket(int maxTicket) {
            this.maxTicket = maxTicket;
        }

        public synchronized int getTicket() {
            int t = next;
            next = (next % maxTicket) + 1;
            return t;
        }
    }

    static class Counter {
        private final int maxTicket;
        private int nextToServe = 1;

        Counter(int maxTicket) {
            this.maxTicket = maxTicket;
        }

        public synchronized void serve(int ticket, String name) {
            try {
                while (ticket != nextToServe) {
                    wait();
                }
                System.out.println(name + " with ticket " + ticket + " gets cheese");
                nextToServe = (nextToServe % maxTicket) + 1;
                notifyAll();
            } catch (InterruptedException e) {
            }
        }
    }

    static class Customer extends Thread {
        private final String name;
        private final Ticket ticket;
        private final Counter counter;

        Customer(String name, Ticket ticket, Counter counter) {
            this.name = name;
            this.ticket = ticket;
            this.counter = counter;
        }

        public void run() {
            int t = ticket.getTicket();
            System.out.println(name + " takes ticket " + t);
            counter.serve(t, name);
        }
    }

    public static void main(String[] args) throws Exception {
        int bold = 2;
        int meek = 2;
        int maxTicket = 3;

        Ticket ticket = new Ticket(maxTicket);
        Counter counter = new Counter(maxTicket);

        int n = bold + meek;
        Customer[] customers = new Customer[n];

        int k = 0;
        for (int i = 1; i <= bold; i++) {
            customers[k++] = new Customer("bold" + i, ticket, counter);
        }
        for (int i = 1; i <= meek; i++) {
            customers[k++] = new Customer("meek" + i, ticket, counter);
        }

        for (Customer c : customers) {
            c.start();
        }
        for (Customer c : customers) {
            c.join();
        }
    }
}
