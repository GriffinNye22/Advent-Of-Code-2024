import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.*;

public class Solution2 {
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<String> memory = new ArrayList<>();
		
		try {
			fileIn = openFile(FILENAME);
		} catch (FileNotFoundException e) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
	
		readFile(fileIn, memory);
		
		System.out.println( calcResult(memory) );
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
	
	public static void readFile(Scanner fin, ArrayList<String> inp) {
		
		while(fin.hasNextLine() ) {
			inp.add( fin.nextLine() );
		}//end while
		
	}//end readFile
	
	public static long calcResult(ArrayList<String> inp) {
		long total = 0;
		boolean multOff = false;
		
		String mulRegex = "mul\\((\\d{1,3}),\s*(\\d{1,3})\\)";
		String dontRegex = "don't\\(\\)";
		String doRegex = "do\\(\\)";
		
		Pattern mulPattern = Pattern.compile(mulRegex);
		Pattern dontPattern = Pattern.compile(dontRegex);
		Pattern doPattern = Pattern.compile(doRegex);
		
		Matcher mulMatcher;
		Matcher dontMatcher;
		Matcher doMatcher;
		
		for(int i = 0; i < inp.size(); i++) {
			String line = inp.get(i);
			mulMatcher = mulPattern.matcher( line );
			dontMatcher = dontPattern.matcher( line );
			doMatcher = doPattern.matcher( line );
			
			int idx = 0;
			
			//System.out.println(line.length() );
			
			while (idx < line.length() ) {
				int mulIdx = mulMatcher.find(idx) ? mulMatcher.end() : line.length() + 1;
				int dontIdx = dontMatcher.find(idx) ? dontMatcher.end() : line.length() + 1;
				int doIdx = doMatcher.find(idx) ? doMatcher.end() : line.length() + 1;
				int min = Math.min( Math.min(mulIdx, dontIdx), doIdx); 
				
				//System.out.println("idx: " + idx);
				
				if (min == line.length() + 1) {
					break;
				}//end if
				
				if (min == mulIdx) {
						
					if (!multOff) {
						long mult = Integer.valueOf( mulMatcher.group(1) ) * Integer.valueOf( mulMatcher.group(2) );
						System.out.println(mult);
						total += mult;
					}//ebd if
						
				} else if (min == dontIdx) {
					multOff = true;
					System.out.println("DONT" + total);
				} else if (min == doIdx) {
					System.out.println("DO");
					multOff = false;
				} else {
					System.err.println("Congrats, you fucked it up.");
				}//end if
				
				idx = min;
			}//end while
			
		}//end for
		
		return total;
	}//end calcResult
	
}//end Solution