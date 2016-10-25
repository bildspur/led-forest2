package ch.bildspur.ledforest.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cansik on 25.10.16.
 */
public class MapUtils {
    public static <K extends Object, V extends Object> Map<V, List<K>> flipMap(Map<K, V> map) {
        Map<V, List<K>> out = new HashMap<>();

        for (K key : map.keySet()) {
            V value = map.get(key);

            // create key
            if (!out.containsKey(value))
                out.put(value, new ArrayList<>());

            List<K> list = out.get(value);
            list.add(key);
        }

        return out;
    }
}
