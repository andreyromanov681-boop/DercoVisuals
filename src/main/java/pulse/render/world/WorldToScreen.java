package pulse.render.world;

import lombok.Generated;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import pulse.client.MinecraftContext;
import pulse.render.ScreenScale;

public final class WorldToScreen implements MinecraftContext {
   public static final Matrix4f a = new Matrix4f();
   public static final Matrix4f b = new Matrix4f();
   public static final Matrix4f e = new Matrix4f();

   private static double[] a(double d, double d2, double d3) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null && mc.getWindow() != null) {
         Camera camera = mc.gameRenderer.getCamera();
         Vec3d camPos = camera.getCameraPos();
         float relX = (float)(d - camPos.x);
         float relY = (float)(d2 - camPos.y);
         float relZ = (float)(d3 - camPos.z);
         Vector3f eye = new Vector3f(relX, relY, relZ);
         eye.rotate(camera.getRotation().conjugate(new Quaternionf()));
         Vector4f clip = new Vector4f(eye.x, eye.y, eye.z, 1.0F);
         a.transform(clip);
         if (clip.w <= 0.05F) {
            return null;
         }

         float ndcX = clip.x / clip.w;
         float ndcY = clip.y / clip.w;
         double scale = ScreenScale.d();
         float width = (float)(mc.getWindow().getFramebufferWidth() / scale);
         float height = (float)(mc.getWindow().getFramebufferHeight() / scale);
         double screenX = (ndcX + 1.0F) * 0.5F * width;
         double screenY = (1.0F - ndcY) * 0.5F * height;
         return new double[]{screenX, screenY, clip.w};
      } else {
         return null;
      }
   }

   public static Vec3d a(Vec3d Vec3dVar) {
      if (c.gameRenderer != null && c.getWindow() != null && c.player != null) {
         double[] dArrA = a(Vec3dVar.x, Vec3dVar.y, Vec3dVar.z);
         return dArrA == null ? null : new Vec3d(dArrA[0], dArrA[1], dArrA[2]);
      } else {
         return null;
      }
   }

   public static boolean b(Vec3d Vec3dVar) {
      if (Vec3dVar == null) {
         return false;
      }

      double scale = ScreenScale.d();
      float width = (float)(c.getWindow().getFramebufferWidth() / scale);
      float height = (float)(c.getWindow().getFramebufferHeight() / scale);
      return Vec3dVar.x >= -100.0 && Vec3dVar.x <= width + 100.0F && Vec3dVar.y >= -100.0 && Vec3dVar.y <= height + 100.0F;
   }

   public static boolean c(Vec3d Vec3dVar) {
      if (c.player != null && c.gameRenderer != null && c.gameRenderer.getCamera() != null) {
         Vec3d camPos = c.gameRenderer.getCamera().getCameraPos();
         Vec3d look = c.player.getRotationVec(1.0F);
         Vec3d dir = Vec3dVar.subtract(camPos).normalize();
         return look.dotProduct(dir) > 0.05;
      } else {
         return false;
      }
   }

   @Generated
   private WorldToScreen() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
