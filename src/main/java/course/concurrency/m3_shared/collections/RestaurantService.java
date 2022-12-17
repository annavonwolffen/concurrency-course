package course.concurrency.m3_shared.collections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.*;

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
        return stat.stream().collect(groupingBy((name) -> name, counting())).entrySet()
                .stream()
                .map(e -> e.getKey() + " - " + e.getValue())
                .collect(toSet());
    }
}
