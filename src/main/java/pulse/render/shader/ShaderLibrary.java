package pulse.render.shader;

public final class ShaderLibrary {
   private static final ShaderRegistry REGISTRY = new ShaderRegistry();

   private ShaderLibrary() {
   }

   public static ShaderRegistry getRegistry() {
      return REGISTRY;
   }

   public static void loadDefaultShaders() {
      if (!REGISTRY.find("round_rect").isPresent()) {
         register("round_rect", "fragment/round_rect.fsh");
         register("round_rect_outline", "fragment/round_rect_outline.fsh");
         register("drop_shadow", "fragment/drop_shadow.fsh");
         register("rect_glow", "fragment/rect_glow.fsh");
         register("round_texture", "fragment/round_texture.fsh");
         register("gradient", "fragment/gradient.fsh");
         register("hue_bar", "fragment/hue_bar.fsh");
         register("hand_shader", "fragment/hand_shader.fsh");
         register("block_nebula", "fragment/block_nebula.fsh");
         register("block_starfield", "fragment/block_starfield.fsh");
         register("block_cobweb", "fragment/block_cobweb.fsh");
         register("block_plasma", "fragment/block_plasma.fsh");
         REGISTRY.register(
            "passthrough_color",
            PulseShaderBuilder.create()
               .attach("vertex/passthrough_color.vsh", PulseShaderBuilder.Stage.VERTEX)
               .attach("fragment/passthrough_color.fsh", PulseShaderBuilder.Stage.FRAGMENT)
               .link()
               .build()
         );
      }
   }

   private static void register(String str, String str2) {
      REGISTRY.register(
         str,
         PulseShaderBuilder.create()
            .attach("vertex/passthrough.vsh", PulseShaderBuilder.Stage.VERTEX)
            .attach(str2, PulseShaderBuilder.Stage.FRAGMENT)
            .link()
            .build()
      );
   }
}
