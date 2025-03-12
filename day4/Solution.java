import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.ArrayList;

public class Solution {
  
  final static String KEY = "XMAS";
  
  public static void main(String[] args) {
    final String FILENAME = "inp.dat";
		Scanner fileIn = null;
    ArrayList<StringBuilder> grid = new ArrayList<StringBuilder>();
		int total;
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		readFile(fileIn, grid);
    
		total = checkRows(grid) + checkColumns(grid) + checkDiagonals(grid);
		
		System.out.println(total);
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
	
	public static void readFile(Scanner fin, ArrayList<StringBuilder> grid) {
		
		while(fin.hasNextLine() ) {
      grid.add(new StringBuilder( fin.nextLine() ) );
		}//end while
		
	}//end readFile
  
  public static int checkRows(ArrayList<StringBuilder> grid) {
    int ctr = 0;
    
    //Check all rows for copies of KEY
    for(int r = 0; r < grid.size(); r++) {
			
      for(int c = 0; c < grid.get(0).length() - 3; c++) {    
        
        //Check row forwards
        if ( grid.get(r).substring(c, c+4).equals(KEY) ) {
          ctr++;
        }//end if
        
        grid.get(r).reverse(); //Flip it!
        
        //Check row backwards 
        if ( grid.get(r).substring(c, c+4).equals(KEY) ) {
          ctr++;
        }//end if
        
        grid.get(r).reverse(); //Put that thing back where it came from or so help me! SO HELP ME!
        
      }//end for
    }//end for
    
    return ctr;
  }//end checkRows
  
  public static int checkColumns(ArrayList<StringBuilder> grid) {
    int ctr = 0;
		
		//Check all columns for copies of KEY
    for(int c = 0; c < grid.get(0).length(); c++) {
		
			StringBuilder col = new StringBuilder(); //StringBuilder for the column
			
			//Build the column String
			for(int r = 0; r < grid.size(); r++) {
				col.append( grid.get(r).charAt(c) );
			}//end for
			
			//Search the column String
			for(int r = 0; r < col.length() - 3; r++) {
				
				//Check column forwards
				if( col.substring(r, r+4).equals(KEY) ) {
					ctr++;
				}//end if
				
				col.reverse(); //Put it in reverse, Terry!
				
				//Check column backwards
				if( col.substring(r, r+4).equals(KEY) ) {
					ctr++;
				}//end if
				
			}//end for				
    }//end for
    
    return ctr;
  }//end checkColumns
  
  public static int checkDiagonals(ArrayList<StringBuilder> grid) {
		int ctr = 0;
		
		//Check lower left-to-right diagonals
    for(int r = 0; r < grid.size() - 3; r++) {
			
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
			
			//Build the diagonal string	
      for(int c = 0; c < grid.size() - r; c++) {
				diag.append( grid.get(r+c).charAt(c) );
      }//end for
			
			//Search the diagonal 
			for(int d = 0; d < diag.length() - 3; d++) {

				//Search diagonal forwards
				if( diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				//Search diagonal backwards
				if( diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
			}//end for
    }//end for
		
		//Check upper left-to-right diagonals
    for(int c = 1; c < grid.get(0).length() - 3; c++) {
      
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
			
			//Build the diagonal string
      for(int r = 0; r < grid.size() - c; r++) {
				diag.append( grid.get(r).charAt(r+c) );
      }//end for
			
			//Search the diagonal 
			for(int d = 0; d < diag.length() - 3; d++) {
				
				//Search the diagonal forwards
				if (diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				if( diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
			}//end for
    }//end for
		
		//Check lower right-to-left diagonals
    for(int r = 0; r < grid.size() - 3; r++) {
			
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
			
			//Build the diagonal string	
      for(int c = grid.get(0).length() - 1; c >= r; c--) {
				diag.append( grid.get(r + grid.get(0).length() - 1 - c).charAt(c) );
      }//end for
			
			//Search the diagonal 
			for(int d = 0; d < diag.length() - 3; d++) {

				//Search diagonal forwards
				if( diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				//Search diagonal backwards
				if( diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
			}//end for
    }//end for
		
		//Check upper right-to-left diagonals
    for(int c = grid.get(0).length() - 2; c >= 3; c--) {
      
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
			
			//Build the diagonal string
      for(int r = 0; r <= c; r++) {
				diag.append( grid.get(r).charAt(c-r) );
      }//end for
			
			//Search the diagonal 
			for(int d = 0; d < diag.length() - 3; d++) {
				
				//Search the diagonal forwards
				if (diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				if( diag.substring(d, d+4).equals(KEY) ) {
					ctr++;
				}//end if
				
			}//end for
    }//end for
		
		return ctr;
  }//end checkDiagonals
 
}//end Solution

