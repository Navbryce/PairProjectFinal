import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Navbryce on 4/14/2017.
 */
public class Hurdler extends Game {
    private JPanel mainPanel = this;
    private JLayeredPane layeredPane;
    private JLabel[] backgroundLabels;
    private ArrayList<JLabel> hurdles = new ArrayList();
    private JLabel runnerLabel;
    private boolean jumping=false;
    private int timerInterval;
    private double yVelocity=0;
    private int initialSpeed;
    private int counter=0;
    private boolean descending=false;
    private int runnerX;
    private int runnerY;
    private int speed; //Speed of objects and track
    private Timer timer;
    private int width;
    private int height;
	final Window window;
	private ActionListener timerListener;


    public Hurdler(Window mainWindow, int difficulty){
    	window=mainWindow;
    	
        width=1200;
        height=720;

        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new GridLayout(1, 0, 0, 0));

        layeredPane = new JLayeredPane();

        ParsedImageIcon backgroundImage = new ParsedImageIcon(".\\Hurdler\\background.png", 1200, 720);
        JLabel backgroundLabel1 = new JLabel(backgroundImage);
        backgroundLabel1.setSize(new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight()));
        layeredPane.add(backgroundLabel1, new Integer(0));

        JLabel backgroundLabel2 = new JLabel(backgroundImage);
        backgroundLabel2.setSize(new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight()));
        backgroundLabel2.setLocation(1200, 0);
        layeredPane.add(backgroundLabel2, new Integer(0));

        backgroundLabels = new JLabel[2];
        backgroundLabels[0]=backgroundLabel1;
        backgroundLabels[1]=backgroundLabel2;

        ParsedImageIcon runnerImage = new ParsedImageIcon(".\\Hurdler\\character.gif");
        runnerLabel = new JLabel(runnerImage);

        runnerY=300;
        int runnerWidth = runnerImage.getIconWidth();
        int runnerHeight = runnerImage.getIconHeight();
        int middleOfPanel = width/2;
        runnerX=middleOfPanel-(runnerWidth/2); //Runner will be centered with panel;
        runnerY=500-runnerHeight; //Keeps height factored out of character location. Constant centers with track


        runnerLabel.setSize(runnerWidth, runnerHeight);
        runnerLabel.setLocation(runnerX, runnerY);
        layeredPane.add(runnerLabel, new Integer(2));

        this.add(layeredPane);

        timerInterval=10; //Interval for the timer. The lower the number, the smoother the animations
        initialSpeed=(difficulty*2)+6;
        speed=initialSpeed;

        timerListener = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                JLabel backgroundLabel1 = backgroundLabels[0];
                JLabel backgroundLabel2 = backgroundLabels[1];

                double convertedInterval = ((double)timerInterval/(double)1000);
                int current1X = backgroundLabel1.getX();
                int current2X = backgroundLabel2.getX();

                counter++;
                double counterInSeconds = convertedInterval*counter; //Finds time. Converted interval already in units of seconds

                //Increases speed as time progresses
                double speedIncreaseInterval = 10; //In seconds
                int maxSpeed=speed+9;
                if(speed<maxSpeed && counter!=0 && (counterInSeconds%speedIncreaseInterval==0)){
                    speed++;//Intervals of timer might skip over 5. Check to make sure this is not occurring]
                }


                int widthOfBackground=backgroundLabel1.getWidth();

                if(current1X<=((-1*widthOfBackground)+speed)){ //+speed because normally, if the animation continued, this tick would make the background invisible. <= because a change in speed might cause the value to be passed
                    backgroundLabel1.setLocation(width, 0);

                    current2X=0+speed; //The variable must be updated because the if statement in the block after this block references it
                    backgroundLabel2.setLocation(current2X, 0); //Ensures that when it wraps, the other background is in the correct position. +speed because the other if statement will run

                }else{
                    backgroundLabel1.setLocation(current1X-speed, 0);
                }
                if(current2X<=((-1*widthOfBackground)+speed)){ //+speed because normally, if the animation continued, this tick would make the background invisible. <= because a change in speed might cause the value to be passed
                    backgroundLabel2.setLocation(width, 0);
                    backgroundLabel1.setLocation(0, 0); //Ensures that when it wraps, the other background is in the correct position. No + speed because the other if statement won't run
                }else{
                    backgroundLabel2.setLocation(current2X-speed, 0);

                }
                if(jumping){
                    double modifiedTimerInterval=convertedInterval;
                    double metersToPixels=200+5.0*Math.pow((double)(speed-4), 2); //Ensure initial speed is greater than constant and max speed does not reach a point where -.4*(speed-4) > constant
                    double initialYVelocity=5.25-.24*(double)(speed-4);
                    double descendEffect=7;
                    if(yVelocity==0){ //If yVelocity=null, the jump has just started
                        yVelocity=initialYVelocity; //Initial yVelocity
                    }else{
                        if(descending){
                            yVelocity=-descendEffect;
                            descending=false;
                        }
                        double gravity = 9.8;
                        yVelocity=yVelocity-modifiedTimerInterval*gravity;
                        int translation = (int)(yVelocity * modifiedTimerInterval*metersToPixels);
                        int newY=runnerLabel.getY()-translation; //-translation because y increases down the panel

                        if(newY>runnerY){ //If the newY is lower than the runner's initial height
                            jumping=false;
                            yVelocity=0;
                            runnerLabel.setLocation(runnerX, runnerY);

                        }else{
                            runnerLabel.setLocation(runnerX, newY);
                        }
                    }
                }
                if(hurdles.size()>0 && checkForCollisions()){
                    endGame();
                }

                hurdles(); //Generates hurdles and maintains hurdle positions
            }
        };
        AbstractAction jump = new AbstractAction(){
            public void actionPerformed(ActionEvent event){
                jumping=true;
            }
        };
        AbstractAction descend = new AbstractAction(){
            public void actionPerformed(ActionEvent event){
                if(jumping){ //You can only go down faster if you're already in the air
                    descending=true;
                }
            }
        };

        this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "jump");
        this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "jump");

        this.getActionMap().put("jump", jump);

        this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "descend");
        this.getActionMap().put("descend", descend);

    }
    public void hurdles(){
        generateHurdle();
        for(int hurdleCounter=0; hurdleCounter<hurdles.size(); hurdleCounter++){
            JLabel hurdle = hurdles.get(hurdleCounter);
            int currentX = hurdle.getX();
            int currentY = hurdle.getY();

            int newX = currentX-speed;
            if(newX<-1*hurdle.getWidth()){
                hurdles.remove(hurdleCounter); //If the hurdle is no longer visible on screen
                hurdleCounter--;
            }else{
                hurdle.setLocation(currentX-speed, currentY);
            }
        }

    }
    public void generateHurdle(){
        int hurdleWidth=150;
        int hurdleHeight=175;

        int maxBound=50;

        Random random = new Random();
        int randomNumber = random.nextInt(maxBound);

        int numberOfHurdles = hurdles.size();
        int minimumSpaceBetweenHurdles = runnerLabel.getWidth() + hurdleWidth + 100; //aside from two hurdles place direclty next to eachother. + iconwidth because distance is measured from left corner to left corner
        int locationOfMostRecentHurdle=-1;
        int locationOfSecondMostRecentHurdle=-12;
        if(numberOfHurdles>0){
            locationOfMostRecentHurdle = hurdles.get(hurdles.size()-1).getX();
            if(numberOfHurdles>1){
                locationOfSecondMostRecentHurdle=hurdles.get(hurdles.size()-2).getX();
            }
        }

        if(randomNumber==0 && (numberOfHurdles==0 || (locationOfMostRecentHurdle<getWidth()-minimumSpaceBetweenHurdles) || (speed>8 && hurdles.size()>1 && locationOfMostRecentHurdle>=getWidth()-hurdleWidth-50) && (locationOfMostRecentHurdle-locationOfSecondMostRecentHurdle>=minimumSpaceBetweenHurdles))){ //Generates hurdles randomly--only when a 0 is returned by the random
            ParsedImageIcon hurdleImage = new ParsedImageIcon(".\\Hurdler\\hurdle.png", hurdleWidth, hurdleHeight);
            JLabel hurdleLabel = new JLabel(hurdleImage);
            hurdleLabel.setSize(hurdleImage.getIconWidth(), hurdleImage.getIconHeight());
            hurdleLabel.setLocation(getWidth(), runnerY+runnerLabel.getHeight()-hurdleImage.getIconHeight()+5); //Translates out the effect of the runner's height and accounts for the effect of the hurldle's height
            hurdles.add(hurdleLabel);
            layeredPane.add(hurdleLabel, new Integer(3));


        }
    }

    public boolean checkForCollisions(){
        boolean collision=false;

        int runnerX = runnerLabel.getX();
        int runnerY=runnerLabel.getY();
        int runnerWidth = runnerLabel.getWidth();
        int runnerHeight = runnerLabel.getHeight();
        int hurdleWidth = hurdles.get(0).getWidth();
        int hurdleHeight = hurdles.get(0).getHeight();
        int hurdleY = hurdles.get(0).getY(); //All the hurdles are at the same height
        for(int hurdleCounter=hurdles.size()-1; hurdleCounter>=0 && !collision; hurdleCounter--){
            JLabel hurdle = hurdles.get(hurdleCounter);
            int hurdleX = hurdle.getX();
            if((runnerX<=hurdleX && runnerX+runnerWidth>=hurdleX+hurdleWidth) || (runnerX>=hurdleX && runnerX<=hurdleX+hurdleWidth) || (runnerX+runnerWidth>=hurdleX && runnerX+runnerWidth<=hurdleX+hurdleWidth)){
                if(runnerY+10>hurdleY){ //Not +runnerHeight because the game would be nearly impossible
                    collision=true;
                }
            }

        }
        return collision;
    }
    public void endGame(){
        timer.stop();
        JLabel endText = new JLabel("Game Over");
        endText.setSize(endText.getPreferredSize());
        endText.setLocation(getWidth()/2-endText.getWidth()/2, getHeight()/2-endText.getHeight()/2);
        layeredPane.add(endText, new Integer(4));

        JOptionPane.showMessageDialog(this, "Game Over!\nReturn to main menu.");
        
			this.setVisible(false);
			window.refresh();
		

    }
	@Override
	public boolean run() {
		// TODO Auto-generated method stub
		timer = new Timer(timerInterval, timerListener);
        timer.setInitialDelay(0);
        timer.start();
        
		return false;
	}

}
