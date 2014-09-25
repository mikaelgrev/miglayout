package org.tbee.javafx.scene.layout;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

/**
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 14-09-24
 *         Time: 16:05
 */
public class LayoutAnimator
{
	private static final String ANIM_REPLACE_ID = "mig-anim";

	enum TransType {
		BOUNDS, OPACITY
	}

	public static final Duration ANIM_DURATION = new Duration(2000);

	private final MigPane pane;
	private final ArrayList<Node> addedNodes = new ArrayList<>();
	private final ArrayList<Node> removedNodes = new ArrayList<>();
	private final IdentityHashMap<Node, HashMap<TransType, Transition>> nodeAnimMap = new IdentityHashMap<>();
	private final IdentityHashMap<Node, Node> replacedNodeMap = new IdentityHashMap<>();

	public LayoutAnimator(MigPane pane)
	{
		this.pane = pane;
	}

	/** Animates the node.
	 * @param node The node to animate. Not null.
	 * @param toBounds If != null the animation will be to these bounds.
	 */
	void animate(Node node, Rectangle2D toBounds)
	{
		nodeAnimMap.compute(node, (n, oldTrans) -> createOrUpdateAnimation(n, oldTrans, toBounds));

		Node replNode = replacedNodeMap.get(node);
		if (replNode != null)
			nodeAnimMap.compute(replNode, (n, oldTrans) -> createOrUpdateAnimation(n, oldTrans, toBounds));
	}

	private HashMap<TransType, Transition> createOrUpdateAnimation(Node node, HashMap<TransType, Transition> transMap, Rectangle2D toBounds)
	{
		if (transMap == null)
			transMap = new HashMap<>();

		double toOpacity = extractOpacity(node);

		transMap.compute(TransType.OPACITY, (transType, oldTrans) -> {
			if (toOpacity != -1) {
				if (oldTrans != null)
					oldTrans.stop();
				FadeTransition fadeTrans = new FadeTransition(ANIM_DURATION, node);
				fadeTrans.setToValue(toOpacity);

				if (isReplacement(node)) {
					fadeTrans.setOnFinished(event -> {
						Node realNode = (Node) node.getUserData();
						if (realNode != null) {
							Rectangle2D rb = getBounds(node);
							realNode.resizeRelocate(rb.getMinX(), rb.getMinY(), rb.getWidth(), rb.getHeight());
							realNode.setVisible(true);
						}
						pane.remove(node);
					});
				}

				fadeTrans.play();

//				System.out.println("fade to " + toOpacity);
				return fadeTrans;
			}
			return oldTrans;
		});

		if (toBounds != null) {
			transMap.compute(TransType.BOUNDS, (transType, oldTrans) -> {
				Rectangle2D curBounds = getBounds(node);

				if (!curBounds.equals(toBounds) && (oldTrans == null || !(((LayoutTrans) oldTrans).toBounds.equals(toBounds)))) {
					if (oldTrans != null)
						oldTrans.stop();

					if (toOpacity == -1) {
						LayoutTrans trans = new LayoutTrans(node, ANIM_DURATION, toBounds);
						trans.play();
//  					System.out.println("layout to " + toBounds.toString());
						return trans;
					} else {

						node.resizeRelocate(toBounds.getMinX(), toBounds.getMinY(), toBounds.getWidth(), toBounds.getHeight());
					}
				}
				return oldTrans;
			});
		}

		return transMap;
	}

	private double extractOpacity(Node node)
	{
		if (addedNodes.remove(node)) {
			node.setOpacity(0);
			return 1;
		} else if (removedNodes.remove(node)) {
			return 0;
		}
		return -1;
	}

	void nodeAdded(Node node)
	{
		if (isReplacement(node))
			return;

		Node replNode = createReplacement(node);
		addedNodes.add(replNode);
		removedNodes.remove(node);

		node.setVisible(false);

		Platform.runLater(() -> {
			pane.add(0, replNode);
//			animate(replNode, null);
		});
	}

	void nodeRemoved(Node node)
	{
		if (isReplacement(node))
			return;

		Node replNode = createReplacement(node);
		removedNodes.add(replNode);
		addedNodes.remove(node);

		Platform.runLater(() -> {
			pane.add(0, replNode);
			animate(replNode, null);
		});
	}

	private static boolean isReplacement(Node node)
	{
		return ANIM_REPLACE_ID.equals(node.getId());
	}

	public Node createReplacement(Node node)
	{
		Rectangle2D b = getBounds(node);

		Node replNode = new ImageView(node.snapshot(new SnapshotParameters(), null));

		replacedNodeMap.put(node, replNode);
		replNode.setUserData(node);

		replNode.setManaged(false);
		replNode.setId(ANIM_REPLACE_ID);
		replNode.resizeRelocate(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());

		return replNode;
	}

	void start()
	{
//		if (status == PAUSED)
//			nodeAnimMap.values().forEach(map -> map.values().forEach(Animation::play));
//		status = RUNNING;
	}

	private static Rectangle2D getBounds(Node node)
	{
		Bounds lBounds = node.getLayoutBounds();

		return new Rectangle2D(
			node.getLayoutX(),
			node.getLayoutY(),
			lBounds.getWidth(),
			lBounds.getHeight()
		);
	}

	private class LayoutTrans extends Transition
	{
		private final Node node;
		private final double fromX, fromY, fromW, fromH;
		private final Rectangle2D toBounds;

		/**
		 * @param node The node to animate
		 * @param toBounds Target bounds. Never null.
		 */
		LayoutTrans(Node node, Duration duration, Rectangle2D toBounds)
		{
			this.node = node;
			this.fromX = node.getLayoutX();
			this.fromY = node.getLayoutY();

			Bounds bounds = node.getLayoutBounds();
			this.fromW = bounds.getWidth();
			this.fromH = bounds.getHeight();

			this.toBounds = toBounds;

			setCycleDuration(duration);

//			setInterpolator(Interpolator.SPLINE(0.8, 0.2, 0.2, 0.8));
			setInterpolator(Interpolator.SPLINE(0.0, 0.0, 0.2, 0.8));
			//				setInterpolator(Interpolator.EASE_OUT);
		}

		protected void interpolate(double frac)
		{
			double x = fromX + (toBounds.getMinX() - fromX) * frac;
			double y = fromY + (toBounds.getMinY() - fromY) * frac;
			double w = fromW + (toBounds.getWidth() - fromW) * frac;
			double h = fromH + (toBounds.getHeight() - fromH) * frac;

			pane.incLayoutInhibit();
			node.resizeRelocate(x, y, w, h);
			pane.decLayoutInhibit();
		}
	}
}
