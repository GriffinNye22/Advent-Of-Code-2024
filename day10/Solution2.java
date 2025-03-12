import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

class Coords {
	
	public int r;
	public int c;
	
	Coords(int _r, int _c) {
		r = _r;
		c = _c;
	}//end constructor
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass() ) return false;
		Coords obj = (Coords) o;
		return this.r == obj.r && this.c == obj.c;
	}//end equals
	
	@Override
	public int hashCode() {
		return 31 * r + c;
	}//end hashCode
	
}//end Coords

public class Solution2 {
	
	public static enum DIR {UP, DOWN, LEFT, RIGHT};
	public static int[][] move = { {-1,0}, {1,0}, {0,-1}, {0,1} };
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList< ArrayList<Integer> > grid = new ArrayList<>();
		ArrayList<Coords> trailheads = new ArrayList<>();
		int totalRatings = 0;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read contents of the file into grid
		readFile(fileIn, grid);
		
		//Find all possible trailheads in the graph
		findTrailHeads(grid, trailheads);
		
		//Traverse all possible trails and tally total ratings of all trails
    //Where ratings the total number of unique valid paths from a trailhead to 
    //all trail ends
		for(Coords trail : trailheads) {
			totalRatings += traversePath(grid, trail);
		}//end for
		
		System.out.println(totalRatings);
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
	
	public static void readFile(Scanner fin, ArrayList< ArrayList<Integer> > grid) {
		
		while(fin.hasNextLine() ) {
			String line = fin.nextLine();
      ArrayList<Integer> lineVals = new ArrayList<>();
      
      for(int i = 0; i < line.length(); i++) {
        lineVals.add( Character.getNumericValue( line.charAt(i) ) );
      }//end for
      
			
			grid.add(lineVals);
		}//end while
		
	}//end readFile
	
	public static void findTrailHeads(ArrayList< ArrayList<Integer> > grid, ArrayList<Coords> heads) {
		
		for(int i = 0; i < grid.size(); i++) {
			for(int j = 0; j < grid.get(0).size(); j++) {
				if( grid.get(i).get(j) == 0 ) {
					heads.add(new Coords(i,j) );
				}//end if
			}//end for
		}//end for
		
	}//end findTrailHeads
	
	public static int traversePath(ArrayList< ArrayList<Integer> > grid, Coords start) {
		int rating = 0;
    int height = grid.size();
		int width = grid.get(0).size();
		int curr = grid.get(start.r).get(start.c);
		int nextR, nextC;
		
		if( curr == 9 ) {
			return ++rating;
		}//end if
		
		for(DIR d : DIR.values() ) {
			nextR = start.r + move[d.ordinal()][0];
			nextC = start.c + move[d.ordinal()][1];
			if( inBounds(nextR, nextC, height, width) && grid.get(nextR).get(nextC) == curr + 1) {
				rating += traversePath(grid, new Coords(nextR, nextC));
			}//end if
		}//end for
		
		return rating;
	}//end traversePath
	
	public static boolean inBounds(int r, int c, int h, int w) {
		return r >= 0 && r < h && c >= 0 && c < w;
	}//end inBounds
	
	
	
}//end Solution