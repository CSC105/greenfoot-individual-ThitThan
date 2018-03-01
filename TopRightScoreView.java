import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class CenteredScoreView here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TopRightScoreView extends ScoreView
{
    // MARGINS
    int marginTop;
    int marginRight;
    World world;
    
    @Override
    public void addedToWorld(World world) {
        this.world = world;
        redrawScore();
    }
    
    public TopRightScoreView(String scoreFormat, int marginTop, int marginRight) {
        super(scoreFormat);
        this.marginTop = marginTop;
        this.marginRight = marginRight;
    }
    
    @Override
    public void redrawScore() {
        super.redrawScore();
        updatePosition();
    }
    public void updatePosition() {
        if (world != null)
            this.setLocation(world.getWidth() - (image.getWidth()/2) - marginRight, (image.getHeight() / 2) + marginTop);
    }
}
