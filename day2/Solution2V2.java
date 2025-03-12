import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

public class Solution2V2 {
	
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
		int lineNum = 0;
		
		while(fin.hasNextLine() ) {
			lineNum++;
			ArrayList<String> line = new ArrayList<>(Arrays.asList( fin.nextLine().split(" ") ) );
			
			if (isSafe(line, false) ) {
				safes++;
				System.out.println("Line " + lineNum + " is valid: " + line);
			}//end if
			
		}//end while
			
		return safes;
	}//end readFile
	
	public static boolean isSafe(ArrayList<String> line, boolean isSubList) {
		if (line.size() < 2) {
			return true;
		}//end if
		
		int numViolations = 0;
		int violationIdx = -1;
		
		int prev = Integer.valueOf( line.get(0) );
		int curr = Integer.valueOf( line.get(1) );
		boolean descending = prev > curr;
		
		//Check for boundary violations in first 2 numbers
		if ( Math.abs(prev - curr) > 3 || prev == curr) {
			numViolations++;
			violationIdx = 1;
			ArrayList<String> myList = new ArrayList<>(line);
			myList.remove(1);
			//curr = prev; //Skip the problematic value to avoid additional unnecessary violations
			//descending = prev > Integer.valueOf( line.get(2) );
		}//end if
		
		for(int i = 2; i < line.size(); i++) {
			//Shift curr to prev and retrieve new curr
			prev = curr;
			curr = Integer.valueOf( line.get(i) );
			
			//Check for violations
			if ( (descending && (prev <= curr || prev - 3 > curr) )       //descending violations 
				 ||(!descending && (curr <= prev || curr - 3 > prev) ) ) {  //ascending violations
					
				//On second violation, GET OUTTA HEEYAH!
				if (numViolations == 1) {
						
					//Check for stupid first number causing problems cuz of direction switch
					if ( i == 2 ) {
						ArrayList<String> noFirstList = new ArrayList<>(line);
						noFirstList.remove(0);
						
						if ( isSafe(noFirstList, true) ) {
							return true;
						}//end if

					}//end if
					
					return false;
					
				} else {
					numViolations++;
					violationIdx = i;
					// ArrayList<String> myList = new ArrayList<>(line);
					// myList.remove(i);
					// if (isSafe(myList, true) && !isSubList) {
						// return true;
					// } else 					
					// Special handling for early violations
					if (numViolations == 1 && i == 2) {
							int first = Integer.valueOf(line.get(0));
							int third = Integer.valueOf(line.get(2));
							
							// Check if removing middle number (index 1) makes sequence valid
							if (Math.abs(first - third) <= 3) {
									violationIdx = 1;
									descending = first > third;
							}//end if
					}//end if

					// if (i < line.size() - 1 && i < line.size()/2) {
						// descending = prev > nextVal;
					// }//end if
				}//end if	
			}// end if
	
		}//end for
		
		if (numViolations == 0) {
			return true;
		} else if (numViolations == 1 && isSubList) {
			return false;
		} else {
			ArrayList<String> list1 = new ArrayList<>(line);
			ArrayList<String> list2 = new ArrayList<>(line);
			
			list1.remove(violationIdx);
			list2.remove(violationIdx - 1);

			 boolean valid1 = isSafe(list1, true);
			 boolean valid2 = isSafe(list2, true);
			
		  if (valid1) {
				System.out.println("Valid after removing index " + violationIdx + ": " + list1);
			}
			if (valid2) {
				System.out.println("Valid after removing index " + (violationIdx-1) + ": " + list2);
			}
			
			return valid1 || valid2;
		}//end if
	}//end isSafe
	
}//end Solution