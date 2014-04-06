package es.darkhogg.ld22;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public final class Sounds {

	/* package */static final Sound BIG_EXPLOSION;
	/* package */static final Sound[] FOOTSTEPS;
	/* package */static final Sound JUMP;
	/* package */static final Sound DISAPPEAR;
	
	static {
		Sound sndExplosion = null, sndFoot1 = null, sndFoot2 = null, sndFoot3 = null, sndJump = null, sndDisap = null;
		try {
			sndExplosion = new Sound( "bigexplosion.wav" );
			sndJump = new Sound( "jump.wav" );
			sndFoot1 = new Sound( "fstep1.wav" );
			sndFoot2 = new Sound( "fstep2.wav" );
			sndFoot3 = new Sound( "fstep3.wav" );
			sndDisap = new Sound( "disappear.wav" );
		} catch ( SlickException e ) {
			Main.fatal( e );
		}
		
		BIG_EXPLOSION = sndExplosion;
		JUMP = sndJump;
		FOOTSTEPS = new Sound[]{ sndFoot1, sndFoot2, sndFoot3 };
		DISAPPEAR = sndDisap;
	}
	
	private Sounds () {}
}
