package org.tbee.javafx.scene.layout.fxml;

import javafx.beans.DefaultProperty;
import javafx.scene.Node;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ConstraintParser;

/**
 * This class provides some API enhancements to implement FXML (and this keep the original's API clean)
 * @author User
 *
 */
@DefaultProperty(value = "children") // for FXML integration
public class MigPane extends org.tbee.javafx.scene.layout.MigPane
{
    // The FXML simply is matching tag- and attributes names to classes and properties (getter/setter) in the imported Java files
    // Many thanks to Michael Paus for the grunt work!
	
	/** layout called in FXML on MigPane itself */
	public void setLayout(String value) 
	{
		this.fxmLayoutConstraints = value;
		setLayoutConstraints( ConstraintParser.parseLayoutConstraint( ConstraintParser.prepare( value ) ) );		
	}
	public String getLayout() { return fxmLayoutConstraints; }
	private String fxmLayoutConstraints;

	/** cols called in FXML on MigPane itself */
	public void setCols(String value) 
	{
		this.fxmlColumConstraints = value;
		setColumnConstraints( ConstraintParser.parseColumnConstraints( ConstraintParser.prepare( value ) ) );
	}
	public String getCols() { return fxmlColumConstraints; }
	private String fxmlColumConstraints;
	
	/** rows called in FXML on MigPane itself */
	public void setRows(String value) 
	{
		this.fxmlRowConstraints = value;
		setRowConstraints( ConstraintParser.parseRowConstraints( ConstraintParser.prepare( value ) ) );
	}
	public String getRows() { return fxmlRowConstraints; }
	private String fxmlRowConstraints;

    /** called from the subnodes in FXML via MigPane.cc="..." */
	public static void setCc(Node node, CC cc) 
	{
		// temporarily put it in a map
		cNodeToCC.put(node, cc);
	}
	public static void setCc(Node node, String cc) 
	{
		CC lCC = ConstraintParser.parseComponentConstraint( ConstraintParser.prepare( cc ) );
		setCc(node, lCC);
	}

}
