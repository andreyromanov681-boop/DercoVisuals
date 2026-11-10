package pulse.hud.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HudServiceInfo {
   boolean enabledByDefault() default true;
}
