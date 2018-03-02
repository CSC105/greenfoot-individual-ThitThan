import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Well, this is a main world, where the gameplay happens.
 * 
 * @author Thitiwat Thanyapakluepong
 * @version beta1
 */
public class GameWorld extends World
{   
    // WORLD
    public static final int WIDTH = 640;
    public static final int HEIGHT = 400;
    public static final double G = 9.8;
    public static final double G_MODIFIER = 0.05;
    
    // GROUND
    static final int GROUND_HEIGHT = 24;
    
    // INVINCIBLE MODE
    public static final int INVINCIBLE_MODE_MULTIPLIER = 3;
    
    // ACTORS
    Flamingo fla;
    Dalek dalek;
    Ground ground;
    
    double xPos;
    int groundY;
    
    // SCORE VIEW(s)
    static final double SCORE_INCREASE_RATE = 0.02;
    TopRightScoreView scoreView;
    TopRightScoreView highScoreView;
    
    // SOUND
    GreenfootSound[] theme;
    
    // DALEK ANIMATION
    static final int DALEK_DEFAULT_POS = -36;
    static final int DALEK_PREPARE_POS = 72;
    //static final int DALEK_PREPARE_POS = 64;
    //static final int DALEK_EXTERMINATE_POS = 180;
    //static final int DALEK_PREPARE_POS = 84;
    static final int DALEK_EXTERMINATE_POS = 216;
    
    //static final int DALEK_DEFAULT_POS = 0;
    //static final int DALEK_PREPARE_POS = 90;
    //static final int DALEK_EXTERMINATE_POS = 180;
    
    // DALEK
    boolean dalekCatchedUp = false;
    boolean dalekExterminating = false;
    boolean dalekKeyPressed = false;
    boolean dalekSoundPlayed = false;
    int obstacleToOutrunDalek = 3;
    int duckedObstacle = 0;
    int dalekPrepareVelo = 2;
    int dalekExterminateVelo = 2;
    
    // OBSTACLE : GRASS
    static final int GRASS_DISTANCE = 1024;
    double lastGrassX = 0;
    double grassModifier = 1;
    
    // OBSTACLE : ENEMY
    static final int ENEMY_DISTANCE = 3456;
    double lastEnemyX = 0;
    double enemyModifier = 1;
    
    // SPEED UP
    static final int SPEED_UP_EVERY = 30000;
    double lastSpeedup = 0;
    
    // INVINCIBLE MODE
    boolean invincibleMode = false;
    boolean cheatKeyPressed = false;
    
    public GameWorld()
    {    
        // Create a new world with 640x480 cells with a cell size of 1x1 pixels.
        super(WIDTH, HEIGHT, 1, false);
        this.groundY = getHeight() - GROUND_HEIGHT;
        
        // BACKGROUND
        this.setupBackground();
        this.setupObstacles();
        
        // GRAVITY
        this.setupGravity();
        
        // SOUND
        theme = new GreenfootSound[] { 
            new GreenfootSound("iAmTheDoctor01.mp3"), 
            new GreenfootSound("iAmTheDoctor02.mp3") 
        };
        //theme[0].playLoop();
        
        // Add a Flamingo
        fla = new Flamingo();
        fla.setCallback(new Flamingo.Callback() {
            public void onJumpStarted(Flamingo f) {
                //System.out.println("JUMP!!");
                if (ignoreGravity.indexOf(f) == -1)
                    ignoreGravity.add(f);
            }
            public void onJumpFinished(Flamingo f) {
                ignoreGravity.remove(f);
            }
            public boolean isFloating(Flamingo f) {
                return GameWorld.this.isFloating(f);
            }
        });
        addGroundObject(fla, (getWidth()/2) - 24, getHeight() / 2);
        
        // ENEMY IN THE BACK (DALEK)
        dalek = new Dalek();
        setPaintOrder(Dalek.class, Grass.class);
        addGroundObject(dalek, DALEK_DEFAULT_POS, (int) (getHeight() * 0.25));
        
        // GROUND
        ground = new Ground(GROUND_HEIGHT);
        addObject(ground, 0, 0);
        
        // ADD HIGHSCORE VIEW
        highScoreView = new TopRightScoreView("BEST:  %d", 16, 32);
        addObject(highScoreView, 0, 0);
        highScoreView.setFontSize(19);
        highScoreView.setTextColor(new Color(255, 255, 255, (int) (255 * 0.6)));
        highScoreView.setScore(ScoreView.getHighScore());
        
        // ADD SCORE VIEW
        scoreView = new TopRightScoreView("%d", 32, 30);
        addObject(scoreView, 0, 0);
        scoreView.setFontSize(40);
        scoreView.setScore(0);
    }
    
    //
    // MAIN GAME EVENTS
    //
    boolean stumblingSlowDown = false;
    double stumblingSlowDownRatio = 0.5;
    
    // MOUSE
    boolean mouseClicked = true;
    @Override
    public void act() {
        applyGravity();
        if (invincibleMode) {
            scrollBackground(INVINCIBLE_MODE_MULTIPLIER);
            scoreView.append(INVINCIBLE_MODE_MULTIPLIER * SCORE_INCREASE_RATE * sceneVelo);
        }
        else {
            scrollBackground(1);
            
            // count score if the player haven't lost yet
            if (!dalekExterminating) {
                scoreView.append(1 * SCORE_INCREASE_RATE * sceneVelo);
            }
        }
        
        // CONTROL
        // CLICK ANYWHERE TO JUMP
        boolean mousePressing = false;
        for (Background b : bg) {        
            mousePressing = mousePressing || Greenfoot.mousePressed(b);
        }
        
        if (mousePressing) {
            if (!mouseClicked) {
                if (fla.isJumpingEnabled() && fla.jumpCount < Flamingo.MAX_JUMP) {
                    fla.jump();
                }
                mouseClicked = true;
            }
        }
        else {
            mouseClicked = false;
        }
        /*if (Greenfoot.isKeyDown("right")) {
            scrollBackground(1);
        }*/
        /*if (Greenfoot.isKeyDown("left")) {
            scrollBackground(-2);
        }*/
        
        // SOUND
        playTheme();
        
        // ADD GRASS
        if (xPos - lastGrassX >= (GRASS_DISTANCE * grassModifier / DEFAULT_SCENE_VELO * sceneVelo)) {
            //if (Greenfoot.getRandomNumber(3) == 0) {
                Grass grass = new Grass(fla);
                grass.setCallback(new Grass.Callback() {
                    @Override
                    public void onHitMainChar(Actor a) {
                        if (invincibleMode)     // ignore when INVINCIBLE
                            return;
                        
                        if (!dalekCatchedUp) {
                            dalekCatchedUp = true;
                            
                            // disable JUMPING
                            //fla.setJumpingEnabled(false);
                        }
                        else {
                            // loose
                            dalekExterminating = true;
                            dalek.playExterminateSound();
                            stopTheme();
                            
                            // disable JUMPING
                            fla.setJumpingEnabled(false);
                            //ignoreGravity.remove(fla);
                        }
                        
                    }
                });
                addObstacle(grass);
            //}
            
            grassModifier = ((Greenfoot.getRandomNumber(3) * 2) + 9) / 10.0;
            lastGrassX = xPos;
        }
        // ADD enemy
        else if (xPos - lastEnemyX >= (ENEMY_DISTANCE * enemyModifier / DEFAULT_SCENE_VELO * sceneVelo)) {
            if (Greenfoot.getRandomNumber(2) == 0) {
                WeepingAngel angel = new WeepingAngel(fla);
                angel.setCallback(new Obstacle.Callback() {
                    @Override
                    public void onHitMainChar(Actor a) {
                        if (invincibleMode)     // ignore when INVINCIBLE
                            return;
                        
                        gameOver();
                    }
                });
                addObstacle(angel);
            }
            else {
                CybermanHead cyberman = new CybermanHead(fla);
                cyberman.setCallback(new Obstacle.Callback() {
                    @Override
                    public void onHitMainChar(Actor a) {
                        if (invincibleMode)     // ignore when INVINCIBLE
                            return;
                        
                        gameOver();
                    }
                });
                addObstacle(cyberman);
            }
            
            //enemyModifier = (Greenfoot.getRandomNumber(5) + 10) / 10.0;
            enemyModifier = ((Greenfoot.getRandomNumber(4) * 2) + 9) / 10.0;
            lastEnemyX = xPos;
        }
        
        // SPPED UP
        if (xPos - lastSpeedup >= SPEED_UP_EVERY) {
            sceneVelo += 1;
            lastSpeedup = xPos;
            
            System.out.println("speedup at x = " + lastSpeedup + ", velo = " + sceneVelo);
        }
        
        // DALEK catching up (PREPARE_POSITION)
        if (dalekCatchedUp) {
            if (!dalekSoundPlayed) {
                dalek.playSound();
                dalekSoundPlayed = true;
            }
            if (dalek.getX() < DALEK_PREPARE_POS) {
                //dalek.setLocation(dalek.getX() + (int) (sceneVelo * 2.0/3), dalek.getY());
                dalek.setLocation(dalek.getX() + (int) (dalekPrepareVelo * sceneVelo / DEFAULT_SCENE_VELO) , dalek.getY());
                
                stumblingSlowDown = true;
            }
            else {
                stumblingSlowDown = false;
                
                // allow jumping again
                if (!fla.isJumpingEnabled() && !dalekExterminating) {
                    fla.setJumpingEnabled(true);
                }
            }
        }
        else {
            dalekSoundPlayed = false;
            if (dalek.getX() > DALEK_DEFAULT_POS) {
                //dalek.setLocation(dalek.getX() - (int) (sceneVelo * 2.0/3), dalek.getY());
                dalek.setLocation(dalek.getX() - (int) (dalekPrepareVelo * sceneVelo / DEFAULT_SCENE_VELO), dalek.getY());
            }
        }
        
        // DALEK catching up (EXTERMINATE)
        if (dalekExterminating) {
            // prevent player from ESCAPING DEATH xD
            fla.setJumpingEnabled(false);
            
            if (dalek.getX() < DALEK_EXTERMINATE_POS) {
                //dalek.setLocation(dalek.getX() + (int) (sceneVelo * 2.0/3), dalek.getY());
                dalek.setLocation(dalek.getX() + (int) (dalekExterminateVelo * sceneVelo / DEFAULT_SCENE_VELO), dalek.getY());
                
                stumblingSlowDown = true;
            }
            else if (dalek.exterminate.isPlaying()) {
                // wait until the "EXTERMINATE" SFX played to the end
                stumblingSlowDown = true;
                
                // stop background
                scrollBackground(-1);
                fla.animateWalk(-1);
            }
            else {
                // lose
                gameOver();
            }
        }
        
        // PRESS 'C' FOR INVINCIBLE MODE
        if (Greenfoot.isKeyDown("c")) {
            if (!cheatKeyPressed) {
                invincibleMode = !invincibleMode;
                fla.setInvincible(invincibleMode);
                dalekCatchedUp = false;
                System.out.println("invincible mode : " + (invincibleMode ? "ON":"OFF"));
                
                cheatKeyPressed = true;
            }
        }
        else {
            cheatKeyPressed = false;
        }
        
        // UPDATE HIGHSCORE
        if (scoreView.getScore() > ScoreView.getHighScore()) {
            ScoreView.setHighScore(scoreView.getScore());
            highScoreView.setScore(scoreView.getScore());
        }
    }
    
    public void gameOver() {
        // loose
        //Greenfoot.stop();
        stopTheme();
        dalek.stopSound();
        
        Greenfoot.setWorld(new GameOver((int) scoreView.getScore()));
    }
    
    @Override
    public void started() {
        //playTheme();
    }
    
    @Override
    public void stopped() {
        stopTheme();
    }
    
    
    //
    // SOUND
    //
    int track = 1;
    public void playTheme() {
        if (!theme[track].isPlaying()) {
            track++;
            track %= theme.length;
            theme[track].play();
            //System.out.println("Playing Track " + track);
        }
    }
    public void stopTheme() {
        for (GreenfootSound track : theme) {
            track.stop();
        }
        if (dalek != null)
            dalek.stopSound();
    }
    
    
    //
    // GRAVITY SYSTEM
    //
    HashMap<Actor, Double> velos;
    HashMap<Actor, Double> times;
    ArrayList<Actor> ignoreGravity;
    
    ArrayList<Actor> groundObjects;
    public void addGroundObject(Actor actor, int x, int y) {
        this.addObject(actor, x, y);
        groundObjects.add(actor);
        //System.out.println("groundObjects : " + groundObjects.size());
    }
    public void removeGroundObject(Actor actor) {
        this.removeObject(actor);
        groundObjects.remove(actor);
        //System.out.println("groundObjects : " + groundObjects.size());
    }
    
    public void setupGravity() {
        this.groundObjects = new ArrayList<>();
        
        this.ignoreGravity = new ArrayList<>();
        this.velos = new HashMap<>();
        this.times = new HashMap<>();
    }
    public int getBottomY(Actor obj) {
        return obj.getY() + (obj.getImage().getHeight() / 2);
    }
    public int distanceAboveGround(Actor obj) {
        int result = groundY - getBottomY(obj);
        //System.out.println(result + " px above ground");
        return result;
    }
    public boolean isFloating(Actor obj) {
        return distanceAboveGround(obj) > 0;
    }
    public void applyGravity() {
        for (int i = 0; i < groundObjects.size(); i++) {
            Actor obj = groundObjects.get(i);
            
            // ignore gravity for that object
            if (ignoreGravity.indexOf(obj) != -1) {
                velos.put(obj, null);
                times.put(obj, null);
                continue;
            }
            
            if (isFloating(obj)) {
                if (velos.get(obj) == null)
                    velos.put(obj, 0.0);
                if (times.get(obj) == null)
                    times.put(obj, 0.0);
                
                double v = velos.get(obj);
                double t = times.get(obj);
                
                v = v + (G * G_MODIFIER * t);
            
                // if too far below ground -> GET BACK UP
                int newY = (int) (obj.getY() + v);
                obj.setLocation(obj.getX(), newY);
                if (getBottomY(obj) > groundY) {
                    newY = groundY - (obj.getImage().getHeight() / 2);
                    obj.setLocation(obj.getX(), newY);
                    
                    // if Flamingo -> save the Y value
                    if (obj instanceof Flamingo) {
                        Flamingo.onGroundY = newY;
                    }
                }
                
                t += 0.1;
            
                //System.out.println("v : " + v + "\t t : " + t);
                velos.put(obj, v);
                times.put(obj, t);
                
                // Animate Flamingo feet
                if (obj instanceof Flamingo)
                    ((Flamingo) obj).animateWalk(-1 * (invincibleMode ? INVINCIBLE_MODE_MULTIPLIER:1));
            }
            else if (velos.get(obj) != null) {
                velos.put(obj, null);
                times.put(obj, null);
            }
        }
    }
    
    
    //
    // OBSTACLES
    //
    ArrayList<Actor> obstacles;
    public void setupObstacles() {
        this.obstacles = new ArrayList<>();
    }
    public void addObstacle(Actor actor) {
        // add to world first
        addGroundObject(actor, 0, 0);
        
        // set location (put on the ground)
        int x = getWidth() + (actor.getImage().getWidth() / 2);
        int y = getHeight() - GROUND_HEIGHT - (actor.getImage().getHeight()/2);
        actor.setLocation(x, y);
        
        // add to ArrayList
        obstacles.add(actor);
    }
    public void removeObstacle(Actor actor) {
        removeGroundObject(actor);
        obstacles.remove(actor);
    }
    public void scrollObstacles(int frames) {
        for (int i = 0; i < obstacles.size(); i++) {
            Actor obj = obstacles.get(i);
            obj.setLocation(obj.getX() - frames, obj.getY());
            
            // REMOVE when offscreen
            if (obj.getX() < -(obj.getImage().getWidth() / 2)) {
                removeObstacle(obj);
                
                if (dalekCatchedUp && !dalekExterminating
                    && obj instanceof Obstacle && !((Obstacle)obj).isHit()) {
                    duckedObstacle++;
                    
                    if (duckedObstacle >= obstacleToOutrunDalek) {
                        dalekCatchedUp = false;
                        duckedObstacle = 0;
                        obstacleToOutrunDalek++;
                    }
                }
            }
        }
    }
    
    //
    // SCROLLING BACKGROUND
    //
    Background[] bg;
    static final double DEFAULT_SCENE_VELO = 4;
    static final double BG_VELO = 0.5;
    
    double sceneVelo = 4;
    //double bgVelo = 0.5;
    
    double maxBgX = getWidth() * 2.5;
    double minBgX = - (getWidth()/2);
    int centerY = getHeight()/2;
    
    double[] bgX;
    
    public void setupBackground() {
        bg = new Background[3];
        
        bg[0] = new Background();
        addObject(bg[0], getWidth()/2, centerY);
        
        bg[1] = new Background();
        addObject(bg[1], (int)(getWidth() * 1.5), centerY);
        
        bg[2] = new Background();
        addObject(bg[2], (int) maxBgX, centerY);
        
        // x position in double
        bgX = new double[bg.length];
        for (int i = 0; i < bg.length; i++) {
            bgX[i] = bg[i].getX();
        }
        
        //System.out.println("minBgX = " + minBgX);
        //System.out.println("maxBgX = " + maxBgX);
    }
    public void scrollBackground(int n) {
        for (int i = 0; i < Math.abs(n); i++)
            scrollBackground(n > 0);
    }
    public void scrollBackground(boolean forward) {
        for (int i = 0; i < bg.length; i++) {
            double newX = bgX[i] - ((BG_VELO / DEFAULT_SCENE_VELO * sceneVelo) * (forward ? 1.0:-1.0) * (stumblingSlowDown ? stumblingSlowDownRatio:1));
            //double newX = bgX[i] - (1 * (forward ? 1:-1));
            
            // DISTANCE
            //System.out.print(newX + " ");
            xPos += (sceneVelo * (forward ? 1:-1) * (stumblingSlowDown ? stumblingSlowDownRatio:1));
            
            //System.out.print(" <= " + minBgX + " : " + (newX <= minBgX) + " ");
            
            if (newX <= minBgX) {
                //System.out.println("nope it's " + newX + " ");
                newX = newX + minBgX + maxBgX;
            }
            bgX[i] = newX;
            bg[i].setLocation((int) newX, bg[i].getY());
        }
        //System.out.println();
        scrollObstacles((int) (sceneVelo * (stumblingSlowDown ? stumblingSlowDownRatio:1)) * (forward ? 1:-1));
    }
}
