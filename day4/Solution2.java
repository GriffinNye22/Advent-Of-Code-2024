import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

//Brief little struct for storing middle coordinates of X-MAS's
class Coords {
  public int r;
  public int c;
  
  public Coords(int r, int c) {
    this.r = r;
    this.c = c;
  }//end constructor
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true; 
    if (obj == null || getClass() != obj.getClass() ) return false;
    Coords coords = (Coords) obj;
    return r == coords.r && c == coords.c;
  }//end equals
  
  @Override
  public int hashCode() {
    return 31 * r + c; 
  }//end hashCode
  
}//end coords


public class Solution2 {
  
  final static String KEY = "MAS";
  
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
    
		total = checkDiagonals(grid);
		
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
    Set<Coords> coordsSet = new HashSet<>();
		int ctr = 0;
    
    /*** Check top-left diagonals and capture center coords in coordsSet ***/
    
    //Check lower diagonals starting in top-left
    findCoordsInDiagonal(grid, coordsSet, 0, grid.size() - 2, 0, grid.get(0).length(), true);
    //Check upper diagonals starting in top-left
    findCoordsInDiagonal(grid, coordsSet, 0, grid.size() - 3, 1, grid.get(0).length(), false);
    
    /*** Check bottom-right diagonals and only count if center coords found in coordsSet ***/
    
    //Check upper diagonals starting in bottom-right
    ctr += countXsInDiagonal(grid, coordsSet, 0, grid.size() - 2, 0, grid.get(0).length(), true);
    System.out.println("UPPER");
    //Check upper diagonals starting in bottom-right
    ctr += countXsInDiagonal(grid, coordsSet, 0, grid.size() - 3, 0, grid.get(0).length() - 1, false);
		
		return ctr;
  }//end checkDiagonals
  
  public static int countKeyInDiagonal(ArrayList<StringBuilder> grid, int rStart, 
                                        int rEnd, int cStart, int cEnd, 
                                        boolean isLower, boolean isRToL) {
    int ctr = 0;
    
    for(int r = rStart; r < rEnd; r++) {
			
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
      
			//Build the diagonal string	
      for(int c = cStart; c < cEnd; c++) {
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
			for(int d = 0; d < diag.length() - (KEY.length() - 1); d++) {

				//Search diagonal forwards
				if( diag.substring(d, d + KEY.length()).equals(KEY) ) {
					ctr++;
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				//Search diagonal backwards
				if( diag.substring(d, d + KEY.length() ).equals(KEY) ) {
					ctr++;
				}//end if
				
      }//end for
      
    }//end for
    
    return ctr;
    
  }//end countKeyInDiagonal
  
  public static void findCoordsInDiagonal(ArrayList<StringBuilder> grid, 
                                          Set<Coords> coordSet, int rStart, 
                                          int rEnd, int cStart, int cEnd,
                                          boolean isLower) {
    int ctr = 0;
    
    for(int r = rStart; r < rEnd; r++) {
			
			StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
      int x = -1, y = -1;
      
			//Build the diagonal string	
      for(int c = cStart; c < cEnd; c++) {
        
        
        if (isLower) { //Lower L-R case
          x = r + c;
          y = c;
        } else { //Upper L-R case
          x = c - 1;
          y = r + c;
        }//end if
        
        //Break if we leave the grid boundaries
        if (x >= grid.size() || y >= grid.get(0).length() ) break;
				diag.append( grid.get(x).charAt(y) );
      }//end for
      
      //Skip diagonals that are too short to contain the KEY
      if (diag.length() < KEY.length() ) continue;
			
			//Search the diagonal 
			for(int d = 0; d < diag.length() - (KEY.length() - 1); d++) {

				//Search diagonal forwards
				if( diag.substring(d, d + KEY.length()).equals(KEY) ) {
          
          //Reverse-engineer center coords
          if (isLower) {
            coordSet.add( new Coords(r + d + KEY.length() / 2, d + KEY.length() / 2) );
          } else {
            coordSet.add( new Coords(d + KEY.length() / 2, (r + 1) + d + KEY.length() / 2) );
          }//end if
          
				}//end if
				
				diag.reverse(); //Put it in reverse, Terry!
				
				//Search diagonal backwards
				if( diag.substring(d, d + KEY.length() ).equals(KEY) ) {
          
          //Reverse-engineer center coords
          if (isLower) {
            coordSet.add( new Coords(r + (diag.length() - 1 - d) - KEY.length() / 2, diag.length() - 1 - d - KEY.length() / 2) );
          } else {
            coordSet.add( new Coords( diag.length() - 1 - d - KEY.length() / 2, (r + 1) + diag.length() - 1 - d - KEY.length() / 2) );
          }//end if
				}//end if
        
        diag.reverse(); //Put that thing back where it came from or so help me... SO HELP ME!
				
      }//end for
      
    }//end for
    
  }//end findCoordsInDiagonal
  
  public static int countXsInDiagonal(ArrayList<StringBuilder> grid, 
                                      Set<Coords> coordsSet, int rStart, 
                                      int rEnd, int cStart, int cEnd, 
                                      boolean isLower) {
    int ctr = 0;
    
    for(int r = rStart; r < rEnd; r++) {
      
      StringBuilder diag = new StringBuilder(); //StringBuilder for the diagonal
      int x = -1, y = -1;
      
      //Build the diagonal string	
      for(int c = cStart; c < cEnd; c++) {
        
        if (isLower) { //Lower R-L case
          x = grid.size() - 1 - c;
          y = c + r;
        } else { //Upper R-L case
          x = cEnd - 1 - c - r;
          y = c;
        }//end if
        
        //Break if we leave the grid boundaries
        if (x < 0 || y < 0 || x >= grid.size() || y >= grid.get(0).length() ) break;
        
        diag.append( grid.get(x).charAt(y) );
      }//end for
      
      //Skip diagonals that are too short to contain the KEY
      if (diag.length() < KEY.length() ) continue;
     
      //Search the diagonal 
      for(int d = 0; d < diag.length() - (KEY.length() - 1); d++) {

        //Search diagonal forwards
        if( diag.substring(d, d + KEY.length()).equals(KEY) ) {
      
          //Reverse-engineer center coords and verify they exist in coordsSet
          if ( isLower && coordsSet.contains( new Coords(grid.size() - 1 - d - KEY.length() / 2, r + d + KEY.length() / 2) ) ) {
            ctr++;
          } else if (!isLower && coordsSet.contains( new Coords(cEnd - 1 - d - r - KEY.length() / 2, d + KEY.length() / 2) ) ) {
            ctr++;
          }//end if
          
        }//end if
        
        diag.reverse(); //Put it in reverse, Terry!
        
        //Search diagonal backwards
        if( diag.substring(d, d + KEY.length() ).equals(KEY) ) {
          
          //Reverse-engineer center corods and verify they exist in coordsSet
          if( isLower && coordsSet.contains( new Coords( (grid.size() - 1) - (diag.length() - d - 1) + KEY.length() / 2, r + (diag.length() - d - 1) - KEY.length() / 2) ) )
            ctr++;
          } else if(!isLower && coordsSet.contains( new Coords(d + KEY.length() / 2, diag.length() - 1 - d - KEY.length() / 2) ) ) {
            ctr++;
          }//end if
          
        }//end if
        
        diag.reverse(); //Put that thing back where it came from or so help me... SO HELP ME!
      }//end for
      
    }//end for
    
    return ctr;
    
  }//end countXsInDiagonal
 
}//end Solution
