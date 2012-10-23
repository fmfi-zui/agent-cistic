
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Vykresluje svet
 *
 */
public class WorldRenderer {

	private World world;

	private Image img_wall = null;
	private Image img_wall2 = null;
	private Image img_wall3 = null;
	private Image img_clean = null;
	private Image img_clean2 = null;
	private Image img_clean3 = null;
	private Image img_dirt = null;
	private Image img_dirt2 = null;
	private Image img_dirt3 = null;
	private Image img_agent = null;

	public WorldRenderer(World world) {
		this.world = world;
		
		img_wall = createImage("img/img_wall.jpg");
		img_wall2 = createImage("img/img_wall2.jpg");
		img_wall3 = createImage("img/img_wall3.jpg");
		img_clean = createImage("img/img_clean.jpg");
		img_clean2 = createImage("img/img_clean2.jpg");
		img_clean3 = createImage("img/img_clean3.jpg");
		img_dirt = createImage("img/img_dirt.jpg");
		img_dirt2 = createImage("img/img_dirt2.jpg");
		img_dirt3 = createImage("img/img_dirt3.jpg");
		img_agent = createImage("img/img_agent.jpg");
	}

	private BufferedImage createImage(String resource) {
		try {
			return ImageIO.read(GuiStarter.class.getResource(resource));
		} catch (IOException ex) {
			Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}
		return null;
	}
	
	public Dimension getImageSize() {
		return new Dimension(world.getWidth() * 20, world.getHeight() * 20);
	}

	public void render(Graphics g) {
		for (int i = 0; i < world.net.length; i++) {
			for (int j = 0; j < world.net[i].length; j++) {
				if (world.net[i][j] == World.WALL){

					if(i >= world.ay - world.getPerception() && i <= world.ay + world.getPerception() && j >= world.ax - world.getPerception() && j<= world.ax + world.getPerception()){
						g.drawImage(img_wall,20*j,20*i,null);
					} else if (world.v[i][j]){
						g.drawImage(img_wall2,20*j,20*i,null);
					} else{
						g.drawImage(img_wall3,20*j,20*i,null);
					}
				}
				if (world.net[i][j] == World.CLEAN){
					if(i >= world.ay - world.getPerception() && i <= world.ay + world.getPerception() && j >= world.ax - world.getPerception() && j<= world.ax + world.getPerception()){
						g.drawImage(img_clean,20*j,20*i,null);
					} else if (world.v[i][j]){
						g.drawImage(img_clean2,20*j,20*i,null);
					} else{
						g.drawImage(img_clean3,20*j,20*i,null);
					}
				}
				if (world.net[i][j] == World.DIRTY){
					if(i >= world.ay - world.getPerception() && i <= world.ay + world.getPerception() && j >= world.ax - world.getPerception() && j<= world.ax + world.getPerception()){
						g.drawImage(img_dirt,20*j,20*i,null);
					} else if (world.v[i][j]){
						g.drawImage(img_dirt2,20*j,20*i,null);
					} else{
						g.drawImage(img_dirt3,20*j,20*i,null);
					}
				}
				if (world.anet[i][j] == World.AGENT) g.drawImage(img_agent,20*j,20*i,null);
			}
		}

		g.setColor(Color.RED);
		g.drawRect(20*(world.ax - world.getPerception()), 20*(world.ay - world.getPerception()), 20*(1+2*world.getPerception()), 20*(1+2*world.getPerception()));
	}
	
	public void saveImage(File file) throws IOException {
		Dimension size = getImageSize();
		BufferedImage bi = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		render(g);
		ImageIO.write(bi, "png", file);
	}

}
