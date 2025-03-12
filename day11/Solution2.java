import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Map;
import java.util.HashMap;

public class Solution2 {
  
  private static int numBlinks;
  private static Map<String, Long> cache; //Cache for retrieving previously calculate values
                                          //uses num + " " + blinksLeft as key and result as value
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
    Scanner userIn = new Scanner(System.in);
		ArrayList<Long> stones = new ArrayList<>();
    long numStones = 0;
    cache = new HashMap<>();
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
    
    //Capture number of blinks from command line
    numBlinks = Integer.parseInt(args[0]);
    
    //Read stone numbers into the stone list
		readFile(fileIn, stones);
    
    //Prompt user for number of blinks
    System.out.println("FILE SUCCESSFULLY READ");
    
    //Perform number of blinks and modify list
    for(int i = 0; i < stones.size(); i++) {
      numStones += blink(stones.get(i), numBlinks);
      System.out.println("Stone " + (i+1) + " COMPLETE");
    }//end for
    
    //Print number of stones
    System.out.println(numStones);
    
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
	
	public static void readFile(Scanner fin, ArrayList<Long> s) {
		s.addAll( Arrays.stream( fin.nextLine().split(" ") ).map(Long::parseLong).collect(Collectors.toList() ) );
	}//end readFile

  public static long blink(long s, int numTimes) {
    long numStones = 0;
    String cacheKey;
    
    //Base case
    if( numTimes == 0) {
      return 1;
    }//end if
    
    //Construct cacheKey
    cacheKey = String.valueOf(s) + " " + String.valueOf(numTimes);
    
    //Check if value was already computed for this blink depth to save time
    if(cache.containsKey(cacheKey) ) {
      return cache.get(cacheKey);
    }//end if
    
    //Calculate length of value
    long len = (long) Math.log10(s) + 1;
    
    //Apply operation to stone
    if (s == 0) {
      numStones += blink(1L, numTimes - 1);
    } else if (len % 2 == 0) {
      long split = (long) Math.pow(10, len/2);
      long s1 = s / split;
      long s2 = s % split;
      numStones += blink(s1, numTimes - 1);
      numStones += blink(s2, numTimes - 1);
    } else {
      numStones += blink(s * 2024, numTimes - 1);
    }//end if
    
    //Store result in the cache for later retrieval
    cache.put(cacheKey, numStones);
    
    return numStones;
  }//end blink

}//end Solution