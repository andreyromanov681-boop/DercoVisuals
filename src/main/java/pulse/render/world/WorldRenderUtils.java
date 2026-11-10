package pulse.render.world;

import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.util.HashSet;
import java.util.Iterator;
import lombok.Generated;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferRenderer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import pulse.client.MinecraftContext;
import pulse.render.RenderSystemHelper;
import pulse.render.system.ClientPipelines;

public final class WorldRenderUtils implements MinecraftContext {
   private static final Tessellator e = Tessellator.getInstance();
   private static final Identifier f = Identifier.of("pulse", "textures/bloom.png");
   public static int a;
   public static boolean b;

   public static void a(MatrixStack MatrixStackVar, Vec3d Vec3dVar, float f2, int i, Identifier IdentifierVar, float f3, boolean z) {
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystemHelper.setShaderTexture(0, IdentifierVar);
      MatrixStackVar.push();
      RenderSystemHelper.disableCull();
      if (z) {
         RenderSystemHelper.disableDepthTest();
         GL11.glDepthMask(false);
      } else {
         GL11.glDepthMask(false);
      }

      RenderSystemHelper.enableBlend();
      int i2 = i >> 16;
      int i3 = (~i2 | 0xFF) - ~i2;
      int i4 = i >> 8;
      int i5 = (~i4 | 0xFF) - ~i4;
      int i6 = i >> 24;
      int i7 = (~i6 | 0xFF) - ~i6;
      RenderSystemHelper.blendFunc(770, 771);
      Vec3d Vec3dVarGetPos = c.getEntityRenderDispatcher().camera.getCameraPos();
      MatrixStackVar.translate(
         -Vec3dVarGetPos.x + Vec3dVar.x, -Vec3dVarGetPos.y + Vec3dVar.y, -Vec3dVarGetPos.z + Vec3dVar.z
      );
      MatrixStackVar.multiply(c.getEntityRenderDispatcher().camera.getRotation());
      MatrixStackVar.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f3));
      MatrixStackVar.scale(f2, f2, f2);
      VertexConsumer consumer = MinecraftClient.getInstance()
         .getBufferBuilders()
         .getEntityVertexConsumers()
         .getBuffer(z ? ClientPipelines.getTextureLayerThrough(IdentifierVar) : ClientPipelines.getTextureLayer(IdentifierVar));
      float f4 = i3 / 255.0F;
      float f5 = i5 / 255.0F;
      float f6 = ((~i | 0xFF) - ~i) / 255.0F;
      float f7 = i7 / 255.0F;
      consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(f4, f5, f6, f7);
      consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(f4, f5, f6, f7);
      consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(f4, f5, f6, f7);
      consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(f4, f5, f6, f7);
      if (z) {
         RenderSystemHelper.enableDepthTest();
         GL11.glDepthMask(true);
      } else {
         GL11.glDepthMask(true);
      }

      RenderSystemHelper.enableCull();
      MatrixStackVar.pop();
   }

   public static void a(MatrixStack MatrixStackVar, Vec3d Vec3dVar, float f2, int i) {
      RenderSystemHelper.setShaderTexture(0, f);
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      MatrixStackVar.push();
      RenderSystemHelper.disableCull();
      GL11.glDepthMask(false);
      RenderSystemHelper.enableBlend();
      int i2 = i >> 16;
      int i3 = (~i2 | 0xFF) - ~i2;
      int i4 = i >> 8;
      int i5 = (~i4 | 0xFF) - ~i4;
      int i6 = (~i | 0xFF) - ~i;
      int i7 = i >> 24;
      int i8 = (~i7 | 0xFF) - ~i7;
      if (((i3 ^ i5) + 2 * (i3 & i5) - ~i6 - 1) / 765.0F >= 0.2F) {
         RenderSystemHelper.blendFunc(770, 1);
         Vec3d Vec3dVarGetPos = c.getEntityRenderDispatcher().camera.getCameraPos();
         MatrixStackVar.translate(
            -Vec3dVarGetPos.x + Vec3dVar.x,
            -Vec3dVarGetPos.y + Vec3dVar.y,
            -Vec3dVarGetPos.z + Vec3dVar.z
         );
         MatrixStackVar.multiply(c.getEntityRenderDispatcher().camera.getRotation());
         MatrixStackVar.scale(f2, f2, f2);
         VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(ClientPipelines.getTextureLayerThrough(f));
         float f3 = i3 / 255.0F;
         float f4 = i5 / 255.0F;
         float f5 = i6 / 255.0F;
         float f6 = i8 / 255.0F;
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(f3, f4, f5, f6);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(f3, f4, f5, f6);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(f3, f4, f5, f6);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(f3, f4, f5, f6);
         RenderSystemHelper.blendFunc(770, 771);
      } else {
         RenderSystemHelper.blendFunc(770, 771);
         Vec3d Vec3dVarMethod_193262 = c.getEntityRenderDispatcher().camera.getCameraPos();
         MatrixStackVar.translate(
            -Vec3dVarMethod_193262.x + Vec3dVar.x,
            -Vec3dVarMethod_193262.y + Vec3dVar.y,
            -Vec3dVarMethod_193262.z + Vec3dVar.z
         );
         MatrixStackVar.multiply(c.getEntityRenderDispatcher().camera.getRotation());
         MatrixStackVar.scale(f2 * 1.2F, f2 * 1.2F, f2 * 1.2F);
         VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(ClientPipelines.getTextureLayerThrough(f));
         float f7 = i8 / 255.0F * 0.3F;
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, f7);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, f7);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, f7);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, f7);
         MatrixStackVar.scale(0.8333333F, 0.8333333F, 0.8333333F);
         float f8 = i3 / 255.0F;
         float f9 = i5 / 255.0F;
         float f10 = i6 / 255.0F;
         float f11 = i8 / 255.0F;
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(f8, f9, f10, f11);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(f8, f9, f10, f11);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(f8, f9, f10, f11);
         consumer.vertex(MatrixStackVar.peek().getPositionMatrix(), -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(f8, f9, f10, f11);
      }

      GL11.glDepthMask(true);
      RenderSystemHelper.enableCull();
      MatrixStackVar.pop();
   }

   public static void a(MatrixStack MatrixStackVar, Box BoxVar, int i) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(true);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(-1.0F, -1.0F);
      RenderSystemHelper.lineWidth(1.5F);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      double d = BoxVar.minX - Vec3dVarGetPos.x;
      double d2 = BoxVar.minY - Vec3dVarGetPos.y;
      double d3 = BoxVar.minZ - Vec3dVarGetPos.z;
      double d4 = BoxVar.maxX - Vec3dVarGetPos.x;
      double d5 = BoxVar.maxY - Vec3dVarGetPos.y;
      double d6 = BoxVar.maxZ - Vec3dVarGetPos.z;
      int i2 = i >> 16;
      float f2 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i3 = i >> 8;
      float f3 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      float f4 = ((~i | 0xFF) - ~i) / 255.0F;
      int i4 = i >> 24;
      float f5 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      if (f5 == 0.0F) {
         f5 = 1.0F;
      }

      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      BufferBuilder BufferBuilderVarMethod_608272 = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarMethod_608272.end());
      BufferBuilder BufferBuilderVarMethod_608273 = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarMethod_608273.end());
      GL11.glPolygonOffset(0.0F, 0.0F);
      GL11.glDisable(10754);
      RenderSystemHelper.lineWidth(1.0F);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   public static void a(MatrixStack MatrixStackVar, Vec3d Vec3dVar, float f2, int i, int i2, float f3) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(true);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(-1.0F, -1.0F);
      RenderSystemHelper.lineWidth(1.5F);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      int i3 = i2 >> 16;
      float f4 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      int i4 = i2 >> 8;
      float f5 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      float f6 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i5 = i2 >> 24;
      float f7 = ((~i5 | 0xFF) - ~i5) / 255.0F;
      if (f7 == 0.0F) {
         f7 = 1.0F;
      }

      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

      for (int i6 = 0; i6 <= i; i6++) {
         double d = (Math.PI * 2) * i6 / i;
         BufferBuilderVarBegin.vertex(
               matrix4fGetPositionMatrix,
               (float)(Vec3dVar.x + f2 * Math.cos(d) - Vec3dVarGetPos.x),
               (float)(Vec3dVar.y + f3 + 1.0 - Vec3dVarGetPos.y),
               (float)(Vec3dVar.z + f2 * Math.sin(d) - Vec3dVarGetPos.z)
            )
            .color(f4, f5, f6, f7);
      }

      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      GL11.glPolygonOffset(0.0F, 0.0F);
      GL11.glDisable(10754);
      RenderSystemHelper.lineWidth(1.0F);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   public static void a(MatrixStack MatrixStackVar, Vec3d Vec3dVar, float f2, int i, int i2) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.disableDepthTest();
      RenderSystemHelper.depthMask(false);
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      int i3 = i2 >> 16;
      float f3 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      int i4 = i2 >> 8;
      float f4 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      float f5 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i5 = i2 >> 24;
      float f6 = ((~i5 | 0xFF) - ~i5) / 255.0F * 0.3F;
      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
      double d = Vec3dVar.x - Vec3dVarGetPos.x;
      double d2 = Vec3dVar.y - Vec3dVarGetPos.y;
      double d3 = Vec3dVar.z - Vec3dVarGetPos.z;
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f3, f4, f5, f6);

      for (int i6 = 0; i6 <= i; i6++) {
         double d4 = (Math.PI * 2) * i6 / i;
         BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)(d + f2 * Math.cos(d4)), (float)d2, (float)(d3 + f2 * Math.sin(d4)))
            .color(f3, f4, f5, f6);
      }

      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(true);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   public static void a(MatrixStack MatrixStackVar, Vec3d Vec3dVar, float f2, float f3, int i, int i2) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.disableDepthTest();
      RenderSystemHelper.depthMask(false);
      RenderSystemHelper.lineWidth(1.5F);
      a(MatrixStackVar, Vec3dVar, f2, i, i2, 0.0F);
      a(MatrixStackVar, Vec3dVar, f2, i, i2, f3);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      int i3 = i2 >> 16;
      float f4 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      int i4 = i2 >> 8;
      float f5 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      float f6 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i5 = i2 >> 24;
      float f7 = ((~i5 | 0xFF) - ~i5) / 255.0F;
      if (f7 == 0.0F) {
         f7 = 1.0F;
      }

      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      for (int i6 = 0; i6 < 8; i6++) {
         double d = (Math.PI * 2) * i6 / 8.0;
         double dCos = Vec3dVar.x + f2 * Math.cos(d) - Vec3dVarGetPos.x;
         double dSin = Vec3dVar.z + f2 * Math.sin(d) - Vec3dVarGetPos.z;
         double d2 = Vec3dVar.y - Vec3dVarGetPos.y;
         BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)dCos, (float)d2, (float)dSin).color(f4, f5, f6, f7);
         BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)dCos, (float)(d2 + f3), (float)dSin).color(f4, f5, f6, f7);
      }

      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      RenderSystemHelper.lineWidth(1.0F);
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(true);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   public static void b(MatrixStack MatrixStackVar, Box BoxVar, int i) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(false);
      RenderSystemHelper.disableCull();
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      double d = BoxVar.minX - Vec3dVarGetPos.x;
      double d2 = BoxVar.minY - Vec3dVarGetPos.y;
      double d3 = BoxVar.minZ - Vec3dVarGetPos.z;
      double d4 = BoxVar.maxX - Vec3dVarGetPos.x;
      double d5 = BoxVar.maxY - Vec3dVarGetPos.y;
      double d6 = BoxVar.maxZ - Vec3dVarGetPos.z;
      int i2 = i >> 16;
      float f2 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i3 = i >> 8;
      float f3 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      float f4 = ((~i | 0xFF) - ~i) / 255.0F;
      int i4 = i >> 24;
      float f5 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      if (f5 == 0.0F) {
         f5 = 1.0F;
      }

      float f6 = f5 * 0.11F;
      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.QUADS, VertexFormats.POSITION_COLOR);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f6);
      BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f6);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      RenderSystemHelper.enableCull();
      RenderSystemHelper.depthMask(true);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(-1.0F, -1.0F);
      RenderSystemHelper.lineWidth(1.5F);
      BufferBuilder BufferBuilderVarMethod_608272 = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarMethod_608272.end());
      BufferBuilder BufferBuilderVarMethod_608273 = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608273.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarMethod_608273.end());
      BufferBuilder BufferBuilderVarMethod_608274 = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d3).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d4, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d, (float)d2, (float)d6).color(f2, f3, f4, f5);
      BufferBuilderVarMethod_608274.vertex(matrix4fGetPositionMatrix, (float)d, (float)d5, (float)d6).color(f2, f3, f4, f5);
      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarMethod_608274.end());
      GL11.glPolygonOffset(0.0F, 0.0F);
      GL11.glDisable(10754);
      RenderSystemHelper.lineWidth(1.0F);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   public static void a(MatrixStack MatrixStackVar, Iterable<BlockPos> iterable, int i) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(true);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(-1.0F, -1.0F);
      RenderSystemHelper.lineWidth(1.5F);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      int i2 = i >> 16;
      float f2 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i3 = i >> 8;
      float f3 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      float f4 = ((~i | 0xFF) - ~i) / 255.0F;
      int i4 = i >> 24;
      float f5 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      if (f5 == 0.0F) {
         f5 = 1.0F;
      }

      HashSet<BlockPos> hashSet = new HashSet<>();
      Iterator<BlockPos> it = iterable.iterator();

      while (it.hasNext()) {
         hashSet.add(it.next());
      }

      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      for (BlockPos BlockPosVar : hashSet) {
         float fGetX = BlockPosVar.getX() - (float)Vec3dVarGetPos.x;
         float fGetY = BlockPosVar.getY() - (float)Vec3dVarGetPos.y;
         float fGetZ = BlockPosVar.getZ() - (float)Vec3dVarGetPos.z;
         float f6 = fGetX + 1.0F;
         float f7 = fGetY + 1.0F;
         float f8 = fGetZ + 1.0F;
         if (!hashSet.contains(BlockPosVar.down())) {
            if (!hashSet.contains(BlockPosVar.west())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f8).color(f2, f3, f4, f5);
            }

            if (!hashSet.contains(BlockPosVar.east())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, fGetZ).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, f8).color(f2, f3, f4, f5);
            }

            if (!hashSet.contains(BlockPosVar.north())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, fGetZ).color(f2, f3, f4, f5);
            }

            if (!hashSet.contains(BlockPosVar.south())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f8).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, f8).color(f2, f3, f4, f5);
            }
         }

         if (!hashSet.contains(BlockPosVar.up())) {
            if (!hashSet.contains(BlockPosVar.west())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, fGetZ).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, f8).color(f2, f3, f4, f5);
            }

            if (!hashSet.contains(BlockPosVar.east())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, fGetZ).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, f8).color(f2, f3, f4, f5);
            }

            if (!hashSet.contains(BlockPosVar.north())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, fGetZ).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, fGetZ).color(f2, f3, f4, f5);
            } else if (b) {
            }

            if (!hashSet.contains(BlockPosVar.south())) {
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, f8).color(f2, f3, f4, f5);
               BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, f8).color(f2, f3, f4, f5);
            }
         }

         if (!hashSet.contains(BlockPosVar.west()) && !hashSet.contains(BlockPosVar.north())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, fGetZ).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.east()) && !hashSet.contains(BlockPosVar.north())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, fGetZ).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, fGetZ).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.east()) && !hashSet.contains(BlockPosVar.south())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, f8).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, f8).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.west()) && !hashSet.contains(BlockPosVar.south())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f8).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, f8).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.west())
            && hashSet.contains(BlockPosVar.north())
            && !hashSet.contains(BlockPosVar.west().north())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, fGetZ).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.north())
            && hashSet.contains(BlockPosVar.west())
            && !hashSet.contains(BlockPosVar.north().west())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, fGetZ).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.east())
            && hashSet.contains(BlockPosVar.north())
            && !hashSet.contains(BlockPosVar.east().north())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, fGetZ).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, fGetZ).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.north())
            && hashSet.contains(BlockPosVar.east())
            && !hashSet.contains(BlockPosVar.north().east())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, fGetZ).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, fGetZ).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.east())
            && hashSet.contains(BlockPosVar.south())
            && !hashSet.contains(BlockPosVar.east().south())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, f8).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, f8).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.south())
            && hashSet.contains(BlockPosVar.east())
            && !hashSet.contains(BlockPosVar.south().east())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, fGetY, f8).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f6, f7, f8).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.west())
            && hashSet.contains(BlockPosVar.south())
            && !hashSet.contains(BlockPosVar.west().south())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f8).color(f2, f3, f4, f5);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, f8).color(f2, f3, f4, f5);
         }

         if (!hashSet.contains(BlockPosVar.south())) {
            if (hashSet.contains(BlockPosVar.west())) {
               if (!hashSet.contains(BlockPosVar.south().west())) {
                  BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f8).color(f2, f3, f4, f5);
                  BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f7, f8).color(f2, f3, f4, f5);
               }
            } else if (b) {
            }
         }
      }

      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      GL11.glPolygonOffset(0.0F, 0.0F);
      GL11.glDisable(10754);
      RenderSystemHelper.lineWidth(1.0F);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   public static void b(MatrixStack MatrixStackVar, Iterable<BlockPos> iterable, int i) {
      MatrixStackVar.push();
      RenderSystemHelper.enableBlend();
      RenderSystemHelper.defaultBlendFunc();
      RenderSystemHelper.enableDepthTest();
      RenderSystemHelper.depthMask(false);
      RenderSystemHelper.disableCull();
      RenderSystemHelper.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator TessellatorVarGetInstance = Tessellator.getInstance();
      Matrix4f matrix4fGetPositionMatrix = MatrixStackVar.peek().getPositionMatrix();
      Vec3d Vec3dVarGetPos = c.gameRenderer.getCamera().getCameraPos();
      int i2 = i >> 16;
      float f5 = ((~i2 | 0xFF) - ~i2) / 255.0F;
      int i3 = i >> 8;
      float f6 = ((~i3 | 0xFF) - ~i3) / 255.0F;
      float f7 = ((~i | 0xFF) - ~i) / 255.0F;
      int i4 = i >> 24;
      float f8 = ((~i4 | 0xFF) - ~i4) / 255.0F;
      if (f8 == 0.0F) {
         f8 = 1.0F;
      }

      float f9 = f8 * 0.2F;
      HashSet<BlockPos> hashSet = new HashSet<>();
      int iMin = Integer.MAX_VALUE;
      int iMin2 = Integer.MAX_VALUE;
      int iMin3 = Integer.MAX_VALUE;
      int iMax = Integer.MIN_VALUE;
      int iMax2 = Integer.MIN_VALUE;
      int iMax3 = Integer.MIN_VALUE;

      for (BlockPos BlockPosVar : iterable) {
         hashSet.add(BlockPosVar);
         iMin = Math.min(iMin, BlockPosVar.getX());
         iMin2 = Math.min(iMin2, BlockPosVar.getY());
         iMin3 = Math.min(iMin3, BlockPosVar.getZ());
         iMax = Math.max(iMax, BlockPosVar.getX());
         iMax2 = Math.max(iMax2, BlockPosVar.getY());
         iMax3 = Math.max(iMax3, BlockPosVar.getZ());
      }

      BufferBuilder BufferBuilderVarBegin = TessellatorVarGetInstance.begin(class_5596.QUADS, VertexFormats.POSITION_COLOR);

      for (BlockPos BlockPosVar2 : hashSet) {
         float f10 = BlockPosVar2.getX() != iMin ? 0.0F : 0.01F;
         float f11 = BlockPosVar2.getY() != iMin2 ? 0.0F : 0.01F;
         float f12 = BlockPosVar2.getZ() != iMin3 ? 0.0F : 0.01F;
         float f3;
         if (BlockPosVar2.getX() != iMax) {
            f3 = 0.0F;
         } else {
            f3 = 0.01F;
         }

         float f13 = f3;
         float f4;
         if (BlockPosVar2.getY() != iMax2) {
            f4 = 0.0F;
         } else {
            f4 = 0.01F;
         }

         float f14 = f4;
         float f15 = BlockPosVar2.getZ() != iMax3 ? 0.0F : 0.01F;
         float fGetX = BlockPosVar2.getX() - (float)Vec3dVarGetPos.x + f10;
         float fGetY = BlockPosVar2.getY() - (float)Vec3dVarGetPos.y + f11;
         float fGetZ = BlockPosVar2.getZ() - (float)Vec3dVarGetPos.z + f12;
         float f16 = fGetX + 1.0F - f10 - f13;
         float f17 = fGetY + 1.0F - f11 - f14;
         float f18 = fGetZ + 1.0F - f12 - f15;
         if (!hashSet.contains(BlockPosVar2.down())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, fGetY, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, fGetY, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f18).color(f5, f6, f7, f9);
         }

         if (!hashSet.contains(BlockPosVar2.up())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f17, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f17, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, f17, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, f17, fGetZ).color(f5, f6, f7, f9);
         }

         if (!hashSet.contains(BlockPosVar2.north())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f17, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, f17, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, fGetY, fGetZ).color(f5, f6, f7, f9);
         }

         if (!hashSet.contains(BlockPosVar2.south())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, fGetY, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, f17, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f17, f18).color(f5, f6, f7, f9);
         }

         if (!hashSet.contains(BlockPosVar2.west())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, fGetY, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f17, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, fGetX, f17, fGetZ).color(f5, f6, f7, f9);
         }

         if (!hashSet.contains(BlockPosVar2.east())) {
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, fGetY, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, f17, fGetZ).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, f17, f18).color(f5, f6, f7, f9);
            BufferBuilderVarBegin.vertex(matrix4fGetPositionMatrix, f16, fGetY, f18).color(f5, f6, f7, f9);
         }
      }

      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarBegin.end());
      RenderSystemHelper.enableCull();
      RenderSystemHelper.depthMask(true);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(-1.0F, -1.0F);
      RenderSystemHelper.lineWidth(1.5F);
      BufferBuilder BufferBuilderVarMethod_608272 = TessellatorVarGetInstance.begin(class_5596.DEBUG_LINES, VertexFormats.POSITION_COLOR);

      for (BlockPos BlockPosVar3 : hashSet) {
         float f19 = BlockPosVar3.getX() != iMin ? 0.0F : 0.01F;
         float f20 = BlockPosVar3.getY() != iMin2 ? 0.0F : 0.01F;
         float f21 = BlockPosVar3.getZ() != iMin3 ? 0.0F : 0.01F;
         float f22 = BlockPosVar3.getX() != iMax ? 0.0F : 0.01F;
         float f23 = BlockPosVar3.getY() != iMax2 ? 0.0F : 0.01F;
         float f2;
         if (BlockPosVar3.getZ() != iMax3) {
            f2 = 0.0F;
         } else {
            f2 = 0.01F;
         }

         float fMethod_102632 = BlockPosVar3.getX() - (float)Vec3dVarGetPos.x + f19;
         float fMethod_102642 = BlockPosVar3.getY() - (float)Vec3dVarGetPos.y + f20;
         float fMethod_102602 = BlockPosVar3.getZ() - (float)Vec3dVarGetPos.z + f21;
         float f24 = fMethod_102632 + 1.0F - f19 - f22;
         float f25 = fMethod_102642 + 1.0F - f20 - f23;
         float f26 = fMethod_102602 + 1.0F - f21 - f2;
         if (!hashSet.contains(BlockPosVar3.down())) {
            if (!hashSet.contains(BlockPosVar3.west())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, fMethod_102602)
                  .color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, f26).color(f5, f6, f7, f8);
            }

            if (!hashSet.contains(BlockPosVar3.east())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, f26).color(f5, f6, f7, f8);
            }

            if (!hashSet.contains(BlockPosVar3.north())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, fMethod_102602)
                  .color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
            }

            if (!hashSet.contains(BlockPosVar3.south())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, f26).color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, f26).color(f5, f6, f7, f8);
            }
         }

         if (!hashSet.contains(BlockPosVar3.up())) {
            if (!hashSet.contains(BlockPosVar3.west())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, fMethod_102602).color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, f26).color(f5, f6, f7, f8);
            }

            if (!hashSet.contains(BlockPosVar3.east())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, fMethod_102602).color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, f26).color(f5, f6, f7, f8);
            }

            if (!hashSet.contains(BlockPosVar3.north())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, fMethod_102602).color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, fMethod_102602).color(f5, f6, f7, f8);
            }

            if (!hashSet.contains(BlockPosVar3.south())) {
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, f26).color(f5, f6, f7, f8);
               BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, f26).color(f5, f6, f7, f8);
            }
         }

         if (!hashSet.contains(BlockPosVar3.west()) && !hashSet.contains(BlockPosVar3.north())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, fMethod_102602).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.east()) && !hashSet.contains(BlockPosVar3.north())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, fMethod_102602).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.east()) && !hashSet.contains(BlockPosVar3.south())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, f26).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, f26).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.west()) && !hashSet.contains(BlockPosVar3.south())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, f26).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, f26).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.west())
            && hashSet.contains(BlockPosVar3.north())
            && !hashSet.contains(BlockPosVar3.west().north())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, fMethod_102602).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.north())
            && hashSet.contains(BlockPosVar3.west())
            && !hashSet.contains(BlockPosVar3.north().west())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, fMethod_102602).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.east())) {
            if (hashSet.contains(BlockPosVar3.north())) {
               if (!hashSet.contains(BlockPosVar3.east().north())) {
                  BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
                  BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, fMethod_102602).color(f5, f6, f7, f8);
               }
            } else if (b) {
            }
         }

         if (!hashSet.contains(BlockPosVar3.north())) {
            if (hashSet.contains(BlockPosVar3.east())) {
               if (!hashSet.contains(BlockPosVar3.north().east())) {
                  BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, fMethod_102602).color(f5, f6, f7, f8);
                  BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, fMethod_102602).color(f5, f6, f7, f8);
               }
            } else if (b) {
            }
         }

         if (!hashSet.contains(BlockPosVar3.east())
            && hashSet.contains(BlockPosVar3.south())
            && !hashSet.contains(BlockPosVar3.east().south())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, f26).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, f26).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.south())
            && hashSet.contains(BlockPosVar3.east())
            && !hashSet.contains(BlockPosVar3.south().east())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, fMethod_102642, f26).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, f24, f25, f26).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.west())
            && hashSet.contains(BlockPosVar3.south())
            && !hashSet.contains(BlockPosVar3.west().south())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, f26).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, f26).color(f5, f6, f7, f8);
         }

         if (!hashSet.contains(BlockPosVar3.south())
            && hashSet.contains(BlockPosVar3.west())
            && !hashSet.contains(BlockPosVar3.south().west())) {
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, fMethod_102642, f26).color(f5, f6, f7, f8);
            BufferBuilderVarMethod_608272.vertex(matrix4fGetPositionMatrix, fMethod_102632, f25, f26).color(f5, f6, f7, f8);
         }
      }

      BufferRenderer.drawWithGlobalProgram(BufferBuilderVarMethod_608272.end());
      GL11.glPolygonOffset(0.0F, 0.0F);
      GL11.glDisable(10754);
      RenderSystemHelper.lineWidth(1.0F);
      RenderSystemHelper.disableBlend();
      MatrixStackVar.pop();
   }

   @Generated
   private WorldRenderUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static String b(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }
}
