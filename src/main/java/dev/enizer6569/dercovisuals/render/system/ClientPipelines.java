// Maintainer: enizer6569 (DercoVisuals). Original authorship and rights remain with their respective holders.
package dev.enizer6569.dercovisuals.render.system;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ClientPipelines {
   public static final RenderPipeline QUADS_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_quads")
         .withVertexShader("core/position_color")
         .withFragmentShader("core/position_color")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.QUADS)
         .build()
   );
   public static final RenderLayer QUAD = RenderLayer.of("dercovisuals_quads", RenderSetup.builder(QUADS_PIPELINE).expectedBufferSize(4096).build());
   public static final RenderPipeline QUADS_THROUGH_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_quads_through")
         .withVertexShader("core/position_color")
         .withFragmentShader("core/position_color")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.QUADS)
         .build()
   );
   public static final RenderLayer QUAD_THROUGH = RenderLayer.of(
      "dercovisuals_quads_through", RenderSetup.builder(QUADS_THROUGH_PIPELINE).expectedBufferSize(4096).build()
   );
   public static final RenderPipeline FILL_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_fill")
         .withVertexShader("core/position_color")
         .withFragmentShader("core/position_color")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.QUADS)
         .build()
   );
   public static final RenderLayer FILL = RenderLayer.of("dercovisuals_fill", RenderSetup.builder(FILL_PIPELINE).expectedBufferSize(4096).build());
   public static final RenderPipeline OUTLINE_PIPELINE_NO = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_outline_lines")
         .withVertexShader("core/position_color")
         .withFragmentShader("core/position_color")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINES)
         .build()
   );
   public static final RenderLayer OUTLINE_NO = RenderLayer.of(
      "dercovisuals_outline_lines", RenderSetup.builder(OUTLINE_PIPELINE_NO).expectedBufferSize(4096).build()
   );
   public static final RenderPipeline OUTLINE_THROUGH_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_outline_lines_through")
         .withVertexShader("core/position_color")
         .withFragmentShader("core/position_color")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_COLOR, class_5596.DEBUG_LINES)
         .build()
   );
   public static final RenderLayer OUTLINE_THROUGH = RenderLayer.of(
      "dercovisuals_outline_lines_through", RenderSetup.builder(OUTLINE_THROUGH_PIPELINE).expectedBufferSize(4096).build()
   );
   public static final RenderPipeline TEXTURED_QUAD_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_tex_quad")
         .withVertexShader("core/position_tex_color")
         .withFragmentShader("core/position_tex_color")
         .withSampler("Sampler0")
         .withBlend(BlendFunction.ADDITIVE)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, class_5596.QUADS)
         .build()
   );
   public static final RenderPipeline TEXTURED_QUAD_THROUGH_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET})
         .withLocation("pipeline/dercovisuals_tex_quad_through")
         .withVertexShader("core/position_tex_color")
         .withFragmentShader("core/position_tex_color")
         .withSampler("Sampler0")
         .withBlend(BlendFunction.ADDITIVE)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, class_5596.QUADS)
         .build()
   );
   private static final Map<Identifier, RenderLayer> TEXTURED_LAYERS = new ConcurrentHashMap<>();
   private static final Map<Identifier, RenderLayer> TEXTURED_THROUGH_LAYERS = new ConcurrentHashMap<>();
   public static final RenderPipeline SKY_NEBULA_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation("pipeline/nebula")
         .withUniform("ShaderFogData", UniformType.UNIFORM_BUFFER)
         .withVertexShader(Identifier.of("dercovisuals", "core/shader_fog"))
         .withFragmentShader(Identifier.of("dercovisuals", "core/nebula"))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, class_5596.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SKY_PLASMA_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation("pipeline/plasma")
         .withUniform("ShaderFogData", UniformType.UNIFORM_BUFFER)
         .withVertexShader(Identifier.of("dercovisuals", "core/shader_fog"))
         .withFragmentShader(Identifier.of("dercovisuals", "core/plasma"))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, class_5596.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SKY_BLOOM_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation("pipeline/bloom")
         .withUniform("ShaderFogData", UniformType.UNIFORM_BUFFER)
         .withVertexShader(Identifier.of("dercovisuals", "core/shader_fog"))
         .withFragmentShader(Identifier.of("dercovisuals", "core/bloom"))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, class_5596.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SKY_CAUSTIC_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation("pipeline/caustic")
         .withUniform("ShaderFogData", UniformType.UNIFORM_BUFFER)
         .withVertexShader(Identifier.of("dercovisuals", "core/shader_fog"))
         .withFragmentShader(Identifier.of("dercovisuals", "core/caustic"))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, class_5596.TRIANGLES)
         .build()
   );
   public static final RenderPipeline SKY_DRAIN_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET})
         .withLocation("pipeline/drain")
         .withUniform("ShaderFogData", UniformType.UNIFORM_BUFFER)
         .withVertexShader(Identifier.of("dercovisuals", "core/shader_fog"))
         .withFragmentShader(Identifier.of("dercovisuals", "core/drain"))
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, class_5596.TRIANGLES)
         .build()
   );

   public static RenderLayer getTextureLayer(Identifier texture) {
      return TEXTURED_LAYERS.computeIfAbsent(
         texture,
         tex -> RenderLayer.of(
            "dercovisuals_tex_" + tex.getPath().replace('/', '_'),
            RenderSetup.builder(TEXTURED_QUAD_PIPELINE).texture("Sampler0", tex).expectedBufferSize(4096).build()
         )
      );
   }

   public static RenderLayer getTextureLayerThrough(Identifier texture) {
      return TEXTURED_THROUGH_LAYERS.computeIfAbsent(
         texture,
         tex -> RenderLayer.of(
            "dercovisuals_tex_through_" + tex.getPath().replace('/', '_'),
            RenderSetup.builder(TEXTURED_QUAD_THROUGH_PIPELINE).texture("Sampler0", tex).expectedBufferSize(4096).build()
         )
      );
   }
}
