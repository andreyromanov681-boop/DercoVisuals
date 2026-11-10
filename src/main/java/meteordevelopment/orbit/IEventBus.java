package meteordevelopment.orbit;

import meteordevelopment.orbit.listeners.IListener;
import meteordevelopment.orbit.listeners.LambdaListener;

public interface IEventBus {
   void registerLambdaFactory(String var1, LambdaListener.Factory var2);

   boolean isListening(Class<?> var1);

   <ConfigTextDialog> ConfigTextDialog post(ConfigTextDialog var1);

   <ConfigTextDialog extends ICancellable> ConfigTextDialog post(ConfigTextDialog var1);

   void subscribe(Object var1);

   void subscribe(Class<?> var1);

   void subscribe(IListener var1);

   void unsubscribe(Object var1);

   void unsubscribe(Class<?> var1);

   void unsubscribe(IListener var1);
}
