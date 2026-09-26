package net.miginfocom.swing;

import net.miginfocom.layout.PlatformDefaults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import static org.junit.Assert.assertEquals;

/** The "laf.scaleFactor" a look and feel publishes, such as FlatLaf, scales every size given in the default unit,
 * including the column and row sizes of the layout constraints. */
public class LafScaleFactorTest
{
	private int logicalPixelBase;

	@Before
	public void scaleByTheScaleFactor()
	{
		logicalPixelBase = PlatformDefaults.getLogicalPixelBase();
		PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_SCALE_FACTOR);
	}

	@After
	public void removeScaleFactor()
	{
		UIManager.put("laf.scaleFactor", null);
		PlatformDefaults.setLogicalPixelBase(logicalPixelBase);
	}

	@Test
	public void aChangedScaleFactorIsUsedByTheNextSizeQueryOfTheSameLayout()
	{
		JPanel panel = new JPanel(new MigLayout("insets 0", "[100!]", "[20!]"));
		panel.add(new JLabel("scaled"));

		UIManager.put("laf.scaleFactor", 1f);
		assertEquals(100, panel.getPreferredSize().width);

		UIManager.put("laf.scaleFactor", 2f);
		panel.invalidate();
		assertEquals(200, panel.getPreferredSize().width);

		UIManager.put("laf.scaleFactor", 1.5f);
		panel.invalidate();
		assertEquals(150, panel.getMinimumSize().width);
	}
}
