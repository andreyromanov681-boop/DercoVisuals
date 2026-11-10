package pulse.render.shader;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;

public class ShaderRegistry {
   private final Map<String, PulseShaderProgram> programs = Maps.newLinkedHashMap();

   public void register(String str, PulseShaderProgram pulseShaderProgram) {
      this.programs.put(str, pulseShaderProgram);
   }

   public void replace(String str, PulseShaderProgram pulseShaderProgram) {
      this.programs.replace(str, pulseShaderProgram);
   }

   public Optional<PulseShaderProgram> find(String str) {
      return Optional.ofNullable(this.programs.get(str));
   }
}
