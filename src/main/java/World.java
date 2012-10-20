import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.LinkedList;

public class World {
	public static final int CLEAN = 0;
	public static final int DIRTY = 1;
	public static final int WALL = 2;
	public static final int AGENT =3;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;	
	
	public static final String FW = "FW";
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String SUCK = "SUCK";
	
	private boolean randomMap;
	
	public static Random r = new Random(new Date().getTime());
	
	private long WAIT = 1000;
	private int PERCEPTION = 0; 
		
	public Present presenter;
	
	public int net[][];
	public boolean v[][]; // visited?
	public int anet[][];
	public int ax, ay;
	private int h;
	private int w;
	
	private Map xcoor = new HashMap();
	private Map ycoor = new HashMap();
	private Map orient = new HashMap();
	private List agents= new ArrayList();
	
	boolean save = false;
	
	public World(String fileName, long wait, int perception) {
		load(fileName);
		this.WAIT=wait;
		this.PERCEPTION=perception;
	}
	
	private void load(String fileName) {
		//random xy;
		try {
		
			///////////////////////////////////////////
		URL url = GuiStarter.class.getResource(fileName);
		if (url == null) {
			throw new IllegalArgumentException("Subor " + fileName +
					" nebol najdeny.");
		}
		File f = new File(url.toURI());
			///////////////////////////////////////////
		
		
			BufferedReader r = new BufferedReader(new FileReader(f));
			String s=r.readLine();
			if (randomMap = s.equals("random")){
				s=r.readLine();
				h=Integer.parseInt(s);
				s=r.readLine();
				w=Integer.parseInt(s);				
				s=r.readLine();
				double di=Double.parseDouble(s);				
				s=r.readLine();
				double wa=Double.parseDouble(s);				

				v = new boolean[w][h];				
				net = new int[w][h];
				anet = new int[w][h];
				untidy(di,wa);
				
				Random rand = new Random(System.currentTimeMillis());
				
				ax =rand.nextInt(getWidth()-1)+1;
				ay =rand.nextInt(getHeight()-1)+1;
				while (!freePlace(ax, ay)){
					ax =rand.nextInt(getWidth()-1)+1;
					ay =rand.nextInt(getHeight()-1)+1;
				}
			}else{
				s=r.readLine();
				h=Integer.parseInt(s);
				s=r.readLine();
				w=Integer.parseInt(s);
				net = new int[w][h];
				v = new boolean[w][h];
				anet = new int[w][h];
				for (int i = 0; i < w; i++) {
					s=r.readLine();
					for (int j = 0; j < s.length(); j++) {
						v[i][j] = false;
						switch (s.charAt(j)) {
						case '#':
							net[i][j]=WALL;	
							break;
						case '*':								
							net[i][j]=DIRTY;
							break;
						case '@':								
							ax = j;
							ay = i;
							break;
						default:
							net[i][j]=CLEAN;							
						}
						
					}
				}
			}
			r.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	public void addAgent(Agent agent){
		
		if (!agents.contains(agent)&& freePlace(ax, ay)) {
				agent.putInWorld(this);
				agents.add(agent);
				xcoor.put(agent,ax);
				ycoor.put(agent,ay);
				orient.put(agent,randomMap?new Integer(r.nextInt(4)):NORTH);
			    anet[ay][ax]=AGENT;
		}
	}
	
	public boolean freePlace(int x,int y){
		if (x>=h || y>=w || x<0 || y<0) return false;
		if (net[y][x]==WALL || anet[y][x]==AGENT) return false;
		return true;
		
	}
	
	public Result run(long timeout){
		Result result = new Result();
		result.state = Result.State.HALTED;
		result.runtimeMillis = 0;
		result.correctSolution = false;
		
		if (presenter!=null)
			presenter.redraw();

		Agent element = (Agent) agents.get(0);
		System.out.println(element.isRunning());

		try {
			do{
				if (presenter!=null){
					presenter.redraw();
					Thread.sleep(WAIT);
				}

				final long s = element.getSteps();
				final long p = element.getPercepted();

				final long startTime = System.currentTimeMillis();
				element.act();
				System.out.println("HHH");
				result.runtimeMillis += System.currentTimeMillis() - startTime;
				
				if (timeout > 0 && result.runtimeMillis > timeout) {
					result.state = Result.State.TIMEOUT;
					break;
				}

				if (element.getSteps() - s > 1){
					throw new Exception("Agent performed more than one action in act.");								
				}else if (element.getPercepted() - p > 1){
					throw new Exception("Agent performed more than one perception in act.");
				}
				System.out.println(element.isRunning());
			} while(element.isRunning());
		} catch (Exception e) {
			result.state = Result.State.EXCEPTION;
			e.printStackTrace();
		}
		
		result.steps = element.getSteps();
		if (Result.State.HALTED.equals(result.state)) {
			result.correctSolution = checkSolution();
		}

		save = true; 

		if (presenter!=null){
			presenter.redraw();
		}

		return result;
	}
	
	private boolean checkSolution() {
		boolean[][] added = new boolean[getHeight()][getWidth()];
		
		LinkedList<Point> open = new LinkedList<Point>();		
		open.add(new Point(ax, ay));
		added[ay][ax] = true;
		
		int i = 5;
		
		Point currentState;
		
		do{
			currentState = open.removeFirst();			
			if (badTile(currentState)){
				return false;
			}
			
			int y = currentState.y;
			int x = currentState.x;						
			
			// move NORTH, if possible
			if (!added[y-1][x] && net[y-1][x] != WALL){
				added[y-1][x] = true;
				open.add(new Point(x, y-1));
			}
			// move SOUTH, if possible
			if (!added[y+1][x] && net[y+1][x] != WALL){
				added[y+1][x] = true;				
				open.add(new Point(x, y+1));
			}
			// move EAST, if possible
			if (!added[y][x+1] && net[y][x+1] != WALL){
				added[y][x+1] = true;				
				open.add(new Point(x+1, y));
			}
			// move WEST, if possible
			if (!added[y][x-1] && net[y][x-1] != WALL){
				added[y][x-1] = true;				
				open.add(new Point(x-1, y));
			}			
		}while(!open.isEmpty());		

		return true;
	}

	private boolean badTile(Point currentState){
		int y = currentState.y;
		int x = currentState.x;
		
			// unseen reachable tile or uncleaned reachable dirty
		return !v[y][x] || net[y][x] == DIRTY;
	}

	private void untidy(double di, double wa){
		for (int i=0;i<w;i++)
			for (int j=0;j<h;j++){
				double d = r.nextDouble();
				if (d<di){
					net[i][j]=DIRTY;
					anet[i][j]=CLEAN;
				}else if (d<di+wa){					
					net[i][j]=WALL;
					anet[i][j]=CLEAN;
				}
				if (i==0 || j==0 || i==w-1 || j==h-1){
					net[i][j]=WALL;
				}
			}
	}
	
	private int getX(Agent agent){
		if (xcoor.containsKey(agent)){
			return ((Integer)xcoor.get(agent)).intValue();
		}
		return -1;
	}
	
	private int getY(Agent agent){
		if (ycoor.containsKey(agent)){
			return ((Integer)ycoor.get(agent)).intValue();
		}
		return -1;
	}
	
	public int getO(Agent agent){
		if (orient.containsKey(agent)){
			return ((Integer)orient.get(agent)).intValue();
		}
		return -1;
	}
	
	public void moveFW(Agent agent){
		int p = perceptFW(agent);
		//System.out.println("FW = "+p);
		int o=getO(agent);
		//System.out.println("O = "+o);
		if (p!=WALL && p!=AGENT){
			anet[getY(agent)][getX(agent)]=CLEAN;
			switch (o) {
			case NORTH:
				ycoor.put(agent,new Integer(getY(agent)-1));	
				break;
			case SOUTH:
				ycoor.put(agent,new Integer(getY(agent)+1));	
				break;
			case EAST:
				xcoor.put(agent,new Integer(getX(agent)+1));	
				break;
			case WEST:
				xcoor.put(agent,new Integer(getX(agent)-1));	
				break;

			default:
				break;
			}
			
			anet[getY(agent)][getX(agent)]=AGENT;
			
			ax = getX(agent);
			ay = getY(agent);			
			
		}
		
	}
	
	
	
	
	public void turnRIGHT(Agent agent){
		int o = getO(agent);
		orient.put(agent, new Integer((o+1)%4));
	}

	public void turnLEFT(Agent agent){
		int o = getO(agent);
		orient.put(agent, new Integer((o+3)%4));
	}

	public void suck(Agent agent){
		int x = getX(agent);
		int y = getY(agent);
			net[y][x] = CLEAN;
	}
	
	public int[][] percept(Agent agent){
		int[][] result = new int[1+2*PERCEPTION][1+2*PERCEPTION];		
		int x1=getX(agent);
		int y1=getY(agent);
		int x2=x1-PERCEPTION;
		int y2=y1-PERCEPTION;
		
		ax = x1;
		ay = y1;
		
		for (int i = 0; i < net.length; i++) {
			for (int j = 0; j < net[i].length; j++) {
				if ((y1 - PERCEPTION <= i) && (i <= y1 + PERCEPTION) && (x1 - PERCEPTION <= j) && (j <= x1 + PERCEPTION)) {
					v[i][j] = true;
				}
			}
		}
		
//		int o=getO(agent);
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				int x=x2+j;
				int y=y2+i;
				if (x<0||y<0||x>=h||y>=w) {
					result[i][j]=WALL;	
				}
					/*
					switch (o) {
					case NORTH:
							result[i][j]=WALL;	
						break;
					case EAST:
						result[result[i].length-1-j][i]=WALL;	
					break;
					case SOUTH:
						result[result.length-1-i][result[i].length-1-j]=WALL;	
					break;
					case WEST:
						result[j][result.length-1-i]=WALL;	
					break;
					}
					*/					
				else {
					int r=0;
					if (net[y][x] == DIRTY) {
						r = DIRTY;
					}
					else if (anet[y][x]==AGENT) {
						r=AGENT;
					} else {
						r=net[y][x];
					}
					result[i][j]=r;	
					/*
					switch (o) {
					case NORTH:
							result[i][j]=r;	
						break;
					case EAST:
						result[result[i].length-1-j][i]=r;	
					break;
					case SOUTH:
						result[result.length-1-i][result[i].length-1-j]=r;	
					break;
					case WEST:
						result[j][result.length-1-i]=r;	
					break;
					}
					*/		
				}
			}	
		}
		/*
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				System.out.print(result[i][j]);
			}
			System.out.println();
		} 
		System.out.println();  */
		return result;
	}

	
	public int perceptFW(Agent agent){
		int o = getO(agent);
		int x = getX(agent);
		int y = getY(agent);
		if (x>=0 && x<h && y>=0 && y<w){			
			if (o==NORTH){
				if (y>0){
					if (anet[y-1][x]==AGENT) return AGENT;
					else return net[y-1][x];
				}
				return WALL;
			}
			if (o==EAST){
				if (x<h-1){
					if (anet[y][x+1]==AGENT) return AGENT;
					else return net[y][x+1];
				}
				return WALL;
			}
			if (o==SOUTH){
				if (y<w-1){
					if (anet[y+1][x]==AGENT) return AGENT;
					else return net[y+1][x];
				}
				return WALL;
			}
			if (o==WEST){
				if (x>0){
					if (anet[y][x-1]==AGENT) return AGENT;
					else return net[y][x-1];
				}
				return WALL;
			}
		}
		return WALL;
	}
	
	
	public String print(){
		char s[][] = new char[w][h];
		for( int i=0;i<w;i++)
			for (int j=0;j<h;j++){
        if (anet[i][j]==AGENT){
          s[i][j]='0';
        }
        else{
  				switch (net[i][j]) {
  				case DIRTY:
  					s[i][j]='*';
  					break;
  				case CLEAN:
  					s[i][j]='_';
  					break;
  				case WALL:
  					s[i][j]='#';
  					break;
  				default:
  					break;
  				}
        }        
			}
		for (Iterator it = agents.iterator(); it.hasNext();) {
			Agent element = (Agent) it.next();
			int x = getX(element);
			int y = getY(element);
			s[y][x]='@';			
		}
		StringBuffer res = new StringBuffer(" ");
		for (int i=0;i<w;i++){
			res.append(s[i]).append("\n ");
		}
		//res.append(lastAction).append("\n");
		return res.toString();
	}
	
	public int getWidth(){
		return h;
	}
	
	public int getHeight(){
		return w;
	}
	
	public int getPerception(){
		return PERCEPTION;
	}
	
	
	public static class Result {
		public State state;
		public boolean correctSolution;
		public long runtimeMillis;
		public long steps;
		
		public static enum State {
			EXCEPTION, TIMEOUT, HALTED;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder("Result{");
			sb.append("state=").append(state);
			sb.append(", ");
			sb.append("correctSolution=").append(correctSolution);
			sb.append(", ");
			sb.append("runtimeMillis=").append(runtimeMillis);
			sb.append(", ");
			sb.append("steps=").append(steps);
			sb.append("}");
			return sb.toString();
		}
	}
}