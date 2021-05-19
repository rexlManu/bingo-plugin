package de.rexlmanu.bingo.utility.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class Invoker {

    public static void run(Object classInstance, String methodName, Objects... arguments) {
        try {
            Class<?> aClass = classInstance.getClass();
            Method method = aClass.getMethod(methodName, Arrays.stream(arguments).map(Objects::getClass).toArray(Class[]::new));
            method.invoke(classInstance, arguments);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
