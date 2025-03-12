import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Solution {
	
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
			boolean decreasing = false;
			boolean success = true;
			String[] line = fin.nextLine().split(" ");
			
			int int1 = Integer.valueOf(line[0]);
			int int2 = Integer.valueOf(line[1]);
			
			if (int1 > int2 && int1 - 3 <= int2) {
				decreasing = true;
			} else if (int1 < int2 && int2 - 3 <= int1) {
			  decreasing = false;
			} else {		
			  //Bad case, so gtfoh
			  success = false;
			  continue;
			}//end if
			
			for (int i = 2; i < line.length; i++) {
				int1 = int2;
				int2 = Integer.valueOf(line[i]);
				
				if (decreasing && (int1 <= int2 || int1 - 3 > int2) ||
				   !decreasing && (int1 >= int2 || int2 - 3 > int1) ) {
							
					success = false;
					break;
					
				}//end if
				
			}//end for
			
			if (success) {
				safes++;
			}//end if
		
		}//end while
		
		return safes;
	}//end readFile
	
}//end Solution