package org.tbee.javafx.scene.layout.test;

import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AssertNode {

    public AssertNode(Node node) {
        this.node = node;
        this.description = "node=" + this.node;
    }
    final Node node;
    final String description;


    public AssertNode assertXYWH(double x, double y, double w, double h, double accuracy) {
        try {
            Assert.assertEquals(description + ", X", x, node.getLayoutX(), accuracy);
            Assert.assertEquals(description + ", Y", y, node.getLayoutY(), accuracy);
            Assert.assertEquals(description + ", W", w, width(node), accuracy);
            Assert.assertEquals(description + ", H", h, height(node), accuracy);
        }
        catch (java.lang.AssertionError e) {
            AssertNode.generateSource("migPane", node, Collections.emptyList(), false, AssertNode.A.XYWH);
            throw e;
        }
        return this;
    }

    public AssertNode assertRotate(double x, double y, double angle, double accuracy) {
        Rotate r = null;
        for (Transform t : node.getTransforms()) {
            if (t instanceof Rotate) {
                r = (Rotate)t;
                break;
            }
        }
        Assert.assertEquals(description + ", PivotX", x, r.getPivotX(), accuracy);
        Assert.assertEquals(description + ", PivotY", y, r.getPivotY(), accuracy);
        Assert.assertEquals(description + ", Angle", angle, r.getAngle(), accuracy);
        return this;
    }

    public AssertNode assertScale(double x, double y, double scaleX, double scaleY, double accuracy) {
        Scale s = null;
        for (Transform t : node.getTransforms()) {
            if (t instanceof Scale) {
                s = (Scale)t;
                break;
            }
        }
        Assert.assertEquals(description + ", PivotX", x, s.getPivotX(), accuracy);
        Assert.assertEquals(description + ", PivotY", y, s.getPivotY(), accuracy);
        Assert.assertEquals(description + ", X", scaleX, s.getX(), accuracy);
        Assert.assertEquals(description + ", Y", scaleY, s.getY(), accuracy);
        return this;
    }

    public AssertNode assertArcCenterRadiusAngleLength(double x, double y, double radiusX, double radiusY, double startAngle, double length, double accuracy) {
        Arc arc = (Arc)node;
        Assert.assertEquals(description + ", CenterX", x, arc.getCenterX(), accuracy);
        Assert.assertEquals(description + ", CenterY", y, arc.getCenterY(), accuracy);
        Assert.assertEquals(description + ", RadiusX", radiusX, arc.getRadiusX(), accuracy);
        Assert.assertEquals(description + ", RadiusY", radiusY, arc.getRadiusY(), accuracy);
        Assert.assertEquals(description + ", StartAngle", startAngle, arc.getStartAngle(), accuracy);
        Assert.assertEquals(description + ", Length", length, arc.getLength(), accuracy);
        return this;
    }

    public AssertNode assertClass(Class clazz) {
        Assert.assertEquals(description, clazz, node.getClass());
        return this;
    }

    public AssertNode assertClassName(String className) {
        Assert.assertEquals(description, className, node.getClass().getName());
        return this;
    }

    public AssertNode assertTextText(String text) {
        Assert.assertEquals(description, text, ((javafx.scene.text.Text)node).getText());
        return this;
    }

    static private double width(Node n) {
        return n.getLayoutBounds().getWidth() + n.getLayoutBounds().getMinX();
    }

    static private double height(Node n) {
        return n.getLayoutBounds().getHeight() + n.getLayoutBounds().getMinY();
    }

    static public enum A {XYWH, ROTATE, SCALE, ARC, CLASS, CLASSNAME, TEXTTEXT}
    static public void generateSource(String paneVariableName, List<Node> nodes, List<String> excludedNodeClasses, boolean newline, AssertNode.A... asserts) {

        // init
        int idx = 0;
        String lNewline = (newline ? "\n    " : "");
        // if no asserts are specified, use the default
        List<AssertNode.A> lAsserts = Arrays.asList( (asserts != null && asserts.length > 0 ? asserts : new AssertNode.A[]{AssertNode.A.XYWH, AssertNode.A.CLASS}) );

        // for all nodes
        for (Node lNode : nodes) {

            // skip node because of class?
            if (excludedNodeClasses != null && excludedNodeClasses.contains(lNode.getClass().getName())) {
                continue;
            }

            // output an assertline
            System.out.print("new AssertNode(" + paneVariableName + ".getChildren().get(" + idx + "))");
            generateAsserts(lNode, lNewline, lAsserts);
            System.out.println(";");

            idx++;
        }
    }

    static public void generateSource(String variableName, Node node, List<String> excludedNodeClasses, boolean newline, AssertNode.A... asserts) {

        // init
        String lNewline = (newline ? "\n    " : "");
        // if no asserts are specified, use the default
        List<AssertNode.A> lAsserts = Arrays.asList( (asserts != null && asserts.length > 0 ? asserts : new AssertNode.A[]{AssertNode.A.XYWH, AssertNode.A.CLASS}) );

        // output an assertline
        System.out.print("new AssertNode(" + variableName + ")");
        generateAsserts(node, lNewline, lAsserts);
        System.out.println(";");
    }

    private static void generateAsserts(Node node, String newline, List<AssertNode.A> asserts) {
        for (AssertNode.A a : asserts) {
            if (a == AssertNode.A.XYWH) {
                System.out.print(newline + ".assertXYWH(" + node.getLayoutX() + ", " + node.getLayoutY() + ", " + width(node) + ", " + height(node) + ", 0.01)");
            }
            if (a == AssertNode.A.ROTATE) {
                Rotate r = null;
                for (Transform t : node.getTransforms()) {
                    if (t instanceof Rotate) {
                        r = (Rotate)t;
                        break;
                    }
                }
                System.out.print(newline + ".assertRotate(" + r.getPivotX() + ", " + r.getPivotY() + ", " + r.getAngle() + ", 0.01)");
            }
            if (a == AssertNode.A.SCALE) {
                Scale s = null;
                for (Transform t : node.getTransforms()) {
                    if (t instanceof Scale) {
                        s = (Scale)t;
                        break;
                    }
                }
                System.out.print(newline + ".assertScale(" + s.getPivotX() + ", " + s.getPivotY() + ", " + s.getX() + ", " + s.getY() + ", 0.01)");
            }
            if (a == AssertNode.A.ARC) {
                Arc arc = (Arc)node;
                System.out.print(newline + ".assertArcCenterRadiusAngleLength(" + arc.getCenterX() + ", " + arc.getCenterY() + ", " + arc.getRadiusX() + ", " + arc.getRadiusY() + ", " + arc.getStartAngle() + ", " + arc.getLength() + ", 0.01)");
            }
            if (a == AssertNode.A.CLASS) {
                System.out.print(newline + ".assertClass(" + node.getClass().getName() + ".class)");
            }
            if (a == AssertNode.A.CLASSNAME) {
                System.out.print(newline + ".assertClassName(\"" + node.getClass().getName() + "\")");
            }
            if (a == AssertNode.A.TEXTTEXT) {
                System.out.print(newline + ".assertTextText(\"" + ((javafx.scene.text.Text)node).getText() + "\")");
            }
        }
    }
}
