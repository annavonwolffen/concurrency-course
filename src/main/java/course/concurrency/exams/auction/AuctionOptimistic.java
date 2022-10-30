package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public boolean propose(Bid bid) {
        Bid expected, updated;
        boolean isSuccess;
        do {
            expected = latestBid.get();
            updated = bid;
            if (updated.getPrice() > expected.getPrice()) {
                isSuccess = latestBid.compareAndSet(expected, updated);
            } else {
                return false;
            }
        } while (!isSuccess);

        notifier.sendOutdatedMessage(expected);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
