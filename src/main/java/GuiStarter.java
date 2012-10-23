
import java.awt.*;
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
	private WorldRenderer renderer;
	private static Random r = new Random(System.currentTimeMillis());
	private JPanel area;
	private Button b;	
	
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
		
		String file = args[0];
		long w = Long.parseLong(args[1]);
		int p = Integer.parseInt(args[2]);		
		World world = new World(file,w,p);

		MyAgent agent = new MyAgent(world.net.length, world.net[0].length);
		world.addAgent(agent);
		
		GuiStarter s = new GuiStarter(world);
		world.presenter = s;
		
		s.setVisible(true);
	}

	public void redraw(){
		area.repaint();
	}
        
        

	public GuiStarter(World w){
		this.world = w;
		this.renderer = new WorldRenderer(world);
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
				Thread t = new Thread(new Runnable() {

					public void run() {
						World.Result result = world.run(0);
						System.out.println(result.steps + " steps");
						if (World.Result.State.EXCEPTION.equals(result.state)) {
							System.exit(1);
						}
						
						try {
							renderer.saveImage(new File("screen.png"));
							System.out.println("screen successfully created");
						} catch (IOException ex) {
							Logger.getLogger(GuiStarter.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					
				});
				t.start();
			}
	      });

		
		
		area = new JPanel(){
			public void paint(Graphics g) {
				renderer.render(g);
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