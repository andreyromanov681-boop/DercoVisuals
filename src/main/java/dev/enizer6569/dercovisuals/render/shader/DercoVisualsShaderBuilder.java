// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render.shader;

import java.io.InputStream;
import dev.enizer6569.dercovisuals.util.ResourceTextReader;
import dev.enizer6569.dercovisuals.DercoVisuals;

public class DercoVisualsShaderBuilder {
   private final DercoVisualsShaderProgram program = new DercoVisualsShaderProgram();

   private DercoVisualsShaderBuilder() {
   }

   public static DercoVisualsShaderBuilder create() {
      return new DercoVisualsShaderBuilder();
   }

   public DercoVisualsShaderBuilder attach(String str, DercoVisualsShaderBuilder.Stage stage) {
      InputStream resourceAsStream = this.getClass().getResourceAsStream("/assets/dercovisuals/shaders/".concat(str));
      if (resourceAsStream == null) {
         DercoVisuals.getLOGGER().error("Shader file not found: /assets/dercovisuals/shaders/{}", str);
         return this;
      } else {
         this.program.a(ResourceTextReader.a(resourceAsStream), stage.glType());
         return this;
      }
   }

   public DercoVisualsShaderBuilder link() {
      this.program.c();
      return this;
   }

   public DercoVisualsShaderProgram build() {
      return this.program;
   }

   public enum Stage {
      VERTEX(35633),
      FRAGMENT(35632);

      private final int glType;

      public int glType() {
         return this.glType;
      }

      Stage(int i) {
         this.glType = i;
      }
   }
}
