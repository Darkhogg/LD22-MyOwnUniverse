package es.darkhogg.ld22;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SpriteSheetFont;
import org.newdawn.slick.tiled.TiledMap;

public final class LD22Game extends BasicGame {
	
	private static final String TITLE = "Darkhogg @ Ludum Dare 22";
	
	/* package */static final int WIDTH = 16 * 24;
	/* package */static final int HEIGHT = 16 * 18;
	/* package */static final int SCALE = 2;
	
	private static final float PLAYER_STEP = 1.6f;
	private static final int FOOTSTEPTIME = 150;
	
	private static final String RESDIR = "fuck";
	
	private static Font messageFont;
	
	private Input input;
	private int lastFStep = 0;
	
	public LD22Game () {
		super( TITLE );
	}
	
	private TiledMap level = null;
	private Color backgroundColor;
	private final Player player = new Player();
	
	private final Set<String> flags = new HashSet<String>();
	
	private int explosionEvent = -1;
	
	private final Collection<Entity> regularEntities = new LinkedList<Entity>();
	
	private final ParticleSystem particles = new ParticleSystem();
	private int partSmoke, partBlue;
	
	private int gameEnd = 0;
	
	@Override
	public boolean closeRequested () {
		return true;
	}
	
	@Override
	public void init ( GameContainer cont ) throws SlickException {
		try {
			// Reference Sounds to initialize them
			Sounds.JUMP.getClass(); // Don't do this at home
			
			partSmoke = particles.addParticleType( Sprites.SMOKE );
			partBlue = particles.addParticleType( Sprites.BLUE );
			
			input = cont.getInput();
			
			level = new TiledMap( "home_1.tmx", RESDIR );
			backgroundColor = new Color( Integer.parseInt( level.getMapProperty( "sky_color", "FFFFFF" ), 16 ) );
			
			player.xPos = Integer.parseInt( level.getMapProperty( "player_x", "0" ) ) * 16;
			player.yPos = Integer.parseInt( level.getMapProperty( "player_y", "0" ) ) * 16;
			
			for ( int _ = 0; _ < 32; _++ ) {
				Entity ent = new Person();
				ent.yPos = player.yPos;
				ent.xPos = (float) ( 128 + Math.random() * 64 );
				regularEntities.add( ent );
			}
		} catch ( Exception ex ) {
			Main.fatal( ex );
		}
	}
	
	@Override
	public void update ( GameContainer cont, int delta ) throws SlickException {
		try {
			if ( lastFStep <= FOOTSTEPTIME ) {
				lastFStep += delta;
			}
			Sprites.update( delta );
			
			// Input check
			boolean keyLeft = input.isKeyDown( Input.KEY_LEFT );
			boolean keyRight = input.isKeyDown( Input.KEY_RIGHT );
			boolean keyUp = input.isKeyDown( Input.KEY_UP );
			boolean keyDown = input.isKeyDown( Input.KEY_DOWN );
			
			boolean canMove = true;
			if ( gameEnd > 0 ) {
				gameEnd++;
				canMove = false;
			}
			
			int solidLayer = level.getLayerIndex( "Solid" );
			
			final int lvlw = level.getWidth();
			final int lvlh = level.getHeight();
			
			// EVENT
			if ( explosionEvent >= 0 ) {
				explosionEvent++;
				// canMove = false;
			}
			
			if ( explosionEvent > 0 ) {
				LinkedList<Entity> newEntities = new LinkedList<Entity>();
				for ( Entity ent : regularEntities ) {
					if ( ent instanceof Person && Math.random() < 0.008f ) {
						Sounds.DISAPPEAR.play();
						Person pers = (Person) ent;
						pers.gone = true;
						
						for ( int _ = 0; _ < 8; _++ ) {
							particles.createParticle( pers.xPos - 3f + (float) Math.random() * 6f, pers.yPos - 8f
								+ (float) Math.random() * 16f, 0f, -0.1f - (float) Math.random(), 0f, 0f, 1.0f, -0.02f,
								60, partBlue );
						}
						
						if ( Math.random() < 0.1 ) {
							Ghost ghost = new Ghost();
							ghost.xPos = ent.xPos;
							ghost.yPos = ent.yPos;
							ghost.xSpd = ent.xSpd / 3f;
							ghost.ySpd = ent.ySpd;
							newEntities.add( ghost );
						}
					}
				}
				
				regularEntities.addAll( newEntities );
			}
			
			boolean onGround;
			{
				int px0 = (int) ( ( player.xPos + 6 ) / 16 );
				int px1 = (int) ( ( player.xPos + 9 ) / 16 );
				int py2 = (int) ( ( player.yPos + 16 + player.ySpd ) / 16 );
				
				int id1 = level.getTileId( px0, py2, solidLayer );
				int id2 = level.getTileId( px1, py2, solidLayer );
				
				onGround = ( id1 != 0 ) | ( id2 != 0 );
			}
			
			// Player movement
			player.walking = false;
			if ( canMove & ( keyRight | keyLeft ) & !( keyRight & keyLeft ) ) {
				if ( lastFStep > FOOTSTEPTIME & onGround ) {
					lastFStep %= FOOTSTEPTIME;
					Sounds.FOOTSTEPS[ (int) ( Math.random() * Sounds.FOOTSTEPS.length ) ].play();
				}
				
				player.walking = true;
				float mv = keyRight ? PLAYER_STEP : -PLAYER_STEP;
				player.right = keyRight;
				
				int px0 = (int) ( ( player.xPos + 6 + mv ) / 16 );
				int px1 = (int) ( ( player.xPos + 9 + mv ) / 16 );
				int py0 = (int) ( ( player.yPos + 6 ) / 16 );
				int py1 = (int) ( ( player.yPos + 9 ) / 16 );
				
				boolean move = true;
				if ( px0 >= 0 && px1 < lvlw && py0 >= 0 && py1 < lvlh ) {
					int id1 = level.getTileId( ( keyRight ? px1 : px0 ), py0, solidLayer );
					int id2 = level.getTileId( ( keyRight ? px1 : px0 ), py1, solidLayer );
					
					if ( id1 != 0 || id2 != 0 ) {
						move = false;
					}
				}
				
				if ( move ) {
					player.xPos += mv;
				}
			}
			
			// Ground / Fall
			if ( onGround & player.ySpd >= 0.01f ) {
				player.yPos = Math.round( ( player.yPos + player.ySpd ) / 16f ) * 16f;
				player.ySpd = 0f;
			} else {
				int px0 = (int) ( ( player.xPos + 6 ) / 16 );
				int px1 = (int) ( ( player.xPos + 9 ) / 16 );
				int pyU = (int) ( ( player.yPos + player.ySpd ) / 16 );
				
				int idU0 = level.getTileId( px0, pyU, solidLayer );
				int idU1 = level.getTileId( px1, pyU, solidLayer );
				
				if ( ( idU0 != 0 ) | ( idU1 != 0 ) ) {
					player.yPos = Math.round( ( player.yPos + player.ySpd ) / 16f ) * 16f;
					player.ySpd = 0f;
				}
				player.ySpd += player.yAcc;
			}
			player.yPos += player.ySpd;
			
			// Jump
			if ( canMove & keyUp & onGround & Math.abs( player.ySpd ) < 0.01f ) {
				Sounds.JUMP.play();
				player.ySpd = -4f;
			}
			
			// Player warping
			TiledMap newLevel = null;
			float lvlPosX = 0;
			float lvlPosY = 0;
			
			int px = (int) player.xPos + 8;
			int py = (int) player.yPos + 8;
			
			// Objects
			int numObjGrs = level.getObjectGroupCount();
			for ( int grp = 0; grp < numObjGrs; grp++ ) {
				int numObjs = level.getObjectCount( grp );
				
				for ( int obj = 0; obj < numObjs; obj++ ) {
					int ox = level.getObjectX( grp, obj );
					int oy = level.getObjectY( grp, obj );
					int ow = level.getObjectWidth( grp, obj );
					int oh = level.getObjectHeight( grp, obj );
					
					if ( "entity".equals( level.getObjectType( grp, obj ) ) ) {
						String flag = "::" + level.getObjectName( grp, obj );
						
						if ( !flags.contains( flag ) ) {
							flags.add( flag );
							
							String className = level.getObjectProperty( grp, obj, "entity", "" );
							
							Entity ent = (Entity) Class.forName( className ).newInstance();
							ent.xPos =  level.getObjectX( grp, obj );
							ent.yPos =  level.getObjectY( grp, obj );
							
							regularEntities.add( ent );
						}
					}
					
					if ( px >= ox && px <= ox + ow && py >= oy && py <= oy + oh ) {
						
						if ( "flag".equals( level.getObjectType( grp, obj ) ) ) {
							String[] set = level.getObjectProperty( grp, obj, "set", "" ).split( "," );
							String[] clear = level.getObjectProperty( grp, obj, "clear", "" ).split( "," );
							
							for ( String flag : set ) {
								flags.add( flag );
							}
							for ( String flag : clear ) {
								flags.remove( flag );
							}
							
						} else if ( "warp".equals( level.getObjectType( grp, obj ) ) ) {
							String lvlName = level.getObjectProperty( grp, obj, "warp", "" );
							newLevel = new TiledMap( lvlName + ".tmx", RESDIR );
							lvlPosX = Integer.parseInt( newLevel.getMapProperty( "player_x", "0" ) ) * 16;
							lvlPosY = Integer.parseInt( newLevel.getMapProperty( "player_y", "0" ) ) * 16;
							regularEntities.clear();
							player.ySpd = 0f;

						} else if ( gameEnd == 0 && "final".equals( level.getObjectType( grp, obj ) ) ) {
							gameEnd++;

							player.ySpd = 0;
							player.yAcc = 0;
							
							Ghost ghost = new Ghost();
							ghost.xPos = player.xPos;
							ghost.yPos = player.yPos;
							
							for ( int _ = 0; _ < 8; _++ ) {
								particles.createParticle( ghost.xPos - 3f + (float) Math.random() * 6f, ghost.yPos - 8f
									+ (float) Math.random() * 16f, 0f, -0.1f - (float) Math.random(), 0f, 0f, 1.0f, -0.02f,
									60, partBlue );
							}
							
							Sounds.DISAPPEAR.play();
							
						} else if ( "event".equals( level.getObjectType( grp, obj ) ) ) {
							String sflag = level.getObjectProperty( grp, obj, "ifset", "" );
							String cflag = level.getObjectProperty( grp, obj, "ifclr", "" );
							
							if ( ( "".equals( sflag ) || flags.contains( sflag ) )
								&& ( "".equals( cflag ) || !flags.contains( cflag ) ) )
							{
								flags.add( "_" + level.getObjectName( grp, obj ) );
								for ( Entity ent : regularEntities ) {
									if ( ent instanceof Person ) {
										ent.xSpd = 1.5f + (float) Math.random() * 1.5f;
									}
								}
								
								// HARDCODED Explosion event
								explosionEvent = 0;
								String lvlName = level.getObjectProperty( grp, obj, "warp", "" );
								newLevel = new TiledMap( lvlName + ".tmx", RESDIR );
								lvlPosX = player.xPos;
								lvlPosY = player.yPos;
								
								Sounds.BIG_EXPLOSION.play();
								
								for ( int _ = 0; _ < 64; _++ ) {
									FlyingRock rock = new FlyingRock();
									
									rock.xPos = (float) ( Math.random() * 192 - 256 );
									rock.yPos = (float) ( Math.random() * 32 + lvlh * 16 - 64 );
									
									double pow = 1.0 + Math.random() * 11.0;
									double ang = ( Math.PI * 0.1 ) + ( Math.random() * Math.PI * 0.2 );
									
									rock.xSpd = (float) ( pow * Math.cos( ang ) );
									rock.ySpd = (float) ( -pow * Math.sin( ang ) );
									
									rock.yAcc = 0.04f;
									
									regularEntities.add( rock );
								}
							}
						}
					}
				}
			}
			
			// Entity tick
			for ( Iterator<Entity> it = regularEntities.iterator(); it.hasNext(); ) {
				Entity ent = it.next();
				
				ent.xPos += ent.xSpd;
				ent.yPos += ent.ySpd;
				ent.xSpd += ent.xAcc;
				ent.ySpd += ent.yAcc;
				
				if ( ent.isRemovable() ) {
					it.remove();
				}
			}
			
			particles.update();
			
			// LAST
			// Level change
			if ( newLevel != null ) {
				level = newLevel;
				
				player.xPos = lvlPosX;
				player.yPos = lvlPosY;
				
				backgroundColor = new Color( Integer.parseInt( level.getMapProperty( "sky_color", "FFFFFF" ), 16 ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			Main.fatal( e );
		}
	}
	
	@Override
	public void render ( GameContainer cont, Graphics gr ) throws SlickException {
		// Initialize the font
		if ( messageFont == null ) {
			try {
				SpriteSheetFont font = new SpriteSheetFont( new SpriteSheet( "Acorntileset8x8.png", 8, 8 ), '\0' );
				messageFont = font;
			} catch ( SlickException e ) {
				Main.fatal( e );
			}
		}
		
		// Draw a white background
		gr.setColor( backgroundColor );
		gr.fillRect( 0f, 0f, WIDTH, HEIGHT );
		
		final int W2 = WIDTH / 2;
		final int H2 = HEIGHT / 2;
		
		// Level info
		final int lvll = level.getLayerCount();
		final int lvlw = level.getWidth();
		final int lvlh = level.getHeight();
		
		// Camera tracking!
		int px = (int) player.xPos + 8;
		int py = (int) player.yPos + 8;
		
		int cx = ( px - W2 <= 0 ? W2 : ( px + W2 >= lvlw * 16 ? lvlw * 16 - W2 : px ) );
		int cy = ( py - H2 < 0 ? H2 : ( py + H2 >= lvlh * 16 ? lvlh * 16 - H2 : py ) );
		
		// Draw the map!
		int cinix = Math.max( 0, ( cx - W2 ) / 16 );
		int ciniy = Math.max( 0, ( cy - H2 ) / 16 );
		int cendx = Math.min( lvlw - 1, ( cx + W2 ) / 16 );
		int cendy = Math.min( lvlh - 1, ( cy + H2 ) / 16 );
		
		for ( int l = 0; l < lvll; l++ ) {
			for ( int j = ciniy; j <= cendy; j++ ) {
				for ( int i = cinix; i <= cendx; i++ ) {
					Image image = level.getTileImage( i, j, l );
					
					if ( image != null ) {
						gr.drawImage( image, i * 16f - cx + W2, j * 16f - cy + H2 );
					}
				}
			}
		}
		
		// Objects
		int numObjGrs = level.getObjectGroupCount();
		for ( int grp = 0; grp < numObjGrs; grp++ ) {
			int numObjs = level.getObjectCount( grp );
			for ( int obj = 0; obj < numObjs; obj++ ) {
				if ( "message".equals( level.getObjectType( grp, obj ) ) ) {
					int ox = level.getObjectX( grp, obj );
					int oy = level.getObjectY( grp, obj );
					int ow = level.getObjectWidth( grp, obj );
					int oh = level.getObjectHeight( grp, obj );
					
					if ( px >= ox && px <= ox + ow && py >= oy && py <= oy + oh ) {
						String[] strs = level.getObjectProperty( grp, obj, "string", "null" ).split( "\\$" );
						int mx = Integer.parseInt( level.getObjectProperty( grp, obj, "x", "0" ) );
						int my = Integer.parseInt( level.getObjectProperty( grp, obj, "y", "0" ) );
						Color color =
							new Color( Integer.parseInt( level.getObjectProperty( grp, obj, "color", "000000" ), 16 ) );
						
						gr.setColor( color );
						gr.setFont( messageFont );
						
						float sx = mx + ox - cx + W2;
						float sy = my + oy - cy + H2;
						
						for ( String str : strs ) {
							gr.drawString( str, sx, sy );
							sy += 8;
						}
					}
				}
			}
		}
		
		particles.render( gr, cx, cy );
		
		// Regular Entities
		for ( Entity ent : regularEntities ) {
			if ( ent instanceof Ghost ) {
				gr.setDrawMode( Graphics.MODE_ADD );
			}
			gr.drawImage( ent.getImage(), (int) ent.xPos - cx + W2, (int) ent.yPos - cy + H2 );
			gr.setDrawMode( Graphics.MODE_NORMAL );
		}
		
		// Draw the player
		if ( gameEnd == 0 ) {
			gr.drawImage( player.getImage(), (int) player.xPos - cx + W2, (int) player.yPos - cy + H2 );
		} else {
			if ( gameEnd > 120 ) {
				gr.setDrawMode( Graphics.MODE_COLOR_MULTIPLY );
				
				float a = 1.0f - ( ( gameEnd-120 ) / 180f );
				gr.setColor( new Color( (int)(a*255), (int)(a*255), (int)(a*255), 255 ) );
				
				gr.fillRect( 0f, 0f, WIDTH, HEIGHT );
				
				if ( gameEnd > 240 ) {
					gr.setDrawMode( Graphics.MODE_ADD );
					
					int b = gameEnd > 240+120 ? 255 : (int) ( ( ( gameEnd-240 ) / 120f ) * 255 );
					
					gr.setColor( new Color( b, b, b, 255 ) );
					gr.setFont( messageFont );
					
					gr.drawString( "Looks like nobody can help us now", W2-(33*4), 128 );
					gr.drawString( "Humanity is condemned to a lonely life", W2-(38*4), 136 );
				}
				
				gr.setDrawMode( Graphics.MODE_NORMAL );
			}
		}
		
		// EVENT
		if ( explosionEvent >= 0 && explosionEvent <= 120 ) {
			gr.setDrawMode( Graphics.MODE_ADD );
			
			float a = 0.9f - ( explosionEvent / 120f );
			gr.setColor( new Color( (int)(a*200), (int)(a*230), (int)(a*255), 255 ) );
			
			gr.fillRect( 0f, 0f, WIDTH, HEIGHT );
			gr.setDrawMode( Graphics.MODE_NORMAL );
		}
	}
}
