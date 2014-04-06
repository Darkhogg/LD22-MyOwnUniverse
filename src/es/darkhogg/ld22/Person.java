package es.darkhogg.ld22;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public final class Person extends Entity {
	
	private int sprite = (int) ( Math.random() * Sprites.PEOPLE_STILL.length );
	
	boolean gone = false;
	
	@Override
	public Image getImage () {
		if ( xSpd > 0 ) {
			Animation anim = Sprites.PEOPLE_WALKING[ sprite ];
			return anim.getImage( anim.getFrame() );
		} else {
			return Sprites.PEOPLE_STILL[ sprite ];
		}
	}
	
	@Override
	public boolean isRemovable () {
		return gone;
	}
}
