package course.concurrency.m3_shared.collections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final List<String> stat = Collections.synchronizedList(new ArrayList<>());

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        stat.add(restaurantName);
    }

    public Set<String> printStat() {
        final Map<String, Long> statMap = stat.stream().collect(groupingBy((name) -> name, counting()));
        final Set<String> statSet = new HashSet<>();
        statMap.forEach((name, count) -> statSet.add(name + " - " + count));
        return statSet;
    }
}
