import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class Solution2 {
	
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
    
    //Scale the grid up by 2x
    scaleGrid(grid);
		
		//Find robot and store his coordinates
		robotLoc = findRobot(grid);

    //Move the robot according to all instructions in the moveset
		for(int i = 0; i < moveset.length(); i++) {
			moveRobot(grid, robotLoc, moveset.charAt(i) );
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
  
  //Scales grid up by 2 horizontally according to instructions
  public static void scaleGrid(ArrayList<StringBuilder> grid) {
    
    for(int i = 0; i < grid.size(); i++) {
      StringBuilder line = grid.get(i);
      
      //Increment j by 2 to skip newly inserted char
      for(int j = 0; j < grid.get(0).length(); j+=2) {
        char ch = line.charAt(j);
        
        switch(ch) {
          case '#':
            line.insert(j+1, '#');
            break;
          case 'O':
            line.setCharAt(j, '[');
            line.insert(j+1, ']');
            break;
          case '.':
            line.insert(j+1, '.');
            break;
          case '@':
            line.insert(j+1, '.');
            break;
        }//end switch
        
      }//end for
    }//end for
      
  }//end scaleGrid
	
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
		//Marks new desired position for robot
		int swapX = rob[0] + change[0], swapY = rob[1] + change[1];
		//Marks elt contents of new desired position for robot
		char neighbor = grid.get( rob[0] + change[0]).charAt(rob[1] + change[1]);
    //Mark whether a stack of boxes would run into a wall at any point
    boolean boxesHitWall = false;
		
		//If moving into wall, no move possible
		if(neighbor == '#') return;
		
		//Search for chains of boxes until chain ends or wall encountered
    if(neighbor == '[' || neighbor == ']') {
      boxesHitWall = doBoxesHitWall(grid, new int[] {swapX, swapY}, cmd);
    }//end if
		
		//If boxes stacked against wall, no move possible
		if(neighbor == '#') return;
    
    //If boxes would hit the wall, no move possible
    if(boxesHitWall) return;
    
    //Push stacks of boxes and move robot
    moveBoxes(grid, new int[] {swapX, swapY}, cmd);
    grid.get(rob[0]).setCharAt(rob[1], '.'); //Set orig robot elt to empty in grid
    grid.get(swapX).setCharAt(swapY, '@');   //Place robot in new pos
    rob[0] = swapX;                          //Update robot position
    rob[1] = swapY;
    
	}//end moveRobot
  
  public static boolean doBoxesHitWall(ArrayList<StringBuilder> grid, int[] root, char cmd) {
		char rootCh = grid.get(root[0]).charAt(root[1]);
		int[] rootTwin, vel = calcMoveChange(cmd);
		char rootNext, twinNext;
		boolean rootHit = false, twinHit = false;
		
		//Get other half of box
		if(rootCh == '[') {
			rootTwin = new int[] {root[0], root[1] + 1};
		} else {
			rootTwin = new int[] {root[0], root[1] - 1};
		}//end if
		
		//Skip second box half for horiz traversal (Inf Recursion)
    if(cmd == '<' || cmd == '>') { 
      Arrays.stream(vel).forEach(i -> i *= 2);
      rootTwin = null;
    }//end if
    
    //Search contents of next position for box halves
    rootNext = grid.get(root[0] + vel[0]).charAt(root[1] + vel[1]);
    twinNext = rootTwin != null ? grid.get(rootTwin[0] + vel[0]).charAt(rootTwin[1] + vel[1]) : ' ';
		
		//If either hit wall, then return false
		if(rootNext == '#' || twinNext == '#') return true;
		
		//If both hit nothing, then return true
		if(rootNext == '.' && twinNext == '.') return false;
		
		//If either hit another box, then check if that box hit a wall
		if(rootNext == '[' || rootNext == ']') {
			rootHit = doBoxesHitWall(grid, new int[] {root[0] + vel[0], root[1] + vel[1]}, cmd);
		}//end if
		
		if(twinNext == '[' || twinNext == ']') {
			rootTwin[0] += vel[0];
      rootTwin[1] += vel[1];
			twinHit = doBoxesHitWall(grid, rootTwin, cmd);
		}//end if
			
		//Return whether or not ANY boxes have hit a wall
		return rootHit || twinHit;
	}//end doBoxesHitWall
  
  public static void moveBoxes(ArrayList<StringBuilder> grid, int[] root, char cmd) {
    int[] twin, vel = calcMoveChange(cmd);
    char rootCh = grid.get(root[0]).charAt(root[1]); //Stores char found at root
    char twinCh;                                     //Stores char found at twin
    
    //If we've reached an empty space, begin transferring boxes
    if(rootCh == '.') return;
		
		//Get other half of box
		if(rootCh == '[') {
			twin = new int[] {root[0], root[1] + 1};
		} else {
			twin = new int[] {root[0], root[1] - 1};
		}//end if
		
    //Get Twin's contents
    twinCh = grid.get(twin[0]).charAt(twin[1]);
		
    //Move Root child boxes
		if(vel[0] != 0 ) {
      //Only want to move root elt if moving vertically
      //Otherwise, doing so would invoke recursive call to twin (inf. recursion)
			moveBoxes(grid, new int[] {root[0] + vel[0], root[1] + vel[1]}, cmd);
		}//end if
		
    //Move Root Twin child boxes
		moveBoxes(grid, new int[] {twin[0] + vel[0], twin[1] + vel[1]}, cmd);
    
    //Move the current boxes into empty spaces left by child boxes
    grid.get(root[0] + vel[0]).setCharAt(root[1] + vel[1], rootCh);
    grid.get(twin[0] + vel[0]).setCharAt(twin[1] + vel[1], twinCh);
    
    //Clear the area this box resided in
    grid.get(root[0]).setCharAt(root[1], '.');
    
    //Only clear root twin in the case of vertical movement.
    if(vel[0] != 0) {
      grid.get(twin[0]).setCharAt(twin[1], '.');
    }//end if
    
  }//end moveBoxes
	
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
				if(grid.get(i).charAt(j) != '[') continue;
				tot += 100 * i + j;
			}//end for
		}//end for
		
		return tot;
	}//end sumBoxGPSCoords
	
}//end Solution