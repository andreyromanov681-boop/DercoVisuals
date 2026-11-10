package pulse.events;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import meteordevelopment.orbit.EventBus;

public final class EventBusService {
   public static final EventBus EVENT_BUS = new EventBus();

   private EventBusService() {
   }

   private static Lookup createLookup(Method method, Class<?> cls) throws IllegalAccessException, InvocationTargetException {
      return (Lookup)method.invoke(null, cls, MethodHandles.lookup());
   }

   static {
      EVENT_BUS.registerLambdaFactory("pulse", EventBusService::createLookup);
   }
}
