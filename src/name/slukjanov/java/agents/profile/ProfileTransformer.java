package name.slukjanov.java.agents.profile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @author slukjanov
 */
public class ProfileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> redefClass,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.startsWith("java")) {
            return classfileBuffer;
        }
        try {
            ClassPool classPool = ClassPool.getDefault(); // javassist helps us!
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            if ("name/slukjanov/java/agents/profile/LruCache".equals(className)) {
                StringBuilder body = new StringBuilder();
                body.append("public Object put(Object key, Object value) {")
                        .append("long $__call__start__$ = System.nanoTime();")
                        .append("try {")
                        .append("    return super.put(key,value);")
                        .append("} finally {")
                        .append("    name.slukjanov.java.agents.profile.Profiler.get()")
                        .append("      .registerCallTime(\"name.slukjanov.java.agents.profile.LruMap.put(")
                        .append("Object,Object)Object\"")
                        .append("      , System.nanoTime() - $__call__start__$);")
                        .append("}")
                        .append("}");

                ctClass.addMethod(CtNewMethod.make(body.toString(), ctClass));
            }

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (method.getAnnotation(PrintCall.class) != null) {
                    String methodName = method.getLongName() + method.getReturnType().getName();
                    method.insertBefore("System.out.println(\"Method '" + methodName + "' called\");");
                }
            }

            return ctClass.toBytecode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classfileBuffer;
    }

}
