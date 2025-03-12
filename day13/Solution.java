import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

class Coords {
	float x, y;
	
	public Coords(float _x, float _y) {
		this.x = _x;
		this.y = _y;
	}//end constructor
	
}//end Coords
	
	
public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Coords[]> clawMachines = new ArrayList<>();
		int totalTokens = 0;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read machine tuples from file into clawMachines list
		readFile(fileIn, clawMachines);
		
		for(Coords[] machine : clawMachines) {
			totalTokens += findMinTokens(machine);
		}//end for
		
		// for(int i = 0; i < clawMachines.size(); i++) {
			// Coords[] mach = clawMachines.get(i);
			// int tokens = findMinTokens(mach);
			
			// if (tokens == 0)
				// System.out.println("Machine " + (i + 1) + " failed." );
			// else
				// System.out.println("Machine " + (i + 1) + " succeeded with " + tokens);
		// }//end for
		
		System.out.println(totalTokens);
		
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
	
	public static void readFile(Scanner fin, ArrayList<Coords[]> claw) {
		
		while(fin.hasNextLine() ) {
			
			//Read 3 lines for machine, split on colons, discard label, and split on commas
			String[] btnA = fin.nextLine().split(":")[1].split(",");
			String[] btnB = fin.nextLine().split(":")[1].split(",");
			String[] prize = fin.nextLine().split(":")[1].split(",");
			
			//Construct array of Coords for storing tuples for this machine
			Coords[] machineCoords = new Coords[3];
			
			//Remove X and Y labels and construct Coords tuple for each button and prize
			machineCoords[0] = new Coords( Float.parseFloat( btnA[0].substring(3) ),
																		 Float.parseFloat( btnA[1].substring(3) ) );
			machineCoords[1] = new Coords( Float.parseFloat( btnB[0].substring(3) ),
																		 Float.parseFloat( btnB[1].substring(3) ) );
			machineCoords[2] = new Coords( Float.parseFloat( prize[0].substring(3) ),
																		 Float.parseFloat( prize[1].substring(3) ) );
			
			//Add machine tuples to claw list			
			claw.add(machineCoords);
			
			//If another entry exists, consume blank line between machine entries
			if( fin.hasNextLine() ) fin.nextLine();
		}//end while
		
	}//end readFile
	
	public static int findMinTokens(Coords[] mach) {
		final int ACOST = 3, BCOST = 1;
		final float TOLERANCE = 0.001f;
		float ax = mach[0].x, ay = mach[0].y;
		float bx = mach[1].x, by = mach[1].y;
		float px = mach[2].x, py = mach[2].y;
		
		//Solve System of equations
		//Solve intermediate value for b presses in terms of a presses
		//Use resulting equation to solve for a presses
		
		float rightSide = py - by * px / bx;
		float leftSide = ay - by * ax / bx;
		
		float a = rightSide / leftSide;
		
		//Use value of a presses to solve for b presses
		float b = (px - ax * a) / bx;
		
		//Compute tolerance for float values
		float aTol = Math.abs(a - Math.round(a) );
		float bTol = Math.abs(b - Math.round(b) );

		//Return minimum tokens to win if both a and b are integers
		if( aTol < TOLERANCE && bTol < TOLERANCE ) {
			return ACOST * Math.round(a) + BCOST * Math.round(b);
		} else { //Otherwise, impossible
			return 0;
		}//end if
		
	}//end findMinTokens
	
}//end Solution