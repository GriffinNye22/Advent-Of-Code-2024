import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

public class Solution2V3 {
	
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
		
    //Read from the file and count all safe records
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
	
  //Name: readFile
  //Desc: Reads the contents of the file into the ArrayList structure and determines
  //      if each sequence is safe as it is read in.
  //Params: Scanner fin - The Scanner object connected to the file
  //Return: int - The number of safe sequences discovered in the file.
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
	
  //Name: isSafe
  //Desc: Recursive function of depth 1 for returning whether or not a sequence
  //      is safe according to the rules (ie increasing or decreasing by 1-3 and 
  //      only one bad element can be removed)
  //Params: ArrayList<String> line - String list containing the numbers of the sequence
  //        boolean isSubList - Whether or not the list being passed has already
  //                            had an element removed (to prevent recursion past
  //                            depth 1)
  //Returns: boolean - Whether or not the sequence is safe according to the rules
	public static boolean isSafe(ArrayList<String> line, boolean isSubList) {
		
    //Technically unnecessary but good practice
    if (line.size() < 2) {
			return true;
		}//end if
		
    int prev = Integer.valueOf( line.get(0) ); //Prev elt in sequence
		int curr = Integer.valueOf( line.get(1) ); //Curr elt in sequence
		boolean descending = isDescending(line);   //Whether or not descending
    
    //Check all elements after the first
    for(int i = 1; i < line.size(); i++) {
      
      //Check for violations
			if ( (descending && (prev <= curr || prev - 3 > curr) )       //descending violations 
				 ||(!descending && (curr <= prev || curr - 3 > prev) ) ) {  //ascending violations
         
         //If violation occurred and already a sublist, then GTFOH!
         if(isSubList) return false;
         
         //Otherwise, check if removing curr or prev elt would result in safe seq
         ArrayList<String> myList = new ArrayList<>(line);
         boolean valid1, valid2;
         
         myList.remove(i);
         valid1 = isSafe(myList, true);
         
         myList.insert(curr, i);
         myList.remove(i-1);
         valid2 = isSafe(myList, true);
         
         // if (valid1) {
           // System.out.println("Valid after removing index " + i + ": " + myList);
         // }//end if
         // if (valid2) {
           // System.out.println("Valid after removing index " + (i-1) + ": " + myList2);
         // }//end if
			
        return valid1 || valid2;
      }//end if
      
      //Slide to next elt pair (curr doesn't matter for last elt, since it will exit loop)
      prev = curr;
      curr = i < line.size() - 1 ? Integer.valueOf( line.get(i + 1) ) : 0;
    }//end for
    
    return true;
		
	}//end isSafe
  
  //Name: isDescending
  //Desc: Returns whether or not the current sequence is descending by comparing
  //      4 of its elements
  //Params: ArrayList<String> line - String list containing the numbers of the sequence
  //Return: boolean - True if descending, false otherwise.
  public static boolean isDescending(ArrayList<String> line) {
    //By looking at 4 numbers we can safely evaluate the order of the sequence
    //Because if one is the "bad guy", the majority will override it.
    //As opposed to looking at just 3, as the bad guy would taint all comparisons.
    //This is only viable because of the constraint that all lists will be of at 
    //least length 5, as a recursive call to isSafe() will also call this function
    int num1 = Integer.valueOf( line.get(0) );
    int num2 = Integer.valueOf( line.get(1) );
    int num3 = Integer.valueOf( line.get(2) );
    int num4 = Integer.valueOf( line.get(3) );
    int desc = 0;
    
    if (num1 > num2) desc++;
    if (num2 > num3) desc++;
    if (num3 > num4) desc++;
    
    return desc >= 2;
  }//end isDecreasing
	
}//end Solution