import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ScoreView here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScoreView extends Actor
{
    // HIGH SCORE
    static double highScore = 0;
    public static double getHighScore() {
        return highScore;
    }
    public static void setHighScore(double newHighScore) {
        highScore = newHighScore;
    }
    
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
        setScore(this.score + amount);
    }
    
    // TEXT FORMATTING
    String scoreFormat;
    public void setScoreFormat(String format) {
        if (format != null) {
            this.scoreFormat = format;
            redrawScore();
        }
    }
    public String getScoreFormat() {
        return this.scoreFormat;
    }
    
    // FONT SIZE
    public static final int DEFAULT_FONT_SIZE = 32;
    int fontSize = DEFAULT_FONT_SIZE;
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        redrawScore();
    }
    public int getFontSize() {
        return this.fontSize;
    }
    
    // TEXT COLOR
    public static final Color DEFAULT_COLOR = Color.WHITE;
    Color textColor = DEFAULT_COLOR;
    public void setTextColor(Color color) {
        this.textColor = color;
        redrawScore();
    }
    public Color getTextColor() {
        return this.textColor;
    }
    
    // IMAGE
    GreenfootImage image;
    
    /*public ScoreView(int marginTop, int marginRight) {
        this(0, marginTop, marginRight);
    }
    public ScoreView(double score, int marginTop, int marginRight) {
        this("%d", score, marginTop, marginRight);
    }*/
    public ScoreView(String scoreFormat, int marginTop, int marginRight) {
        this.scoreFormat = scoreFormat;
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
        image = new GreenfootImage(String.format(this.scoreFormat, (int) this.score), fontSize, textColor, new Color(255, 255, 255, 0));
        setImage(image);
        updatePosition();
    }
    public void updatePosition() {
        if (world != null)
            this.setLocation(world.getWidth() - (image.getWidth()/2) - marginRight, (image.getHeight() / 2) + marginTop);
    }
}
