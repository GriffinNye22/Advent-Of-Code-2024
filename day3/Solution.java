import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.*;

public class Solution {
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
	
	public static int calcResult(ArrayList<String> inp) {
		int total = 0;
		String regex = "mul\\((\\d{1,3}),\s*(\\d{1,3})\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher;
		
		for(int i = 0; i < inp.size(); i++) {
			matcher = pattern.matcher(inp.get(i) );
			
			while (matcher.find() ) {
				int mult = Integer.valueOf( matcher.group(1) ) * Integer.valueOf( matcher.group(2) );
				total += mult;
			}//end while
			
		}//end for
		
		return total;
	}//end calcResult
	
}//end Solution