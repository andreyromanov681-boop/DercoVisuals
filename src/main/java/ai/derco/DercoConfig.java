package ai.derco;

import ai.derco.theme.DercoTheme;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Player settings on disk, written lazily.
 *
 * <p>A module toggle used to hit the disk synchronously from the render thread.
 * Now a change only raises a dirty flag; {@link #tick()} flushes at most once a
 * second and {@link #flush()} does it on shutdown, so a session's toggles cost one
 * or two writes instead of one per click. The write goes to a temp file that then
 * replaces the real one, so a crash mid-write cannot leave a truncated config.</p>
 */
public final class DercoConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("dercovisuals.properties");
    private static final int FLUSH_DELAY_TICKS = 20;

    private static boolean dirty;
    private static int countdown = FLUSH_DELAY_TICKS;

    private DercoConfig() {}

    public static void load() {
        if (!Files.isRegularFile(FILE)) {
            DercoTheme.reset();
            return;
        }
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(FILE)) {
            properties.load(input);
        } catch (IOException exception) {
            DercoVisualsClient.LOGGER.warn("Could not read DercoVisuals config", exception);
            return;
        }
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
        DercoTheme.read(properties);
    }

    /** Marks the file stale; the actual write is coalesced by {@link #tick()}. */
    public static void markDirty() {
        dirty = true;
    }

    public static void tick() {
        if (!dirty) return;
        if (--countdown > 0) return;
        flush();
    }

    /** Writes now if anything changed. Safe to call from shutdown. */
    public static void flush() {
        countdown = FLUSH_DELAY_TICKS;
        if (!dirty) return;
        dirty = false;
        Properties properties = new Properties();
        for (Module module : Modules.ALL) {
            properties.setProperty("module." + module.id, Boolean.toString(module.isEnabled()));
        }
        properties.setProperty("fullbright.restoreGamma", Double.toString(Modules.getSavedGamma()));
        DercoTheme.write(properties);
        try {
            Files.createDirectories(FILE.getParent());
            Path temp = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            try (OutputStream output = Files.newOutputStream(temp)) {
                properties.store(output, "DercoVisuals settings");
            }
            Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            dirty = true;
            DercoVisualsClient.LOGGER.warn("Could not save DercoVisuals config", exception);
        }
    }
}
