import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

//Brief little robot class for storing robot positions, velocities, and 
//handling movement.
class Robot {
  
  /*** STATIC MEMBERS ***/
  public static final int GRIDLEN = 103, GRIDWID = 101; //Dimensions
  public static int[][] grid;                        //Grid shared by robots
  private static boolean firstAccess = true;         //Grid init on first inst creation
  
  /*** PRIVATE MEMBERS ***/
  private int r, c;   //curr pos
  private int vr, vc; //velocity
  
  public Robot(int _r, int _c, int _vr, int _vc) {
    this.r = _r;
    this.c = _c;
    this.vr = _vr;
    this.vc = _vc;
    
    //Initialize grid on first Robot creation
    if(firstAccess) {
      this.grid = new int[GRIDLEN][GRIDWID];
      firstAccess = false;
    }//end if
    
    grid[r][c]++;
  }//end constructor
  
  public int getR() {
    return r;
  }//end getR
  
  public int getC() {
    return c;
  }//end getC
  
  public void move() {
    //Remove robot from grid space
    grid[r][c]--; 
    
    //Update robot position
    r += vr;
    c += vc;
    
    //Wraparound if edges hit
    if (r >= GRIDLEN || r < 0) r += GRIDLEN * -(r / Math.abs(r) );
    if (c >= GRIDWID || c < 0) c += GRIDWID * -(c / Math.abs(c) );
    
    //Place robot in grid space
    grid[r][c]++;
  }//end move
  
}//end Robot

public class Solution2 {
	
	public static void main(String[] args) {
    final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Robot> robots = new ArrayList<>();
    int safetyFactor;
    int t = 0;

		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
    //Read robot data in from file
		readFile(fileIn, robots);
    
    //Simulate all robot movement until all robots are in unique positions
    while( Arrays.stream(robots.get(0).grid)   //Stream rows of 2d grid
                 .flatMapToInt(Arrays::stream) //Flatten grid into 1D array
                 .anyMatch(i -> i > 1) ) {     //Return true if elt exceeds 1
                   
      robots.forEach( r -> r.move() );
  
      // System.out.println("TIME " + t + " PROCESSED");
      // for(int i = 0; i < robots.get(0).grid.length; i++) {
        // for(int j = 0; j < robots.get(0).grid[0].length; j++) {
          // if(robots.get(0).grid[i][j] == 0) System.out.print(".");
          // else System.out.print(robots.get(0).grid[i][j]);
        // }//end for
        // System.out.println();
      // }//end for
      t++;
    }//end for
    
    System.out.println(t);
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
	
	public static void readFile(Scanner fin, ArrayList<Robot> rob) {
		
		while(fin.hasNextLine() ) {
			String[] line = fin.nextLine().split(" ");
      String[] pos = line[0].substring(2).split(",");
      String[] vel = line[1].substring(2).split(",");
      
      //Note: File lists (x,y) but Robot uses (r,c)
      Robot newRob = new Robot( Integer.valueOf(pos[1]), Integer.valueOf(pos[0]), 
                                Integer.valueOf(vel[1]), Integer.valueOf(vel[0]) );
      rob.add(newRob);
		}//end while
		
	}//end readFile
  
 public static int calcSafetyFactor(ArrayList<Robot> rob) { 
    int q1 = 0, q2 = 0, q3 = 0, q4 = 0;
    int[][] grid = rob.get(0).grid;
    int len = rob.get(0).GRIDLEN, wid = rob.get(0).GRIDWID;
    
    for(int i = 0; i < grid.length; i++) {
      
      if(i == len/2) continue; //Skip center row
      
      for(int j = 0; j < grid[0].length; j++) {
        
        if(j == wid/2) continue; //Skip center column
        
        //Tally robots in each quadrant
        if(i < len/2 && j < wid/2)  q1 += grid[i][j];
        else if(i < len/2 && j > wid/2) q2 += grid[i][j];
        else if(i > len/2 && j < wid/2) q3 += grid[i][j];
        else q4+= grid[i][j]; 
        
      }//end for
      
    }//end for
    
    return q1 * q2 * q3 * q4;
  }//end calcSafetyFactor
  
}//end Solution