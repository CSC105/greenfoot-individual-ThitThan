import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class WeepingAngel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class WeepingAngel extends Obstacle
{
    public WeepingAngel(Actor mainChar) {
        super(mainChar, "WeepingAngel-small-2.png", 0.52);
    }
    
    @Override
    public void addedToWorld(World world) {
        super.addedToWorld(world);
        //image.setTransparency((int) (255 * 0.75));
    }
    
    @Override
    public void act() 
    {
        // Check if hit
        if (mainChar != null 
            //&& this.getOneObjectAtOffset(0, 0, mainChar.getClass()) != null
            //&& this.intersects(mainChar)
            && this.getX() > 244 && this.getX() < 361 && mainChar.getY() >= Flamingo.onGroundY - (image.getHeight() * 0.4)
            && this.hit == false
            && this.mCallback != null) {
                
            this.mCallback.onHitMainChar(mainChar);
            this.hit = true;
        }
    } 
}
