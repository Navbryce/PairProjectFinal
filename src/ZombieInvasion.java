import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;



import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


public class ZombieInvasion extends Game{
	private JPanel mainPanel = this;
	private JLayeredPane layerPane;
	private JLabel scopeLabel;
	private ArrayList <JLabel> zombieLabels = new ArrayList();
	private ArrayList <Velocity> zombieVelocities = new ArrayList();
	private ArrayList <JLabel> bloodLabels = new ArrayList();
	private ArrayList <JLabel> bulletLabels = new ArrayList();
	private ArrayList <Double> bulletAlpha = new ArrayList();
	private ArrayList <Double> bloodAlpha = new ArrayList();
	private ArrayList <Weapon> weaponList = new ArrayList();
	private JLabel weaponLabel=new JLabel();
	private JLabel reloadText;
	private JLabel ammoLabel=new JLabel();
	private int timerCounter=0;
	private Point shootingLocation=null;
	private Weapon currentWeapon;
	private int currentWeaponId=-1;
	final int timerInterval=5;
	private double automaticFire = -1;
	private int reloading = -1;
	private Timer timer;
	private ArrayList<JLabel> leftWall = new ArrayList();
	private ArrayList<JLabel> rightWall = new ArrayList();
	private ArrayList<JLabel> topWall = new ArrayList();
	private ArrayList<JLabel> bottomWall = new ArrayList();
	private ArrayList<JLabel> aestheticWall = new ArrayList();
	private ArrayList<ArrayList<Double>> wallHealth = new ArrayList();
	private int wallWidth;
	private int wallHeight;
	private int gameOver=0;
	private int numberOfZombiesToSpawn=0;
	private boolean returnToMenu=false;
	final Window window;
	private ActionListener timerListener;



	public ZombieInvasion(Window mainWindow, int difficulty){
		//System.out.println("constructor");
		window=mainWindow; //Sets the mainWindow
		
		generateWeaponList();
		equipWeapon(weaponList.get(0), 0);
		
		numberOfZombiesToSpawn=(difficulty+1)*15;
		
		layerPane = new JLayeredPane();
		layerPane.setSize(1200, 720);
		layerPane.setPreferredSize(layerPane.getSize());
		
		//Wall
		createWall(); //Also sets wall width and wall height private variables
		
		ParsedImageIcon background = new ParsedImageIcon(".\\ZombieInvasion\\background.jpg");
		JLabel backgroundLabel = new JLabel(background);
		backgroundLabel.setSize(background.getIconWidth(), background.getIconHeight());
		
		layerPane.add(backgroundLabel, new Integer(0));
		
		
		
		ParsedImageIcon scope = new ParsedImageIcon(".\\ZombieInvasion\\scope.png");
		scopeLabel = new JLabel(scope);
		scopeLabel.setSize(scope.getIconWidth(), scope.getIconHeight());
		layerPane.add(scopeLabel, new Integer(300));
		mainPanel.addMouseMotionListener(new MouseAdapter(){
		    public void mouseMoved(MouseEvent mouseEvent) {
		        int xTranslation = scopeLabel.getWidth()/2;
		        int yTranslation = scopeLabel.getHeight()/2;
		        
		        Point panelLocation = mainPanel.getLocationOnScreen();
		        Point mouseLocation = mouseEvent.getLocationOnScreen();
		        
		        int x = (int)(mouseLocation.getX() - panelLocation.getX() - xTranslation);
		        int y = (int)(mouseLocation.getY() - panelLocation.getY() - yTranslation);
		        scopeLabel.setLocation(x, y);
		      }
		    public void mouseDragged(MouseEvent mouseEvent) {
		        int xTranslation = scopeLabel.getWidth()/2;
		        int yTranslation = scopeLabel.getHeight()/2;
		        
		        Point panelLocation = mainPanel.getLocationOnScreen();
		        Point mouseLocation = mouseEvent.getLocationOnScreen();
		        
		        int x = (int)(mouseLocation.getX() - panelLocation.getX() - xTranslation);
		        int y = (int)(mouseLocation.getY() - panelLocation.getY() - yTranslation);
		        scopeLabel.setLocation(x, y);
		        

		      }
			
		});
		mainPanel.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(currentWeapon.getFireType()==Weapon.normalWeapon){
					shootingLocation = new Point(e.getX(), e.getY());
				}else{
					automaticFire=timerCounter;
				}
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				automaticFire=-1;

			}
			
		});

		BufferedImage blankCursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImage, new Point(0, 0), "blank");
		this.setCursor(blankCursor);
		
		int numberOfZombiesInWave=numberOfZombiesToSpawn;
		for(int elementCounter=0; elementCounter<numberOfZombiesInWave; elementCounter++){
			spawnZombie();
		}
		
		timerListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				timerCounter++;
				double timerIntervalInSec = (double)timerInterval/1000.0;
				double second = timerIntervalInSec * timerCounter; 
				
				if(gameOver!=0 && !returnToMenu){
					gameOver();
				}
				
				decay();
				if(automaticFire!=-1 && ((timerCounter-automaticFire)%currentWeapon.getDelay()==0 || timerCounter-automaticFire<2)){ //%5 adds a delay
					shootingLocation = mainPanel.getMousePosition();

				}
				if(reloading>0){
					reloading--; //Reloading is set to the reload delay of the weapon
				}else if(reloading==0){
					shootingLocation=null;
					automaticFire=-1;
					try {
						currentWeapon.reload();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					reloadText.setVisible(false);
					reloading=-1;
					setAmmoLabel(currentWeapon.getBulletsLeftInClip(), currentWeapon.getTotalBullets());
					reloadText.setText("You need to reload. Press R.");
					reloadText.setSize(reloadText.getPreferredSize());
					reloadText.setLocation((int)(layerPane.getWidth()/2.0- reloadText.getWidth()/2.0), (int)(layerPane.getHeight()/2.0 - reloadText.getHeight()));


				}
				if(currentWeapon.needToReload()){
					if(currentWeapon.getTotalBullets()<=0){
						outOfAmmo();
	
					}
					if(!reloadText.isVisible()){ //Will set the reload text label to visible if the gun needs to be reloaded or if the gun runs out of ammo
						reloadText.setVisible(true);
					}

				}else if(shootingLocation!=null){
					shoot((int)shootingLocation.getX(), (int)shootingLocation.getY());
					try {
						currentWeapon.fire();
						setAmmoLabel(currentWeapon.getBulletsLeftInClip(), currentWeapon.getTotalBullets());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("Tried to shoot an ureloaded weapon.");
					}
					
					shootingLocation=null;
				}
				
				
				moveZombies(timerIntervalInSec);
				mainPanel.repaint();
				
			}
			
		};
		//Weapon Panel GUI
		JPanel weaponPanel = new JPanel();
		weaponPanel.setSize(400,125);
		weaponPanel.setOpaque(false);
		weaponPanel.setPreferredSize(new Dimension(400, 125));
		weaponPanel.setLayout(new GridBagLayout());
		weaponPanel.setLocation(layerPane.getWidth()-weaponPanel.getWidth(), layerPane.getHeight()-weaponPanel.getHeight());
		weaponPanel.add(weaponLabel);
		GridBagConstraints ammoLabelConstraints = new GridBagConstraints();
		ammoLabelConstraints.gridy=1;
		ammoLabel.setFont(new Font("Lucida Console", Font.BOLD, 40));
		ammoLabel.setForeground(Color.WHITE);
		weaponPanel.add(ammoLabel, ammoLabelConstraints);
		
		layerPane.add(weaponPanel, new Integer(10));
		
		//Reload GUI
		reloadText = new JLabel("You need to reload. Press R.");
		reloadText.setForeground(Color.yellow);
		reloadText.setFont(new Font("Lucida Console", Font.BOLD, 50));
		reloadText.setSize(reloadText.getPreferredSize());
		reloadText.setLocation((int)(layerPane.getWidth()/2.0- reloadText.getWidth()/2.0), (int)(layerPane.getHeight()/2.0 - reloadText.getHeight()));
		reloadText.setVisible(false);
		layerPane.add(reloadText, new Integer(20));
		
		//Reload Mechanics
		AbstractAction reloadAction = new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(currentWeapon.getBulletsLeftInClip()!=currentWeapon.getMagazineSize() && reloading==-1 && currentWeapon.getTotalBullets()>0){
					reloading=currentWeapon.getReloadDelay();
					reloadText.setText("Reloading...");
					reloadText.setVisible(true);
					reloadText.setSize(reloadText.getPreferredSize());
					reloadText.setLocation((int)(layerPane.getWidth()/2.0- reloadText.getWidth()/2.0), (int)(layerPane.getHeight()/2.0 - reloadText.getHeight()));
				}


				
			}
			
		};
		//Key Bindings
        this.getInputMap().put(KeyStroke.getKeyStroke("R"), "reloadWeapon");
        this.getActionMap().put("reloadWeapon", reloadAction);
        
        setWeaponBindings();
        



		timer = new Timer(timerInterval, timerListener);
		
		
		layerPane.repaint();
		layerPane.setVisible(true);
		

		this.add(layerPane);
		this.setLayout(new GridLayout(0, 1, 0, 0));
		this.setPreferredSize(layerPane.getSize());
		
	}
	@Override
	public boolean run() {
		// TODO Auto-generated method stub
		
		timer.start();
		return true;
	}
	
	public void spawnZombie(){
		int maxVelocity=20;
		int minVelocity=10;
		ParsedImageIcon zombie = new ParsedImageIcon(".\\ZombieInvasion\\zombie.png");
		JLabel zombieLabel = new JLabel(zombie);
		zombieLabel.setSize(zombie.getIconWidth(), zombie.getIconHeight());
		
		numberOfZombiesToSpawn--; //Keeps track of the number of zombies that NEED to be spawned. NOT THE NUMBER OF SPAWNED ZOMBIES OR LIVE ZOMBIES
		
		Velocity velocity = new Velocity(minVelocity, maxVelocity);
		
		rotateZombie(zombieLabel, velocity);
		
		Random randomNumber = new Random();
		int xMax = layerPane.getWidth()-wallWidth-zombie.getIconWidth();
		int xMin = wallWidth;
		
		int yMax = layerPane.getHeight()-wallHeight-zombie.getIconHeight();
		int yMin=wallHeight;
	
		int x = xMin + randomNumber.nextInt(xMax-xMin);
		int y = yMin + randomNumber.nextInt(yMax-yMin);


		
		zombieLabel.setLocation(x, y);
		
		zombieLabels.add(zombieLabel);
		zombieVelocities.add(velocity);
		
		layerPane.add(zombieLabel, new Integer(7));
		
		layerPane.repaint();
		
	}
	public void moveZombies(double timerIntervalInSec){
		final double metersToPixel=20;
		for(int zombieCounter=0; zombieCounter<zombieLabels.size(); zombieCounter++){
			JLabel zombieLabel = zombieLabels.get(zombieCounter);
			Velocity zombieVelocity = zombieVelocities.get(zombieCounter);
			
			int currentX = zombieLabel.getX();
			int currentY = zombieLabel.getY();
			
			int velocityX = zombieVelocity.getVelocityX();
			int velocityY = zombieVelocity.getVelocityY();
			
			int newX = (int)(((double)velocityX)*timerIntervalInSec*metersToPixel) + currentX;
			int newY = (int)(((double)velocityY)* timerIntervalInSec * metersToPixel) + currentY;
			
			if(gameOver!=-1){
				if(newX<=wallWidth || newX>layerPane.getWidth()-wallWidth-zombieLabel.getWidth()){
					int wallIndex = getWallHitIndex(newY, wallHeight);

					if(newX<=wallWidth){
						decayWall(wallHealth.get(0), leftWall.get(wallIndex), wallIndex);
					}else{
						decayWall(wallHealth.get(1), rightWall.get(wallIndex), wallIndex);
					}
					velocityX = velocityX*-1;
					zombieVelocity.setVelocityX(velocityX);
					rotateZombie(zombieLabel, zombieVelocity);
					newX = (int)(((double)velocityX)*timerIntervalInSec*metersToPixel) + currentX;
		
				}
				if(newY<wallHeight || newY>layerPane.getHeight()-wallHeight-zombieLabel.getHeight()){
					int wallIndex = getWallHitIndex(newX, wallWidth)-1; //Because the first tile is on the side wall. Only works because tiles are square.

					if(newY<=wallHeight){
						decayWall(wallHealth.get(2), topWall.get(wallIndex), wallIndex);
					}else{
						decayWall(wallHealth.get(3), bottomWall.get(wallIndex), wallIndex);
					}
					
					velocityY = velocityY*-1;
					zombieVelocity.setVelocityY(velocityY);
					rotateZombie(zombieLabel, zombieVelocity);
					newY = (int)(((double)velocityY)* timerIntervalInSec * metersToPixel) + currentY;

				}
			}
			
			zombieLabel.setLocation(newX, newY);
			


		}
	}
	
	public void shoot(int x, int y){
		boolean hit = false;
		for(int zombieCounter=0; zombieCounter<zombieLabels.size(); zombieCounter++){
			JLabel zombieLabel = zombieLabels.get(zombieCounter);
			int zombieX = zombieLabel.getX();
			int zombieY = zombieLabel.getY();
			int zombieWidth = zombieLabel.getWidth();
			int zombieHeight = zombieLabel.getHeight();
			
			if(x>zombieX && x<zombieX+zombieWidth){
				if(y>zombieY && y<zombieY+zombieHeight){
					zombieLabels.remove(zombieLabel);
					layerPane.remove(zombieLabel);
					spawnBlood(zombieLabel);
					zombieVelocities.remove(zombieCounter);
					zombieCounter--;
					mainPanel.repaint();
					hit=true;
				}
			}
		}
		
		if(!hit){
			bulletHole(x, y);
		}else{
			if(zombieLabels.size()==0 && numberOfZombiesToSpawn==0){ //Checks win conditions
				
				gameOver=1; 
			}
		}
		
	}
	public void spawnBlood(JLabel zombieLabel){
		int zombieX = zombieLabel.getX();
		int zombieY = zombieLabel.getY();
		int zombieWidth = zombieLabel.getWidth();
		int zombieHeight = zombieLabel.getHeight();
		int bloodWidth;
		int bloodHeight;
		int bloodType = new Random().nextInt(5);
		ParsedImageIcon bloodImage;
		if(bloodType==0){
			bloodImage = new ParsedImageIcon(".\\ZombieInvasion\\blood1.png");
		}else{
			bloodImage = new ParsedImageIcon(".\\ZombieInvasion\\blood0.png");

		}
		JLabel blood = new JLabel(bloodImage);
		bloodWidth = bloodImage.getIconWidth();
		bloodHeight = bloodImage.getIconHeight();
		blood.setSize(bloodWidth, bloodHeight);
		
		int zombieMidX = zombieX + zombieWidth/2; //Gets center
		int zombieMidY = zombieY + zombieHeight/2; //Gets center
		
		int bloodX = zombieMidX - (bloodWidth/2); //Places blood on center
		int bloodY = zombieMidY - (bloodHeight/2);
		
		blood.setLocation(bloodX, bloodY);
		layerPane.add(blood, new Integer(1));
		bloodLabels.add(blood);
		bloodAlpha.add(255.0);
		blood.setForeground(new Color(255, 255, 255, 255));
		
	}
	public void decay(){
		double bloodDecayFactor=.2*timerInterval;
		
		for(int bloodCounter=0; bloodCounter<bloodLabels.size(); bloodCounter++){
			JLabel bloodLabel = bloodLabels.get(bloodCounter);
			double currentAlpha = bloodAlpha.get(bloodCounter);
			
			double newAlpha = currentAlpha - bloodDecayFactor;
			
			if(newAlpha<=0){ //If the bloodlabel will no longer be visible
				bloodLabels.remove(bloodCounter);
				bloodAlpha.remove(bloodCounter);
				layerPane.remove(bloodLabel);
			}else{
				bloodAlpha.set(bloodCounter, newAlpha);
				((ParsedImageIcon)bloodLabel.getIcon()).setAlpha((int)newAlpha);
			}
			
		}
		for(int bulletLabelCounter=0; bulletLabelCounter<bulletLabels.size(); bulletLabelCounter++){
			JLabel bulletLabel = bulletLabels.get(bulletLabelCounter);
			double currentAlpha = bulletAlpha.get(bulletLabelCounter);
			
			double newAlpha = currentAlpha - bloodDecayFactor;
			
			if(newAlpha<=0){ //If the bloodlabel will no longer be visible
				bulletLabels.remove(bulletLabelCounter);
				bulletAlpha.remove(bulletLabelCounter);
				layerPane.remove(bulletLabel);
			}else{
				bulletAlpha.set(bulletLabelCounter, newAlpha);
				((ParsedImageIcon)bulletLabel.getIcon()).setAlpha((int)newAlpha);
			}
		}
	}
	public void generateWeaponList(){
		weaponList.add(new Weapon(5, Weapon.normalWeapon, 50, 5, 20, new ParsedImageIcon(".\\ZombieInvasion\\sniper.png")));
		weaponList.add(new Weapon(32, Weapon.automaticWeapon, 15, 100, 64, new ParsedImageIcon(".\\ZombieInvasion\\ak47.png")));
		weaponList.add(new Weapon(10, Weapon.normalWeapon, 25, 200, 30, new ParsedImageIcon(".\\ZombieInvasion\\shotgun.png")));
		weaponList.add(new Weapon(100, Weapon.automaticWeapon, 3, 300, 150, new ParsedImageIcon(".\\ZombieInvasion\\chainGun.png")));

	}
	public void setAmmoLabel(int bulletsLeft, int totalAmmoLeft){
		ammoLabel.setText(bulletsLeft  + "/" + (totalAmmoLeft-currentWeapon.getBulletsLeftInClip()));
	}
	public void setWeaponLabel(ParsedImageIcon weaponImage){
		weaponLabel.setIcon(weaponImage);
	}
	public void equipWeapon(Weapon weapon, int weaponId){
		//Reset reload and fire (In case weapon was reloading during weapon switch)
		resetReloadAndFire();
		
		//Archives bullet use
		if(currentWeaponId!=-1 && currentWeapon!=null){
			Weapon currentWeaponInList = weaponList.get(currentWeaponId);
			currentWeaponInList.setBulletsLeftInClip(currentWeapon.getBulletsLeftInClip());
			currentWeaponInList.setTotalBullets(currentWeapon.getTotalBullets());
		}
		//If the new weapon needs to be reloaded
		if(weapon.getBulletsLeftInClip()==0){
			if(weapon.getTotalBullets()<=0){
				outOfAmmo();
			}
			reloadText.setVisible(true);
		}
		Weapon newWeapon =weapon.replicate();
		currentWeapon=newWeapon;
		currentWeaponId=weaponId;
		setAmmoLabel(newWeapon.getBulletsLeftInClip(), newWeapon.getTotalBullets());
		setWeaponLabel(newWeapon.getImage());
		
	}
	public void setWeaponBindings(){
		for(int weaponCounter=0; weaponCounter<weaponList.size(); weaponCounter++){
			final int weaponIndex=weaponCounter;
			AbstractAction equipWeaponAction = new AbstractAction(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					equipWeapon(weaponList.get(weaponIndex), weaponIndex);
					
				}
				
			};
			this.getInputMap().put(KeyStroke.getKeyStroke("" + (weaponCounter+1)), "equipWeapon" + weaponIndex);
	        this.getActionMap().put("equipWeapon" + weaponIndex, equipWeaponAction);
		}

		
	}
	public void resetReloadAndFire(){
		shootingLocation=null;
		automaticFire=-1;
		reloading=-1;
		
		//Because equipweaon is called (which calls this method) is called at the very start of the game, before the reload label is created
		if(reloadText!=null){
			reloadText.setVisible(false);
			reloadText.setForeground(Color.YELLOW);
			reloadText.setText("You need to reload. Press R.");
			reloadText.setSize(reloadText.getPreferredSize());
			reloadText.setLocation((int)(layerPane.getWidth()/2.0- reloadText.getWidth()/2.0), (int)(layerPane.getHeight()/2.0 - reloadText.getHeight()));	
		}

	}
	public void outOfAmmo(){
		reloadText.setText("Weapon out of ammo!");
		reloadText.setForeground(Color.RED);
		reloadText.setSize(reloadText.getPreferredSize());
		reloadText.setLocation((int)(layerPane.getWidth()/2.0- reloadText.getWidth()/2.0), (int)(layerPane.getHeight()/2.0 - reloadText.getHeight()));
	}
	
	//Rotates the zombie from 0 degrees
	public void rotateZombie(JLabel zombieLabel, Velocity velocity){
	
		int velocityX = velocity.getVelocityX();
		int velocityY = -1*velocity.getVelocityY();
		double angle;
		if(velocityX==0){
			if(velocityY>0){
				angle=Math.PI/2.0;
			}else{
				angle=3.0*Math.PI/2.0;
			}
		}else{
			 angle=(Math.atan((double)velocityY/(double)velocityX));
		}
		if(velocityX<0){
			if(velocityY<0){
				angle+=Math.PI;
			}else{
				angle+=Math.PI;
			}
		}
		
		ParsedImageIcon zombieIcon = (ParsedImageIcon)zombieLabel.getIcon();
		Dimension rotatedDimension = zombieIcon.setRotation(angle);
		zombieLabel.setIcon(zombieIcon);
		zombieLabel.setSize(rotatedDimension);
	}
	public void bulletHole(int x, int y){
		int width;
		int height;
		ParsedImageIcon bulletImage = new ParsedImageIcon(".\\ZombieInvasion\\bullet.png");
		width = bulletImage.getIconWidth();
		height = bulletImage.getIconHeight();
		JLabel bulletLabel = new JLabel(bulletImage);
		bulletLabel.setSize(width, height);
		int xPositionOfBullet = x-(int)((double)width/2.0);
		int yPositionOfBullet = y-(int)((double)height/2.0);
		bulletLabel.setLocation(xPositionOfBullet, yPositionOfBullet);
		layerPane.add(bulletLabel, new Integer(6));
		
		bulletLabels.add(bulletLabel);
		bulletAlpha.add(255.0);
		

	}
	public void createWall(){
		int y=0;
		int width=-1;
		int height=-1;
		int sideCounter=0; 

		
		ArrayList <Double> leftWallHealth = new ArrayList();
		ArrayList <Double> rightWallHealth = new ArrayList();

		//Create sideWalls
		while(y<layerPane.getHeight()){
			ParsedImageIcon wallImage = new ParsedImageIcon(".\\ZombieInvasion\\stoneWall.png");
			
			if(y==0){ //Sets the width and height of the walls
				width = wallImage.getIconWidth();
				wallWidth=width;
				height = wallImage.getIconHeight(); 
				wallHeight=height;
			}

			int side = sideCounter%2;
			JLabel wall = new JLabel(wallImage);
			wall.setSize(width, height);
			wall.setLocation(side*(1200-width), y);
			
			
			if(side==1){ //After adding the second side, it goes down another tile
				y+=height;
				rightWall.add(wall); //Reason for multiple wall lists it to minimize the "list" of walls that needs to be checked for collisions
				leftWallHealth.add(255.0);
			}else{
				leftWall.add(wall);
				rightWallHealth.add(255.0);

			}
			sideCounter++;
			
			
			layerPane.add(wall, new Integer(5));
			
		}
		//Adds health arraylists to health arraylist
		wallHealth.add(leftWallHealth); //Left wall health at index 0
		wallHealth.add(rightWallHealth); //Right wall health at index 1
		
		ArrayList<Double> topWallHealth = new ArrayList();
		ArrayList<Double> bottomWallHealth = new ArrayList();
		
		int x=width; //x starts at the width of the image because the left and right walls act "in the corners" as the first parts of the top and bottom walls
		
		sideCounter=0;
		while(x<layerPane.getWidth()-width){ //Same as the above reason
			ParsedImageIcon wallImage = new ParsedImageIcon(".\\ZombieInvasion\\stoneWall.png");

			int side = sideCounter%2;
			JLabel wall = new JLabel(wallImage);
			wall.setSize(width, height);
			wall.setLocation(x, side*leftWall.get(leftWall.size()-1).getY()); //For tiling purposes. This wall will most likely be a "small slither"
			sideCounter++;
			
			aestheticWall.add(wall); //Stores them to destroy them when the game ends
			layerPane.add(wall, new Integer(5));
			
			if(side==1){ //After adding the second side, it goes down another tile
				wall = new JLabel(wallImage);
				wall.setSize(width, height);
				wall.setLocation(x, side*leftWall.get(leftWall.size()-2).getY()); //This is the actual bottom wall
				bottomWall.add(wall); //Reason for multiple wall lists it to minimize the "list" of walls that needs to be checked for collisions
				layerPane.add(wall, new Integer(5));

				x+=width;
				
				bottomWallHealth.add(255.0);

			}else{
				topWall.add(wall);
				topWallHealth.add(255.0);
			}		
		}
		wallHealth.add(topWallHealth); //Top wall health at index 2
		wallHealth.add(bottomWallHealth); //Bottom wall health at index 3
	}
	public int getWallHitIndex(int coordinate, int wallDimension){
		int index = coordinate/wallDimension;
				
		return index;	
	}
	public void decayWall(ArrayList<Double> wallHealth, JLabel wall, int indexOfWall){
		double decayFactor=25;
		
		double wallHealthValue = wallHealth.get(indexOfWall);
		wallHealthValue-=decayFactor;
		if(wallHealthValue<0){
			wallHealthValue=0;
			
			destroyWalls();
			gameOver=-1;
		}
		
		ParsedImageIcon wallIcon = (ParsedImageIcon)wall.getIcon();
		wallIcon.setAlpha((int)wallHealthValue);
		wall.setIcon(wallIcon);
		
		wallHealth.set(indexOfWall, wallHealthValue);
		
		
		
		
	}
	public void destroyWalls(){
		destroyWall(leftWall);
		destroyWall(topWall);
		destroyWall(rightWall);
		destroyWall(bottomWall);
		destroyWall(aestheticWall);

		
	}
	public void destroyWall(ArrayList<JLabel> wall){
		do{
			layerPane.remove(wall.remove(0));
		}while(wall.size()>=1);
	}
	public void gameOver(){
		returnToMenu=true;
		if(gameOver==-1){
			reloadText.setForeground(Color.RED);
			reloadText.setText("You lose. The zombies broke free.");
		}else{
			reloadText.setForeground(Color.GREEN);
			reloadText.setText("The zombie hordes have been eliminated, and you have emerged Victorious!");
			reloadText.setFont(new Font("Lucida Console", Font.BOLD, 25));

		}
		reloadText.setSize(reloadText.getPreferredSize());
		reloadText.setLocation((int)(layerPane.getWidth()/2.0- reloadText.getWidth()/2.0), (int)(layerPane.getHeight()/2.0 - reloadText.getHeight()));	
		reloadText.setVisible(true);
		
		JOptionPane.showMessageDialog(this,reloadText.getText() + "\nReturn to Menu?");
		
			this.setVisible(false);
			window.refresh();
			

	}
	
	

}
