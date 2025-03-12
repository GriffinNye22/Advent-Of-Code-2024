import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Solution {
  
  enum DIR {UP, RIGHT, DOWN, LEFT};
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<char[]> grid = new ArrayList<>();
    int[] guard = new int[2];
    DIR direction;
    
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		readFile(fileIn, grid);
    
    //Find guard in the grid
    direction = findGuard(grid, guard);
    
    //Move guard until they exit the grid
    moveGuard(grid, new int[]{guard[0], guard[1]} , direction);
    
    //Count positions visited by the guard
    System.out.println( countPositionsVisited(grid) );
		
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
	
	public static void readFile(Scanner fin, ArrayList<char[]> grid) {
		
		while(fin.hasNextLine() ) {
			char[] line = fin.nextLine().toCharArray();
      grid.add(line);
		}//end while
		
	}//end readFile
	
	public static DIR findGuard(ArrayList<char[]> grid, int[] coords) {
    Map<Character, DIR> direct = Map.of('^', DIR.UP, '>', DIR.RIGHT, 'v', DIR.DOWN, '<', DIR.LEFT);
    
    //Search for guard and store coords
    for(int i = 0; i < grid.size(); i++) {
      for(int j = 0; j < grid.get(0).length; j++) {
        
        if( direct.containsKey( grid.get(i)[j] ) ) {
          coords[0] = i;
          coords[1] = j;
          return direct.get( grid.get(i)[j] );
        }//end if
        
      }//end for
    }//end for
    
    return null;
  }//end findGuard
  
  public static void moveGuard(ArrayList<char[]> grid, int[] guard, DIR dir) {
    final int[][] move = { {-1, 0}, {0,1}, {1,0}, {0,-1} };
    
    //Continue while guard is in the bounds of grid
    while( isInBounds(grid, guard) ) {
    
      grid.get( guard[0] )[ guard[1] ] = 'X'; //Mark current cell
      
      int rChange = move[dir.ordinal()][0];
      int cChange = move[dir.ordinal()][1];
      
      guard[0] += rChange;
      guard[1] += cChange;
      
      //If wall found, rotate clockwise
      if( isInBounds(grid, guard) && grid.get(guard[0])[guard[1]] == '#' ) {
        guard[0] -= rChange;
        guard[1] -= cChange;
         dir = DIR.values()[ (dir.ordinal() + 1) % DIR.values().length] ;
      }//end if        
      
    }//end while
    
    
  }//end moveGuard
  
  public static boolean isInBounds(ArrayList<char[]> grid, int[] guard) {
    return guard[0] >= 0 && guard[0] < grid.size() && 
           guard[1] >= 0 && guard[1] < grid.get(0).length;
  }//end isInBounds
  
  public static int countPositionsVisited(ArrayList<char[]> grid) {
    int ctr = 0;
    
    //Count X's in grid
    for(int i = 0; i < grid.size(); i++) {
      for(int j = 0; j < grid.get(0).length; j++) {
        ctr += grid.get(i)[j] == 'X' ? 1 : 0;
      }//end for
    }//end for
    
    return ctr;
  }//end countPositionsVisited
	
}//end Solution