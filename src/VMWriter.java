
// Created by Austin Patel on 8/2/16 at 5:38 PM

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Emits VM commands into a file, using the VM command syntax. */
public class VMWriter {

	enum Operation {
		ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
	}

	private BufferedWriter writer;
	private static int uniqueWhileNum, uniqueIfNum;

	/** Creates a new file and prepares it for writing. */
	public VMWriter(File output) {
		try {
			writer = new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the if and while counters when a new subroutine is reached since
	 * each label is unique to a subroutine so the number of if and while statements can
	 * be relative to the amount in a certain subroutine.
	 */
	static void subroutineDeclaration() {
		uniqueIfNum = uniqueWhileNum = 0;
	}

	/** Creates a unique label for use in loops. */
	static public String uniqueLabel(String base) {
		if (base.equals("WHILE_EXP"))
			uniqueWhileNum++;
		else if (base.equals("IF_TRUE"))
			uniqueIfNum++;

		return base + ((base.contains("IF") ? uniqueIfNum : uniqueWhileNum) - 1);
	}

	/** Writes a VM push command. */
	public void writePush(String segment, int index) {
		write("push " + segment + " " + index);
	}

	/** Writes a VM pop command. */
	public void writePop(String segment, int index) {
		write("pop " + segment + " " + index);
	}

	/** Writes a VM arithmetic command. */
	public void writeArithmetic(Operation operation) {
		write(operation.toString().toLowerCase());
	}

	/** Writes a VM label command. */
	public void writeLabel(String label) {
		write("label " + label);
	}

	/** Writes a VM goto command. */
	public void writeGoto(String label) {
		write("goto " + label);
	}

	/** Writes a VM if-goto command. */
	public void writeIf(String label) {
		write("if-goto " + label);
	}

	/** Writes a VM call command. */
	public void writeCall(String name, int nArgs) {
		write("call " + name + " " + nArgs);
	}

	/** Writes a VM function command. */
	public void writeFunction(String name, int nLocals) {
		write("function " + name + " " + nLocals);
	}

	/** Writes a VM return command. */
	public void writeReturn() {
		write("return");
	}

	/** Writes to the output file. */
	private void write(String string) {
		try {
			writer.write(string);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Closes the output file. */
	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
