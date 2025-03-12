import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;

public class Solution2 {
	
	private enum CMD {ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV}; 
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Long> registers = new ArrayList<>();
		ArrayList<Long> opCodes = new ArrayList<>();
		ArrayList<Long> operands = new ArrayList<>();
    StringBuilder program = new StringBuilder();
    String output;
    long regA;
		
		try {
			fileIn = openFile(FILENAME);
			readFile(fileIn, registers, opCodes, operands);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}//end try-catch
    
    for(int i = 0; i < opCodes.size(); i++) {
      program.append(opCodes.get(i)).append(",").append(operands.get(i)).append(",");
    }//end for
    
    program.deleteCharAt(program.length() - 1);
    regA = 1;
    
    do {
      
      System.out.println("Testing Register Value First Pass: " + regA);
      registers.set(0, regA);
      output = executeProgram(registers, opCodes, operands);
      regA *= 10;
      
    } while( output.length() <= program.length() );
    
    regA /= 10;
    int factor = 10;
    int i = 2;
    boolean found = false;
    long lowerBound = regA / factor;
    long upperBound = regA;
    
    while(!found) {
      boolean shrunk = false;
    
      for(regA = lowerBound; regA < upperBound; regA += (upperBound - lowerBound) / 10) {
        
        System.out.println("Testing Register Value " + i + "th pass: " + regA);
        registers.set(0, regA);
        output = executeProgram(registers, opCodes, operands);
        
        System.out.println(output);
        
        //If exact match, we're done!!!
        if( output.equals(program.toString() ) ) {
          found = true;
          break;
        }//end if
       
        //If generated output is longer than program, regA is higher than solution!
        if( output.length() > program.length() ) {
          
          System.out.println("EXCEEDED! " + output.toString() ); 
          
          //Adjust bounds based on which region would provide narrower search
         //if( regA - lowerBound < upperBound - regA) { //Smaller lowerBound -> regA region
            upperBound = regA;
            shrunk = true;
          //} else {
            //lowerBound = regA;
            //shrunk = true;
          //}//end if
          
          break;
        }//end if
        
      }//end for
      
      if(!shrunk) break;
      i++;
      
    }//end while
    
    System.out.println("Brute forcing between: " + lowerBound + "  and " + upperBound);
    
    for(regA = lowerBound; regA < upperBound; regA++) {
       System.out.println("Brute Forcing Register Value: " + regA);
       registers.set(0, regA);
       output = executeProgram(registers, opCodes, operands);
       System.out.println(output);
       if( output.equals(program.toString() ) ) break;
    }
		
		System.out.println("Program Output: " + output);
    System.out.println("Register A Value: " + regA);

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
	
	public static void readFile(Scanner fin, ArrayList<Long> reg, 
															ArrayList<Long> opC, ArrayList<Long> ops)
															throws NoSuchElementException, ArrayIndexOutOfBoundsException,
															NumberFormatException{
		
		try {
			//Read in register values
			for(int i = 0; i < 3; i++) {
				reg.add( Long.valueOf( fin.nextLine().split(":")[1].trim() ) );
			}//end for
			
			//Skip blank line
			fin.nextLine();
			
			//Read in program instructions and operands
			String[] program = fin.nextLine().split(":")[1].trim().split(",");
			
			//Split opcodes and operands into respective lists
			for(int i = 0; i < program.length; i+=2) {
				opC.add( Long.valueOf( program[i] ) );
				ops.add( Long.valueOf( program[i+1] ) );
			}//end for
			
		} catch (NoSuchElementException e) {
			String format = "Register A: <val>\nRegister B: <val>\nRegister C: <val>\n"
			              + "\nProgram: <opcode>,<operand>...\n";
			throw new NoSuchElementException("ERROR: Incorrect file format.\n" +
			                                "Format should match: \n" + format);
		} catch (ArrayIndexOutOfBoundsException e) {
			String format = "Register A: <val>\nRegister B: <val>\nRegister C: <val>\n"
			              + "\nProgram: <opcode>,<operand>...\n";
			throw new ArrayIndexOutOfBoundsException("ERROR: Incorrect file format.\n" +
			                                "Format should match: \n" + format);
		} catch (NumberFormatException e) {
			String format = "Register A: <val>\nRegister B: <val>\nRegister C: <val>\n"
			              + "\nProgram: <opcode>,<operand>...\n";
			throw new NumberFormatException("ERROR: Incorrect file format.\n" +
			                                "Format should match: \n" + format);
		}//end try-catch
		
	}//end readFile
	
	public static String executeProgram(ArrayList<Long> reg, ArrayList<Long> opC, 
	                                    ArrayList<Long> ops) {
																				
		long regA = reg.get(0); long regB = reg.get(1); long regC = reg.get(2);
		int iPtr = 0;
		StringBuilder output = new StringBuilder();
		
		//Continue until end of program reached
		while(iPtr < opC.size() ) {
			
			//Get next command	
			long opCode = opC.get(iPtr);
			
			//Get next operand
			long arg = ops.get(iPtr);
			
			//Determine command type
			CMD cmd = convertToCMD(opCode);
			
			//Process command
			switch(cmd) {
				case ADV: //Divide regA by 2^Combo arg => regA
					arg = getComboOperandValue(arg, regA, regB, regC);
					regA = regA / (int) Math.pow(2, arg);
					break;
				case BXL: //XOR regB and literal arg => regB
					regB = XOR(regB, arg);
					break;
				case BST: //Combo arg Mod 8 => regB
					arg = getComboOperandValue(arg, regA, regB, regC);
					regB = arg % 8;
					break;
				case JNZ: //Jump instruction pointer to arg
					if (regA == 0) break;
					iPtr = (int) arg; //divided by 2???
					break;
				case BXC: //XOR regB and regC => regC
					regB = XOR(regB, regC);
					break;
				case OUT: //Output Combo arg Mod 8
					arg = getComboOperandValue(arg, regA, regB, regC);
					output.append( String.valueOf(arg % 8) ).append(",");
					break;
				case BDV: //Divide regA by 2^Combo arg => regB
					arg = getComboOperandValue(arg, regA, regB, regC);
					regB = regA / (int) Math.pow(2, arg);
					break;
				case CDV: //Divide regA by 2^Combo arg => regC
					arg = getComboOperandValue(arg, regA, regB, regC);
					regC = regA / (int) Math.pow(2, arg);
					break;
			}//end switch
			
			//Update instruction pointer
			if(cmd != CMD.JNZ || (cmd == CMD.JNZ && regA == 0) ) {
				iPtr ++;
			}//end if
			
		}//end while
		
		//Debug
		// System.out.println("Register A: " + regA);
		// System.out.println("Register B: " + regB);
		// System.out.println("Register C: " + regC);
		
		//Remove extra comma and convert to string		
		return output.length() == 0 ? 
		       "" : output.deleteCharAt( output.length() - 1 ).toString();										
	}//end executeProgram
	
	public static CMD convertToCMD(long opcode) {
		return CMD.values()[(int)opcode];
	}//end converToCMD
	
	public static long getComboOperandValue(long arg, long regA, long regB, long regC) {
		
		switch((int) arg) {
			case 0:
			case 1:
			case 2:
			case 3:
				return arg;
			case 4:
				return regA;
			case 5:
				return regB;
			case 6:
				return regC;
			default:
				return Long.MAX_VALUE;
		}//end switch
	}//end getComboOperandValue
	
	public static long XOR(long op1, long op2) {
		return op1 ^ op2;
	}//end XOR
	
	
	
}//end Solution