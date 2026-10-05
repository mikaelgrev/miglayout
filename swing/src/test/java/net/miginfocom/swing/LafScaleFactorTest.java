package net.miginfocom.swing;

import net.miginfocom.layout.PlatformDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The "laf.scaleFactor" a look and feel publishes, such as FlatLaf, scales every size given in the default unit,
 * including the column and row sizes of the layout constraints. */
public class LafScaleFactorTest
{
	private int logicalPixelBase;

	@BeforeEach
	public void scaleByTheScaleFactor()
	{
		logicalPixelBase = PlatformDefaults.getLogicalPixelBase();
		PlatformDefaults.setLogicalPixelBase(PlatformDefaults.BASE_SCALE_FACTOR);
	}

	@AfterEach
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

	@Test
	public void aWrapperMadeOutsideMigLayoutReadsTheScaleFactorOnEveryCall()
	{
		SwingContainerWrapper wrapper = new SwingContainerWrapper(new JPanel());

		UIManager.put("laf.scaleFactor", 1f);
		assertEquals(1f, wrapper.getPixelUnitFactor(true), 0f);
		assertEquals(1f, wrapper.getPixelUnitFactor(false), 0f);

		UIManager.put("laf.scaleFactor", 2f);
		assertEquals(2f, wrapper.getPixelUnitFactor(true), 0f);
		assertEquals(2f, wrapper.getPixelUnitFactor(false), 0f);
	}
}
