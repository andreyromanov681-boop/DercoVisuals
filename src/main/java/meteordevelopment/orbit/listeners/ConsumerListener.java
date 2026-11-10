package meteordevelopment.orbit.listeners;

import java.util.function.Consumer;

public class ConsumerListener<T> implements IListener {
   private final Class<T> target;
   private final int priority;
   private final Consumer<T> executor;

   public ConsumerListener(Class<T> cls, int i, Consumer<T> consumer) {
      this.target = cls;
      this.priority = i;
      this.executor = consumer;
   }

   public ConsumerListener(Class<T> cls, Consumer<T> consumer) {
      this(cls, 0, consumer);
   }

   @Override
   public void call(Object obj) {
      this.executor.accept((T)obj);
   }

   @Override
   public Class<T> getTarget() {
      return this.target;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @Override
   public boolean isStatic() {
      return false;
   }
}
