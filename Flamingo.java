import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Flamingo here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Flamingo extends Actor
{
    // INVINCIBLE MODE
    boolean invincible = false;
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    
    // CAN JUMP
    boolean jumpingEnabled = true;
    public void setJumpingEnabled(boolean enabled) {
        this.jumpingEnabled = enabled;
    }
    public boolean isJumpingEnabled() {
        return this.jumpingEnabled;
    }
    
    public static double onGroundY = 0;
    
    // SCALING
    public static double scale = 1;
    //public static double scale = 0.15;
    public static double width = 449 * scale;
    public static double height = 729 * scale;
    
    // STATIC IMAGES
    static GreenfootImage[] images;
    
    // SPEED
    int walkingSpeed = 2;
    public int getWalkingSpeed() {
        return this.walkingSpeed;
    }
    public void setWalkingSpeed(int speed) {
        this.walkingSpeed = speed;
    }
    
    // CALLBACK
    public interface Callback {
        void onJumpStarted(Flamingo fla);
        void onJumpFinished(Flamingo fla);
        //void onCrashed(Flamingo fla, Actor act);
        boolean isFloating(Flamingo fla);
    }
    Callback mCallback;
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }
    
    @Override
    public void addedToWorld(World world) {
        images = new GreenfootImage[36];
        for (int i = 0; i < 36; i++) {
            images[i] = new GreenfootImage("Flamingo" + (i+1) + ".png");
            //images[i].scale((int) width, (int) height);
        }
        setImage( images[0] );
    }
    
    boolean jumpPressed = false;
    boolean isJumping = false;
    boolean wasOnGround = false;
    
    public int jumpCount = 0;
    
    public void act() {
        if (invincible)
            animateWalk(GameWorld.INVINCIBLE_MODE_MULTIPLIER);
        else
            animateWalk(1);
        
        // CONTROLS
        /*if (Greenfoot.isKeyDown("right")) {
            animateWalk(1);
        }*/
        /*if (Greenfoot.isKeyDown("left")) {
            animateWalk(-2);
        }*/
        if (Greenfoot.isKeyDown("space") || Greenfoot.isKeyDown("up")) {
            pressJumpKey();
        }
        else {
            releaseJumpKey();
        }
        
        // reset jump counting
        if (mCallback != null && !mCallback.isFloating(this) && !isJumping && !wasOnGround) {
            jumpCount = 0;
            //System.out.println("[ONGROUND]");
            
            wasOnGround = true;
        }
        
        // JUMPING
        if (isJumping) {
            applyJumping();
        }
    }   
    
    void pressJumpKey() {
        //System.out.println("PRESSING");
        if (jumpingEnabled && !jumpPressed && v < START_SPEED * 2/3) {
            jump();

            //System.out.println("JumpCount : " + jumpCount);
            jumpPressed = true;
        }
    }
    void releaseJumpKey() {
        //System.out.println("RELEASED");
        jumpPressed = false;
    }
    
    // JUMPING
    //static final int JUMP_HEIGHT = 120;
    static final int MAX_JUMP = 2;
    static final double G = GameWorld.G;
    static final double G_MODIFIER = GameWorld.G_MODIFIER;
    static final double START_SPEED = 11.5;
    double v = 0, t = 0;
    public void jump() {
        //System.out.println("Jumped!!");
        if (jumpCount >= MAX_JUMP)
            return;
        
        jumpCount++;
        
        if (mCallback != null)
            mCallback.onJumpStarted(this);
          
        v = START_SPEED;
        t = 0;
        isJumping = true;
        //this.setLocation(getX(), getY() - JUMP_HEIGHT);
    }
    public void applyJumping() {
        v = v - (G * G_MODIFIER * t);
        t += 0.1;
        
        if (v < START_SPEED * 3/5)
            animateWalk(-1 * (invincible ? GameWorld.INVINCIBLE_MODE_MULTIPLIER:1));
        
        //frame = 6;
        
        this.setLocation(getX(), (int) (getY() - v));
        //System.out.println("v : " + v + "\t t : " + t);
        
        // SHOULD STOP?
        if (v <= 0) {
            v = 0;
            t = 0;
            isJumping = false;
            wasOnGround = false;
            
            if (mCallback != null)
                mCallback.onJumpFinished(this);
        }
    }
    
    // WALKING ANIMATION
    int frame = 0;
    public void animateWalk(int frames) {
        this.frame += (frames*walkingSpeed);
        
        if (this.frame >= 36)
            this.frame -= 36;
        else if (frame < 0)
            this.frame += 36;
            
        // INVINCIBILITY
        if (invincible)
            images[this.frame].setTransparency((int) (255 * 0.6));
        else
            images[this.frame].setTransparency((int) (255 * 1.0));
        
        //if (frame % 3 == 0)
            setImage(images[this.frame]);
    }
}
