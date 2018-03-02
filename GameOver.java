import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class GameOver here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GameOver extends World
{
    GreenfootSound dwTheme;
    GreenfootImage[] bg;
    
    public GameOver(int score)
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(GameWorld.WIDTH, GameWorld.HEIGHT, 1); 
        
        bg = new GreenfootImage[16];
        for (int i = 0; i < bg.length; i++) {
            bg[i] = new GreenfootImage("timevortex" + i + ".jpg");
            //bg[i].scale(bg[i].getWidth() * bg[i].getHeight() / GameWorld.HEIGHT, GameWorld.HEIGHT);
            bg[i].scale(GameWorld.WIDTH, GameWorld.HEIGHT);
        }
        
        animateBg(1);
        
        // setup sound
        dwTheme = new GreenfootSound("dw-intro.mp3");
        
        // add ScoreView
        ScoreView scoreView = new ScoreView("%d");
        addObject(scoreView, getWidth() / 2, getHeight() / 2);
        scoreView.setFontSize(100);
        scoreView.setScore(score);
        
        // add Title text with ScoreView
        //System.out.println(score + " == " + ScoreView.getHighScore() + " : " + (score == ScoreView.getHighScore()));
        ScoreView txtTitle = new ScoreView(((int)score == (int)ScoreView.getHighScore()) ? "N E W\nH I G H S C O R E !!" : "Y O U   S C O R E D");
        addObject(txtTitle, getWidth() / 2, getHeight() / 2);
        txtTitle.setFontSize(24);
        txtTitle.setScore(0);
        txtTitle.setTextColor(new Color(255, 255, 255, (int) (255 * 0.75)));
        txtTitle.setLocation(txtTitle.getX(), txtTitle.getY() - (txtTitle.getImage().getHeight() / 2 + 20));
        scoreView.setLocation(scoreView.getX(), scoreView.getY() + (txtTitle.getImage().getHeight() / 2 + 8));
        
        // add Instruction text with ScoreView
        //ScoreView txtIns = new ScoreView("p r e s s   < S P A C E >   t o   t r y   a g a i n");
        ScoreView txtIns = new ScoreView("w a n n a   t r y   a g a i n ?      p r e s s   < S P A C E >");
        addObject(txtIns, getWidth() / 2, getHeight() - 16);
        txtIns.setFontSize(19);
        txtIns.setScore(0);
        txtIns.setBackgroundColor(new Color(0, 0, 0, (int) (255 * 0.9)));
        txtIns.setTextColor(new Color(255, 255, 255, (int) (255 * 0.9)));
        txtIns.setLocation(txtIns.getX(), txtIns.getY() - (txtIns.getImage().getHeight() / 2));
    }
    
    @Override
    public void act() {
        animateBg(1);
        dwTheme.playLoop();
        
        // CONTROL KEY
        if (frame >= 1 && Greenfoot.isKeyDown("enter") || Greenfoot.isKeyDown("space") || Greenfoot.mousePressed(this)) {
            playAgain();
        }
    }
    
    public void playAgain() {
        dwTheme.stop();
        Greenfoot.setWorld(new GameWorld());
    }
    
    // WALKING ANIMATION
    static final double START_FRAME = 0;
    
    double frame = START_FRAME;
    double bgSpeed = 0.1;
    public void animateBg(int frames) {
        this.frame += (frames * bgSpeed);
        
        //Skip Lightning
        /*if (this.frame >= bg.length - 3) {
            if (skipLightning)
                this.frame++;
            else
                skipLightning = true;
        }*/
        
        if (this.frame >= bg.length)
            this.frame -= (bg.length - START_FRAME);
        else if (frame < START_FRAME)
            this.frame += bg.length;
        
        //if (frame % 3 == 0)
            setBackground(bg[(int) this.frame]);
    }
    
    @Override
    public void started() {
        //dwTheme.playLoop();
    }
    
    @Override
    public void stopped() {
        frame = START_FRAME;
        dwTheme.stop();
    }
}
