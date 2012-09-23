package name.slukjanov.java.agents.profile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import name.slukjanov.java.agents.Agent;

import java.lang.instrument.ClassDefinition;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author slukjanov
 */
public class Main {

    public static final int ITERATIONS = 1000000;
    public static final int CACHE_SIZE = 1000000;
    public static final int KEY_RAND_RANGE = Integer.MAX_VALUE;

    public static void main(String[] args) throws Exception {
        instrumentTreeMap();

        Map<Integer, Integer> map = new LruCache<Integer, Integer>(CACHE_SIZE);
        testMap(map, ITERATIONS, KEY_RAND_RANGE);

        map = new TreeMap<Integer, Integer>();
        testMap(map, 10, KEY_RAND_RANGE);

        Profiler.get().printAllStats();
    }

    private static void instrumentTreeMap() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass treeMap = classPool.get(TreeMap.class.getName());
        CtMethod putMethod = treeMap.getMethod("put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        putMethod.insertBefore("System.out.println(\"TreeMap#put() called\");");

        Agent.instrumentation.redefineClasses(new ClassDefinition(TreeMap.class, treeMap.toBytecode()));
    }

    @PrintCall
    private static void testMap(Map<Integer, Integer> map, int iterations, int keyRandRange) {
        Random rand = new Random(System.nanoTime());
        for (int i = 0; i < iterations; i++) {
            map.put(rand.nextInt(keyRandRange), rand.nextInt());
        }
        System.out.println("Map size: " + map.size());
    }

}
