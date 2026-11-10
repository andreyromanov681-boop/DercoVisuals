package pulse.modules.visuals;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import pulse.events.PacketEvent;
import pulse.module.ClientModule;
import pulse.module.ModuleCategory;
import pulse.module.ModuleInfo;
import pulse.settings.ModeSetting;

@ModuleInfo(a = "Atmosphere", b = "Настройка времени суток и погоды", c = ModuleCategory.VISUALS)
public class Atmosphere extends ClientModule {
   public final ModeSetting timeOfDay = new ModeSetting(
      "Время суток", new String[]{"Как на сервере", "День", "Закат", "Ночь", "Полночь", "Рассвет"}, "Как на сервере"
   );
   public final ModeSetting weather = new ModeSetting("Погода", new String[]{"Как на сервере", "Ясно", "Дождь", "Снег", "Гроза"}, "Как на сервере");

   public boolean isTimeCustom() {
      return this.k() && !this.timeOfDay.selectedValue().equals("Как на сервере");
   }

   public long getCustomTime() {
      if (!this.k()) {
         return -1L;
      }

      String mode = this.timeOfDay.selectedValue();
      switch (mode) {
         case "День":
            return 6000L;
         case "Закат":
            return 12500L;
         case "Ночь":
            return 14000L;
         case "Полночь":
            return 18000L;
         case "Рассвет":
            return 23000L;
         default:
            return -1L;
      }
   }

   public boolean isWeatherCustom() {
      return this.k() && !this.weather.selectedValue().equals("Как на сервере");
   }

   public String getWeatherMode() {
      return this.k() ? this.weather.selectedValue() : "Как на сервере";
   }

   @EventHandler
   public void onPacket(PacketEvent event) {
      if (this.k() && this.isTimeCustom()) {
         if (event.e() == PacketEvent.MessageDirection.RECIEVE && event.d() instanceof WorldTimeUpdateS2CPacket) {
            event.b();
         }
      }
   }
}
