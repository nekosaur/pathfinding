package org.nekosaur.pathfinding.lib.interfaces;

import java.util.EnumSet;
import java.util.List;

import javafx.scene.image.Image;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.node.Node;

public interface SearchSpace extends Copy<SearchSpace> {
	List<Node> getNeighbours(Node n);
	double getMovementCost(Node n1, Node n2);
	int getWidth();
	int getHeight();
	Node getNode(double x, double y);
	boolean isWalkableAt(double x, double y);
	void allow(EnumSet<Option> options);
	boolean allows(Option option);
	Image draw(int side);
}
