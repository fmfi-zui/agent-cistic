public  class MyAgent extends Agent{	
	int[][] net;
		
	public MyAgent(int height, int width) {
		
	}

	public void act(){			
		/* ZACIATOK MIESTA PRE VAS KOD */
		
		net = percept();	// do pola net sa priradi vystup metody percept() - teda entity okolo agenta vo forme intov
        // (funguju konstanty, ktore su namapovane na tieto cisla: CLEAN=0, DIRTY=1, WALL=2, AGENT=3)

		int orientation = getOrientation(); // aktualne natocenie agenta
        // (funguju konstanty, ktore su namapovane na tieto cisla: World.NORTH=0, World.EAST=1, World.SOUTH=2, World.WEST=3)		
		
		
		// nasledujuci kod je len ukazkou, ktora nerobi nic rozumne	
		
		switch(orientation){
			case World.NORTH:
				moveFW(); 
				break;
			case World.EAST:
				turnLEFT();
				break;				
			case World.WEST:
				turnRIGHT();
				break;				
			case World.SOUTH:
				suck(); // vycisti miesto pod agentom 
				break;				
		}
		if (Math.random() > 0.5)
			halt(); //  ukonci cinnost agenta. na konzole sa vypise pocet vykonanych akcii
		
		/* KONIEC MIESTA PRE VAS KOD */
	}
	
}