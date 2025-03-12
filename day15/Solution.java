import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<StringBuilder> grid = new ArrayList<>();
    StringBuilder moveset = new StringBuilder();
		int[] robotLoc = new int[2];
		int totalBoxCoords;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read from input file into grid and moveset
		readFile(fileIn, grid, moveset);
		
		//Find robot and store his coordinates
		robotLoc = findRobot(grid);
		
		// System.out.println("INITIAL STATE");
		// for(int j = 0; j < grid.size(); j++) {
			// System.out.println(grid.get(j));
		// }//end for

		for(int i = 0; i < moveset.length(); i++) {
			moveRobot(grid, robotLoc, moveset.charAt(i) );
			
			// System.out.println("AFTER MOVING: " + moveset.charAt(i) );
			// for(int j = 0; j < grid.size(); j++) {
				// System.out.println(grid.get(j));
			// }//end for
			
		}//end for
		
		//Sum the GPS coordinates of all boxes
		totalBoxCoords = sumBoxGPSCoords(grid);
		
		System.out.println(totalBoxCoords);
	}//end main
	
	public static Scanner openFile(String fname) throws FileNotFoundException{
		Scanner scanner = null;
		
		try {
			File file = new File(fname);
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("ERROR: " + fname + " not found.");
		}//end try-catch
	
		return scanner;
	}//end openFile
	
	public static void readFile(Scanner fin, ArrayList<StringBuilder> grid, StringBuilder mov) {
		
		while(fin.hasNextLine() ) {
			String line = fin.nextLine();
			
			if(line.equals("") ) break; //Break when empty line encountered
			
			grid.add( new StringBuilder( line ) );
		}//end while
		
		while(fin.hasNextLine() ) {
			mov.append( fin.nextLine() );
		}//end while
		
	}//end readFile
	
	public static int[] findRobot(ArrayList<StringBuilder> grid) {
		
		for(int i = 0; i < grid.size(); i++) {
			for(int j = 0; j < grid.get(0).length(); j++) {
				if( grid.get(i).charAt(j) == '@') return new int[] {i,j};
			}//end for
		}//end for
		
		return null;
	}//end findRobot
	
	public static void moveRobot(ArrayList<StringBuilder> grid, int[] rob, char cmd) {
		//Contains index changes for movement
		int[] change = calcMoveChange(cmd); 
		//Marks first non-box elt within direction of robot movement
		int[] endBoxes = new int[] {rob[0] + change[0], rob[1] + change[1]};
		//Marks new desired position for robot
		int swapX = rob[0] + change[0], swapY = rob[1] + change[1];
		//Marks elt contents of new desired position for robot
		char neighbor = grid.get( rob[0] + change[0]).charAt(rob[1] + change[1]);
		
		//If moving into wall, no move possible
		if(neighbor == '#') return;
		
		//Search for chains of boxes until chain ends or wall encountered
		while(neighbor == 'O') {
			endBoxes[0] += change[0];
			endBoxes[1] += change[1];
			neighbor = grid.get(endBoxes[0]).charAt(endBoxes[1]);
		}//end if
		
		//If boxes stacked against wall, no move possible
		if(neighbor == '#') return;
		
		//Move robot
		grid.get(rob[0]).setCharAt(rob[1], '.'); //Set orig robot elt to empty in grid
		grid.get(swapX).setCharAt(swapY, '@');   //Place robot in new pos
		rob[0] = swapX;                          //Update robot position
		rob[1] = swapY;
		
		//Push stacks of boxes if necessary
		if(endBoxes[0] != swapX || endBoxes[1] != swapY) {
			grid.get( endBoxes[0] ).setCharAt(endBoxes[1], 'O');
		}//end if
		
	}//end moveRobot
	
	public static int[] calcMoveChange(char cmd) {
		
		switch(cmd) {
			case '>':
				return new int[] {0,1};
			case '<':
				return new int[] {0,-1};
			case '^':
				return new int[] {-1,0};
			case 'v':
				return new int[] {1,0};
		}//end switch
		
		return null;
	}//end calcMoveChange
	
	public static int sumBoxGPSCoords(ArrayList<StringBuilder> grid) {
		int tot = 0;
		
		for(int i = 0; i < grid.size(); i++) {
			for(int j = 0; j < grid.get(0).length(); j++) {
				if(grid.get(i).charAt(j) != 'O') continue;
				tot += 100 * i + j;
			}//end for
		}//end for
		
		return tot;
	}//end sumBoxGPSCoords
	
}//end Solution