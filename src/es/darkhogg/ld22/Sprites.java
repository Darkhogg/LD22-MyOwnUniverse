package es.darkhogg.ld22;

import org.lwjgl.Sys;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

public final class Sprites {

	/* package */static final Animation PLAYER_RIGHT;
	/* package */static final Animation PLAYER_LEFT;
	/* package */static final Image ROCK;
	/* package */static final Image SMOKE;
	/* package */static final Image BLUE;
	/* package */static final Image[] PEOPLE_STILL;
	/* package */static final Animation[] PEOPLE_WALKING;
	/* package */static final Animation GHOST;
	
	static {
		SpriteSheet sheet = null;
		try {
			sheet = new SpriteSheet( "sprites1.png", 16, 16 );
		} catch ( SlickException e ) {
			Log.error( e );
			Sys.alert( "Error", e.toString() );
		}

		PLAYER_RIGHT = new Animation( sheet, 0, 0, 3, 0, true, 48, false );
		PLAYER_LEFT = new Animation( sheet, 1, 3, 4, 3, true, 48, false );
		ROCK = sheet.getSprite( 3, 1 );
		SMOKE = sheet.getSprite( 4, 0 );
		BLUE = sheet.getSprite( 5, 0 );
		PEOPLE_STILL = new Image[]{
			sheet.getSprite( 6, 2 ),
			sheet.getSprite( 4, 4 ),
			sheet.getSprite( 4, 5 ),
			sheet.getSprite( 4, 6 )
		};
		PEOPLE_WALKING = new Animation[]{
			new Animation( sheet, 2, 2, 5, 2, true, 48, false ),
			new Animation( sheet, 0, 4, 3, 4, true, 48, false ),
			new Animation( sheet, 0, 5, 3, 5, true, 48, false ),
			new Animation( sheet, 0, 6, 3, 6, true, 48, false )
		};
		GHOST = new Animation( sheet, 0, 7, 1, 7, true, 100, false );
	}
	
	private Sprites () {}
	
	/* package */static void update ( int delta ) {
		PLAYER_RIGHT.update( delta );
		PLAYER_LEFT.update( delta );
		for ( Animation anim : PEOPLE_WALKING ) {
			anim.update( delta );
		}
		GHOST.update( delta );
	}
}
