package com.reflection;


import com.reflection.methods.Mark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Run {

    public static final String METHODS_CLASS = "com.reflection.methods.Methods";
    private static Map<String, Method> methodMap;
    private static Class methodClass;

    public static void main(String[] args) {
        Run run = new Run();
        run.executeMethod(args);
    }

    public void executeMethod(String[] args) {
        if (args.length != 0) {
            try {
                methodClass = getMethodClass();
                loadMethods();
                Method method = methodMap.getOrDefault(args[0], this.getClass().getDeclaredMethod("defaultMethod"));
                boolean isStatic = Modifier.isStatic(method.getModifiers());
                Object classInstance = isStatic ? null : method.getDeclaringClass().newInstance();
                if (args.length == 1) {
                    method.invoke(classInstance);
                } else {
                    method.invoke(classInstance, (Object[]) Arrays.copyOfRange(args, 1, args.length));
                }
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private Class getMethodClass() throws ClassNotFoundException {
        RunLoader loader = new RunLoader();
        return loader.findClass(METHODS_CLASS);
    }

    private void loadMethods() {
        methodMap = Stream.of(methodClass.getMethods())
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

}
