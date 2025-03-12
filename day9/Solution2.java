import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;

class MemSpace {
  
  int startIdx;
  int length;
  
  MemSpace(int start, int len) {
    startIdx = start;
    length = len;
  }//end constructor
  
}//end MemSpace

public class Solution2 {
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		StringBuilder fileSystem = new StringBuilder();
    ArrayList<MemSpace> fileTable = new ArrayList<>();
    ArrayList<MemSpace> unallocatedTable = new ArrayList<>();
		long checksum;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		//Read Disk Map from file and assemble file structure
		readFile(fileIn, fileSystem);
    
    //Construct file table mapping out starting indices and length of files in one list
    //And mapping out starting indices and lengths of unallocated space
    constructFileTable(fileSystem, fileTable, unallocatedTable);
    
    //Reverse File Table to prepare for defragmentation
    Collections.reverse(fileTable);
    
		//Defragment the file structure by moving rightmost files
		//into the leftmost unallocated space that can store the file
    //until all files have been attempted to be relocated
		defragment(fileSystem, fileTable, unallocatedTable);
    
		//Calculate file system checksum 
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
	
	public static String getUni(int x) {
		//Convert int to Unicode-equivalent char
		//Start at 0 => 33 (start of printable characters)
		return String.valueOf( (char) (x + 33) ); 
	}//end getUni
	
	public static long getVal(char c) {
		return (long) (c - 33);
	}//end getVal
	
	public static void defragment(StringBuilder fsys, ArrayList<MemSpace> fTab, 
                                ArrayList<MemSpace> uTab) {
    
    //Attempt to move each file into leftmost unallocated space
    for(int i = 0; i < fTab.size(); i++) {
      MemSpace currFile = fTab.get(i);
      
      for(int j = 0; j < uTab.size(); j++) {
        MemSpace currFree = uTab.get(j);
        
        //Do not attempt to move files into rightmost unallocated space
        if(currFree.startIdx > currFile.startIdx) continue;
        
        //Skip relocation if unallocated space cannot store full file
        if(currFree.length < currFile.length) continue;
        
        //Move full currFile into currFree and deallocate currFile's memory space
        for(int k = currFree.startIdx; k < currFree.startIdx + currFile.length; k++) {
          fsys.setCharAt(k, fsys.charAt(currFile.startIdx) );
          fsys.setCharAt(currFile.startIdx, ' ');
          currFile.startIdx++;
        }//end for
        
        //Update file properties for currFile and currFree
        currFile.startIdx = currFree.startIdx;
        currFree.startIdx += currFile.length;
        currFree.length -= currFile.length;
        
        //If unallocated space was filled completely, remove from uTab
        if(currFree.length == 0) {
          uTab.remove(j);
        }//end if
       
      }//end for
      
    }//end for
		
	}//end defragment
  
  public static void constructFileTable(StringBuilder fsys, ArrayList<MemSpace> fTab, 
                                        ArrayList<MemSpace> uTab) {
    char prev = fsys.charAt(0);
    int start = 0;
    
    for(int i = 1; i < fsys.length(); i++) {
      char curr = fsys.charAt(i);
      
      if(curr == prev) continue; //Continue if still in same file block
      
      //Add file properties to proper file table
      if(prev == ' ') {
        uTab.add( new MemSpace(start, i - start) );
      } else {
        fTab.add( new MemSpace(start, i - start) );
      }//end if
      
      //Update tracking for next file
      prev = curr;
      start = i;
      
    }//end for
    
    //Add last file to proper file table
    if(prev == ' ') {
      uTab.add( new MemSpace(start, fsys.length() - start) );
    } else {
      fTab.add( new MemSpace(start, fsys.length() - start) );
    }//end if
    
  }//end constructFileTable
	
	public static long calcChecksum(StringBuilder fsys) {
		long sum = 0;
		
		for(int i = 0; i < fsys.length(); i++) {
			char curr = fsys.charAt(i);
			if( curr == ' ' ) continue; //Skip unallocated space
			sum += i * getVal(curr);
		}//end for
		
		return sum;
	}//end calcChecksum
	
}//end Solution