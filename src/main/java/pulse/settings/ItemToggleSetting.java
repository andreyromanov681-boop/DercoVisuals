package pulse.settings;

import java.awt.Color;
import net.minecraft.item.Item;

public class ItemToggleSetting extends Setting<Boolean> {
   private final Item item;
   private Color color;

   public ItemToggleSetting(String str, String str2, Item ItemVar, boolean z, Color color) {
      super(str, str2, z);
      this.item = ItemVar;
      this.color = color;
   }

   public ItemToggleSetting(String str, Item ItemVar, boolean z, Color color) {
      this(str, "", ItemVar, z, color);
   }

   public ItemToggleSetting(String str, Item ItemVar, Color color) {
      this(str, "", ItemVar, false, color);
   }

   public boolean get() {
      return this.k();
   }

   public void set(boolean z) {
      super.a(z);
   }

   public void toggle() {
      this.set(!this.get());
   }

   public Item item() {
      return this.item;
   }

   public Color color() {
      return this.color;
   }

   public int rgb() {
      return this.color.getRGB();
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public boolean a() {
      return this.get();
   }

   public void a(boolean z) {
      this.set(z);
   }

   public void b() {
      this.toggle();
   }

   public Item c() {
      return this.item();
   }

   public Color d() {
      return this.color();
   }

   public int e() {
      return this.rgb();
   }

   public void a(Color color) {
      this.setColor(color);
   }
}
