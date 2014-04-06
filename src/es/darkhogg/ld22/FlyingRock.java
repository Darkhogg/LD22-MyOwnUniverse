package es.darkhogg.ld22;

import org.newdawn.slick.Image;


public final class FlyingRock extends Entity {

	@Override
	public Image getImage () {
		return Sprites.ROCK;
	}

	@Override
	public boolean isRemovable () {
		return yPos > 1024;
	}
	
}
