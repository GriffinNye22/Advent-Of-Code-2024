import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.ArrayList;

public class Solution1Clean {
  
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
			
      for(int c = 0; c < grid.get(0).length() - (KEY.length() - 1); c++) {    
        
        //Check row forwards
        if ( grid.get(r).substring(c, c + KEY.length() ).equals(KEY) ) {
          ctr++;
        }//end if
        
        grid.get(r).reverse(); //Flip it!
        
        //Check row backwards 
        if ( grid.get(r).substring(c, c + KEY.length() ).equals(KEY) ) {
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
			for(int r = 0; r < col.length() - (KEY.length() - 1); r++) {
				
				//Check column forwards
				if( col.substring(r, r + KEY.length() ).equals(KEY) ) {
					ctr++;
				}//end if
				
				col.reverse(); //Put it in reverse, Terry!
				
				//Check column backwards
				if( col.substring(r, r + KEY.length() ).equals(KEY) ) {
					ctr++;
				}//end if
				
			}//end for				
    }//end for
    
    return ctr;
  }//end checkColumns
  
  public static int checkDiagonals(ArrayList<StringBuilder> grid) {
		int ctr = 0;
    
    //Lower diagonals starting in top-left
    ctr += countKeyInDiagonal(grid, 0, grid.size() - 3, 0, grid.get(0).length(), true, false);
    //Upper diagonals starting in top-left
    ctr += countKeyInDiagonal(grid, 0, grid.size() - 4, 1, grid.get(0).length(), false, false);
    //Lower diagonals starting in bottom-right
    ctr += countKeyInDiagonal(grid, 0, grid.size() - 3, 0, grid.get(0).length(), true, true);
		//Upper diagonals starting in bottom-right
    ctr += countKeyInDiagonal(grid, 0, grid.size() - 4, 0, grid.get(0).length() - 1, false, true);
    
		return ctr;
  }//end checkDiagonals
  
  public static int countKeyInDiagonal(ArrayList<StringBuilder> grid, int rStart, 
                                        int rEnd, int cStart, int cEnd, 
                                        boolean isLower, boolean isRToL) {
    int ctr = 0;
    
    //Check each diagonal for the key
    for(int r = rStart; r < rEnd; r++) {
			
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
      
			//Build the diagonal string	
      for(int c = cStart; c < cEnd - r; c++) {
        int x, y;
        
        
        if (isLower && isRToL) { //Lower R-L case
          x = grid.get(0).length() - 1 - c;
          y = c + r;
        } else if (!isLower && isRToL) { //Upper R-L case
          x = cEnd - 1 - c - r;
          y = c;
        } else if (isLower) { //Lower L-R case
          x = r + c;
          y = c;
        } else { //Upper L-R case
          x = c - 1;
          y = r + c;
        }//end if
        
				diag.append( grid.get(x).charAt(y) );
      }//end for
			
			//Search the diagonal 
			for(int d = 0; d < diag.length() - (KEY.length() - 1) ; d++) {

				//Search diagonal forwards
				if( diag.substring(d, d + KEY.length() ).equals(KEY) ) {
					ctr++;
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				//Search diagonal backwards
				if( diag.substring(d, d + KEY.length()).equals(KEY) ) {
					ctr++;
				}//end if
				
      }//end for
      
    }//end for
    
    return ctr;
    
  }//end countKeyInDiagonal
 
}//end Solution

