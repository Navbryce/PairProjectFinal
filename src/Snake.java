import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.*;


public class Snake extends Game implements ActionListener{
	private Window window;
	//private JFrame frame;
	private JPanel mainPanel;
	private JGridPanel grid;
	private JLabel score;
	private ArrayList<Coordinate> snake;
	private ArrayList<String> dirs;
	private Coordinate food;
	private String dir;
	//1200x720
	private int cellSide;
	private int gridWidth;
	private int gridHeight;
	public Timer timer;
	private int speed;
	private boolean dead = false;
	private int lvl;
	
	public Snake(Window wind, int level) {
		window = wind;
		//frame = new JFrame();
		lvl = level;
		if(level==0) {
			speed=200;
			cellSide=35;
			gridWidth=34;
			gridHeight=18;
		} else if(level==1) {
			speed=150;
			cellSide=30;
			gridWidth=39;
			gridHeight=23;
		} else if(level==2) {
			speed=100;
			cellSide=25;
			gridWidth=47;
			gridHeight=27;
		} else if(level==3) {
			speed=50;
			cellSide=20;
			gridWidth=59;
			gridHeight=25;
		}
		
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		mainPanel.setPreferredSize(new Dimension(1200,720));
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Color.white);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		score = new JLabel("length: 0");
		score.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(score, gbc);
		
		grid = new JGridPanel();
		gbc.gridx = 0;
		gbc.gridy = 1;
		grid.setPreferredSize(new Dimension(gridWidth*cellSide+1, gridHeight*cellSide+1));
		grid.setBackground(Color.white);
		
		
		generateFood();
		
		int xVal = 0, yVal = 0;
		xVal = (int)(Math.random()*(gridWidth-1))+1;
		yVal = (int)(Math.random()*gridHeight);
		snake = new ArrayList<Coordinate>();
		snake.add(new Coordinate(xVal, yVal));
		
		//dir = "left";
		AbstractAction left = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("left");
				dir = "left";
			}
		};
		
		AbstractAction right = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("right");
				dir = "right";
			}
		};
		
		AbstractAction up = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("up");
				dir = "up";
			}
		};
		
		AbstractAction down = new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("down");
				dir = "down";
			}
		};
		
		
		grid.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		grid.getActionMap().put("left", left);
		
		grid.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		grid.getActionMap().put("right", right);
		
		grid.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		grid.getActionMap().put("up", up);
		
		grid.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		grid.getActionMap().put("down", down);
		
		
		mainPanel.add(grid, gbc);
		
		
		
		this.add(mainPanel);
		/*frame.setTitle("Snake");
		frame.setContentPane(mainPanel);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		*/
		
		
	}
	
	
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("action");
		if(dir == null) {
			
		} else if(dir.equals("left")) {
			if(snake.get(0).getX() > 0) {
				Coordinate next = new Coordinate(snake.get(0).getX()-1, snake.get(0).getY());
				//eat self death
				for(Coordinate coord : snake) {
					if(coord.equals(next)) {
						death();
					}
				}
				//not dead, grow!
				if(!dead) {
					snake.add(0, next);
					if(!snake.get(0).equals(food)) { //eat food
						snake.remove(snake.get(snake.size()-1));
					} else {
						generateFood();
					}
					grid.repaint();
				}
				
			} else { //outside of grid death
				death();
				
			}
		} else if(dir.equals("right")) {
			if(snake.get(0).getX() < gridWidth-1) {
				Coordinate next = new Coordinate(snake.get(0).getX()+1, snake.get(0).getY());
				//eat self death
				for(Coordinate coord : snake) {
					if(coord.equals(next)) {
						death();
					}
				}
				//not dead, grow!
				if(!dead) {
					snake.add(0, next);
					if(!snake.get(0).equals(food)) { //eat food
						snake.remove(snake.get(snake.size()-1));
					} else {
						generateFood();
					}
					grid.repaint();
				}
			} else {
				death();
			}
		} else if(dir.equals("up")) {
			if(snake.get(0).getY() > 0) {
				Coordinate next = new Coordinate(snake.get(0).getX(), snake.get(0).getY()-1);
				//eat self death
				for(Coordinate coord : snake) {
					if(coord.equals(next)) {
						death();
					}
				}
				//not dead, grow!
				if(!dead) {
					snake.add(0, next);
					if(!snake.get(0).equals(food)) { //eat food
						snake.remove(snake.get(snake.size()-1));
					} else {
						generateFood();
					}
					grid.repaint();
				}
			} else {
				death();
			}
		} else if(dir.equals("down")) {
			if(snake.get(0).getY() < gridHeight-1) {
				Coordinate next = new Coordinate(snake.get(0).getX(), snake.get(0).getY()+1);
				//eat self death
				for(Coordinate coord : snake) {
					if(coord.equals(next)) {
						death();
					}
				}
				//not dead, grow!
				if(!dead) {
					snake.add(0, next);
					if(!snake.get(0).equals(food)) { //eat food
						snake.remove(snake.get(snake.size()-1));
					} else {
						generateFood();
					}
					grid.repaint();
				}
			} else {
				death();
			}
		}
		
	}
	
	private void death() {
		score.setText("GAME OVER. Final Length: " + snake.size());
		timer.stop();
		dead = true;	
		//System.out.println(lvl);
		//System.out.println("Score: " + snake.size()*(lvl+1) );
		JOptionPane.showMessageDialog(this,
				"GAME OVER.\nFinal Score: " + snake.size()*(lvl+1) + "\nReturn to main menu.");
		this.setVisible(false);
		window.refresh();
		
		
	}
	
	private void reset() {
		snake.clear();
		int xVal = 0, yVal = 0;
		xVal = (int)(Math.random()*(gridWidth-1))+1;
		yVal = (int)(Math.random()*gridHeight);
		snake = new ArrayList<Coordinate>();
		snake.add(new Coordinate(xVal, yVal));
		
		generateFood();

		repaint();
	}
	
	public boolean run() {
		reset();
		
		timer = new Timer(speed, this);
		timer.setInitialDelay(500);
		timer.start();
		
		return false;
	}
	
	private void generateFood() {
		//System.out.println("generate food");
		//check where snake is
		int xVal = 0, yVal = 0;
		
		boolean clean = false;
		while(!clean) {
			xVal = (int)(Math.random()*gridWidth);
			yVal = (int)(Math.random()*gridHeight);
			
			food = new Coordinate(xVal, yVal);
			
			if(snake!=null) {
				clean = true;
				for(Coordinate snkPt : snake) {
					if(snkPt.equals(food)) {
						clean = false;
					}
				}
			} else {
				clean = true;
			}
			
		}
		//System.out.println(new Coordinate(xVal, yVal));
		
		//grid.repaint();
		//grid.makeFood(food);
	}

	private class JGridPanel extends JPanel {
		
		public JGridPanel(){}


		public void paint(Graphics g) {			
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);
					
			for(int i = 0; i <= getHeight(); i+=cellSide) { //horizontal
				g2.setColor(Color.black);
				g2.draw(new Line2D.Double(0, i, getWidth(), i));
			}
					
			for(int j = 0; j <= getWidth(); j += cellSide) { //vertical
				g2.setColor(Color.black);
				g2.draw(new Line2D.Double(j, 0, j, getHeight()));
			}
			
			if(food != null) {
				g2.setColor(Color.red);
				Rectangle foodBox = new Rectangle(food.getX()*cellSide, food.getY()*cellSide, cellSide, cellSide);
				g2.draw(foodBox);
				g2.fill(foodBox);
			}
			
			makeSnake(g2);
		}
		
		public void makeSnake(Graphics2D g2) {
			if(snake==null) {
				//assign random
				int xVal = 0, yVal = 0;
				xVal = (int)(Math.random()*gridWidth);
				yVal = (int)(Math.random()*gridHeight);
				snake = new ArrayList<Coordinate>();
				snake.add(new Coordinate(xVal, yVal));
			}
			//draw the snake
			for(Coordinate box : snake) {
				Rectangle snakeBox = new Rectangle(box.getX()*cellSide, box.getY()*cellSide, cellSide, cellSide);

				g2.setColor(Color.blue);
				g2.fill(snakeBox);
				g2.setColor(Color.white);
				g2.draw(snakeBox);
			}
			
			if(!dead) {
				score.setText("length: " + snake.size());
			} else {
				score.setText("GAME OVER. Final Length: " + snake.size());
			}
			
		}
		
			
	}

}



