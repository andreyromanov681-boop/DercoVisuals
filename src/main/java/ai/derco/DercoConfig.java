package ai.derco;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DercoConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("dercovisuals.properties");

    private DercoConfig() {}

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(FILE)) {
            properties.load(input);
            String restoreGamma = properties.getProperty("fullbright.restoreGamma");
            if (restoreGamma != null) {
                try {
                    Modules.loadSavedGamma(Double.parseDouble(restoreGamma));
                } catch (NumberFormatException exception) {
                    DercoVisualsClient.LOGGER.warn("Invalid saved Fullbright gamma value", exception);
                }
            }
            for (Module module : Modules.ALL) {
                String value = properties.getProperty("module." + module.id);
                if (value != null) module.loadEnabled(Boolean.parseBoolean(value));
            }
        } catch (IOException exception) {
            DercoVisualsClient.LOGGER.warn("Could not read DercoVisuals config", exception);
        }
    }

    public static void save() {
        Properties properties = new Properties();
        for (Module module : Modules.ALL) {
            properties.setProperty("module." + module.id, Boolean.toString(module.isEnabled()));
        }
        properties.setProperty("fullbright.restoreGamma", Double.toString(Modules.getSavedGamma()));
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream output = Files.newOutputStream(FILE)) {
                properties.store(output, "DercoVisuals module settings");
            }
        } catch (IOException exception) {
            DercoVisualsClient.LOGGER.warn("Could not save DercoVisuals config", exception);
        }
    }
}
