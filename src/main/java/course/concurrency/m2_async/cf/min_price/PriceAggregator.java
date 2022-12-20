package course.concurrency.m2_async.cf.min_price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        final List<CompletableFuture<Double>> futures = new ArrayList<>(shopIds.size());
        shopIds.forEach((id) -> futures.add(CompletableFuture
                .supplyAsync(() -> priceRetriever.getPrice(itemId, id))
                .completeOnTimeout(Double.NaN, 2900, TimeUnit.MILLISECONDS)
                .exceptionally((th) -> Double.NaN)
        ));
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[shopIds.size()])).join();
        return futures
                .stream()
                .mapToDouble(CompletableFuture::join)
                .filter((it) -> !Double.isNaN(it))
                .min()
                .orElse(Double.NaN);
    }
}
