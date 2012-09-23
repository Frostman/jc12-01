package name.slukjanov.java.agents.profile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author slukjanov
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

    private final int MAX_SIZE;

    public LruCache(int size) {
        super(size + 1, 1.0F, true);
        MAX_SIZE = size;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldestEntry) {
        return size() > MAX_SIZE;
    }

}
