package es.darkhogg.ld22;

import org.newdawn.slick.Image;


public final class Ghost extends Entity {
	
	@Override
	public Image getImage () {
		return Sprites.GHOST.getImage( Sprites.GHOST.getFrame() );
	}
	
}
