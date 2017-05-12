
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ParsedImageIcon extends ImageIcon{
	
	private String filePath;
	private Image unrotatedImage;

	
	ParsedImageIcon(String pathOfFile, int width, int height){
		filePath=pathOfFile;
		Image image;
		try {
			image = ImageIO.read(new File(filePath));
			image=image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			unrotatedImage=image;

			this.setImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Image file could not be found");
		}
		
	}
	ParsedImageIcon(String pathOfFile){
		filePath=pathOfFile;
		try {
			URL fileURL =new File(pathOfFile).toURI().toURL(); //this.... says to look in the src folder than down the path
			ImageIcon image = new ImageIcon(fileURL);
			unrotatedImage=image.getImage();

			this.setImage(image.getImage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Image file could not be found:" + pathOfFile);
			try{
				this.setImage(ImageIO.read(new File(".\\Icons\\noImage.jpg")));
			}catch(Exception ex){
				System.out.println("Failed to load back-up image.");
			}
		}
		
	}
	public void setWidth(int width){
		Image image = this.getImage();
		image=image.getScaledInstance(width, this.getIconHeight(), Image.SCALE_SMOOTH);
		this.setImage(image);
		unrotatedImage=image;

	}
	public void setHeight(int height){
		Image image = this.getImage();
		image=image.getScaledInstance(this.getIconWidth(), height, Image.SCALE_SMOOTH);
		this.setImage(image);
		unrotatedImage=image;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void fitImage(int maxWidth, int maxHeight){
		double originalWidth = this.getIconWidth();
		double originalHeight = this.getIconHeight();
		double widthRatio = (originalWidth / originalHeight);
		double heightRatio = (originalHeight / originalWidth);
		int newWidth = (int)(maxHeight * widthRatio);
		int newHeight = (int)(maxWidth * heightRatio);
		if (newHeight >= maxHeight){
			newHeight=maxHeight;
		}
		else{
			newWidth = maxWidth;
		}
		setWidth(newWidth);
		setHeight(newHeight);
	}
	public void setAlpha(int newAlpha){
		Image image = this.getImage();
		int width = this.getIconWidth();
		int height = this.getIconHeight();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		for(int xPixel=0; xPixel<width; xPixel++){
			for(int yPixel=0; yPixel<height; yPixel++){
				Color color = new Color(bufferedImage.getRGB(xPixel, yPixel), true); //getRGB returns series of numbers representative  of a color. New Color converts it into RGB format
				int r = color.getRed();
				int g = color.getGreen();
				int b = color.getBlue();
				int a = color.getAlpha();
				if(a!=0){
					a=newAlpha;
				}
				color = new Color(r, g, b, a);
				bufferedImage.setRGB(xPixel, yPixel, color.getRGB());
			}
		}
		this.setImage(bufferedImage);
	}
	public Dimension setRotation(double angle){
		angle=2.0*Math.PI-angle;
		Image image = unrotatedImage;
		ImageIcon unrotatedIcon = new ImageIcon(image);

		
		int width = unrotatedIcon.getIconWidth();
		int height = unrotatedIcon.getIconHeight();

		
		//Creates a rotated buffered image with rotated versions of imageicon dimensions. Then paints the image icon on it.
	    double sin = Math.abs(Math.sin(angle)); 
	    double cos = Math.abs(Math.cos(angle));
	    int rotatedWidth = (int)(width*cos+height*sin);
	    int rotatedHeight = (int)(height * cos + width * sin);
	    BufferedImage rotatedBuffer = new BufferedImage(rotatedWidth, rotatedHeight, Transparency.TRANSLUCENT);
	    Graphics2D g = rotatedBuffer.createGraphics();
	    g.translate((rotatedWidth - width) / 2, (rotatedHeight - height) / 2);
	    g.rotate(angle, width / 2, height / 2);
	    g.drawImage(image, 0, 0, null); //Draws rotated image on rotated bufferedImage
	    g.dispose();
	    
	    this.setImage(rotatedBuffer);
	    return new Dimension(rotatedWidth, rotatedHeight);
		
		
	}
}
