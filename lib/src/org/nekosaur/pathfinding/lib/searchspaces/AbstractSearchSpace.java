package org.nekosaur.pathfinding.lib.searchspaces;

import java.util.EnumSet;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.nekosaur.pathfinding.lib.common.Option;
import org.nekosaur.pathfinding.lib.interfaces.SearchSpace;

@SuppressWarnings("restriction")
public abstract class AbstractSearchSpace implements SearchSpace {
	
	protected final int width;
	protected final int height;
	protected EnumSet<Option> options;
	
	public AbstractSearchSpace(int width, int height, EnumSet<Option> options) {
		super();
		this.width = width;
		this.height = height;
		this.options = options;
	}
	
	public boolean allows(Option option) {
		return options.contains(option);
	}

	public void allow(EnumSet<Option> options) { this.options = options; }

	protected Image resample(Image input, int newSize) {
		final int W = (int) input.getWidth();
		final int H = (int) input.getHeight();
		final int S = (int) newSize / W;

		System.out.println("Resampling " + W + " to " + S*H);

		if (S <= 1)
			return input;

		WritableImage output = new WritableImage(
				W * S,
				H * S
		);

		PixelReader reader = input.getPixelReader();
		PixelWriter writer = output.getPixelWriter();

		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				final int argb = reader.getArgb(x, y);
				for (int dy = 0; dy < S; dy++) {
					for (int dx = 0; dx < S; dx++) {
						writer.setArgb(x * S + dx, y * S + dy, argb);
					}
				}
			}
		}

		return output;
	}
	
}
