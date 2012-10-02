package net.miginfocom.demo;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SwtTest extends Composite
{
	private Combo combo;
	private Control control;

	public SwtTest(Composite parent, int style, Layout layout)
	{
		super(parent, style);
		setLayout(layout);
		createControls();
	}

	private void createControls()
	{
		Label label = new Label(this, SWT.None);
		label.setText("Select Control: ");

		combo = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData("wrap");
		combo.add("Text Box");
		combo.add("Combo");
		combo.add("Radio Button");

		combo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				if (control != null)
					control.dispose();

				switch (combo.getSelectionIndex()) {
					case 0:
						control = new Text(SwtTest.this, SWT.BORDER);
						break;
					case 1:
						control = new Combo(SwtTest.this, SWT.DROP_DOWN);
						break;
					case 2:
						control = new Button(SwtTest.this, SWT.RADIO);
						break;
				}

				if (SwtTest.this.getLayout() instanceof GridLayout) {
					GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
					data.horizontalSpan = 2;
					control.setLayoutData(data);
				} else if (SwtTest.this.getLayout() instanceof MigLayout) {
					control.setLayoutData("spanx 2, grow");
				}

				SwtTest.this.layout(true);
			}
		});
	}

	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("SWT");
		shell.setLayout(new FillLayout());

		/**
		 * Swap out GridLayout for MigLayout to profile and see resources cleaned up.
		 */
		// GridLayout layout = new GridLayout(2, false);
		MigLayout layout = new MigLayout();
		new SwtTest(shell, SWT.NONE, layout);

		shell.open();

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in event queue
				display.sleep();
			}
		}

		display.dispose();
	}
}