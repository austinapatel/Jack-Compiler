
// Created by Austin Patel on 7/23/16 at 7:59 PM

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

/**
 * Removes all comments and white space from the input stream and breaks it into
 * Jack-language tokens, as specified by the Jack grammar.
 */
public class JackTokenizer {

	private BufferedWriter writer;
	private String token, tokenType, fileContent;

	private String[] keywords = new String[] { "class", "constructor", "function", "method", "field", "static", "var",
			"int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while",
			"return" };

	private char[] symbols = new char[] { '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|',
			'<', '>', '=', '~' };

	// Converts between symbols that are unsupported in *.xml file to their
	// string equivalent. These symbols include '<', '>', '"', '&.'
	@SuppressWarnings("serial")
	private HashMap<Character, String> unsupportedXMLSymbols = new HashMap<Character, String>() {
		{
			put('<', "&lt;");
			put('>', "&gt;");
			put('"', "&quot;");
			put('&', "&amp;");
		}
	};

	/**
	 * Loads the the file input into a string and removes all whitespace and
	 * comments from it. Opens the output / file stream and gets ready to write
	 * the tokenized content to it.
	 */
	public JackTokenizer(File input, File output) {
		// Read the input file and put it into a String that does not have
		// whitespace or comments and open the file writer output.
		List<String> rawContent;
		try {
			rawContent = Files.readAllLines(input.toPath());
			fileContent = removeWhiteSpace(rawContent);

			writer = new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			System.out.println("Failed to open the input to the *.jack file and the writer for the T*.xml file.");
			e.printStackTrace();
		}

		// Write the openning tag of the *.xml file
		write("<tokens>");
	}

	/** Performs the complete tokenization of the input file. */
	public void performTokenization() {
		while (!fileContent.trim().equals("")) {
			advance();

			// Write the current token to the *.xml file
			write("<" + tokenType + '>' + ' ' + token + ' ' + "</" + tokenType + '>');
		}

		// Write the final closing tag of the *.xml file
		write("</tokens>");

		// Close the file writer
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Failed to flush and close the T*.xml file.");
			e.printStackTrace();
		}
	}

	/** Removes all of the whitespace in the file and the comments. */
	private String removeWhiteSpace(List<String> content) {
		boolean multiLineComment = false;

		// "Clean" each line one by one
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);

			// Delete multiple line comments
			if (line.contains("/**"))
				multiLineComment = true;

			if (multiLineComment)
				content.set(i, "");

			if (line.contains("*/"))
				multiLineComment = false;

			// Delete single line comments
			line = content.get(i);

			if (line.contains("//")) {
				content.set(i, line.substring(0, line.indexOf("//")));
			}

			// Remove beginning and trailing whitespace
			content.set(i, content.get(i).trim());
		}

		// Join together all the lines of the file into a single string
		String result = "";

		for (String line : content)
			result += line;

		return result;
	}

	/**
	 * Gets the next token from the input and makes it the current token. This
	 * method should only be called if "hasMoreTokens()" is true. Initially
	 * there is no current token.
	 */
	private void advance() {
		// Remove whitespace from the beginning of the content
		fileContent = fileContent.trim();

		// Handles keywords
		for (String keyword : keywords)
			if (fileContent.startsWith(keyword) && fileContent.charAt(keyword.length()) == ' ') {
				fileContent = fileContent.substring(keyword.length());
				token = keyword;
				tokenType = "keyword";

				return;
			}

		// Handles symbols
		for (char symbol : symbols)
			if (fileContent.charAt(0) == symbol) {
				fileContent = fileContent.substring(1);

				// If the current symbol is one that is unsupported by *.xml
				// files, then use the string equivalent.
				if (unsupportedXMLSymbols.containsKey(symbol))
					token = unsupportedXMLSymbols.get(symbol);
				else
					token = String.valueOf(symbol);

				tokenType = "symbol";

				return;
			}

		// Handles string constants
		if (fileContent.charAt(0) == '"') {
			// Remove the first "
			fileContent = fileContent.substring(1);

			String stringConst = "";

			// Keep reading characters until another " is reached
			while (fileContent.charAt(0) != '"') {
				stringConst += fileContent.charAt(0);
				fileContent = fileContent.substring(1);
			}

			// Remove the final "
			fileContent = fileContent.substring(1);

			token = stringConst;
			tokenType = "stringConstant";

			return;
		}

		// Handles integer constants
		if (Character.isDigit(fileContent.charAt(0))) {
			String integerConst = "";

			while (Character.isDigit(fileContent.charAt(0))) {
				integerConst += fileContent.charAt(0);
				fileContent = fileContent.substring(1);
			}

			token = integerConst;
			tokenType = "integerConstant";

			return;
		}

		// Handle identifiers
		String identifier = "";
		boolean valid = true;

		// Continue to loop through characters until one is not a valid part of
		// an identifier
		while (valid) {
			char curChar = fileContent.charAt(0);

			// Check if the next character is a symbol
			for (char symbol : symbols)
				if (curChar == symbol) {
					valid = false;

					break;
				}

			if (curChar == ' ')
				valid = false;

			// If the current character is valid add it to the identifier and
			// remove it from the file content
			if (valid) {
				identifier += fileContent.charAt(0);
				fileContent = fileContent.substring(1);
			}
		}

		token = identifier;
		tokenType = "identifier";

		return;
	}

	/** Writes a string to the output file. */
	private void write(String content) {
		try {
			writer.write(content);
			writer.newLine();
		} catch (IOException e) {
			System.out.println("Failed to write to the T*.xml file.");
			e.printStackTrace();
		}
	}
}
