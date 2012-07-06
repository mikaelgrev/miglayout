package net.miginfocom.swing;

import java.awt.Toolkit;
import java.util.logging.Logger;

import javax.swing.JButton;

import junit.framework.TestCase;
import net.miginfocom.layout.PlatformDefaults;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * MigLayoutTest
 *
 * @author anavarro
 * @author Jeanette Winzenburg, Berlin
 */
@RunWith(JUnit4.class)
public class MigLayoutTest extends TestCase{

    // reported: http://migcalendar.com/forums/viewtopic.php?f=8&t=3833
    /**
     * Auto-DPI-scaling not working.
     */
    @Test
    public void testDPIScaling() {
       if (Toolkit.getDefaultToolkit().getScreenResolution() == PlatformDefaults.getDefaultDPI()) {
           LOG.info("dpi == default, nothing to test: " + 
                   Toolkit.getDefaultToolkit().getScreenResolution());
           return;
       }
       float factor = (float) Toolkit.getDefaultToolkit().getScreenResolution() / PlatformDefaults.getDefaultDPI();
       
       SwingComponentWrapper wrapper = new SwingComponentWrapper(new JButton());
       assertEquals("dpi scaling factor", factor, wrapper.getPixelUnitFactor(true));
       
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
     * 
     */
    private void setPlatformDefaults() {
//        PlatformDefaults.setPlatform(PlatformDefaults.WINDOWS_XP);
        PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_SCALE_FACTOR);
        PlatformDefaults.setHorizontalScaleFactor(null);
        PlatformDefaults.setVerticalScaleFactor(null);
    }
    
    
    @Override
    @Before
    public void setUp() throws Exception {
        setPlatformDefaults();
    }

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(MigLayoutTest.class
            .getName());
}
