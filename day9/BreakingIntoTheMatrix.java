import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.StringBuilder;

public class BreakingIntoTheMatrix {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		StringBuilder fileSystem = new StringBuilder();
		int checksum;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		readFile(fileIn, fileSystem);
		
		System.out.println(fileSystem);
		
		defragment(fileSystem);
	
		System.out.println(fileSystem);
		
		checksum = calcChecksum(fileSystem);
		
		System.out.println(checksum);
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
	
	public static String getUni(int x) {
		//Convert int to Unicode-equivalent char
		//Start at 0 => 33 (start of printable characters)
		return String.valueOf( (char) (x + 33) ); 
	}//end getUni
	
	public static int getVal(char c) {
		return (int) (c - 33);
	}//end getVal
	
	public static void readFile(Scanner fin, StringBuilder fsys) {

		while(fin.hasNextLine() ) {
      String line = fin.nextLine();
      
      for(int i = 0; i < line.length(); i += 2) {
        fsys.append( getUni(i/2).repeat( Character.getNumericValue( line.charAt(i) ) ) );
        if( i+1 == line.length() ) continue;
        fsys.append( " ".repeat( Character.getNumericValue( line.charAt(i+1) ) ) );
      }//end for
			
		}//end while
		
	}//end readFile
	
	public static void defragment(StringBuilder fsys) {
		int front = 0;
		int back = fsys.length() - 1;
		
		//Find next fragmented file
		while( fsys.charAt(back) == ' ') {
			back--;
		}//end while
		
		while( front < back ) {
			
			if( fsys.charAt(front) != ' ' ) {
				front++; //Search next element for unallocated space
				continue;
			}//end if
			
			fsys.setCharAt(front, fsys.charAt(back) ); //Move file into unallocated space
			fsys.setCharAt(back, ' '); //Deallocate previous memory of moved file
			front++;                   //Search next element for unallocated space
			
			//Find next fragmented file
			while(fsys.charAt(back) == ' ') {
				back--;
			}//end while
			
		}//end while
		
	}//end defragment
	
	public static int calcChecksum(StringBuilder fsys) {
		int sum = 0;
		
		for(int i = 0; i < fsys.length(); i++) {
			char curr = fsys.charAt(i);
			if( curr == ' ' ) break;
			System.out.println( i * getVal(curr) );
			sum += i * getVal(curr);
		}//end for
		
		return sum;
	}//end calcChecksum
	
}//end Solution