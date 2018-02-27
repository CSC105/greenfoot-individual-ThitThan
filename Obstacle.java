import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Obstacle here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Obstacle extends Actor
{
    // SCALING
    double scale = 0.15;
    public static double width;
    public static double height;
    //640 x 256
    protected GreenfootImage image;
    protected String imgFileName;
    
    // CONSTRUCTOR
    public Obstacle(Actor mainChar, String imgFileName, double imgScale) {
        this.setMainChar(mainChar);
        this.imgFileName = imgFileName;
        this.scale = imgScale;
    }
    
    @Override
    public void addedToWorld(World world) {
        super.addedToWorld(world);
        if (image == null) {
            image = new GreenfootImage(imgFileName);
            width = image.getWidth() * scale;
            height = image.getHeight() * scale;
            image.scale((int) width, (int) height);
            //System.out.println("GRASS height : " + height + ", getHeight() : " + image.getHeight());
        }
        //System.out.println("GRASS height : " + height + ", getHeight() : " + image.getHeight());
        setImage(image);
    }
    
    //
    // CALLBACK
    //
    interface Callback {
        void onHitMainChar(Actor a);
    }
    Callback mCallback;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
    
    //
    // MAIN CHARACTER
    //
    Actor mainChar;
    public void setMainChar(Actor a) {
        this.mainChar = a;
    }
    public Actor getMainChar() {
        return this.mainChar;
    }
    
    //
    // PREVENT MULTIPLE HIT
    //
    boolean hit = false;
    public boolean isHit() {
        return this.hit;
    }
    
    //
    // MAIN EVENTS
    //
    @Override
    public void act() 
    {
        // Check if hit
        if (mainChar != null 
            //&& this.getOneObjectAtOffset(0, 0, mainChar.getClass()) != null
            //&& this.intersects(mainChar)
            && this.getX() > 244 && this.getX() < 361 && mainChar.getY() >= Flamingo.onGroundY - (image.getHeight() * 0.9)
            && this.hit == false
            && this.mCallback != null) {
                
            this.mCallback.onHitMainChar(mainChar);
            this.hit = true;
        }
    } 
}
