package es.darkhogg.ld22;

import org.newdawn.slick.Image;

public abstract class Entity {
	
	/* package */float xPos;
	/* package */float yPos;
	
	/* package */float xSpd;
	/* package */float ySpd;
	
	/* package */float xAcc;
	/* package */float yAcc;
	
	public abstract Image getImage ();
	
	public boolean isRemovable () {
		return false;
	}
}
