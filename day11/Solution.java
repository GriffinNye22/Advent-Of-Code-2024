import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Arrays;

public class Solution {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
    Scanner userIn = new Scanner(System.in);
		ArrayList<String> stones = new ArrayList<>();
    int numBlinks = Integer.parseInt(args[0]);
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
    
    //Read stone numbers into the stone list
		readFile(fileIn, stones);
    
    //Prompt user for number of blinks
    System.out.println("FILE SUCCESSFULLY READ");
    //System.out.println("How many blinks would you like to perform? ");
    //numBlinks = userIn.nextInt();
    
    //Perform number of blinks and modify list
    for(int i = 0; i < numBlinks; i++) {
      blink(stones);
      System.out.println("After blink " + (i+1) + ":");
      stones.forEach( s -> System.out.print(s + " ") );
      System.out.println();
    }//end for
    
    //Print number of stones
    System.out.println( stones.size() );
    
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
	
	public static void readFile(Scanner fin, ArrayList<String> s) {
		s.addAll( Arrays.stream( fin.nextLine().split(" ") ).collect(Collectors.toList() ) );
	}//end readFile
	
  public static void blink(ArrayList<String> stones) {
    int length = stones.size();
    
    for(int i = 0; i < length; i++) {
      String s = stones.get(i);
      
      if (s.equals("0") ) {
        stones.set(i, "1");
      } else if (s.length() % 2 == 0) {
        String s1 = String.valueOf( Long.parseLong( s.substring(0, s.length() / 2) ) );
        String s2 = String.valueOf( Long.parseLong( s.substring(s.length() / 2) ) );
        stones.add(i + 1, s2);
        stones.set(i, s1);
        length++; //Increase length due to added stone
        i++;      //Skip added stone for this iteration
      } else {
        stones.set(i, String.valueOf( Long.parseLong(s) * 2024 ) );
      }//end if
    
    }//end for
    
  }//end blink


}//end Solution