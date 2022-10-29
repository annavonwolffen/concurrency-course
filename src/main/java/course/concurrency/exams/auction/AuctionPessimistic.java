package course.concurrency.exams.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;
    private Bid latestBid = new Bid(0L, 0L, 0L);
    private final ReentrantLock lock = new ReentrantLock();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            try {
                lock.lock();
                if (bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        try {
            lock.lock();
            return latestBid;
        } finally {
            lock.unlock();
        }
    }
}
