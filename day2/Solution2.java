import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Solution2 {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		int safeRecords;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
		
		safeRecords = readFile(fileIn);
		
		System.out.println(safeRecords);
		
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
	
	public static int readFile(Scanner fin) {
		int safes = 0;
		
		while(fin.hasNextLine() ) {
			boolean success = true;
			boolean freebee = false;
			String[] line = fin.nextLine().split(" ");
			boolean decreasing = isDecreasing(line);
			
			int int1;
			int int2 = Integer.valueOf(line[0]);
			
			for (int i = 1; i < line.length; i++) {
				int1 = int2;
				int2 = Integer.valueOf(line[i]);
				
				System.out.println(int1 + " " + int2);
				
				//If current pair breaks rules, then take action
				if (decreasing && (int1 <= int2 || int1 - 3 > int2) ||
				   !decreasing && (int1 >= int2 || int2 - 3 > int1) ) {
							
					//If no freebee used, then consume freebee and skip bad value
					if (!freebee) {
						//If second starting number is not in proper place on first iteration OR we are on a subsequent iteration
						//Then skip int2 and recheck with int1 and int3
						if ( i != 1 || 
						    (i == 1 && (decreasing && int2 <= Integer.valueOf(line[i+1]) || 
								           !decreasing && int2 >= Integer.valueOf(line[i+1]) ) ) ) {
							int2 = int1;
						}//end if
						
						freebee = !freebee;
					} else { //Otherwise, freebee already used and bad case overall
						success = false;
						break;
					}//end if
					
				}//end if
				
			}//end for
			
			if (success) {
				System.out.println("SAFE");
				safes++;
			} else {
				System.out.println("UNSAFE");
			}//end if
		
		}//end while
		
		return safes;
	}//end readFile
	
	public static boolean isDecreasing(String[] line) {
		
		int a = Integer.valueOf(line[0]);
		int b = Integer.valueOf(line[1]);
		int c = Integer.valueOf(line[line.length - 1]);
		
		if (a > b && b > c) { //Full descending case
			return true;
		} else if (a < b && b < c) { //Full ascending case
			return false;
		} else { //Tiebreak case, a or b could be outliee
			return a > c || Integer.valueOf(line[2]) > c; //Look at a 4th value to determine true order
		}//end if
		
	}//end isDescending
	
}//end Solution