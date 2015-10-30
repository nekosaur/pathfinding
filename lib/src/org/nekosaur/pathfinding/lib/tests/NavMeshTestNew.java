package org.nekosaur.pathfinding.lib.tests;

import org.nekosaur.pathfinding.lib.common.MapData;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;
import org.nekosaur.pathfinding.lib.searchspaces.navmesh.NavMesh;

public class NavMeshTestNew {
	
	public static void main(String[] args) {
		
		int data[][] = {
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,1,1,1,1,0,0,0,0,0},
				{0,1,1,1,1,0,0,0,0,0},
				{0,0,1,1,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},
		};
		
		NavMesh navMesh = new NavMesh(10, 10);
		
		SearchSpace ss = NavMesh.create(new MapData(data, null));
	}

}
