
import java.io.*;
import java.util.Scanner;

public class Tester{
private World world;	
	
	public static void main(String[] args) throws Throwable {			
		if (args.length == 0){
			System.out.println("argument for student name needed");
			return;
		}
		
		String testCases = "testCases4.txt";
		InputStream inputStream = Tester.class.getResourceAsStream(testCases);
		
		if (inputStream == null){
			System.out.println("File " + testCases + " does not exists");
			return;
		}
		
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			try{
				String line;
				while((line = in.readLine())!=null){
					if (line.equals("")){
						continue;
					}
					Scanner scanner = new Scanner(line);
					
					int repetitions = scanner.nextInt();
					
					String file = scanner.next();					
					
					int p = scanner.nextInt();
					
					long max = scanner.nextLong();
					
					scanner.close();
					
					for(int i = 1; i <= repetitions; i++){
						Tester s = new Tester();
						
						s.world = new World(file, 0, p);


						MyAgent agent = new MyAgent(s.world.net.length, s.world.net[0].length);

						s.world.addAgent(agent);
						World.Result result = s.world.run(max);
						writeLog(args[0], file, max, s.world, result);
						
						System.out.println(i+"/"+repetitions+" of " + line);
					}
					System.out.println();
				}
								
			}finally{
				in.close();
			}		
		}catch (IOException e){
			System.out.println("Error during test occured");			
			e.printStackTrace();
		}		
	}
	
	private static void writeLog(String logName, String mapName, long timeLimit, World world, World.Result result) {
		File dir = new File(logName);
		dir.mkdir();
		
		File file = new File(logName+"/"+logName+".csv");
		boolean alreadyExist = file.exists();
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));							
			try {
				if (!alreadyExist){
					// write a header of the table
					out.write("Map file name"+","+"Map height"+","+"Map width"+","+
							  "Percept size"+","+"Time limit (in ms)"+","+	
							  "Final state"+","+"Steps"+","+
							  "Runtime (in ms)"+","+"Correct?");
					out.newLine();
				}					
				
				out.write(mapName+","+world.getHeight()+","+world.getWidth()+","+
						  +world.getPerception()+","+timeLimit+","+result.state+","+
						  result.steps+","+result.runtimeMillis+","+
						  (result.correctSolution?"YES":"NO"));
				out.newLine();
			} finally {
				out.close();	

				// TODO: portnut aj vyrabanie screenu
//				if(World.Result.State.HALTED.equals(result.state) && !result.correctSolution){
//					makeScreen();
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();							
			System.out.println("Exception occured during LOG creation :(");
		}				
	}
}
