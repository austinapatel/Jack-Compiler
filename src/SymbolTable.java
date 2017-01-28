
// Created by Austin Patel on 8/2/16 at 10:30 AM

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides a symbol table abstraction. The symbol table associates the
 * identifier names found in the program with identifier properties needed for
 * compilation: type, kind, and running index. The symbol table for Jack
 * programs has two nested scopes (class/subroutine).
 */
public class SymbolTable {

	/**
	 * Contains all the information that should be stored in a row of the symbol
	 * table (i.e. type, scope, name, index).
	 */
	private class TableRow {

		/**
		 * Initializes a new TableRow and stores all of the necessary
		 * information.
		 */
		public TableRow(String type, VariableScope scope, String name, int index) {
			this.type = type;
			this.scope = scope;
			this.name = name;
			this.index = index;
		}

		private String type;
		private VariableScope scope;
		private String name;
		private int index;

		public String getType() {
			return type;
		}

		public VariableScope getScope() {
			return scope;
		}

		public String getName() {
			return name;
		}

		public int getIndex() {
			return index;
		}
	}

	public enum VariableScope {
		STATIC, FIELD, ARG, VAR, NONE
	}

	private ArrayList<TableRow> classTable, subroutineTable;
	private HashMap<VariableScope, Integer> variableIndexes;

	/** Creates a new empty symbol table. */
	public SymbolTable() {
		// Initialize the class table
		classTable = new ArrayList<>();
		subroutineTable = new ArrayList<>();

		// Initialize the variable index HashMap
		variableIndexes = new HashMap<VariableScope, Integer>();

		for (VariableScope scope : VariableScope.values())
			variableIndexes.put(scope, 0);
	}

	/** Converts between "VariableScope" enumeration and VM virtual segment. */
	public static String scopeSegment(VariableScope scope) {
		String scopeString = scope.toString();

		if (scopeString.equals("STATIC"))
			return "static";
		else if (scopeString.equals("FIELD"))
			return "this";
		else if (scopeString.equals("ARG"))
			return "argument";
		else if (scopeString.equals("VAR"))
			return "local";
		
		return "";
	}

	/**
	 * Starts a new subroutine scope (i.e., resets the subroutine's symbol
	 * table).
	 */
	public void startSubroutine() {
		// Reset the subroutine table
		subroutineTable = new ArrayList<TableRow>();

		// Reset the "VAR" and "ARG" variable types in "variableIndexes" HashMap
		variableIndexes.put(VariableScope.ARG, 0);
		variableIndexes.put(VariableScope.VAR, 0);
		
		// Reset the label counters
		VMWriter.subroutineDeclaration();
	}

	/**
	 * Defines a new identifier of a given name, type, and kind and assigns it a
	 * running index. STATIC and FIELD identifiers have a class scope, while ARG
	 * and VAR identifiers have a subroutine scope.
	 */
	public void define(String name, String type, VariableScope scope) {
		// Create a new row with the given information
		TableRow tableRow = new TableRow(type, scope, name, variableIndexes.get(scope));

		// Increment the index for the scope of the new variable
		variableIndexes.put(scope, variableIndexes.get(scope) + 1);

		// Add the new variable to the correct table based on its scope
		if (scope.toString().matches("STATIC|FIELD"))
			classTable.add(tableRow);
		else
			subroutineTable.add(tableRow);
	}

	/**
	 * Returns the number of variables of the given kind already defined in the
	 * current scope.
	 */
	public int varCount(VariableScope scope) {
		return variableIndexes.get(scope);
	}

	/**
	 * Returns the kind of the named identifier in the current scope. If the
	 * identifier is unknown in the current scope, returns NONE.
	 */
	public VariableScope kindOf(String name) {
		// Look in the subroutine table to see if it contains the variable
		for (TableRow tableRow : subroutineTable)
			if (tableRow.getName().equals(name))
				return tableRow.getScope();

		// If the variable is not in the subroutine table, then look in the
		// class table
		for (TableRow tableRow : classTable)
			if (tableRow.getName().equals(name))
				return tableRow.getScope();

		return VariableScope.NONE;
	}

	/** Returns the type of the named identifier in the current scope. */
	public String typeOf(String name) {
		// Look in the subroutine table to see if it contains the variable
		for (TableRow tableRow : subroutineTable)
			if (tableRow.getName().equals(name))
				return tableRow.getType();

		// If the variable is not in the subroutine table, then look in the
		// class table
		for (TableRow tableRow : classTable)
			if (tableRow.getName().equals(name))
				return tableRow.getType();

		return null;
	}

	/** Returns the index assigned to the named identifier. */
	public int indexOf(String name) {
		// Look in the subroutine table to see if it contains the variable
		for (TableRow tableRow : subroutineTable)
			if (tableRow.getName().equals(name))
				return tableRow.getIndex();

		// If the variable is not in the subroutine table, then look in the
		// class table
		for (TableRow tableRow : classTable)
			if (tableRow.getName().equals(name))
				return tableRow.getIndex();

		return 0;
	}
}
