package variable;

import java.util.*;
import java.io.*;


public class VariableSystem {
	
	/** The collection of Variable objects. */
	private Vector<Variable> m_variables;
	
	/** The collection of category names. */
	private Vector<String> m_categories;
	
	
	/** Constructs an empty VariableSystem object with a default size of 10. */
	public VariableSystem() {
		m_variables = new Vector<Variable>();
		m_categories = new Vector<String>();
	}
	
	/**
	 * Adds a category and returns the index of it after it is added (or the index of it if it already exists).
	 * 
	 * @param category the name of the category to be added.
	 * @return the index of the category, after it was added.
	 */
	public int addCategory(String category) {
		if(category == null) { return Variable.NO_CATEGORY; }
		String temp = category.trim();
		if(category.length() == 0) { return Variable.NO_CATEGORY; }
		
		for(int i=0;i<m_categories.size();i++) {
			if(temp.equalsIgnoreCase(m_categories.elementAt(i))) {
				return i;
			}
		}
		
		m_categories.add(temp);
		return m_categories.size() - 1;
	}

	/**
	 * Returns the index of the category, if it was found.
	 * 
	 * @param category the name of the category to be located.
	 * @return the index of the category, if it was found.
	 */
	public int indexOfCategory(String category) {
		if(category == null || m_categories.size() == 0) { return Variable.NO_CATEGORY; }
		
		String temp = category.trim();
		
		if(temp.length() == 0) { return Variable.NO_CATEGORY; }
		
		for(int i=0;i<m_categories.size();i++) {
			if(temp.equalsIgnoreCase(m_categories.elementAt(i))) {
				return i;
			}
		}

		return Variable.NO_CATEGORY;
	}

	
	/**
	 * Returns the name of a category at the specified index.
	 * 
	 * @param index the index of the category to be retrieved.
	 * @return the name of a category at the specified index.
	 */
	public String categoryAt(int index) {
		if(index < 0 || index >= m_categories.size()) { return null; }
		return m_categories.elementAt(index);
	}

	/**
	 * Returns the number of Variable objects stored within the collection of Variables.
	 * 
	 * @return the number of Variable objects stored within the collection of Variables.
	 */
	public int size() {
		return m_variables.size();
	}

	/**
	 * Checks to see if a Variable with an id matching the specified parameter exists. 
	 * 
	 * @param id the id to be matched.
	 * @return true if a Variable with an id matching the specified parameter is found.
	 */
	public boolean contains(String id) {
		if(id == null) { return false; }
		String temp = id.trim();
		if(temp.length() == 0) { return false; }
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks to see if a Variable with an id matching the specified parameters exists.
	 * 
	 * @param the id to be matched.
	 * @param category the name of the category the Variable belongs to.
	 * @return true if a Variable with an id matching the specified parameters is found.
	 */
	public boolean contains(String id, String category) {
		if(id == null || category == null) { return false; }
		String temp = id.trim();
		if(temp.length() == 0) { return false; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(categoryIndex == m_variables.elementAt(i).getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks to see if a matching Variable exists within the collection of Variables.
	 * 
	 * @param v the Variable to be matched.
	 * @return true if a matching Variable is found within the collection of Variables.
	 */
	public boolean contains(Variable v) {
		if(v == null) { return false; }
		
		// loop through and search for a Variable matching the corresponding parameter
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == v.getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(v.getID())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the index of the Variable matching the specified id if it exists, otherwise returns -1.
	 * 
	 * @param id the id to be matched.
	 * @return the index of the Variable matching the specified id if it exists, otherwise returns -1.
	 */
	public int indexOf(String id) {
		if(id == null) { return -1; }
		String temp = id.trim();
		if(temp.length() == 0) { return -1; }
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the Variable matching the specified id and category if it exists, otherwise returns -1.
	 * 
	 * @param id the id to be matched.
	 * @param category the name of the category the Variable belongs to.
	 * @return the index of the Variable matching the specified id and category if it exists, otherwise returns -1.
	 */
	public int indexOf(String id, String category) {
		if(id == null) { return -1; }
		String temp = id.trim();
		if(temp.length() == 0) { return -1; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the index of the Variable matching the specified Variable if it exists, otherwise returns -1.
	 * 
	 * @param v the Variable to be matched.
	 * @return the index of the Variable matching the specified Variable if it exists, otherwise returns -1.
	 */
	public int indexOf(Variable v) {
		if(v == null) { return -1; }
		
		// loop through and search for a Variable matching the corresponding parameter
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == v.getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(v.getID())) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the Variable located at the specified index, otherwise returns null if the index is out of range or there are no elements.
	 * 
	 * @param index the index of the Variable to be returned.
	 * @return the Variable located at the specified index, otherwise returns null if the index is out of range or there are no elements.
	 */
	public Variable variableAt(int index) {
		if(index < 0 || index >= m_variables.size()) { return null; }
		
		// return the Variable at the specified index if the index is within the boundaries of the collection of Variables
		return m_variables.elementAt(index);
	}
	
	/**
	 * Returns the Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @return the Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 */
	public Variable getVariable(String id) {
		if(id == null) { return null; }
		String temp = id.trim();
		if(temp.length() == 0) { return null; }
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return m_variables.elementAt(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @param category the name of the category associated with the variable.
	 * @return the Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 */
	public Variable getVariable(String id, String category) {
		if(id == null) { return null; }
		String temp = id.trim();
		if(temp.length() == 0) { return null; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return m_variables.elementAt(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the value of a Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @return the value of a Variable matching the corresponding id if it exists in the collection of Variables, otherwise returns null.
	 */
	public String getValue(String id) {
		if(id == null) { return null; }
		String temp = id.trim();
		if(temp.length() == 0) { return null; }
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return m_variables.elementAt(i).getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the value of a Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 * 
	 * @param id the id to be matched.
	 * @param category the name of the category associated with the variable.
	 * @return the value of a Variable matching the corresponding id and category if it exists in the collection of Variables, otherwise returns null.
	 */
	public String getValue(String id, String category) {
		if(id == null) { return null; }
		String temp = id.trim();
		if(temp.length() == 0) { return null; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id, if one exists then return the value of the corresponding Variable
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getCategory() == categoryIndex &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				return m_variables.elementAt(i).getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a collection of Variable objects associated with the specified category.
	 * 
	 * @param category the category associated with the Variables to be collected.
	 * @return a collection of Variable objects associated with the specified category.
	 */
	public Vector<Variable> getVariablesInCategory(String category) {
		if(category == null) { return null; }

		int categoryIndex = indexOfCategory(category);

		Vector<Variable> variableCollection = new Vector<Variable>();

		// collect all variables in the specified category
		for(int i=0;i<m_variables.size();i++) {
			if(categoryIndex == m_variables.elementAt(i).getCategory()) {
				variableCollection.add(m_variables.elementAt(i));
			}
		}
		
		return variableCollection;
	}
	
	/**
	 * Creates and adds a Variable object to the collection of Variables.
	 * 
	 * @param id the id of the Variable to be created.
	 * @param value the value of the Variable to be created.
	 * @return true if the Variable is valid and not already contained within the collection of Variables.
	 */
	public boolean add(String id, String value) {
		if(id == null || value == null) { return false; }
		if(!contains(id, "")) {
			m_variables.add(new Variable(id, value, Variable.NO_CATEGORY));
			return true;
		}
		return false;
	}
	
	/**
	 * Creates and adds a Variable object to the collection of Variables.
	 * 
	 * @param id the id of the Variable to be created.
	 * @param value the value of the Variable to be created.
	 * @param category the name of the category associated with the Variable.
	 * @return true if the Variable is valid and not already contained within the collection of Variables.
	 */
	public boolean add(String id, String value, String category) {
		if(id == null || value == null || category == null) { return false; }
		if(!contains(id, category)) {
			int categoryIndex = addCategory(category);
			m_variables.add(new Variable(id, value, categoryIndex));
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a Variable object to the collection of Variables.
	 * 
	 * @param v the Variable to be added to the collection of Variables.
	 * @return true if the Variable is valid and not already contained within the collection of Variables.
	 */
	public boolean add(Variable v) {
		// verify that the Variable is valid and not already contained in the Variables collection, then add it
		if(v == null || v.getID().length() == 0) { return false; }
		if(!contains(v) && v.getCategory() < m_categories.size()) {
			m_variables.add(v);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds (merges) a Vector of Variable objects into the current collection.
	 * 
	 * @param v a Vector of Variable objects to add (merge) into the current Variables collection.
	 */
	public void add(Variable[] v) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Vector of Variable objects and add them to the current collection
		for(int i=0;i<v.length;i++) {
			add(v[i]);
		}
	}
	
	/**
	 * Adds (merges) a Vector of Variable objects into the current collection.
	 * 
	 * @param v a Vector of Variable objects to add (merge) into the current Variables collection.
	 */
	public void add(Vector<Variable> v) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Vector of Variable objects and add them to the current collection
		for(int i=0;i<v.size();i++) {
			add(v.elementAt(i));
		}
	}

	/**
	 * Adds (merges) another VariableSystem into the current collection.
	 * 
	 * @param v the collection of Variables to add (merge) into the current VariableSystem. 
	 */
	public void add(VariableSystem v) {
		if(v == null) { return; }
		
		// loop through all of the variables in the specified Variables collection and add them to the current collection
		for(int i=0;i<v.m_variables.size();i++) {
			add(v.m_variables.elementAt(i));
		}
	}
	
	/**
	 * Updates the string value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, String value) {
		if(id == null || value == null) { return; }
		String temp = id.trim();
		if(id.length() == 0) { return; }
		
		boolean valueUpdated = false;
		
		for(int i=0;i<m_variables.size();i++) {
			if(temp.equalsIgnoreCase(m_variables.elementAt(i).getID())) {
				m_variables.elementAt(i).setValue(value);
				valueUpdated = true;
			}
		}

		// if the variable doesn't exist, add it
		if(!valueUpdated) {
			add(id, value);
		}
	}
	
	/**
	 * Updates the boolean value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, boolean value) {
		setValue(id, value ? "true" : "false");
	}
	
	/**
	 * Updates the char value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, char value) {
		setValue(id, Character.toString(value));
	}
	
	/**
	 * Updates the byte value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, byte value) {
		setValue(id, Byte.toString(value));
	}
	
	/**
	 * Updates the short value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, short value) {
		setValue(id, Short.toString(value));
	}
	
	/**
	 * Updates the integer value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, int value) {
		setValue(id, Integer.toString(value));
	}
	
	/**
	 * Updates the long value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, long value) {
		setValue(id, Long.toString(value));
	}
	
	/**
	 * Updates the float value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, float value) {
		setValue(id, Float.toString(value));
	}
	
	/**
	 * Updates the double value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, double value) {
		setValue(id, Double.toString(value));
	}

	/**
	 * Updates the object value of a Variable based on its id.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 */
	public void setValue(String id, Object value) {
		setValue(id, value == null ? "null" : value.toString());
	}
	
	/**
	 * Updates the string value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, String value, String category) {
		if(id == null || value == null || category == null) { return; }
		String temp = id.trim();
		if(temp.length() == 0) { return; }

		int categoryIndex = indexOfCategory(category);
		
		for(int i=0;i<m_variables.size();i++) {
			if(categoryIndex == m_variables.elementAt(i).getCategory() &&
			   temp.equalsIgnoreCase(m_variables.elementAt(i).getID())) {
				m_variables.elementAt(i).setValue(value);
				return;
			}
		}

		// if the variable doesn't exist, add it
		add(id, value, category);
	}

	/**
	 * Updates the boolean value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, boolean value, String category) {
		setValue(id, value ? "true" : "false", category);
	}

	/**
	 * Updates the char value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, char value, String category) {
		setValue(id, Character.toString(value), category);
	}
	
	/**
	 * Updates the short value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, short value, String category) {
		setValue(id, Short.toString(value), category);
	}
	
	/**
	 * Updates the integer value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, int value, String category) {
		setValue(id, Integer.toString(value), category);
	}

	/**
	 * Updates the long value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, long value, String category) {
		setValue(id, Long.toString(value), category);
	}

	/**
	 * Updates the long value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, float value, String category) {
		setValue(id, Float.toString(value), category);
	}
	
	/**
	 * Updates the double value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, double value, String category) {
		setValue(id, Double.toString(value), category);
	}

	/**
	 * Updates the double value of a Variable based on its id and category.
	 * 
	 * @param id the id of the Variable to be updated.
	 * @param value the value to update the Variable with.
	 * @param category the name of the category associated with the Variable.
	 */
	public void setValue(String id, Object value, String category) {
		setValue(id, value == null ? "null" : value.toString(), category);
	}
	
	/**
	 * Removes a Variable located at a specified index.
	 * 
	 * @param index the index from which to remove a Variable.
	 * @return true if the Variable was successfully removed from the Variables collection.
	 */
	public boolean remove(int index) {
		if(index < 0 || index >= m_variables.size()) { return false; }
		m_variables.remove(index);
		return true;
	}
	
	/**
	 * Removes a Variable based on its id.
	 * 
	 * @param id the id of the variable to remove.
	 * @return true if the Variable was located and removed from the collection of Variables.
	 */
	public boolean remove(String id) {
		if(id == null) { return false; }
		String temp = id.trim();
		if(temp.length() == 0) { return false; }
		
		// loop through and check to see if any variables contain a matching id, and remove the corresponding Variable if one is found
		for(int i=0;i<m_variables.size();i++) {
			if(m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				m_variables.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a Variable based on its id.
	 * 
	 * @param id the id of the Variable to remove.
	 * @param category the name of the category associated with the Variable.
	 * @return true if the Variable was located and removed from the collection of Variables.
	 */
	public boolean remove(String id, String category) {
		if(id == null) { return false; }
		String temp = id.trim();
		if(temp.length() == 0) { return false; }
		
		int categoryIndex = indexOfCategory(category);
		
		// loop through and check to see if any variables contain a matching id, and remove the corresponding Variable if one is found
		for(int i=0;i<m_variables.size();i++) {
			if(categoryIndex == m_variables.elementAt(i).getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				m_variables.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes a Variable from the current collection of Variables.
	 * 
	 * @param v the Variable to be removed.
	 * @return true if the Variable was located and removed from the collection of Variables.
	 */
	public boolean remove(Variable v) {
		if(v == null) { return false; }
		String temp = v.getID().trim();
		if(temp.length() == 0) { return false; }
		
		// loop through and check to see if any variables contain a matching id, and remove the corresponding Variable if one is found
		for(int i=0;i<m_variables.size();i++) {
			if(v.getCategory() == m_variables.elementAt(i).getCategory() &&
			   m_variables.elementAt(i).getID().equalsIgnoreCase(temp)) {
				m_variables.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the specified category and any variables associated with it.
	 * 
	 * @param data the name of the category to be removed.
	 */
	public void removeCategory(String data) {
		if(data == null) { return; }
		String category = data.trim();

		int categoryIndex = indexOfCategory(category);

		for(int i=0;i<m_variables.size();i++) {
			if(categoryIndex == m_variables.elementAt(i).getCategory()) {
				m_variables.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Clears the current collection of Variables.
	 */
	public void clear() {
		m_variables.clear();
		m_categories.clear();
	}
	
	// group all variables together based on their categories
	public void sort() {
		Variable temp;
		for(int i=0;i<m_variables.size();i++) {
			for(int j=i;j<m_variables.size();j++) {
				if(m_variables.elementAt(i).getCategory() > m_variables.elementAt(j).getCategory()) {
					temp = m_variables.elementAt(i);
					m_variables.set(i, m_variables.elementAt(j));
					m_variables.set(j, temp);
				}
			}
		}
	}
	
	/**
	 * Reads a collection of Variables from the specified file name.
	 * 
	 * @param fileName the name of the file to be parsed into a collection of variables.
	 * @return a collection of Variables read from the specified file.
	 */
	public static VariableSystem readFrom(String fileName) {
		if(fileName == null) { return null; }
		return readFrom(new File(fileName));
	}
	
	/**
	 * Reads a collection of Variables from the specified file and adds them to the current collection of Variables (if appropriate).
	 * 
	 * @param file the file to be parsed into a collection of Variables.
	 * @return a collection of Variables read from the specified file.
	 */
	public static VariableSystem readFrom(File file) {
		if(file == null || !file.exists() || !file.isFile()) { return null; }
		
		VariableSystem variables;
		
		BufferedReader in;
		String input, data;
		
		try {
			// open the file
			in = new BufferedReader(new FileReader(file));
			
			variables = new VariableSystem();
			String category = null;
			int categoryIndex = Variable.NO_CATEGORY;
			
			// read until the end of the file
			while((input = in.readLine()) != null) {
				data = input.trim();
				if(data.length() == 0) {
					category = null;
					categoryIndex = Variable.NO_CATEGORY;
					continue;
				}
				
				// parse a category
				if(data.length() >= 2 && data.charAt(0) == '[' && data.charAt(data.length() - 1) == ']') {
					category = data.substring(1, data.length() - 1).trim();
					categoryIndex = variables.addCategory(category);
				}
				// parse a variable
				else {
					Variable v = Variable.parseFrom(data);
					if(v != null) {
						v.setCategory(categoryIndex);
						variables.add(v);
					}
				}
			}
			
			in.close();
		}
		catch(IOException e) {
			return null;
		}
		
		return variables;
	}
	
	/**
	 * Outputs a collection of Variables to a specified file.
	 * 
	 * @param fileName the name of the file to write the collection of Variables to.
	 * @return true if writing to the file was successful.
	 */
	public boolean writeTo(String fileName) {
		return writeTo(new File(fileName));
	}
	
	/**
	 * Outputs a collection of Variables to a specified file.
	 * 
	 * @param file the file to write the collection of Variables to.
	 * @return true if writing to the file was successful.
	 */
	public boolean writeTo(File file) {
		if(file == null) { return false; }
		
		PrintWriter out;
		try {
			// open the file for writing, write to it and then close the file
			out = new PrintWriter(new FileWriter(file));
			writeTo(out);
			out.close();
		}
		catch(IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Writes a collection of Variables to a specified PrintWriter.
	 * 
	 * @param out the PrintWriter to write the collection of Variables to.
	 * @throws IOException if there was an error writing to the output stream.
	 */
	public void writeTo(PrintWriter out) throws IOException {
		int lastCategory = Variable.NO_CATEGORY;
		
		boolean firstLine = true;
		
		// output all of the variables to the file, grouped under corresponding categories
		for(int i=0;i<m_variables.size();i++) {
			if(lastCategory == Variable.NO_CATEGORY || lastCategory != m_variables.elementAt(i).getCategory()) {
				if(m_variables.elementAt(i).getCategory() != Variable.NO_CATEGORY) {
					if(!firstLine) { out.println(); }
					out.println("[" + m_categories.elementAt(m_variables.elementAt(i).getCategory()) + "]");
					firstLine = false;
				}
				lastCategory = m_variables.elementAt(i).getCategory();
			}
			m_variables.elementAt(i).writeTo(out);
			firstLine = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if(o == null || !(o instanceof VariableSystem)) { return false; }
		
		VariableSystem v = (VariableSystem) o;
		
		// check the size of each collection of Variables
		if(m_variables.size() != m_variables.size()) { return false; }
		
		// verify that each Variable in the current collection is also in the other collection of Variables
		for(int i=0;i<m_variables.size();i++) {
			if(!v.contains(m_variables.elementAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = new String();
		
		// return a String representation of the collection of Variables using comma to separate the data
		for(int i=0;i<m_variables.size();i++) {
			s += m_variables.elementAt(i);
			if(i < m_variables.size() - 1) {
				s += ", ";
			}
		}
		
		return s;
	}
	
}
