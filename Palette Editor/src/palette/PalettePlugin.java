package palette;

import java.util.*;
import java.util.regex.*;
import java.util.jar.*;
import java.io.*;
import java.lang.reflect.*;
import exception.*;
import utilities.*;
import variable.*;

public class PalettePlugin {
	
	protected String m_name;
	protected Vector<String> m_supportedPaletteFileTypes;
	protected boolean m_instantiable;
	protected double m_pluginVersion;
	protected String m_directoryName;
	protected String m_configFileName;
	protected String m_jarFileName;
	protected String m_paletteClassName;
	protected Class<?> m_paletteClass;
	protected HashMap<String, Class<?>> m_classes;
	protected boolean m_loaded;
	
	public static final String PALETTE_PLUGIN_DEFINITION_FILE_HEADER = "Palette Plugin Definition File";
	public static final String PALETTE_PLUGIN_DEFINITION_FILE_VERSION = "1.0";
	public static final String PLUGIN_VERSION = "1.0";
	public static final String PLUGIN_TYPE = "Palette";
	public static final String CONFIG_FILE_TYPES[] = new String[] { "CFG" };
	
	protected PalettePlugin(String configFileName, String directoryName) {
		m_name = null;
		m_supportedPaletteFileTypes = new Vector<String>();
		m_instantiable = false;
		m_pluginVersion = -1.0;
		m_directoryName = directoryName == null ? null : directoryName.trim();
		m_configFileName = configFileName == null ? null : configFileName.trim();
		m_jarFileName = null;
		m_paletteClassName = null;
		m_classes = new HashMap<String, Class<?>>();
		m_loaded = false;
	}
	
	public String getName() {
		return m_name;
	}
	
	public double getPluginVersion() {
		return m_pluginVersion;
	}
	
	public int numberOfSupportedPaletteFileTypes() {
		return m_supportedPaletteFileTypes.size();
	}
	
	public String getSupportedPaletteFileType(int index) {
		if(index < 0 || index >= m_supportedPaletteFileTypes.size()) { return null; }
		return m_supportedPaletteFileTypes.elementAt(index);
	}
	
	public String getSupportedPaletteFileTypesAsString() {
		String listOfSupportedPaletteFileTypes = "";
		
		for(int i=0;i<m_supportedPaletteFileTypes.size();i++) {
			listOfSupportedPaletteFileTypes += m_supportedPaletteFileTypes.elementAt(i);
			
			if(i < m_supportedPaletteFileTypes.size() - 1) {
				listOfSupportedPaletteFileTypes += ", ";
			}
		}
		
		return listOfSupportedPaletteFileTypes;
	}
	
	public Vector<String> getSupportedPaletteFileTypes() {
		return m_supportedPaletteFileTypes;
	}
	
	public boolean hasSupportedPaletteFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedPaletteFileTypes.size();i++) {
			if(m_supportedPaletteFileTypes.elementAt(i).equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSharedSupportedPaletteFileType(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return false; }
		
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileTypes();i++) {
			if(hasSupportedPaletteFileType(palettePlugin.getSupportedPaletteFileType(i))) {
				return true;
			}
		}
		return false;
	}
	
	public int numberOfSharedSupportedPaletteFileTypes(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return 0; }
		
		int numberOfSharedSupportedPaletteFileTypes = 0;
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileTypes();i++) {
			if(hasSupportedPaletteFileType(palettePlugin.getSupportedPaletteFileType(i))) {
				numberOfSharedSupportedPaletteFileTypes++;
			}
		}
		return numberOfSharedSupportedPaletteFileTypes;
	}

	public Vector<String> getSharedSupportedPaletteFileTypes(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return null; }
		
		Vector<String> sharedSupportedPaletteFileTypes = new Vector<String>();
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileTypes();i++) {
			if(hasSupportedPaletteFileType(palettePlugin.getSupportedPaletteFileType(i))) {
				sharedSupportedPaletteFileTypes.add(palettePlugin.getSupportedPaletteFileType(i));
			}
		}
		return sharedSupportedPaletteFileTypes;
	}
	
	public String getSharedSupportedPaletteFileTypesAsString(PalettePlugin palettePlugin) {
		if(palettePlugin == null) { return null; }
		
		String sharedSupportedPaletteFileTypes = new String();
		for(int i=0;i<palettePlugin.numberOfSupportedPaletteFileTypes();i++) {
			if(hasSupportedPaletteFileType(palettePlugin.getSupportedPaletteFileType(i))) {
				if(sharedSupportedPaletteFileTypes.length() > 0) {
					sharedSupportedPaletteFileTypes += ", ";
				}
				
				sharedSupportedPaletteFileTypes += palettePlugin.getSupportedPaletteFileType(i);
			}
		}
		return sharedSupportedPaletteFileTypes;
	}
	
	public int indexOfSupportedPaletteFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		
		for(int i=0;i<m_supportedPaletteFileTypes.size();i++) {
			if(m_supportedPaletteFileTypes.elementAt(i).equalsIgnoreCase(type)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean addSupportedPaletteFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<m_supportedPaletteFileTypes.size();i++) {
			if(m_supportedPaletteFileTypes.elementAt(i).equalsIgnoreCase(type)) {
				return false;
			}
		}
		
		m_supportedPaletteFileTypes.add(type);
		
		return true;
	}
	
	public boolean isInstantiable() {
		return m_instantiable;
	}
	
	public String getDirectoryName() {
		return m_directoryName;
	}
	
	public String getConfigFileName() {
		return m_configFileName;
	}
	
	public boolean setConfigFileName(String configFileName) {
		if(configFileName == null) { return false; }
		String tempName = configFileName.trim();
		if(tempName.length() == 0) { return false; }
		
		m_configFileName = configFileName;
		
		return true;
	}
	
	public String getJarFileName() {
		return m_jarFileName;
	}

	public String getPaletteClassName() {
		return m_paletteClassName;
	}
	
	public int numberOfClasses() {
		return m_classes.size();
	}
	
	public Class<?> getLoadedClass(String className) {
		if(className == null) { return null; }
		
		return m_classes.get(className.replaceAll("[\\\\/]", ".").replaceAll("\\.[Cc][Ll][Aa][Ss][Ss]$", ""));
	}
	
	public Palette getPaletteInstance(File paletteFile) throws PaletteInstantiationException {
		Constructor<?> constructor = null;
		try { constructor = m_paletteClass.getDeclaredConstructor(File.class); }
		catch(Exception e) { }
		
		if(constructor == null) {
			throw new PaletteInstantiationException("Palette class \"" + m_paletteClassName + "\" must contain a constructor which takes a File as an argument.");
		}
		
		Palette newPalette = null;
		try {
			newPalette = (Palette) constructor.newInstance(paletteFile);
		}
		catch(Exception e) {
			throw new PaletteInstantiationException("Failed to instantiate palette class \"" + m_paletteClassName + "\": " + e.getMessage());
		}
		
		return newPalette;
	}
	
	public boolean isLoaded() {
		return m_loaded;
	}
	
	public boolean load() {
		if(m_loaded) { return true; }
		
		if(!loadClasses()) { return false; }
		
		m_loaded = true;
		
		return true;
	}
	
	protected boolean loadClasses() {
		if(m_loaded) { return true; }
		if(m_jarFileName == null) { return false; }
		
		InputStream in;
		String name;
		byte[] data;
		JarFile jarFile = null;
		JarEntry e;
		Class<?> c;
		
		try {
			jarFile = new JarFile(Utilities.appendSlash(PaletteEditor.settings.pluginDirectoryName) + Utilities.appendSlash(m_directoryName) + "/" + m_jarFileName);
			
			Pattern p = Pattern.compile(".*\\.class$", Pattern.CASE_INSENSITIVE);
			
			Enumeration<JarEntry> contents = jarFile.entries();
			while(contents.hasMoreElements()) {
				e = contents.nextElement();
				if(p.matcher(e.getName()).matches()) {
					in = jarFile.getInputStream(e);
					if(in.available() < 1) {
						jarFile.close();
						
						return false;
					}
					data = new byte[in.available()];
					in.read(data);
					
					name = e.getName().replaceAll("[\\\\/]", ".").replaceAll("\\.[Cc][Ll][Aa][Ss][Ss]$", "");
					
					c = PaletteEditor.classLoader.deserializeClass(name, data);
					if(c == null) {
						jarFile.close();
						
						return false;
					}
					
					m_classes.put(name, c);
				}
			}
			
			jarFile.close();
			
			return true;
		}
		catch(IOException e2) {
			try { jarFile.close(); } catch(Exception e3) { }
			return false;
		}
	}
	
	protected static String readPluginDefinitionFileVersion(BufferedReader in, File file) throws IOException, PalettePluginLoadException {
		if(in == null || file == null) { return null; }
		
		String input, header;
		while(true) {
			input = in.readLine();
			if(input == null) {
				in.close();
				throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no header found.");
			}
			
			header = input.trim();
			if(header.length() == 0) { continue; }
			
			if(!header.matches("^.* ([0-9]\\.?)+$")) {
				in.close();
				throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" has an invalid header: \"" + header + "\".");
			}
			String[] headerData = new String[2];
			int separatorIndex = header.lastIndexOf(' ');
			if(separatorIndex < 0) {
				in.close();
				throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" is missing version number in header.");
			}
			headerData[0] = header.substring(0, separatorIndex);
			headerData[1] = header.substring(separatorIndex + 1, header.length());
			
			if(!headerData[0].trim().equalsIgnoreCase(PALETTE_PLUGIN_DEFINITION_FILE_HEADER)) {
				in.close();
				throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" has an invalid header: \"" + headerData[0] + "\", expected \"" + PALETTE_PLUGIN_DEFINITION_FILE_HEADER + "\".");
			}
			
			return headerData[1];
		}
	}
	
	protected static boolean verifyDefinitionFileVersion(String version) {
		if(version == null) { return false; }
		String cfgVersion = version.trim();
		if(cfgVersion.length() == 0) { return false; }
		
		return Utilities.compareVersions(PALETTE_PLUGIN_DEFINITION_FILE_VERSION, cfgVersion) == 0;
	}
	
	public static boolean isPalettePlugin(File file) throws PalettePluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyDefinitionFileVersion(readPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					throw new PalettePluginLoadException("Unsupported plugin definition file version, only version " + PALETTE_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				throw new PalettePluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			in.close();
			
			return true;
		}
		catch(FileNotFoundException e) {
			throw new PalettePluginLoadException("Missing palette plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new PalettePluginLoadException("Read exception thrown while parsing palette plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
	}
	
	public static String getPalettePluginName(File file) throws PalettePluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		String input, temp;
		Variable v;
		String name = null;
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyDefinitionFileVersion(readPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					throw new PalettePluginLoadException("Unsupported plugin definition file version, only version " + PALETTE_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				throw new PalettePluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette plugin name variable in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Name")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette plugin name variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				name = v.getValue();
				if(name.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Invalid empty palette plugin name found in palette definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			in.close();
			
			return name;
		}
		catch(FileNotFoundException e) {
			throw new PalettePluginLoadException("Missing palette plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new PalettePluginLoadException("Read exception thrown while parsing palette plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
	}
	
	public static PalettePlugin loadFrom(File file) throws PalettePluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		
		PalettePlugin palettePlugin = null;
		
		String directoryName;
		if(file.getPath().matches(".*[\\\\/].*")) {
			directoryName = file.getPath().replaceAll("[\\\\/][^\\\\/]*$", "").replaceAll("^.*[\\\\/]", "");
		}
		else {
			directoryName = "/";
		}
		
		String fileExtension = Utilities.getFileExtension(file.getName());
		
		if(fileExtension.equalsIgnoreCase("cfg")) {
			palettePlugin = loadFromCFGFile(file, directoryName);
		}
		else {
			throw new PalettePluginLoadException("Unsupported palette plugin configuration file type: \"" + fileExtension + "\".");
		}
		
		return palettePlugin;
	}
	
	protected static PalettePlugin loadFromCFGFile(File file, String directory) throws PalettePluginLoadException {
		if(file == null || !file.exists() || !file.isFile()) {
			throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" is missing or invalid.");
		}
		if(directory == null) {
			throw new PalettePluginLoadException("Palette plugin must have a non-null directory name.");
		}
		String directoryName = directory.trim();
		if(directoryName.length() == 0) {
			throw new PalettePluginLoadException("Palette plugin must have a non-empty directory name.");
		}
		
		String input, temp;
		BufferedReader in;
		Variable v;
		PalettePlugin palettePlugin = new PalettePlugin(file.getName(), directoryName);
		try {
			in = new BufferedReader(new FileReader(file));
			
			try {
				if(!verifyDefinitionFileVersion(readPluginDefinitionFileVersion(in, file))) {
					in.close();
					
					throw new PalettePluginLoadException("Unsupported plugin definition file version, only version " + PALETTE_PLUGIN_DEFINITION_FILE_VERSION + " is supported. Maybe check for updates, or verify your plugin definition files?");
				}
			}
			catch(IllegalArgumentException e) {
				in.close();
				
				throw new PalettePluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin version found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette plugin version variable in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Version")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette plugin version variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				String pluginVersionData = v.getValue();
				if(pluginVersionData.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty palette plugin version found in palette definition file: \"" + file.getName() + "\".");
				}
				
				try {
					if(Utilities.compareVersions(PLUGIN_VERSION, pluginVersionData) != 0) {
						in.close();
						
						throw new PalettePluginLoadException("Unsupported plugin version, only version " + PLUGIN_VERSION + " is supported. Maybe check for updates, or verify your config files?");
					}
				}
				catch(IllegalArgumentException e) {
					in.close();
					
					throw new PalettePluginLoadException("Invalid plugin version specified in plugin definition file \"" + file.getName() + "\": " + e.getMessage());
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin type found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette plugin type variable in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Type")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette plugin type variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				String pluginType = v.getValue();
				if(pluginType.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty plugin type found in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!pluginType.equalsIgnoreCase(PLUGIN_TYPE)) {
					in.close();
					throw new PalettePluginLoadException("Unsupported plugin type \"" + pluginType + "\" found in palette definition file: \"" + file.getName() + "\", only type " + PLUGIN_TYPE + " is supported.");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no plugin name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette plugin name variable in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Name")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette plugin name variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				palettePlugin.m_name = v.getValue();
				if(palettePlugin.m_name.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty palette plugin name found in palette definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no suppported palette file type list found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse suppported palette file type list variable in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Supported Palette File Types")) {
					in.close();
					throw new PalettePluginLoadException("Expected suppported palette file type list variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				String supportedPaletteFileTypes = v.getValue();
				if(supportedPaletteFileTypes.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty supported palette file type list found in palette definition file: \"" + file.getName() + "\".");
				}
				
				String supportedPaletteFileType = null;
				String supportedPaletteFileTypeList[] = supportedPaletteFileTypes.split("[;, \t]");
				for(int i=0;i<supportedPaletteFileTypeList.length;i++) {
					supportedPaletteFileType = supportedPaletteFileTypeList[i].trim();
					if(supportedPaletteFileType.length() > 0) {
						palettePlugin.addSupportedPaletteFileType(supportedPaletteFileType);
					}
				}
				
				if(palettePlugin.numberOfSupportedPaletteFileTypes() == 0) {
					throw new PalettePluginLoadException("Palette plugin \"" + palettePlugin.getName() + "\" in palette definition file: \"" + file.getName() + "\" must support at least one file type.");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no instantiable property found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette instantiable property in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Instantiable")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette instantiable variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				String instantiable = v.getValue();
				if(instantiable.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty palette instantiable property found in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(instantiable.equalsIgnoreCase("true") || instantiable.equalsIgnoreCase("1") || instantiable.equalsIgnoreCase("yes") || instantiable.equalsIgnoreCase("enabled")) {
					palettePlugin.m_instantiable = true;
				}
				else if(instantiable.equalsIgnoreCase("false") || instantiable.equalsIgnoreCase("0") || instantiable.equalsIgnoreCase("no") || instantiable.equalsIgnoreCase("disabled")) {
					palettePlugin.m_instantiable = false;
				}
				else {
					throw new PalettePluginLoadException("Invalid palette instantiable property value found in palette definition file: \"" + file.getName() + "\", espected one of: true, false, 1, 0, yes, no, enabled, disabled.");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no jar file name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette plugin jar file name in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Plugin Jar File Name")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette plugin jar file name variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				palettePlugin.m_jarFileName = v.getValue();
				if(palettePlugin.m_jarFileName.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty palette plugin jar file name found in palette definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			while(true) {
				input = in.readLine();
				if(input == null) {
					in.close();
					throw new PalettePluginLoadException("Palette plugin definition file \"" + file.getName() + "\" incomplete or corrupted, no jar file name found.");
				}
				temp = input.trim();
				if(temp.length() == 0) { continue; }
				
				v = Variable.parseFrom(temp);
				if(v == null) {
					in.close();
					throw new PalettePluginLoadException("Failed to parse palette plugin jar file name in palette definition file: \"" + file.getName() + "\".");
				}
				
				if(!v.getID().equalsIgnoreCase("Palette Class Name")) {
					in.close();
					throw new PalettePluginLoadException("Expected palette plugin jar file name variable, found \"" + v.getID() + "\" instead, in palette definition file: \"" + file.getName() + "\".");
				}
				
				palettePlugin.m_paletteClassName = v.getValue();
				if(palettePlugin.m_paletteClassName.length() == 0) {
					in.close();
					throw new PalettePluginLoadException("Empty palette plugin jar file name found in palette definition file: \"" + file.getName() + "\".");
				}
				
				break;
			}
			
			in.close();
		}
		catch(FileNotFoundException e) {
			throw new PalettePluginLoadException("Missing palette plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		catch(IOException e) {
			throw new PalettePluginLoadException("Read exception thrown while parsing palette plugin definition file \"" + file.getName() + "\": " + e.getMessage());
		}
		
		if(!palettePlugin.load()) {
			throw new PalettePluginLoadException("Failed to load palette plugin \"" + palettePlugin.m_jarFileName + "\".");
		}
		
		palettePlugin.m_paletteClass = null;
		try { palettePlugin.m_paletteClass = PaletteEditor.classLoader.loadClass(palettePlugin.m_paletteClassName); }
		catch(ClassNotFoundException e) { throw new PalettePluginLoadException("Class " + palettePlugin.m_paletteClassName + " is missing or not loaded."); }
		if(!(Palette.class.isAssignableFrom(palettePlugin.m_paletteClass))) {
			throw new PalettePluginLoadException("Class " + palettePlugin.m_paletteClassName + " does not extend Palette class.");
		}
		
		return palettePlugin;
	}
	
	public static int numberOfConfigFileTypes() {
		return CONFIG_FILE_TYPES.length;
	}
	
	public static String getConfigFileType(int index) {
		if(index < 0 || index >= CONFIG_FILE_TYPES.length) { return null; }
		return CONFIG_FILE_TYPES[index];
	}
	
	public static String getConfigFileTypesAsString() {
		String listOfConfigFileTypes = "";
		
		for(int i=0;i<CONFIG_FILE_TYPES.length;i++) {
			listOfConfigFileTypes += CONFIG_FILE_TYPES[i];
			
			if(i < CONFIG_FILE_TYPES.length - 1) {
				listOfConfigFileTypes += ", ";
			}
		}
		
		return listOfConfigFileTypes;
	}
	
	public static boolean hasConfigFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<CONFIG_FILE_TYPES.length;i++) {
			if(CONFIG_FILE_TYPES[i].equalsIgnoreCase(type)) {
				return true;
			}
		}
		return false;
	}
	
	public static int indexOfConfigFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		
		for(int i=0;i<CONFIG_FILE_TYPES.length;i++) {
			if(CONFIG_FILE_TYPES[i].equalsIgnoreCase(type)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean equals(Object o) {
		if(o == null || !(o instanceof PalettePlugin)) { return false; }
		
		PalettePlugin p = ((PalettePlugin) o);
		
		if(m_name == null || p.m_name == null) { return false; }
		
		return m_name.equalsIgnoreCase(p.m_name);
	}
	
	public String toString() {
		return m_name;
	}
	
}
