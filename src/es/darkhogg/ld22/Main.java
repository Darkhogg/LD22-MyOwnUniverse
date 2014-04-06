package es.darkhogg.ld22;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.lwjgl.Sys;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.imageout.ImageOut;
import org.newdawn.slick.util.Log;

import es.darkhogg.util.OperatingSystem;

/**
 * A class that serves as an entry point for the game
 */
public final class Main {
	
	public static final Charset CHARSET_LATIN = Charset.forName( "ISO-8859-1" );
	
	/**
	 * Modifies the <tt>java.library.path</tt> system property so it contains the appropriate folder with the LWJGL
	 * natives
	 */
	private static void setupLibraryPath () throws SecurityException, NoSuchFieldException, IllegalAccessException {
		OperatingSystem os = OperatingSystem.getCurrent();
		
		// If the OS is supported by LWJGL (Linux, Mac, Windows, Solaris)
		if ( os == OperatingSystem.LINUX | os == OperatingSystem.MAC | os == OperatingSystem.SOLARIS
			| os == OperatingSystem.WINDOWS )
		{
			String dirname = "natives-" + os.getName().toLowerCase();
			
			String pathsep = System.getProperty( "path.separator" );
			String dirsep = System.getProperty( "file.separator" );
			String oldlibpath = System.getProperty( "java.library.path" );
			
			// <oldpath>:lib/natives-os
			String newlibpath = oldlibpath + pathsep + "lib" + dirsep + dirname;
			System.setProperty( "java.library.path", newlibpath );
			
			// Propagate the change
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			
			if ( fieldSysPath != null ) {
				fieldSysPath.set( System.class.getClassLoader(), null );
			}
		}
	}
	
	/**
	 * Launches the game using an <tt>AppGameContainer</tt>
	 * 
	 * @param args
	 *            Command line arguments, currently ignored
	 */
	public static void main ( String[] args ) {
		try {
			setupLibraryPath();
			
			AppGameContainer container = new AppGameContainer( new WrapperGame() );
			container.setDisplayMode( LD22Game.WIDTH*LD22Game.SCALE, LD22Game.HEIGHT*LD22Game.SCALE, false );
			container.start();
			
		} catch ( Throwable ex ) {
			fatal( ex );
		}
	}
	
	/**
	 * Prints the exception in the console and an alert window and exits
	 * 
	 * @param ex
	 *            The exception that caused the fatal error
	 */
	/* package */static void fatal ( Throwable ex ) {
		Log.error( ex );
		Sys.alert( "Fatal Error", "fatal error ocurred during game execution: " + ex.toString() );
		System.exit( 1 );
	}
	
	/**
	 * A class that wraps the main game class so both the application and applet have a common entry point
	 */
	public static final class WrapperGame implements Game {
		
		private final Game game = new LD22Game();
		private Image buffer = null;
		private Input input = null;
		
		//long renderNanos = 0;
		//long updateNanos = 0;
		
		//long totalGameMillis;
		//long totalGameFrames;
		
		@Override
		public boolean closeRequested () {
			return game.closeRequested();
		}
		
		@Override
		public String getTitle () {
			return game.getTitle();
		}
		
		@Override
		public void init ( GameContainer cont ) throws SlickException {
			cont.setForceExit( true );
			cont.setShowFPS( false );
			cont.setTargetFrameRate( 60 );
			
			game.init( cont );
		}
		
		@Override
		public void update ( GameContainer cont, int delta ) throws SlickException {
			//long st = System.nanoTime();
			
			if ( input == null ) {
				input = cont.getInput();
			}
			
			if ( input.isKeyPressed( Input.KEY_F12 ) ) {
				long millis = System.currentTimeMillis();
				String ssname = "/tmp/" + millis + ".png";
				
				Log.info( "Saving screenshot in '" + ssname + "'" );
				ImageOut.write( buffer, ImageOut.PNG, ssname );
			}

			game.update( cont, delta );
			
			//long nd = System.nanoTime();
			//updateNanos = nd - st;
		}
		
		@Override
		public void render ( GameContainer cont, Graphics gr ) throws SlickException {
			//long st = System.nanoTime();
			
			if ( buffer == null ) {
				buffer = new ImageBuffer( LD22Game.WIDTH, LD22Game.HEIGHT ).getImage( Image.FILTER_NEAREST );
			}
			Graphics bufgr = buffer.getGraphics();
			
			game.render( cont, bufgr );
			
			gr.drawImage( buffer,
				0f, 0f, LD22Game.WIDTH*LD22Game.SCALE, LD22Game.HEIGHT*LD22Game.SCALE,
				0f, 0f, LD22Game.WIDTH, LD22Game.HEIGHT );
			
			/*long nd = System.nanoTime();
			renderNanos = nd - st;

			totalGameMillis += (updateNanos+renderNanos)/1000000L;
			totalGameFrames ++;
			
			gr.drawString( String.valueOf( updateNanos/1000L ) + "us", 64, 0 );
			gr.drawString( String.valueOf( renderNanos/1000L ) + "us", 128, 0 );
			gr.drawString( String.valueOf( (updateNanos+renderNanos)/1000000L ) + "ms", 192, 0 );
			gr.drawString( String.valueOf( totalGameMillis/totalGameFrames ) + "ms", 256, 0 );
			
			double fps = 1000000000.0 / (updateNanos+renderNanos);
			gr.drawString( String.valueOf( (int) fps ), 320, 0 );*/
		}
		
	}
	
}
