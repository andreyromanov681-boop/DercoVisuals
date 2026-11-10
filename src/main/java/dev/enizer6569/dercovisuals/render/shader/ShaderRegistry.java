// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render.shader;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;

public class ShaderRegistry {
   private final Map<String, DercoVisualsShaderProgram> programs = Maps.newLinkedHashMap();

   public void register(String str, DercoVisualsShaderProgram pulseShaderProgram) {
      this.programs.put(str, pulseShaderProgram);
   }

   public void replace(String str, DercoVisualsShaderProgram pulseShaderProgram) {
      this.programs.replace(str, pulseShaderProgram);
   }

   public Optional<DercoVisualsShaderProgram> find(String str) {
      return Optional.ofNullable(this.programs.get(str));
   }
}
