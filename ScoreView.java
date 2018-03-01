import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ScoreView here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScoreView extends Actor
{
    // MARGINS
    int marginTop;
    int marginRight;
    World world;
    
    // SCORE
    double score;
    public double getScore() {
        return this.score;
    }
    public void setScore(double newScore) {
        this.score = newScore;
        redrawScore();
    }
    public void append(double amount) {
        this.score += amount;
        redrawScore();
    }
    
    // IMAGE
    GreenfootImage image;
    
    public ScoreView(int marginTop, int marginRight) {
        this(0, marginTop, marginRight);
    }
    public ScoreView(int score, int marginTop, int marginRight) {
        this.score = score;
        this.marginTop = marginTop;
        this.marginRight = marginRight;
    }
    @Override
    public void addedToWorld(World world) {
        this.world = world;
        redrawScore();
    }
    
    @Override
    public void act() {
        //redrawScore();
        //score++;
    }
    
    // SCORE UPDATING
    public void redrawScore() {
        image = new GreenfootImage((int) (this.score) + "", 32, Color.WHITE, new Color(255, 255, 255, 0));
        setImage(image);
        updatePosition();
    }
    public void updatePosition() {
        this.setLocation(world.getWidth() - (image.getWidth()/2) - marginRight, (image.getHeight() / 2) + marginTop);
    }
}
