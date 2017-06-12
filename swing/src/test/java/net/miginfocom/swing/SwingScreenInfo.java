package net.miginfocom.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Displays information about connected screens (size, DPI, scale factors, etc).
 *
 * @author Karl Tauber
 */
public class SwingScreenInfo
	extends JFrame
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
				} catch( Exception ex ) {
					ex.printStackTrace();
				}

				new SwingScreenInfo();
			}
		} );
	}

	private final JLabel screenDPIField = new JLabel();
	private final JLabel screenSizeField = new JLabel();
	private final JLabel migScaleFactorField = new JLabel();
	private final JPanel screensPanel = new JPanel( new MigLayout() );
	private final ArrayList<ScreenDeviceInfo> screenDeviceInfos = new ArrayList<ScreenDeviceInfo>();

	private SwingScreenInfo() {
		setTitle( "Swing Screen Info" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		Container contentPane = getContentPane();
		contentPane.setLayout( new MigLayout( "", "[][100]") );

		contentPane.add( new JLabel( "Java Version:" ) );
		contentPane.add( new JLabel( System.getProperty( "java.version" ) ), "wrap" );

		contentPane.add( new JLabel( "Toolkit Screen Size:" ) );
		contentPane.add( screenSizeField, "wrap" );

		contentPane.add( new JLabel( "Toolkit Screen Resolution:" ) );
		contentPane.add( screenDPIField, "wrap" );

		contentPane.add( new JLabel( "MigLayout Scale Factor:" ) );
		contentPane.add( migScaleFactorField, "wrap" );

		contentPane.add( screensPanel, "span 2, wrap" );

		update();

		Timer timer = new Timer( 1000, new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				update();
			}
		} );
		timer.start();

		pack();
		setVisible( true );
	}

	private void update() {
		Container contentPane = getContentPane();

		Dimension screenSize = contentPane.getToolkit().getScreenSize();
		int screenResolution = contentPane.getToolkit().getScreenResolution();

		screenSizeField.setText( screenSize.width + " x " + screenSize.height );
		screenDPIField.setText( screenResolution + " DPI" );

		SwingComponentWrapper wrapper = new SwingComponentWrapper( contentPane );
		float migScaleFactorX = wrapper.getPixelUnitFactor( true );
		float migScaleFactorY = wrapper.getPixelUnitFactor( false );
		migScaleFactorField.setText( (migScaleFactorX == migScaleFactorY )
			? String.valueOf( migScaleFactorX )
			: migScaleFactorX + " / " + migScaleFactorY );

		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		if( devices.length != screenDeviceInfos.size() ) {
			// monitor added or removed
			screensPanel.removeAll();
			screenDeviceInfos.clear();

			for( int i = 0; i < devices.length; i++ ) {
				ScreenDeviceInfo screenDeviceInfo = new ScreenDeviceInfo();
				screenDeviceInfos.add( screenDeviceInfo );

				screensPanel.add( new JLabel( "GraphicsDevice[" + i + "]:" ), "gapy para, wrap" );
				screensPanel.add( screenDeviceInfo, "wrap" );
			}
		}

		for( int i = 0; i < devices.length; i++ ) {
			ScreenDeviceInfo screenDeviceInfo = screenDeviceInfos.get( i );
			screenDeviceInfo.update( devices[i] );
		}

		pack();
	}

	//---- class ScreenDeviceInfo ---------------------------------------------

	private static class ScreenDeviceInfo
		extends JPanel
	{
		private final JLabel idField = new JLabel();
		private final JLabel displayModeField = new JLabel();
		private final JLabel boundsField = new JLabel();
		private final JLabel defaultScaleFactorField = new JLabel();
		private final JLabel normalizingScaleFactorField = new JLabel();

		ScreenDeviceInfo() {
			super( new MigLayout( "", "[][200]") );

			add( new JLabel( "ID:" ) );
			add( idField, "wrap" );

			add( new JLabel( "Screen Size:" ) );
			add( displayModeField, "wrap" );

			add( new JLabel( "Screen Bounds:" ) );
			add( boundsField, "wrap" );

			add( new JLabel( "Swing Default Scale Factor:" ) );
			add( defaultScaleFactorField, "split 2" );
			add( new JLabel( "(used for screen)" ), "wrap" );

			add( new JLabel( "Swing Normalizing Scale Factor:" ) );
			add( normalizingScaleFactorField, "split 2" );
			add( new JLabel( "(72 units in user space to 1 inch in device space)" ), "wrap" );
		}

		void update( GraphicsDevice gd ) {
			GraphicsConfiguration gc = gd.getDefaultConfiguration();

			idField.setText( gd.getIDstring() );

			DisplayMode displayMode = gd.getDisplayMode();
			displayModeField.setText( displayMode.getWidth() + " x " + displayMode.getHeight() + " / "
				+ displayMode.getBitDepth() + " Bit / "
				+ displayMode.getRefreshRate() + " Hz" );

			Rectangle bounds = gc.getBounds();
			boundsField.setText( bounds.width + " x " + bounds.height + " / x " + bounds.x + " / y " + bounds.y);

			updateScaleFactorField( defaultScaleFactorField, gc.getDefaultTransform() );
			updateScaleFactorField( normalizingScaleFactorField, gc.getNormalizingTransform() );
		}

		private void updateScaleFactorField( JLabel scaleFactorField, AffineTransform defaultTransform ) {
			double scaleX = defaultTransform.getScaleX();
			double scaleY = defaultTransform.getScaleY();
			scaleFactorField.setText( (scaleX == scaleY ) ? String.valueOf( scaleX ) : scaleX + " / " + scaleY );
		}
	}
}
