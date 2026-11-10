package pulse.settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ModeSetting extends Setting<Integer> {
   private final String[] values;
   private final boolean multiSelect;
   private final Set<Integer> selectedIndices;

   public ModeSetting(String str, String str2, String[] strArr, int i) {
      super(str, str2, clampIndex(strArr, i));
      this.values = strArr;
      this.multiSelect = false;
      this.selectedIndices = new HashSet<>();
   }

   public ModeSetting(String str, String[] strArr, int i) {
      this(str, "", strArr, i);
   }

   public ModeSetting(String str, String[] strArr, String str2) {
      this(str, "", strArr, indexOf(strArr, str2));
   }

   public ModeSetting(String str, String str2, String[] strArr, int[] iArr) {
      super(str, str2, firstSelectedIndex(strArr, iArr));
      this.values = strArr;
      this.multiSelect = true;
      this.selectedIndices = new HashSet<>();

      for (int i : iArr) {
         if (isValidIndex(strArr, i)) {
            this.selectedIndices.add(i);
         }
      }
   }

   public ModeSetting(String str, String[] strArr, int[] iArr) {
      this(str, "", strArr, iArr);
   }

   public String[] values() {
      return this.values;
   }

   public List<String> valueList() {
      return Arrays.asList(this.values);
   }

   public boolean isMultiSelect() {
      return this.multiSelect;
   }

   public String selectedValue() {
      int iIntValue = this.k();
      return isValidIndex(this.values, iIntValue) ? this.values[iIntValue] : "";
   }

   public void select(String str) {
      this.select(indexOf(this.values, str));
   }

   public Set<Integer> selectedIndices() {
      HashSet hashSet = new HashSet<>(this.selectedIndices);
      if (!this.multiSelect) {
         hashSet.add(this.k());
      }

      return hashSet;
   }

   public boolean isSelected(int i) {
      return this.multiSelect ? this.selectedIndices.contains(i) : this.k() == i;
   }

   public void select(int i) {
      if (!this.multiSelect) {
         super.a(clampIndex(this.values, i));
      } else if (this.selectedIndices.contains(i)) {
         this.selectedIndices.remove(i);
      } else if (isValidIndex(this.values, i)) {
         this.selectedIndices.add(i);
      }
   }

   public void setSelectedIndices(Set<Integer> set) {
      this.selectedIndices.clear();

      for (int iIntValue : set) {
         if (isValidIndex(this.values, iIntValue)) {
            this.selectedIndices.add(iIntValue);
         }
      }
   }

   public boolean isSelected(String str) {
      return this.multiSelect ? this.selectedIndices.contains(indexOf(this.values, str)) : this.selectedValue().equals(str);
   }

   @Override
   public Setting<Integer> visibleWhen(Supplier<Boolean> supplier) {
      super.visibleWhen(supplier);
      return this;
   }

   private static int indexOf(String[] strArr, String str) {
      for (int i = 0; i < strArr.length; i++) {
         if (strArr[i].equals(str)) {
            return i;
         }
      }

      return 0;
   }

   private static int firstSelectedIndex(String[] strArr, int[] iArr) {
      return iArr.length > 0 ? clampIndex(strArr, iArr[0]) : 0;
   }

   private static int clampIndex(String[] strArr, int i) {
      return strArr.length == 0 ? 0 : Math.max(0, Math.min(i, strArr.length - 1));
   }

   private static boolean isValidIndex(String[] strArr, int i) {
      return i >= 0 && i < strArr.length;
   }

   public String[] a() {
      return this.values();
   }

   public List<String> b() {
      return this.valueList();
   }

   public boolean c() {
      return this.isMultiSelect();
   }

   public String d() {
      return this.selectedValue();
   }

   public void a(String str) {
      this.select(str);
   }

   public Set<Integer> e() {
      return this.selectedIndices();
   }

   public boolean a(int i) {
      return this.isSelected(i);
   }

   public void b(int i) {
      this.select(i);
   }

   public void a(Set<Integer> set) {
      this.setSelectedIndices(set);
   }

   public boolean b(String str) {
      return this.isSelected(str);
   }

   public boolean c(int i) {
      return this.isSelected(i);
   }

   public boolean c(String str) {
      return this.isSelected(str);
   }

   public ModeSetting a(Supplier<Boolean> supplier) {
      return (ModeSetting)this.visibleWhen(supplier);
   }

   public Setting<Integer> visibleWhen2(Supplier supplier) {
      return this.visibleWhen(supplier);
   }
}
