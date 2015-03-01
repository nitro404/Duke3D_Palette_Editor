package palette;

import java.util.*;
import java.io.*;
import javax.swing.*;
import exception.*;
import utilities.*;
import variable.*;
import settings.*;
import console.*;
import gui.*;

public class PalettePluginManager {
	
	public static PalettePluginManager instance = null;
	
	private Vector<PalettePlugin> m_plugins;
	
	private ProgressDialog m_progressDialog;
	
	public PalettePluginManager() {
		if(instance == null) {
			updateInstance();
		}
		
		m_plugins = new Vector<PalettePlugin>();
		
		m_progressDialog = new ProgressDialog(PaletteEditor.paletteEditorWindow.getFrame());
	}
	
	public void updateInstance() {
		instance = this;
	}
	
	public int numberOfPlugins() {
		return m_plugins.size();
	}
	
	public PalettePlugin getPlugin(int index) {
		if(index < 0 || index >= m_plugins.size()) { return null; }
		
		return m_plugins.elementAt(index);
	}
	
	public PalettePlugin getPlugin(String name) {
		if(name == null) { return null; }
		String temp = name.trim();
		if(temp.length() == 0) { return null; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return m_plugins.elementAt(i);
			}
		}
		return null;
	}
	
	public PalettePlugin getPluginForFileType(String fileType) {
		if(fileType == null) { return null; }
		String type = fileType.trim();
		if(type.length() == 0) { return null; }
		
		for(int i=0;i<m_plugins.size();i++) {
			for(int j=0;j<m_plugins.elementAt(i).numberOfSupportedPaletteFileTypes();j++) {
				if(m_plugins.elementAt(i).getSupportedPaletteFileType(j).equalsIgnoreCase(type)) {
					return m_plugins.elementAt(i);
				}
			}
		}
		return null;
	}
	
	public boolean hasPluginWithFileName(String fileName) {
		if(fileName == null) { return false; }
		String temp = fileName.trim();
		if(temp.length() == 0) { return false; }
		
		String s;
		for(int i=0;i<m_plugins.size();i++) {
			s = m_plugins.elementAt(i).getConfigFileName();
			if(s != null && s.equalsIgnoreCase(temp)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPlugin(String name) {
		if(name == null) { return false; }
		String temp = name.trim();
		if(temp.length() == 0) { return false; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPluginForFileType(String fileType) {
		if(fileType == null) { return false; }
		String type = fileType.trim();
		if(type.length() == 0) { return false; }
		
		for(int i=0;i<m_plugins.size();i++) {
			for(int j=0;j<m_plugins.elementAt(i).numberOfSupportedPaletteFileTypes();j++) {
				if(m_plugins.elementAt(i).getSupportedPaletteFileType(j).equalsIgnoreCase(type)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int indexOfPlugin(String name) {
		if(name == null) { return -1; }
		String temp = name.trim();
		if(temp.length() == 0) { return -1; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).getName().equalsIgnoreCase(temp)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfPlugin(PalettePlugin p) {
		if(p == null) { return -1; }
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).equals(p)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOfPluginForFileType(String fileType) {
		if(fileType == null) { return -1; }
		String type = fileType.trim();
		if(type.length() == 0) { return -1; }
		
		for(int i=0;i<m_plugins.size();i++) {
			for(int j=0;j<m_plugins.elementAt(i).numberOfSupportedPaletteFileTypes();j++) {
				if(m_plugins.elementAt(i).getSupportedPaletteFileType(j).equalsIgnoreCase(type)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int numberOfLoadedPlugins() {
		int numberOfLoadedPlugins = 0;
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).isLoaded()) {
				numberOfLoadedPlugins++;
			}
		}
		
		return numberOfLoadedPlugins;
	}
	
	public String getLoadedPluginsAsString() {
		String loadedPluginList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).isLoaded()) {
				if(loadedPluginList.length() > 0) {
					loadedPluginList += ", ";
				}
				
				loadedPluginList += m_plugins.elementAt(i).getName();
			}
		}
		
		return loadedPluginList;
	}
	
	public Vector<PalettePlugin> getLoadedPlugins() {
		Vector<PalettePlugin> loadedPlugins = new Vector<PalettePlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).isLoaded()) {
				loadedPlugins.add(m_plugins.elementAt(i));
			}
		}
		
		return loadedPlugins;
	}
	
	public int numberOfLoadedInstantiablePlugins() {
		int numberOfLoadedInstantiablePlugins = 0;
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).isLoaded() && m_plugins.elementAt(i).isInstantiable()) {
				numberOfLoadedInstantiablePlugins++;
			}
		}
		
		return numberOfLoadedInstantiablePlugins;
	}
	
	public String getLoadedInstantiablePluginsAsString() {
		String loadedInstantiablePluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).isLoaded() && m_plugins.elementAt(i).isInstantiable()) {
				if(loadedInstantiablePluginsList.length() > 0) {
					loadedInstantiablePluginsList += ", ";
				}
				
				loadedInstantiablePluginsList += m_plugins.elementAt(i).getName();
			}
		}
		
		return loadedInstantiablePluginsList;
	}
	
	public Vector<PalettePlugin> getLoadedInstantiablePlugins() {
		Vector<PalettePlugin> loadedInstantiablePlugins = new Vector<PalettePlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			if(m_plugins.elementAt(i).isLoaded() && m_plugins.elementAt(i).isInstantiable()) {
				loadedInstantiablePlugins.add(m_plugins.elementAt(i));
			}
		}
		
		return loadedInstantiablePlugins;
	}
	
	public String getLoadedInstantiablePluginsAsStringExcluding(String fileType) {
		String type = fileType == null ? null : fileType.trim();
		
		PalettePlugin plugin = null;
		String loadedInstantiablePluginsList = new String();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			if(plugin.isLoaded() && plugin.isInstantiable() && (type != null && !(plugin.numberOfSupportedPaletteFileTypes() == 1 && plugin.hasSupportedPaletteFileType(type)))) {
				if(loadedInstantiablePluginsList.length() > 0) {
					loadedInstantiablePluginsList += ", ";
				}
				
				loadedInstantiablePluginsList += m_plugins.elementAt(i).getName();
			}
		}
		
		return loadedInstantiablePluginsList;
	}
	
	public Vector<PalettePlugin> getLoadedInstantiablePluginsExcluding(String fileType) {
		String type = fileType == null ? null : fileType.trim();
		
		PalettePlugin plugin = null;
		Vector<PalettePlugin> loadedInstantiablePlugins = new Vector<PalettePlugin>();
		
		for(int i=0;i<m_plugins.size();i++) {
			plugin = m_plugins.elementAt(i);
			if(plugin.isLoaded() && plugin.isInstantiable() && (type != null && !(plugin.numberOfSupportedPaletteFileTypes() == 1 && plugin.hasSupportedPaletteFileType(type)))) {
				loadedInstantiablePlugins.add(m_plugins.elementAt(i));
			}
		}
		
		return loadedInstantiablePlugins;
	}
	
	public int numberOfUnloadedPlugins(File file) {
		return numberOfUnloadedPlugins(file, 0);
	}
	
	private int numberOfUnloadedPlugins(File file, int depth) {
		if(file == null || !file.exists() || depth > 2) { return 0; }
		
		if(file.isDirectory()) {
			if(depth > 2) { return 0; }
			
			File[] contents = file.listFiles();
			
			int count = 0;
			for(int i=0;i<contents.length;i++) {
				count += numberOfUnloadedPlugins(contents[i], depth+1);
			}
			
			return count;
		}
		else {
			if(depth != 2) { return 0; }
			
			if(!hasPluginWithFileName(file.getName()) && PalettePlugin.hasConfigFileType(Utilities.getFileExtension(file.getName()))) {
				try {
					if(PalettePlugin.isPalettePlugin(file)) {
						return 1;
					}
				}
				catch(PalettePluginLoadException e) {
					PaletteEditor.console.writeLine(e.getMessage());
					
					return 0;
				}
			}
			return 0;
		}
	}
	
	public VariableCollection getUnloadedPlugins(File file) {
		VariableCollection plugins = new VariableCollection();
		
		getUnloadedPlugins(file, 0, plugins);
		
		return plugins;
	}
	
	private void getUnloadedPlugins(File file, int depth, VariableCollection plugins) {
		if(file == null || !file.exists() || depth > 2 || plugins == null) { return; }
		
		if(file.isDirectory()) {
			if(depth > 2) { return; }
			
			File[] contents = file.listFiles();
			
			for(int i=0;i<contents.length;i++) {
				getUnloadedPlugins(contents[i], depth+1, plugins);
			}
		}
		else {
			if(depth != 2) { return; }
			
			if(!hasPluginWithFileName(file.getName()) && PalettePlugin.hasConfigFileType(Utilities.getFileExtension(file.getName()))) {
				String name = null;
				try { name = PalettePlugin.getPalettePluginName(file); }
				catch(PalettePluginLoadException e) {
					PaletteEditor.console.writeLine(e.getMessage());
				}
				
				if(name != null) {
					plugins.addVariable(name, file.getPath());
				}
			}
		}
	}
	
	public void loadPlugins(File file) {
		loadPlugins(file, null, 0);
	}
	
	public void loadPlugins(File file, Task task) {
		loadPlugins(file, task, 0);
		
		if(task != null && !task.isCompleted()) {
			task.setCompleted();
		}
	}
	
	private void loadPlugins(File file, Task task, int depth) {
		if(file == null || !file.exists() || depth > 2) { return; }
		if(task != null && task.isCancelled()) { return; }
		
		if(file.isDirectory()) {
			if(depth > 2) { return; }
			
			File[] contents = file.listFiles();
			
			for(int i=0;i<contents.length;i++) {
				loadPlugins(contents[i], task, depth+1);
			}
		}
		else {
			if(depth != 2) { return; }
			
			if(!hasPluginWithFileName(file.getName())) {
				if(PalettePlugin.hasConfigFileType(Utilities.getFileExtension(file.getName())) && loadPlugin(file) && task != null) {
					task.addProgress(1);
				}
			}
		}
	}
	
	public boolean loadPlugin(File file) {
		return loadPlugin(file, null);
	}
	
	public boolean loadPlugin(File file, Task task) {
		if(file == null || !file.exists() || !file.isFile()) { return false; }
		
		PalettePlugin newPalettePlugin;
		try {
			newPalettePlugin = PalettePlugin.loadFrom(file);
		}
		catch(PalettePluginLoadException e) {
			PaletteEditor.console.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), e.getMessage(), "Plugin Load Failed", JOptionPane.ERROR_MESSAGE);
			
			if(task != null) {
				task.cancel();
			}
			
			return false;
		}
		
		if(task != null && task.isCancelled()) {
			return false;
		}
		
		if(newPalettePlugin == null) {
			if(task != null) {
				task.cancel();
			}
			
			return false;
		}
		newPalettePlugin.setConfigFileName(file.getName());
		
		for(int i=0;i<m_plugins.size();i++) {
			if(newPalettePlugin.hasSharedSupportedPaletteFileType(m_plugins.elementAt(i))) {
				String sharedSupportedPaletteFileTypes = newPalettePlugin.getSharedSupportedPaletteFileTypesAsString(m_plugins.elementAt(i));
				
				PaletteEditor.console.writeLine("Attempted to load \"" + newPalettePlugin.getName() + "\" plugin for where \"" + sharedSupportedPaletteFileTypes + "\" palette file type(s) were already supported in \"" + m_plugins.elementAt(i).getName() + "\" plugin.");
				
				JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Attempted to load \"" + newPalettePlugin.getName() + "\" plugin where \"" + sharedSupportedPaletteFileTypes + "\" palette file type(s) were already supported in \"" + m_plugins.elementAt(i).getName() + "\" plugin.", "File Type(s) Already Supported", JOptionPane.ERROR_MESSAGE);
				
				if(task != null) {
					task.cancel();
				}
				
				return false;
			}
		}
		
		m_plugins.add(newPalettePlugin);
		
		if(task != null) {
			task.addProgress(1);
		}
		
		return true;
	}
	
	public void displayLoadedPlugins() {
		Vector<PalettePlugin> loadedPlugins = getLoadedPlugins();
		if(loadedPlugins.size() > 0) {
			String listOfLoadedPlugins = new String();
			for(int i=0;i<loadedPlugins.size();i++) {
				listOfLoadedPlugins += (i + 1) + ": " + loadedPlugins.elementAt(i).getName() + (i < loadedPlugins.size() - 1 ? "\n" : "");
			}
			
			JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Detected " + loadedPlugins.size() + " loaded plugin" + (loadedPlugins.size() == 1 ? "" : "s") + ":\n" + listOfLoadedPlugins, loadedPlugins.size() + " Plugin" + (loadedPlugins.size() == 1 ? "" : "s") +" Loaded", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "No plugins currently loaded.", "No Plugins Loaded", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public boolean loadPluginPrompt() {
		VariableCollection plugins = getUnloadedPlugins(new File(SettingsManager.instance.pluginDirectoryName));
		
		if(plugins.numberOfVariables() == 0) {
			JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "No unloaded plugins found.", "No Unloaded Plugins", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		String[] choices = new String[plugins.numberOfVariables()];
		for(int i=0;i<plugins.numberOfVariables();i++) {
			choices[i] = plugins.getVariable(i).getID();
		}
		
		String choice = (String) JOptionPane.showInputDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Choose a plugin to load:", "Load Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(choice == null) { return false; }
		
		Variable plugin = plugins.getVariable(choice);
		if(plugin == null) { return false; }
		
		return loadPlugin(plugin.getID(), plugin.getValue());
	}
	
	public boolean loadPlugin(String pluginName, final String pluginConfigFileName) {
		if(pluginConfigFileName == null) { return false; }
		
		int numberOfLoadedPlugins = m_plugins.size();
		
		final Task task = new Task(1, m_progressDialog);
		
		Thread pluginLoaderThread = new Thread(new Runnable() {
			public void run() {
				loadPlugin(new File(pluginConfigFileName), task);
			}
		});
		pluginLoaderThread.start();
		
		m_progressDialog.display("Loading", "Loading plugin...", 0, 1);
		
		if(m_progressDialog.userCancelled() || !task.isCompleted()) {
			task.cancel();
			
			pluginLoaderThread.interrupt();
			try { pluginLoaderThread.join(); } catch(InterruptedException e) { }
			
			m_progressDialog.clear();
		}
		
		PaletteEditor.paletteEditorWindow.update();
		
		if(numberOfLoadedPlugins == m_plugins.size()) {
			if(!m_progressDialog.userCancelled()) {
				SystemConsole.instance.writeLine("Failed to load plugin" + (pluginName == null ? "" : ": " + pluginName));
				
				JOptionPane.showMessageDialog(PaletteEditor.paletteEditorWindow.getFrame(), "Failed to load plugin " + (pluginName == null ? "" : ": " + pluginName), "Loading Failed", JOptionPane.ERROR_MESSAGE);
			}
			
			return false;
		}
		else {
			SystemConsole.instance.writeLine("Successfully loaded plugin" + (pluginName == null ? "" : ": " + pluginName));
			
			return true;
		}
	}
	
	public void loadPlugins() {
		int numberOfUnloadedPlugins = numberOfUnloadedPlugins(new File(SettingsManager.instance.pluginDirectoryName));
		int numberOfLoadedPlugins = m_plugins.size();
		
		String[] pluginNames = new String[m_plugins.size()];
		for(int i=0;i<m_plugins.size();i++) {
			pluginNames[i] = m_plugins.elementAt(i).getName();
		}
		
		SystemConsole.instance.writeLine("Number of unloaded plugins detected: " + numberOfUnloadedPlugins);
		
		if(numberOfUnloadedPlugins > 0) {
			final Task task = new Task(numberOfUnloadedPlugins, m_progressDialog);
			
			Thread pluginLoaderThread = new Thread(new Runnable() {
				public void run() {
					loadPlugins(new File(SettingsManager.instance.pluginDirectoryName), task);
				}
			});
			pluginLoaderThread.start();
			
			m_progressDialog.display("Loading", "Loading plugins...", 0, numberOfUnloadedPlugins);
			
			if(m_progressDialog.userCancelled() || !task.isCompleted()) {
				task.cancel();
				
				pluginLoaderThread.interrupt();
				try { pluginLoaderThread.join(); } catch(InterruptedException e) { }
				
				m_progressDialog.clear();
			}
			
			PaletteEditor.paletteEditorWindow.update();
			
			if(m_plugins.size() == 0 || m_plugins.size() - numberOfLoadedPlugins == 0) {
				SystemConsole.instance.writeLine("No plugins were loaded.");
			}
			else {
				int totalPluginsLoaded = (m_plugins.size() - numberOfLoadedPlugins);
				
				boolean foundPlugin, firstPlugin = true;
				StringBuffer s = new StringBuffer();
				s.append("Successfully loaded " + totalPluginsLoaded + " plugin" + (totalPluginsLoaded == 1 ? "" : "s") + ": ");
				for(int i=0;i<m_plugins.size();i++) {
					foundPlugin = false;
					for(int j=0;j<pluginNames.length;j++) {
						if(pluginNames[j].equalsIgnoreCase(m_plugins.elementAt(i).getName())) {
							foundPlugin = true;
						}
					}
					
					if(!foundPlugin) {
						if(!firstPlugin) {
							s.append(", ");
						}
						s.append(m_plugins.elementAt(i).getName());
						
						firstPlugin = false;
					}
				}
				
				SystemConsole.instance.writeLine(s.toString());
			}
		}
		else {
			SystemConsole.instance.writeLine("No plugins to load.");
		}
	}
	
}
