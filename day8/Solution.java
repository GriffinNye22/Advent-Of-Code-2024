import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

class Location {
  public int r, c;

  public Location(int _r, int _c) {
    r = _r;
    c = _c;
  }//end Location
  
  @Override
  public boolean equals(Object obj) {
    
    if(this == obj) return true;
    if(obj == null || getClass() != obj.getClass() ) return false;
    
    Location loc = (Location) obj;
    return r == loc.r && c == loc.c;    
  }//end equals
  
  @Override
  public int hashCode() {
    return Objects.hash(r, c);
  }//end hashCode
  
}//end Location

public class Solution {
  
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		Map<Character, ArrayList<Location> > antennas = new HashMap<>();
    int[] dimensions;
    int numAntis;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
    //Read the file and populate map of antennas and store dimensions of grid
		dimensions = readFile(fileIn, antennas);
		
    //Find the number of antinodes that would be spawned.
    numAntis = calcAntinodes(antennas, dimensions);
    
    System.out.println(numAntis);
    
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
	
	public static int[] readFile(Scanner fin, Map<Character, ArrayList<Location> > ant) {
		int lineNum = 0, lineLength = 0;
    
		while(fin.hasNextLine() ) {
			String line = fin.nextLine();
      
      for(int i = 0; i < line.length(); i++) {
        char ch = line.charAt(i);
        if( ch == '.') continue; //Skip all periods
        
        if( ant.containsKey(ch) ) {
          //Insert location into value set
          ant.get(ch).add( new Location(lineNum, i) );
        } else {
          //Create new value set, insert location, place in map
          ArrayList<Location> locs = new ArrayList<>();
          locs.add(new Location(lineNum, i) );
          ant.put(ch, locs);
        }//end if
        
      }//end for
      
      lineLength = line.length();
      lineNum++;
		}//end while
		
    return new int[] {lineNum, lineLength};
	}//end readFile
  
  public static int calcAntinodes(Map<Character, ArrayList<Location> > ant, int[] dim) {
    Set<Location> antinodes = new HashSet<>(); //Set of all antinode locations
    
    //Add antinode locations to the set for each Antenna type
    for(Map.Entry<Character, ArrayList<Location> > entry : ant.entrySet() ) {
      ArrayList<Location> coords = entry.getValue(); //List of locations of curr antenna type
      
      for(int i = 0; i < coords.size() - 1; i++) {
        
        for(int j = i + 1; j < coords.size(); j++) {
          Location loc = coords.get(i), loc2 = coords.get(j); //Get 2 antenna site locations
          
          //Calculate distance between antenna sites
          int r = loc.r - loc2.r, c = loc.c - loc2.c;
          int r1 = loc.r + r, c1 = loc.c + c;
          int r2 = loc2.r - r, c2 = loc2.c - c;
          
          //Add antinodes to map if valid
          if( isValidCoord(r1, c1, dim) ) {
            antinodes.add( new Location(r1, c1) );
          }//end if
          
          if( isValidCoord(r2, c2, dim) ) {
            antinodes.add( new Location(r2, c2) );
          }//end if
          
        }//end for
      }//end for
    }//end for
    
    return antinodes.size();
  }//end calcAntinodes
  
  public static boolean isValidCoord(int r, int c, int[] dim) {
    return r >= 0 && r < dim[0] && c >= 0 && c < dim[1];
  }//end isValidCoord

}//end Solution