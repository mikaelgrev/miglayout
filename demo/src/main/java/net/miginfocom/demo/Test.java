package net.miginfocom.demo;

import net.miginfocom.swt.MigLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class Test extends Dialog
{
	Test(Shell parent) {
		super(parent);
	}

	public void open() {
		Shell parent = getParent();
		Shell dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setSize(300, 200);
		dialog.setText("Java Source and Support");

		// Gridlayout works
		//dialog.setLayout(new GridLayout(2, false));

		// The following Miglayout doesn't work
		//    dialog.setLayout(new MigLayout("wrap 2"));

//		The following Miglayout works !!! Obviously rtl and ltr are mixed up
		dialog.setLayout(new MigLayout("wrap 2"));

		createControls(dialog);

		dialog.open();
		Display display = parent.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void createControls(Shell dialog) {
		Label label = new Label(dialog, SWT.NONE);
		label.setText("Test:");
		Text text = new Text(dialog, SWT.BORDER);
	}

	public static void main(String[] argv) {
		new Test(new Shell(SWT.RIGHT_TO_LEFT)).open();
	}
}
