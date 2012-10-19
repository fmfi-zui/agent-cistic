
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GuiStarter extends Frame implements Present{
	private World world;	
	private static Random r = new Random(System.currentTimeMillis());
	private JPanel area;
	private Button b;	
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
	
        private static void printUsage() {
            System.err.println("GuiStarter world.txt <WAIT-TIME-MSEC> <PERCEPT-SIZE>");
        }
        
	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		if (args.length < 3) {
                    System.err.println("Nedostatok argumentov");
                    printUsage();
                    return;
                }
		
		GuiStarter s = new GuiStarter();
		
		String file = args[0];
		long w = Long.parseLong(args[1]);
		int p = Integer.parseInt(args[2]);		
		s.world = new World(file,w,p);
		int x =r.nextInt(s.world.getWidth()-1)+1;
		int y =r.nextInt(s.world.getHeight()-1)+1;
		while (!s.world.freePlace(x, y)){
			x =r.nextInt(s.world.getWidth()-1)+1;
			y =r.nextInt(s.world.getHeight()-1)+1;
		}

		MyAgent agent = new MyAgent(s.world.net.length, s.world.net[0].length);
		s.world.addAgent(agent, x, y);											

		s.world.presenter = s;		
		s.init();
		
		s.setVisible(true);
	}

	public void redraw(){
		area.repaint();
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

	public void init(){
		  //this.setTitle("PLEASE WAIT!");
		  b = new Button("Run");
		  setSize((world.net[0].length+1)*20,(world.net.length+3)*20);					  	  
		  //  repaint();		  			  		  
	      Panel p;
	      p = new Panel();
	      p.setSize((world.net.length+1)*20,(world.net[0].length+1)*20+30);
	      p.setLayout(new BorderLayout());	      
	      p.add(BorderLayout.NORTH,b);
	      b.addActionListener(new ActionListener(){
			synchronized public void actionPerformed(ActionEvent arg0) {
				b.setEnabled(false);
				world.run();				
			}
	      });

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
		
		area = new JPanel(){
			public void paint(Graphics g) {
				BufferedImage bi = null;
				boolean monitor = false;
				boolean ok = false;
				
				if (world.save){
					bi = new BufferedImage(world.getWidth()*20, world.getHeight()*20, BufferedImage.TYPE_INT_RGB);
					g = bi.createGraphics();					
					monitor = true;
				}
				
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

				ok = true;
				if (world.save){
					try{
						if (!monitor || !ok){
							return;
						}

						ImageIO.write(bi,"png", new File("screen.png"));
						world.save = false;

						System.out.println("screen successfully created");
					//	System.exit(0);
					}catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		};
	      
	      p.add(BorderLayout.CENTER,area);
	      add(p);
	      addWindowListener(new WindowAdapter(){
	    	 public void windowClosing(WindowEvent arg0) {
	    		System.exit(0);
	    	
	    	} 
	      });
	}

}