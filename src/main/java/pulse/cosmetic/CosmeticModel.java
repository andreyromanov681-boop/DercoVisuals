package pulse.cosmetic;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import pulse.render.AttachmentPoint;

public class CosmeticModel {
   private final String c;
   private final int d;
   private final int e;
   private String f;
   private Identifier g;
   private float j;
   private float k;
   private float l;
   private float m;
   private float n;
   private float o;
   private JsonObject s;
   private AttachmentPoint h = AttachmentPoint.HEAD;
   private float i = 1.0F;
   private float p = 0.0F;
   private float q = 1.0F;
   private float r = 0.0F;

   public CosmeticModel(String str, int i, int i2) {
      this.c = str;
      this.d = i;
      this.e = i2;
   }

   public String a() {
      return this.c;
   }

   public int b() {
      return this.d;
   }

   public int c() {
      return this.e;
   }

   public String d() {
      return this.f;
   }

   public void a(String str) {
      this.f = str;
   }

   public Identifier e() {
      return this.g;
   }

   public void a(Identifier IdentifierVar) {
      this.g = IdentifierVar;
   }

   public AttachmentPoint f() {
      return this.h;
   }

   public void a(AttachmentPoint attachmentPoint) {
      this.h = attachmentPoint;
   }

   public float g() {
      return this.i;
   }

   public void a(float f) {
      this.i = f;
   }

   public float h() {
      return this.j;
   }

   public void b(float f) {
      this.j = f;
   }

   public float i() {
      return this.k;
   }

   public void c(float f) {
      this.k = f;
   }

   public float j() {
      return this.l;
   }

   public void d(float f) {
      this.l = f;
   }

   public float k() {
      return this.m;
   }

   public void e(float f) {
      this.m = f;
   }

   public float l() {
      return this.n;
   }

   public void f(float f) {
      this.n = f;
   }

   public float m() {
      return this.o;
   }

   public void g(float f) {
      this.o = f;
   }

   public float n() {
      return this.p;
   }

   public void h(float f) {
      this.p = f;
   }

   public float o() {
      return this.q;
   }

   public void i(float f) {
      this.q = f;
   }

   public float p() {
      return this.r;
   }

   public void j(float f) {
      this.r = f;
   }

   public JsonObject q() {
      return this.s;
   }

   public void a(JsonObject jsonObject) {
      this.s = jsonObject;
   }
}
