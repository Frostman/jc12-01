package name.slukjanov.java.agents.simple;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @author slukjanov
 */
public class SimpleTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String name, Class<?> redefClass,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!name.endsWith("Main")) {
            return classfileBuffer;
        }

        ClassPool classPool = ClassPool.getDefault(); // javassist helps us!
        try {
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            ctClass.addMethod(
                    CtNewMethod.make("public static void foo() {}", ctClass)
            );

            CtMethod ctMethod = ctClass.getMethod("foo", "()V");
            ctMethod.insertBefore("System.out.println(\"before\");");
            ctMethod.insertAfter("System.out.println(\"after\");");

            CtMethod mainMethod = ctClass.getMethod("main", "([Ljava/lang/String;)V");
            mainMethod.insertAt(9, "foo();");

            mainMethod.instrument(new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    try {
                        if ("java.io.PrintStream.println(java.lang.String)".equals(m.getMethod().getLongName())) {
                            m.replace("System.out.println(\"Hello, instrumented world!\");");
                            return;
                        }
                    } catch (NotFoundException e) {
                        // ingore
                    }
                    super.edit(m);    //To change body of overridden methods use File | Settings | File Templates.
                }
            });

            return ctClass.toBytecode();
        } catch (Exception e) {
            System.err.println("Exception while transforming class: " + name);
            e.printStackTrace();
            return classfileBuffer;
        }
    }
}

