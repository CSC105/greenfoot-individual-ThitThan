import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Dalek here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Dalek extends Actor
{
    
    // SCALING
    public static double scale = 1;
    //public static double scale = 0.7;
    public static double width, height;
    
    // IMAGE
    static GreenfootImage image;
    
    // SFX
    public static GreenfootSound[] sfx;
    public static GreenfootSound exterminate;
    
    @Override
    public void addedToWorld(World world) {
        if (image == null) {
            image = new GreenfootImage("dalek-small.png");
            width = image.getWidth() * scale;
            height = image.getHeight() * scale;
            image.scale((int) width, (int) height);
        }
        //System.out.println("DALEK height : " + height + ", getHeight() : " + image.getHeight());
        setImage(image);
        
        if (sfx == null) {
            initSound();
        }
    }
    @Override
    public void act() 
    {
        // Add your action code here.
    }
    
    // SFX
    public void initSound() {
        sfx = new GreenfootSound[3];
        
        sfx[0] = new GreenfootSound("dalek-stay.mp3");
        sfx[0].setVolume(50);
        
        sfx[1] = new GreenfootSound("dalek-emergency.mp3");
        sfx[1].setVolume(75);
        
        sfx[2] = new GreenfootSound("dalek-groan.mp3");
        sfx[2].setVolume(75);
        
        //sfx[2] = new GreenfootSound("dalek-exterminate.mp3");
        //sfx[2].setVolume(75);
        
        //sfx[1] = new GreenfootSound("dalek-destroy.mp3");
        //sfx[1].setVolume(80);
        
        exterminate = new GreenfootSound("dalek-exterminate-2.wav");
        exterminate.setVolume(95);
    }
    
    public void playExterminateSound() {
        stopSound();
        exterminate.play();
    }
    public void playSound() {
        //stopSound();
        int rand = Greenfoot.getRandomNumber(sfx.length);
        sfx[rand].play();
    }
    public void stopSound() {
        for (GreenfootSound sound : sfx) {
            sound.stop();
        }
        //exterminate.stop();
    }
}
