package meteordevelopment.orbit;

public class NoLambdaFactoryException extends RuntimeException {
   public NoLambdaFactoryException(Class<?> cls) {
      super("No registered lambda listener for '" + cls.getName() + "'.");
   }
}
