import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;

public class Solution {
	
	private enum CMD {ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV}; 
	
	public static void main(String[] args) {
		final String FILENAME = "inp.dat";
		Scanner fileIn = null;
		ArrayList<Long> registers = new ArrayList<>();
		ArrayList<Integer> opCodes = new ArrayList<>();
		ArrayList<Integer> operands = new ArrayList<>();
		String output;
		
		try {
			fileIn = openFile(FILENAME);
			readFile(fileIn, registers, opCodes, operands);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}//end try-catch

		output = executeProgram(registers, opCodes, operands);
		
		System.out.println("Program Output: " + output);

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
															ArrayList<Integer> opC, ArrayList<Integer> ops)
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
				opC.add( Integer.valueOf( program[i] ) );
				ops.add( Integer.valueOf( program[i+1] ) );
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
	
	public static String executeProgram(ArrayList<Long> reg, ArrayList<Integer> opC, 
	                                    ArrayList<Integer> ops) {
																				
		long regA = reg.get(0), regB = reg.get(1), regC = reg.get(2);
		int iPtr = 0;
		StringBuilder output = new StringBuilder();
		
		//Continue until end of program reached
		while(iPtr < opC.size() ) {
			
			//Get next command	
			int opCode = opC.get(iPtr);
			
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
				  System.out.println("Reg A: " + regA);
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
		System.out.println("Register A: " + regA);
		System.out.println("Register B: " + regB);
		System.out.println("Register C: " + regC);
		
		//Remove extra comma and convert to string		
		return output.length() == 0 ? 
		       "" : output.deleteCharAt( output.length() - 1 ).toString();										
	}//end executeProgram
	
	public static CMD convertToCMD(int opcode) {
		return CMD.values()[opcode];
	}//end converToCMD
	
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
				return Integer.MAX_VALUE;
		}//end switch
	}//end getComboOperandValue
	
	public static int XOR(int op1, int op2) {
		return op1 ^ op2;
	}//end XOR
	
	public static long XOR(long op1, long op2) {
		return op1 ^ op2;
	}//end XOR
	
	
	
}//end Solution