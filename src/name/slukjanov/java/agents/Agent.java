package name.slukjanov.java.agents;

import name.slukjanov.java.agents.profile.ProfileTransformer;
import name.slukjanov.java.agents.simple.SimpleTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * @author slukjanov
 */
public class Agent {
    public static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
        ClassFileTransformer transformer;
        if ("profile".equals(args)) {
            transformer = new ProfileTransformer();
        } else {
            transformer = new SimpleTransformer();
        }

        instrumentation.addTransformer(transformer);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        Agent.instrumentation = instrumentation;
        premain(args, instrumentation);
    }
}

