package course.concurrency.exams.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;
    private Bid latestBid = new Bid(0L, 0L, 0L);
    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean isAuctionStopped;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        if (!isAuctionStopped && bid.getPrice() > latestBid.getPrice()) {
            try {
                lock.lock();
                if (!isAuctionStopped && bid.getPrice() > latestBid.getPrice()) {
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

    public Bid stopAuction() {
        try {
            lock.lock();
            isAuctionStopped = true;
            return latestBid;
        } finally {
            lock.unlock();
        }
    }
}
