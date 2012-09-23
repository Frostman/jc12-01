package name.slukjanov.java.agents.profile;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * @author slukjanov
 */
public class Profiler {
    private final Multimap<String, Long> callTimes;

    private Profiler() {
        this.callTimes = HashMultimap.create();
    }

    public static Profiler get() {
        return InstanceStorage.INSTANCE;
    }

    public void registerCallTime(String method, long callTime) {
        callTimes.put(method, callTime);
    }

    public void printStats(String method) {
        Collection<Long> callTimes = this.callTimes.get(method);
        if (callTimes == null || callTimes.isEmpty()) {
            System.out.println("There are no stats for method: " + method);
            return;
        }
        System.out.println("Method: " + method);
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE, avg = 0;
        for (long callTime : callTimes) {
            min = Math.min(min, callTime);
            max = Math.max(max, callTime);
            avg += callTime;
        }
        avg /= callTimes.size();

        System.out.println("\t min call time: " + prettifyTime(min));
        System.out.println("\t avg call time: " + prettifyTime(avg));
        System.out.println("\t max call time: " + prettifyTime(max));
    }

    public void printAllStats() {
        for (String method : callTimes.keySet()) {
            printStats(method);
        }
    }

    private String prettifyTime(long time) {
        return time / 1e6 + "ms";
    }

    private static final class InstanceStorage {
        private static final Profiler INSTANCE = new Profiler();
    }
}
