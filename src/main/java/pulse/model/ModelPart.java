package pulse.model;

import java.util.ArrayList;
import java.util.List;

public class ModelPart {
   public ModelPart a;
   public String d;
   public boolean e;
   public float f;
   public float g;
   public float h;
   private float k;
   private float l;
   private float m;
   private float n;
   private float o;
   private float p;
   public List<ModelPart> b = new ArrayList<>();
   public List<ModelCube> c = new ArrayList<>();
   private float q = 1.0F;
   private float r = 1.0F;
   private float s = 1.0F;

   public ModelPart(String str) {
      this.d = str;
   }

   public float a() {
      return this.k;
   }

   public float b() {
      return this.l;
   }

   public float c() {
      return this.m;
   }

   public void a(float f) {
      this.k = f;
   }

   public void b(float f) {
      this.l = f;
   }

   public void c(float f) {
      this.m = f;
   }

   public float d() {
      return this.n;
   }

   public float e() {
      return this.o;
   }

   public float f() {
      return this.p;
   }

   public void d(float f) {
      this.n = f;
   }

   public void e(float f) {
      this.o = f;
   }

   public void f(float f) {
      this.p = f;
   }

   public float g() {
      return this.q;
   }

   public float h() {
      return this.r;
   }

   public float i() {
      return this.s;
   }

   public void g(float f) {
      this.q = f;
   }

   public void h(float f) {
      this.r = f;
   }

   public void i(float f) {
      this.s = f;
   }

   public float j() {
      return this.f;
   }

   public float k() {
      return this.g;
   }

   public float l() {
      return this.h;
   }

   public void j(float f) {
      this.f = f;
   }

   public void k(float f) {
      this.g = f;
   }

   public void l(float f) {
      this.h = f;
   }

   public String m() {
      return this.d;
   }

   public boolean n() {
      return this.e;
   }

   public void a(boolean z) {
      this.e = z;
   }
}
