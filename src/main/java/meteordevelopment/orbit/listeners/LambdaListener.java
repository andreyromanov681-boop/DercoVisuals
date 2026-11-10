package meteordevelopment.orbit.listeners;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import meteordevelopment.orbit.EventHandler;

public class LambdaListener implements IListener {
   private static boolean isJava1dot8;
   private static Constructor<Lookup> lookupConstructor;
   private static Method privateLookupInMethod;
   private final Class<?> target;
   private final boolean isStatic;
   private final int priority;
   private Consumer<Object> executor;

   public LambdaListener(LambdaListener.Factory factory, Class<?> cls, Object obj, Method method) {
      this.target = method.getParameters()[0].getType();
      this.isStatic = Modifier.isStatic(method.getModifiers());
      this.priority = method.getAnnotation(EventHandler.class).priority();

      try {
         String name = method.getName();
         Lookup lookupCreate;
         if (isJava1dot8) {
            boolean zIsAccessible = lookupConstructor.isAccessible();
            lookupConstructor.setAccessible(true);
            lookupCreate = lookupConstructor.newInstance(cls);
            lookupConstructor.setAccessible(zIsAccessible);
         } else {
            lookupCreate = factory.create(privateLookupInMethod, cls);
         }

         MethodType methodType2 = MethodType.methodType(void.class, method.getParameters()[0].getType());
         MethodHandle methodHandleFindVirtual;
         MethodType methodType;
         if (this.isStatic) {
            methodHandleFindVirtual = lookupCreate.findStatic(cls, name, methodType2);
            methodType = MethodType.methodType(Consumer.class);
         } else {
            methodHandleFindVirtual = lookupCreate.findVirtual(cls, name, methodType2);
            methodType = MethodType.methodType(Consumer.class, cls);
         }

         MethodHandle target = LambdaMetafactory.metafactory(
               lookupCreate, "accept", methodType, MethodType.methodType(void.class, Object.class), methodHandleFindVirtual, methodType2
            )
            .getTarget();
         if (this.isStatic) {
            this.executor = (Consumer)target.invoke();
         } else {
            this.executor = (Consumer)target.invoke((Object)obj);
         }
      } catch (Throwable th) {
         th.printStackTrace();
      }
   }

   @Override
   public void call(Object obj) {
      this.executor.accept(obj);
   }

   @Override
   public Class<?> getTarget() {
      return this.target;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @Override
   public boolean isStatic() {
      return this.isStatic;
   }

   static {
      try {
         isJava1dot8 = System.getProperty("java.version").startsWith("1.8");
         if (isJava1dot8) {
            lookupConstructor = Lookup.class.getDeclaredConstructor(Class.class);
         } else {
            privateLookupInMethod = MethodHandles.class.getDeclaredMethod("privateLookupIn", Class.class, Lookup.class);
         }
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      }
   }

   public interface Factory {
      Lookup create(Method var1, Class<?> var2) throws IllegalAccessException, InvocationTargetException;
   }
}
