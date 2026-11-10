package pulse.hud.snap;

import java.util.ArrayList;
import java.util.List;
import pulse.core.Bool;
import pulse.hud.core.HudElement;

public class HudSnapCalculator {
   private static final float c = 6.0F;
   private static final float d = 5.0F;
   private static final float e = 10.0F;
   public static int a;
   public static boolean b;

   public static HudSnapResult a(HudElement hudElement, float f, float f2, float f3, float f4, List<HudElement> list, boolean z) {
      if (z) {
         return HudSnapResult.a(Math.max(5.0F, Math.min(f, f3 - hudElement.n() - 5.0F)), Math.max(5.0F, Math.min(f2, f4 - hudElement.o() - 5.0F)));
      }

      float fN = hudElement.n();
      float fO = hudElement.o();
      ArrayList arrayList = new ArrayList();
      float f5 = f;
      float f6 = f2;
      int i = 0;
      int i2 = 0;
      float f7 = f + fN;
      float f8 = f + fN / 2.0F;
      float f9 = f2 + fO;
      float f10 = f2 + fO / 2.0F;
      HudSnapCalculator.SnapCandidate snapCandidateA = a(f, f7, f8, fN, f3, f4, list, hudElement, f2, f9);
      if (snapCandidateA != null) {
         f5 = snapCandidateA.b;
         arrayList.add(snapCandidateA.c);
         i = 1;
      }

      HudSnapCalculator.SnapCandidate snapCandidateB = b(f2, f9, f10, fO, f3, f4, list, hudElement, f, f7);
      if (snapCandidateB != null) {
         f6 = snapCandidateB.b;
         arrayList.add(snapCandidateB.c);
      }

      return new HudSnapResult(
         Math.max(5.0F, Math.min(f5, f3 - fN - 5.0F)), Math.max(5.0F, Math.min(f6, f4 - fO - 5.0F)), arrayList, Bool.from(i), Bool.from(i2)
      );
   }

   private static HudSnapCalculator.SnapCandidate a(
      float f, float f2, float f3, float f4, float f5, float f6, List<HudElement> list, HudElement hudElement, float f7, float f8
   ) {
      HudSnapCalculator.SnapCandidate snapCandidateA = Math.abs(f - 5.0F) < 6.0F
         ? a(
            null,
            Math.abs(f - 5.0F),
            5.0F,
            new HudSnapGuide(HudSnapGuide.Orientation.VERTICAL, HudSnapGuide.Anchor.SCREEN_EDGE, 5.0F, Math.max(0.0F, f7 - 10.0F), Math.min(f6, f8 + 10.0F))
         )
         : null;
      if (Math.abs(f2 - (f5 - 5.0F)) < 6.0F) {
         snapCandidateA = a(
            snapCandidateA,
            Math.abs(f2 - (f5 - 5.0F)),
            f5 - 5.0F - f4,
            new HudSnapGuide(
               HudSnapGuide.Orientation.VERTICAL, HudSnapGuide.Anchor.SCREEN_EDGE, f5 - 5.0F, Math.max(0.0F, f7 - 10.0F), Math.min(f6, f8 + 10.0F)
            )
         );
      }

      float f9 = f5 / 2.0F;
      if (Math.abs(f3 - f9) < 6.0F) {
         snapCandidateA = a(
            snapCandidateA,
            Math.abs(f3 - f9),
            f9 - f4 / 2.0F,
            new HudSnapGuide(HudSnapGuide.Orientation.VERTICAL, HudSnapGuide.Anchor.SCREEN_CENTER, f9, Math.max(0.0F, f7 - 10.0F), Math.min(f6, f8 + 10.0F))
         );
      }

      for (HudElement hudElement2 : list) {
         if (hudElement2 != hudElement) {
            float fL = hudElement2.l();
            float fL2 = hudElement2.l() + hudElement2.n();
            float fL3 = hudElement2.l() + hudElement2.n() / 2.0F;
            float fM = hudElement2.m();
            float fM2 = hudElement2.m() + hudElement2.o();
            if (Math.abs(f - fL) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f - fL), fL, a(fL, f7, f8, fM, fM2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f2 - fL2) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f2 - fL2), fL2 - f4, a(fL2, f7, f8, fM, fM2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f - fL2) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f - fL2), fL2, a(fL2, f7, f8, fM, fM2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f2 - fL) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f2 - fL), fL - f4, a(fL, f7, f8, fM, fM2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f3 - fL3) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f3 - fL3), fL3 - f4 / 2.0F, a(fL3, f7, f8, fM, fM2, HudSnapGuide.Anchor.ELEMENT_CENTER));
            }
         }
      }

      return snapCandidateA;
   }

   private static HudSnapCalculator.SnapCandidate b(
      float f, float f2, float f3, float f4, float f5, float f6, List<HudElement> list, HudElement hudElement, float f7, float f8
   ) {
      HudSnapCalculator.SnapCandidate snapCandidateA = null;
      if (Math.abs(f - 5.0F) < 6.0F) {
         snapCandidateA = a(
            null,
            Math.abs(f - 5.0F),
            5.0F,
            new HudSnapGuide(HudSnapGuide.Orientation.HORIZONTAL, HudSnapGuide.Anchor.SCREEN_EDGE, 5.0F, Math.max(0.0F, f7 - 10.0F), Math.min(f5, f8 + 10.0F))
         );
      } else if (b) {
      }

      if (Math.abs(f2 - (f6 - 5.0F)) < 6.0F) {
         snapCandidateA = a(
            snapCandidateA,
            Math.abs(f2 - (f6 - 5.0F)),
            f6 - 5.0F - f4,
            new HudSnapGuide(
               HudSnapGuide.Orientation.HORIZONTAL, HudSnapGuide.Anchor.SCREEN_EDGE, f6 - 5.0F, Math.max(0.0F, f7 - 10.0F), Math.min(f5, f8 + 10.0F)
            )
         );
      }

      float f9 = f6 / 2.0F;
      if (Math.abs(f3 - f9) < 6.0F) {
         snapCandidateA = a(
            snapCandidateA,
            Math.abs(f3 - f9),
            f9 - f4 / 2.0F,
            new HudSnapGuide(HudSnapGuide.Orientation.HORIZONTAL, HudSnapGuide.Anchor.SCREEN_CENTER, f9, Math.max(0.0F, f7 - 10.0F), Math.min(f5, f8 + 10.0F))
         );
      }

      for (HudElement hudElement2 : list) {
         if (hudElement2 != hudElement) {
            float fM = hudElement2.m();
            float fM2 = hudElement2.m() + hudElement2.o();
            float fM3 = hudElement2.m() + hudElement2.o() / 2.0F;
            float fL = hudElement2.l();
            float fL2 = hudElement2.l() + hudElement2.n();
            if (Math.abs(f - fM) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f - fM), fM, b(fM, f7, f8, fL, fL2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f2 - fM2) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f2 - fM2), fM2 - f4, b(fM2, f7, f8, fL, fL2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f - fM2) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f - fM2), fM2, b(fM2, f7, f8, fL, fL2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f2 - fM) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f2 - fM), fM - f4, b(fM, f7, f8, fL, fL2, HudSnapGuide.Anchor.ELEMENT_EDGE));
            }

            if (Math.abs(f3 - fM3) < 6.0F) {
               snapCandidateA = a(snapCandidateA, Math.abs(f3 - fM3), fM3 - f4 / 2.0F, b(fM3, f7, f8, fL, fL2, HudSnapGuide.Anchor.ELEMENT_CENTER));
            }
         }
      }

      return snapCandidateA;
   }

   private static HudSnapGuide a(float f, float f2, float f3, float f4, float f5, HudSnapGuide.Anchor anchor) {
      float fMin = Math.min(f2, f4) - 10.0F;
      return new HudSnapGuide(HudSnapGuide.Orientation.VERTICAL, anchor, f, Math.max(0.0F, fMin), Math.max(f3, f5) + 10.0F);
   }

   private static HudSnapGuide b(float f, float f2, float f3, float f4, float f5, HudSnapGuide.Anchor anchor) {
      float fMin = Math.min(f2, f4) - 10.0F;
      return new HudSnapGuide(HudSnapGuide.Orientation.HORIZONTAL, anchor, f, Math.max(0.0F, fMin), Math.max(f3, f5) + 10.0F);
   }

   private static HudSnapCalculator.SnapCandidate a(HudSnapCalculator.SnapCandidate snapCandidate, float f, float f2, HudSnapGuide hudSnapGuide) {
      return snapCandidate != null && !(f < snapCandidate.a) ? snapCandidate : new HudSnapCalculator.SnapCandidate(f, f2, hudSnapGuide);
   }

   public static String a(String str, String str2, int i, int i2, int i3, int i4) {
      return null;
   }

   private static class SnapCandidate {
      final float a;
      final float b;
      final HudSnapGuide c;
      public static int d;
      public static boolean e;

      SnapCandidate(float f, float f2, HudSnapGuide hudSnapGuide) {
         this.a = f;
         this.b = f2;
         this.c = hudSnapGuide;
      }

      public static String a(String str, String str2, int i, int i2, int i3, int i4) {
         return null;
      }
   }
}
