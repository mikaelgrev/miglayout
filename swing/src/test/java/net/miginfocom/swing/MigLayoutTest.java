package net.miginfocom.swing;

import net.miginfocom.layout.PlatformDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
     * <p>
     * Up to Java 8 MigLayout scaled logical pixels by screen DPI / default DPI itself. Since Java 9 Swing scales the
     * whole UI with the system scale factor, so MigLayout must NOT scale again (that would scale twice):
     * the pixel unit factor is 1 whatever the screen DPI is (see SwingComponentWrapper.getPixelUnitFactor).
     * This test used to expect the Java 8 behaviour; it was never noticed because it only ran on Linux/macOS,
     * where CI skipped the tests, and only when the DPI was not the default.
     */
    @Test
    public void testDPIScaling() {
        SwingComponentWrapper wrapper = new SwingComponentWrapper(new JButton());
        int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
        assertEquals(1f, wrapper.getPixelUnitFactor(true), "horizontal pixel unit factor at " + screenResolution + " dpi");
        assertEquals(1f, wrapper.getPixelUnitFactor(false), "vertical pixel unit factor at " + screenResolution + " dpi");
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
