package es.darkhogg.ld22;

import org.newdawn.slick.Image;

public final class Player extends Entity {
	
	/* package */boolean right = false;
	/* package */boolean walking = false;
	
	public Player () {
		yAcc = 0.15f; // Gravity
	}
	
	@Override
	public Image getImage () {
		if ( right ) {
			return Sprites.PLAYER_RIGHT.getImage( walking ? Sprites.PLAYER_RIGHT.getFrame() : 0 );
		} else {
			return Sprites.PLAYER_LEFT.getImage( walking ? Sprites.PLAYER_LEFT.getFrame() : 0 );
		}
	}
	
}
