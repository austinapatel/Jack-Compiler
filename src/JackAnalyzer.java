
// Created by Austin Patel on 7/23/16 at 6:42 PM

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The analyzer program operates on a given source, where source is either a
 * file name of the form Xxx.jack or a directory name containing one or more
 * such files. For each source Xxx.jack file, the analyzer goes through the
 * following logic: 1. Create a JackTokenizer from the Xxx.jack input file that
 * tokenizes the input and writes it to a file. 2. Use the CompilationEngine to
 * compile the input JackTokenizer into a *.vm file.
 */
public class JackAnalyzer {

	/**
	 * Performs all of the functions that the "JackAnalyzer" class intends to
	 * complete after getting the file location of the input files from the
	 * user. "args[0]" is the path of the *.jack file(s).
	 */
	public static void main(String[] args) {
		// Initialize the "jackFiles" array with the *.jack file(s) that should
		// be translated in *.vm file(s). Will ask the user to input a directory
		// containing the *.jack files or the location to a specific *.jack file
		// if no arguments were passed into this program from the command line.
		ArrayList<File> jackFiles = new ArrayList<File>();
		File readLocation;

		if (args.length == 0) {
			System.out.println("Enter the location of a *.jack file or a directory "
					+ "containing *.jack\nfiles to be compiled " + "into the virtual machine language:");

			Scanner scanner = new Scanner(System.in);
			readLocation = new File(scanner.nextLine().replace("\"", ""));
			scanner.close();
		} else {
			readLocation = new File(args[0]);
		}

		if (readLocation.isDirectory()) {
			// Go through every file in the directory and pick out only the *.jack files if the read location is a directory
			File[] files = readLocation.listFiles();
			
			for (File file : files)
				if (file.getAbsolutePath().contains(".jack"))
					jackFiles.add(file);			
		}
		else
			// Initialize the ArrayList with only one File if the read location is a *.jack file
			jackFiles.add(readLocation);

		// For each *.jack file that needs to be converted into *.vm file(s)
		// perform the three functions outlined by the "JackAnalyzer" class.
		for (File jackFile : jackFiles) {
			// 1. Create a JackTokenizer from the Xxx.jack input file that
			// tokenizes the input and writes it to a file.
			File tokenFile = new File(jackFile.getAbsolutePath().replace(".jack", "T_Token.xml"));
			JackTokenizer tokenizer = new JackTokenizer(jackFile, tokenFile);
			tokenizer.performTokenization();

			// 2. Use the CompilationEngine to compile the input JackTokenizer
			// into a *.vm file.
			File vmFile = new File(jackFile.getAbsolutePath().replace(".jack", "_Compiler.xml"));
			CompilationEngine compiler = new CompilationEngine(tokenFile, vmFile);
			compiler.compileClass();
			
			try {
				Files.delete(tokenFile.toPath());
				Files.delete(vmFile.toPath());
			} catch (IOException e) {
				System.out.println("Failed to delete Token and Compiler files.");
				e.printStackTrace();
			}
		}
		
		System.out.println("Compilation completed.");
	}
}
