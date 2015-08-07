package org.nekosaur.pathfinding.lib.searchspaces;

import java.util.EnumSet;

import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;

public abstract class AbstractSearchSpace implements SearchSpace {
	
	protected final int width;
	protected final int height;
	protected final EnumSet<Option> options;
	
	public AbstractSearchSpace(int width, int height, EnumSet<Option> options) {
		super();
		this.width = width;
		this.height = height;
		this.options = options;
	}
	
	public boolean allows(Option option) {
		return options.contains(option);
	}
	
	
}
