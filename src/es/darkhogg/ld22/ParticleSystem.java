package es.darkhogg.ld22;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public final class ParticleSystem {
	
	private static final int NUM_FIELDS = 10;
	private static final int MAX_PARTS = 256;
	
	private static final int FIELD_X = 0; // Position
	private static final int FIELD_Y = 1;
	private static final int FIELD_XS = 2; // Speed
	private static final int FIELD_YS = 3;
	private static final int FIELD_XA = 4; // Accelleration
	private static final int FIELD_YA = 5;
	private static final int FIELD_L = 6; // Life
	private static final int FIELD_A = 7; // Alpha
	private static final int FIELD_AS = 8; // Alpha Speed
	private static final int FIELD_T = 9; // Type
	
	private final List<Image> types = new ArrayList<Image>();
	private final int[] particles = new int[ NUM_FIELDS * MAX_PARTS ];
	
	private int firstUsed = 0;
	private int numUsed = 0;
	
	public void update () {
		for ( int i = 0; i < numUsed; i++ ) {
			int p = ( (firstUsed+i) % MAX_PARTS ) * NUM_FIELDS;
			if ( particles[ p + FIELD_L ] > 0 ) {
				if ( particles[ p + FIELD_A ] <= 0 ) {
					// Die if alpha <= 0
					particles[ p + FIELD_L ] = 0;
					
				} else {
					// Update position
					particles[ p + FIELD_X ] += particles[ p + FIELD_XS ];
					particles[ p + FIELD_Y ] += particles[ p + FIELD_YS ];
					
					// Update speed
					particles[ p + FIELD_XS ] += particles[ p + FIELD_XA ];
					particles[ p + FIELD_YS ] += particles[ p + FIELD_YA ];
					
					// Update alpha and life
					particles[ p + FIELD_A ] += particles[ p + FIELD_AS ];
					particles[ p + FIELD_L ]--;
				}
			}
		}
		
		// Remove particles with life == 0 from the beginning
		while ( numUsed > 0 && particles[ ( firstUsed * NUM_FIELDS ) + FIELD_L ] == 0 ) {
			firstUsed = ( firstUsed + 1 ) % MAX_PARTS;
			numUsed--;
		}
		
		// Remove particles with life == 0 from the end
		while ( numUsed > 0 && particles[ ( ( ( firstUsed + numUsed - 1 ) % MAX_PARTS ) * NUM_FIELDS ) + FIELD_L ] == 0 )
		{
			numUsed--;
		}
	}
	
	public void render ( Graphics gr, int cx, int cy ) {
		int offx = -cx + LD22Game.WIDTH / 2;
		int offy = -cy + LD22Game.HEIGHT / 2;
		
		for ( int i = 0; i < numUsed; i++ ) {
			int p = ( (firstUsed+i) % MAX_PARTS ) * NUM_FIELDS;
			if ( particles[ p + FIELD_L ] > 0 && particles[ p + FIELD_A ] > 0 ) {
				Image img = types.get( particles[ p + FIELD_T ] );
				int x = particles[ p + FIELD_X ] >> 8;
				int y = particles[ p + FIELD_Y ] >> 8;
				
				// HARDCODED PARTICLE MODE! don't do this at home
				Color col;
				int a = particles[ p + FIELD_A ] * 255 / 1024;
				
				if ( img == Sprites.BLUE ) {
					gr.setDrawMode(  Graphics.MODE_ADD );
					col = new Color( a, a, a, 255 );
				} else {
					gr.setDrawMode(  Graphics.MODE_NORMAL );
					col = new Color( 255, 255, 255, a );
				}
				gr.drawImage( img, x + offx, y + offy, col );
			}
		}
		
		gr.setDrawMode( Graphics.MODE_NORMAL );
	}
	
	public int addParticleType ( Image img ) {
		int newType = types.size();
		types.add( img );
		return newType;
	}
	
	public void createParticle (
		float x, float y, float xSpd, float ySpd, float xAcc, float yAcc, float a, float aSpd, int life, int type )
	{	
		if ( numUsed == MAX_PARTS ) {
			firstUsed = ( firstUsed + 1 ) % MAX_PARTS;
		} else {
			numUsed++;
		}
		
		int p = ( ( firstUsed + numUsed - 1 ) % MAX_PARTS ) * NUM_FIELDS;
		
		// Update position
		particles[ p + FIELD_X ] = (int)( x*256 );
		particles[ p + FIELD_Y ] = (int)( y*256 );
		particles[ p + FIELD_XS ] = (int)( xSpd*256 );
		particles[ p + FIELD_YS ] = (int)( ySpd*256 );
		particles[ p + FIELD_XA ] = (int)( xAcc*256 );
		particles[ p + FIELD_YA ] = (int)( yAcc*256 );
		particles[ p + FIELD_A ] = (int)( a*1024 );
		particles[ p + FIELD_AS ] = (int)( aSpd*1024 );
		particles[ p + FIELD_L ] = life;
		particles[ p + FIELD_T ] = type;
	}
}
