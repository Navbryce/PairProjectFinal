import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;


public class Window {
	public JFrame frame;
	private Snake snake;
	private Hurdler hurdler;
	private ZombieInvasion zombie;
	private static Timer timer;
	private JPanel mainPanel, square;
	private JButton snakeButton, hurdlerButton, zombieButton;
	private ParsedImageIcon background;
	private JLabel title, desc;
	private int level = 0;
	private int iconHeight = 250, iconWidth = 550;
	
	public Window() {
		frame = new JFrame();
		frame.setBackground(Color.black);
		

		//background = new ParsedImageIcon(".\\Home\\Background.png");
		//background.setHeight(700);
		//background.setWidth(1200);
		
		mainPanel = new JPanel() {
			//public void paint(Graphics g) {
			     // paint the background image and scale it to fill the entire space
			 	//g.drawImage((Image)background.getImage(), 0, 0, null);
			//}
		};
		mainPanel.setBackground(Color.black);
		mainPanel.setPreferredSize(new Dimension(1200,720));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		mainPanel.setLayout(new GridBagLayout());
		
		//mainPanel.setBackground(Color.white);
		

		/*square = new JPanel();
		square.setBorder(BorderFactory.createEmptyBorder(25,25,0,0));
		square.setLayout(new GridBagLayout());
		square.setBackground(Color.red);
		square.setPreferredSize(new Dimension(700,700));
		*/
		
		GridBagConstraints gbc = new GridBagConstraints();

		title = new JLabel("Arcade Games");
		title.setForeground(Color.white);
		title.setBackground(Color.black);
		title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		title.setFont(new Font("Courier", Font.PLAIN, 30));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		mainPanel.add(title, gbc);
		
		desc = new JLabel("Survive each minigame! Select your level below: ");
		desc.setForeground(Color.white);
		desc.setBackground(Color.black);
		desc.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		desc.setFont(new Font("Courier", Font.PLAIN, 20));
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		mainPanel.add(desc, gbc);
		
		String[] levels = {"Easy", "Medium", "Hard", "Extreme"};
		JComboBox levelList = new JComboBox(levels);
		levelList.setBackground(Color.black);
		levelList.setForeground(Color.white);
		levelList.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		levelList.setSelectedIndex(0);
		levelList.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		levelList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				// TODO Auto-generated method stub
				level = ((JComboBox) ev.getSource()).getSelectedIndex();
				//select level #
				//System.out.println(level);
			}
			
		});
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		mainPanel.add(levelList, gbc);
		
		ParsedImageIcon snakeIcon = new ParsedImageIcon(".\\Snake\\snake.jpg");
		snakeIcon.setWidth(iconWidth);
		snakeIcon.setHeight(iconHeight);
		
		snakeButton = new JButton(snakeIcon);
		snakeButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		snakeButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				runSnake(level);			
				snake.requestDefaultFocus();
				//snake.requestFocus();
				//snake.requestFocusInWindow();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		mainPanel.add(snakeButton,gbc);
		
		ParsedImageIcon hurdlerIcon = new ParsedImageIcon(".\\Hurdler\\cover.png");
		hurdlerIcon.setWidth(iconWidth);
		hurdlerIcon.setHeight(iconHeight);
		
		hurdlerButton = new JButton(hurdlerIcon);
		hurdlerButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		hurdlerButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				//System.out.println("hurdle");
				runHurdler(level);			
				hurdler.requestDefaultFocus();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		mainPanel.add(hurdlerButton,gbc);
		
		
		ParsedImageIcon zombieIcon = new ParsedImageIcon(".\\ZombieInvasion\\cover.png");
		zombieIcon.setWidth(iconWidth);
		zombieIcon.setHeight(iconHeight);
		
		zombieButton = new JButton(zombieIcon);
		zombieButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		zombieButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				//System.out.println("zombie");
				runZombie(level);			
				zombie.requestDefaultFocus();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				mainPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		mainPanel.add(zombieButton,gbc);
		
		
		frame.setTitle("ARCADE");
		frame.setContentPane(mainPanel);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void runSnake(int level) {
		snake = new Snake(this, level);
		//snake.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
		//snake.setLayout(new GridBagLayout());
		frame.setContentPane(snake);
		
		snake.repaint();
		frame.pack();
		snake.run();
		
		
	}
	
	public void refresh() {
		frame.setContentPane(mainPanel);
		mainPanel.requestDefaultFocus();
	}
	
	private void runHurdler(int level) {
		hurdler = new Hurdler(this, level); //level input
		frame.setContentPane(hurdler);

		hurdler.repaint();
		frame.pack();
		hurdler.run();
	}
	
	private void runZombie(int level) {
		zombie = new ZombieInvasion(this, level); //level input
		frame.setContentPane(zombie);

		zombie.repaint();
		frame.pack();
		zombie.run();
	}
	
	

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Window();
			}
		});
	}
	
}
