package com.reflection;


import com.reflection.methods.Mark;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Run {

    public static final String METHODS_CLASS = "com.reflection.methods";
    private static Map<String, Method> methodMap;

    public static void main(String[] args) {
        Run run = new Run();
        run.executeMethod(args);
    }

    public void executeMethod(String[] args) {
        if (args.length != 0) {
            try {
                loadMethods();
                Method method = methodMap.getOrDefault(args[0], this.getClass().getDeclaredMethod("defaultMethod"));
                boolean isStatic = Modifier.isStatic(method.getModifiers());
                Object classInstance = isStatic ? null : method.getDeclaringClass().newInstance();
                if (args.length == 1) {
                    method.invoke(classInstance);
                } else {
                    method.invoke(classInstance, (Object[]) Arrays.copyOfRange(args, 1, args.length));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private Class getMethodClass() throws ClassNotFoundException {
//        RunLoader loader = new RunLoader();
//        return loader.findClass(METHODS_CLASS);
//    }

    private void loadMethods() throws Exception {
        methodMap = getClasses(METHODS_CLASS)
                .flatMap(e -> Arrays.stream(e.getMethods()))
                .filter(f -> f.isAnnotationPresent(Mark.class))
                .collect(Collectors.toMap(e -> e.getAnnotation(Mark.class).name(), e -> e));
    }

    private static void showMethods() {
        methodMap.forEach((k, v) -> {
            System.out.println(k + ": " + v.getName());
        });
    }

    private static void defaultMethod() {
        showMethods();
    }

    private static Stream<Class> getClasses(final String pack) throws Exception {
        final StandardJavaFileManager fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);
        return StreamSupport.stream(fileManager.list(StandardLocation.CLASS_PATH, pack, Collections.singleton(JavaFileObject.Kind.CLASS), false).spliterator(), false)
                .map(javaFileObject -> {
                    try {
                        final String[] split = javaFileObject.getName()
                                .replace(".class", "")
                                .replace(")", "")
                                .split(Pattern.quote(File.separator));

                        final String fullClassName = pack + "." + split[split.length - 1];
                        return Class.forName(fullClassName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

}
