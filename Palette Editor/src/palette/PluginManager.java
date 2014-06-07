package palette;

import java.util.*;
import java.io.*;
import javax.swing.*;
import exception.*;
import utilities.*;
import variable.*;

public class PluginManager {
	
	private Vector<PalettePlugin> m_plugins;
	
	public PluginManager() {
		m_plugins = new Vector<PalettePlugin>();
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
	
	public VariableSystem getUnloadedPlugins(File file) {
		VariableSystem plugins = new VariableSystem();
		
		getUnloadedPlugins(file, 0, plugins);
		
		return plugins;
	}
	
	private void getUnloadedPlugins(File file, int depth, VariableSystem plugins) {
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
					plugins.add(name, file.getPath());
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
	
}
