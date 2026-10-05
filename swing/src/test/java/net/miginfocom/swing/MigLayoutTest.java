package net.miginfocom.swing;

import net.miginfocom.layout.PlatformDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * MigLayoutTest
 *
 * @author anavarro
 * @author Jeanette Winzenburg, Berlin
 */
public class MigLayoutTest {

    // reported: http://migcalendar.com/forums/viewtopic.php?f=8&t=3833
    /**
     * Auto-DPI-scaling not working.
     */
    @Test
    public void testDPIScaling() {
        int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
        assumeFalse(screenResolution == PlatformDefaults.getDefaultDPI(),
                "dpi == default, nothing to test: " + screenResolution);
        // TODO: the assert fails under windows
        assumeFalse(System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows"),
                "dpi scaling assert fails under windows");

        float factor = (float) screenResolution / PlatformDefaults.getDefaultDPI();
        SwingComponentWrapper wrapper = new SwingComponentWrapper(new JButton());
        assertEquals(factor, wrapper.getPixelUnitFactor(true), "dpi scaling factor");
    }

    // reported: http://migcalendar.com/forums/viewtopic.php?f=8&t=3834
    /**
     * PlatformDefaults must accept BASE_REAL_PIXEL.
     */
    @Test
    public void testPlatFormDefaultsNoScale() {
        PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_REAL_PIXEL);
    }

    /**
     * Set PlatformDefaults properties to defaults.
     */
    @BeforeEach
    public void setPlatformDefaults() {
        PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_SCALE_FACTOR);
        PlatformDefaults.setHorizontalScaleFactor(null);
        PlatformDefaults.setVerticalScaleFactor(null);
    }
}
