//Solution makes the assumptions that (based on various analyzed AoC inputs):
//   only one JNZ command will be encountered at the end of the program
//   only one ADV command will be encountered in the program with value of 3
//   only one OUT command will be encountered in the program
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Solution2 {
	
	private enum CMD {ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV}; 
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Long> registers = new ArrayList<>();
		ArrayList<Integer> opCodes = new ArrayList<>();
		ArrayList<Integer> operands = new ArrayList<>();
    ArrayList<Integer> solution = new ArrayList<>();
		Long regA;
		
		try {
			fileIn = openFile(FILENAME);
			readFile(fileIn, registers, opCodes, operands, solution);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}//end try-catch

		//Set Register A to 0 since it is ignored.
		//Also because of Assumption 1, program ends when RegA is 0.
		registers.set(0, 0L);
		
		//Uncorrupt Register A to find value that would produce output that 
		//matches program instructions
		regA = unCorruptRegA(registers, opCodes, operands, solution, true);
		
		System.out.println("Program Output: " + solution);
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
															ArrayList<Integer> opC, ArrayList<Integer> ops, ArrayList<Integer> prgm)
															throws NoSuchElementException, ArrayIndexOutOfBoundsException,
															NumberFormatException{
		
		try {
			//Read in register values
			for(int i = 0; i < 3; i++) {
				reg.add( Long.valueOf( fin.nextLine().split(":")[1].trim() ) );
			}//end for
			
			//Skip blank line
			fin.nextLine();
      
			//Split program instruction set into opCodes and operands
			String[] program = fin.nextLine().split(":")[1].trim().split(",");
			
			//Split opcodes and operands into respective lists
			for(int i = 0; i < program.length; i+=2) {
				Integer opCode = Integer.valueOf(program[i]);
				Integer operand = Integer.valueOf(program[i+1]);
				
				opC.add(opCode);
				ops.add(operand);
				
				prgm.add(opCode);
				prgm.add(operand);
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
  
	//Name: unCorruptRegA()
	//Desc: Recursive function for determining register value of A that will produce
	//      the same output as the input program instructions. Uses a brute-forceish 
	//      algo trying all combinations of 3 bit values to produce the nth last 
	//      value in the program instructions after running through a single iteration
	//      of the program instructions.
	//Params: ArrayList<Long> reg - List of Registers' A,B,C values
	//        ArrayList<Integer> opC - List of opcodes from program instructions
	//        ArrayList<Integer> ops - List of corresponding operands from program instructions
	//                                 (Parallel to opC)
	//        ArrayList<Integer> sol - The unprocessed program instructions remaining (solution)
	//Return: Long - The uncorrupted value of Register A
  public static Long unCorruptRegA(ArrayList<Long> reg, ArrayList<Integer> opC, 
                                   ArrayList<Integer> ops, ArrayList<Integer> sol) {
																		 
	long regAOrig = reg.get(0);
	System.out.println("Reg A: " + regAOrig);
	
	//If sol set is empty, then we've reached our answer!
	if( sol.isEmpty() ) return regAOrig;
	
	
   	//Since printed value is always 3-bit number
 	//only need to focus on testing values from 0 to 8
  	for(int i = 0; i < 8; i++) {
		StringBuilder output = new StringBuilder();
			
	  //Shifting 3 bits allows addition without affecting orig. bits
	  reg.set(0, (regAOrig << 3) + i); 
		reg.set(1, 0L);
		reg.set(1, 0L);
		
		//Process all instructions
		for(int iPtr = 0; iPtr < opC.size() - 1; iPtr++) {
			processCommand(reg, opC.get(iPtr), ops.get(iPtr), output);
		}//end for
		
		//If i value produced proper sol value, use regA value to check next sol value
		if( Integer.valueOf( output.toString() ) == sol.get( sol.size() - 1 ) ) {
			int rem = sol.remove( sol.size() - 1 ); //Remove solution value now that it's processed
			Long subSolution = unCorruptRegA(reg, opC, ops, sol);
			
			//If subSolution failed, move to next i value and add removed value back
			if(subSolution == null) {
				sol.add(rem);
				continue;
			}//end if
			
			return subSolution;
			
		}//end if
	
	}//end for			
    
		return null;
  }//end unCorruptRegA
	
	//Processes a single opCode command and applies changes to registers and or output buffer
	public static void processCommand(ArrayList<Long> reg, int opC, long arg, 
																		StringBuilder out) throws RuntimeException{
		
		long regA = reg.get(0), regB = reg.get(1), regC = reg.get(2);
		
		//Determine command type
		CMD cmd = convertToCMD((opC));
		
		//Process command
		switch(cmd) {
			case ADV: //Divide regA by 2^Combo arg => regA
				// arg = getComboOperandValue(arg, regA, regB, regC);
				// reg.set(0, regA >> arg);
				return; //Ignore ADV instructions due to assumptions
			case BXL: //XOR regB and literal arg => regB
				reg.set(1, XOR(regB, arg) );
				break;
			case BST: //Combo arg Mod 8 => regB
				arg = getComboOperandValue(arg, regA, regB, regC);
				reg.set(1, (long) (arg % 8) );
				break;
			case JNZ: //Jump instruction pointer to arg
				//JNZ instructions break our assumptions
				throw new RuntimeException("Additional jump encountered");
				// if (regA == 0) break;
				// iPtr = arg; //divided by 2???
				// break;
			case BXC: //XOR regB and regC => regC
				reg.set(1, XOR(regB, regC) );
				break;
			case OUT: //Output Combo arg Mod 8
				arg = getComboOperandValue(arg, regA, regB, regC);
				out.append( String.valueOf(arg % 8) );
				break;
			case BDV: //Divide regA by 2^Combo arg => regB
				arg = getComboOperandValue(arg, regA, regB, regC);
				reg.set(1, regA >> arg);
				break;
			case CDV: //Divide regA by 2^Combo arg => regC
				arg = getComboOperandValue(arg, regA, regB, regC);
				reg.set(2, regA >> arg);
				break;
		}//end switch
		
		
	}//end processCommand
	
	//Converts the given opCode to the CMD enum
	public static CMD convertToCMD(int opcode) {
		return CMD.values()[opcode];
	}//end converToCMD
	
	//Converts a literal operand to its combo operand value
	public static long getComboOperandValue(long arg, long regA, long regB, long regC) {
		
		switch( (int) arg) {
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
	
	//Performs bitwise XOR on two operands
	public static int XOR(int op1, int op2) {
		return op1 ^ op2;
	}//end XOR
	
	//Performs bitwise XOR on two operands
	public static long XOR(long op1, long op2) {
		return op1 ^ op2;
	}//end XOR
	
	
	
}//end Solution