package gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import exception.*;
import utilities.*;
import settings.*;
import console.*;
import version.*;
import action.*;
import palette.*;

public class PaletteEditorWindow implements WindowListener, ComponentListener, ChangeListener, ActionListener, PaletteActionListener, Updatable {
	
	private JFrame m_frame;
	private JTabbedPane m_mainTabbedPane;
	private Vector<PalettePanel> m_palettePanels;
	private JTextArea m_consoleText;
	private Font m_consoleFont;
	private JScrollPane m_consoleScrollPane;
	
	private JMenuBar m_menuBar;
	private JMenu m_fileMenu;
	private JMenuItem m_fileNewMenuItem;
	private JMenuItem m_fileOpenMenuItem;
	private JMenuItem m_fileSaveMenuItem;
	private JMenuItem m_fileSaveAsMenuItem;
	private JMenuItem m_fileSaveAllMenuItem;
	private JMenuItem m_fileImportMenuItem;
	private JMenuItem m_fileExportMenuItem;
	private JMenuItem m_fileCloseMenuItem;
	private JMenuItem m_fileCloseAllMenuItem;
	private JMenuItem m_fileExitMenuItem;
	private JMenu m_settingsMenu;
	private JMenuItem m_settingsPluginDirectoryNameMenuItem;
	private JMenuItem m_settingsConsoleLogFileNameMenuItem;
	private JMenuItem m_settingsLogDirectoryNameMenuItem;
	private JMenuItem m_settingsVersionFileURLMenuItem;
	private JMenuItem m_settingsBackgroundColourMenuItem;
	private JCheckBoxMenuItem m_settingsAutoScrollConsoleMenuItem;
	private JMenuItem m_settingsMaxConsoleHistoryMenuItem;
	private JCheckBoxMenuItem m_settingsLogConsoleMenuItem;
	private JCheckBoxMenuItem m_settingsSupressUpdatesMenuItem;
	private JCheckBoxMenuItem m_settingsAutoSaveSettingsMenuItem;
	private JMenuItem m_settingsSaveSettingsMenuItem;
	private JMenuItem m_settingsReloadSettingsMenuItem;
	private JMenuItem m_settingsResetSettingsMenuItem;
	private JMenu m_pluginsMenu;
	private JMenuItem m_pluginsListLoadedMenuItem;
	private JMenuItem m_pluginsLoadMenuItem;
	private JMenuItem m_pluginsLoadAllMenuItem;
	private JCheckBoxMenuItem m_pluginsAutoLoadMenuItem;
	private JMenu m_windowMenu;
	private JMenuItem m_buttonSizeMenuItem;
	private JMenuItem m_paletteSpacingMenuItem;
	private JMenuItem m_windowResetPositionMenuItem;
	private JMenuItem m_windowResetSizeMenuItem;
	private JMenu m_helpMenu;
	private JMenuItem m_helpCheckVersionMenuItem;
	private JMenuItem m_helpAboutMenuItem;
	
	private boolean m_initialized;
	private boolean m_updating;
	
	public static final int SCROLL_INCREMENT = 16;
	
	private TransferHandler m_transferHandler = new TransferHandler() {
		
		private static final long serialVersionUID = 7382995584179254438L;
		
		public boolean canImport(TransferHandler.TransferSupport support) {
			if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}
			
			support.setDropAction(COPY);
			
			return true;
		}
		
		@SuppressWarnings("unchecked")
		public boolean importData(TransferHandler.TransferSupport support) {
			if(!canImport(support)) {
				return false;
			}
			
			try {
				loadPalettes(((java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).toArray(new File[1]));
			}
			catch(UnsupportedFlavorException e) {
				return false;
			}
			catch(IOException e) {
				return false;
			}
			
			return true;
		}
	};
	
	public PaletteEditorWindow() {
		m_frame = new JFrame("Palette Editor Window");
		m_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		m_frame.setMinimumSize(new Dimension(320, 240));
		m_frame.setLocation(SettingsManager.defaultWindowPositionX, SettingsManager.defaultWindowPositionY);
		m_frame.setSize(SettingsManager.defaultWindowWidth, SettingsManager.defaultWindowHeight);
		m_frame.addWindowListener(this);
		m_frame.addComponentListener(this);
		m_frame.setTransferHandler(m_transferHandler);
		
		m_palettePanels = new Vector<PalettePanel>();
		m_initialized = false;
		m_updating = false;
		
		initMenu();
 		initComponents();
 		
 		update();
	}
	
	public boolean initialize() {
		if(m_initialized) { return false; }
		
		updateWindow();
		
		m_frame.setLocation(SettingsManager.instance.windowPositionX, SettingsManager.instance.windowPositionY);
		m_frame.setSize(SettingsManager.instance.windowWidth, SettingsManager.instance.windowHeight);
		
		// update and show the gui window
		update();
		m_frame.setVisible(true);
		
		m_initialized = true;
		
		update();
		
		return true;
	}
	
	// initialize the menu
	private void initMenu() {
		m_menuBar = new JMenuBar();
		
		m_fileMenu = new JMenu("File");
		m_fileNewMenuItem = new JMenuItem("New");
		m_fileOpenMenuItem = new JMenuItem("Open");
		m_fileSaveMenuItem = new JMenuItem("Save");
		m_fileSaveAsMenuItem = new JMenuItem("Save As");
		m_fileSaveAllMenuItem = new JMenuItem("Save All");
		m_fileImportMenuItem = new JMenuItem("Import");
		m_fileExportMenuItem = new JMenuItem("Export");
		m_fileCloseMenuItem = new JMenuItem("Close");
		m_fileCloseAllMenuItem = new JMenuItem("Close All");
		m_fileExitMenuItem = new JMenuItem("Exit");
		
		m_fileNewMenuItem.setMnemonic('N');
		m_fileOpenMenuItem.setMnemonic('O');
		m_fileSaveMenuItem.setMnemonic('S');
		m_fileImportMenuItem.setMnemonic('I');
		m_fileExportMenuItem.setMnemonic('E');
		
		m_fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
		m_fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
		m_fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
		m_fileImportMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK));
		m_fileExportMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK));
		
		m_settingsMenu = new JMenu("Settings");
		m_settingsPluginDirectoryNameMenuItem = new JMenuItem("Plugin Directory Name");
		m_settingsConsoleLogFileNameMenuItem = new JMenuItem("Console Log File Name");
		m_settingsLogDirectoryNameMenuItem = new JMenuItem("Log Directory Name");
		m_settingsVersionFileURLMenuItem = new JMenuItem("Version File URL");
		m_settingsBackgroundColourMenuItem = new JMenuItem("Background Colour");
		m_settingsAutoScrollConsoleMenuItem = new JCheckBoxMenuItem("Auto-Scroll Console");
		m_settingsMaxConsoleHistoryMenuItem = new JMenuItem("Max Console History");
		m_settingsLogConsoleMenuItem = new JCheckBoxMenuItem("Log Console");
		m_settingsSupressUpdatesMenuItem = new JCheckBoxMenuItem("Supress Update Notifications");
		m_settingsAutoSaveSettingsMenuItem = new JCheckBoxMenuItem("Auto-Save Settings");
		m_settingsSaveSettingsMenuItem = new JMenuItem("Save Settings");
		m_settingsReloadSettingsMenuItem = new JMenuItem("Reload Settings");
		m_settingsResetSettingsMenuItem = new JMenuItem("Reset Settings");
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.defaultAutoScrollConsole);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.defaultAutoSaveSettings);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.defaultLogConsole);
		m_settingsSupressUpdatesMenuItem.setSelected(SettingsManager.defaultSupressUpdates);
		
		m_pluginsMenu = new JMenu("Plugins");
		m_pluginsListLoadedMenuItem = new JMenuItem("List Loaded Plugins");
		m_pluginsLoadMenuItem = new JMenuItem("Load Plugin");
		m_pluginsLoadAllMenuItem = new JMenuItem("Load All Plugins");
		m_pluginsAutoLoadMenuItem = new JCheckBoxMenuItem("Auto-Load Plugins");
		m_pluginsAutoLoadMenuItem.setSelected(SettingsManager.defaultAutoLoadPlugins);
		
		m_windowMenu = new JMenu("Window");
		m_buttonSizeMenuItem = new JMenuItem("Pixel Button Size");
		m_paletteSpacingMenuItem = new JMenuItem("Palette Spacing");
		m_windowResetPositionMenuItem = new JMenuItem("Reset Window Position");
		m_windowResetSizeMenuItem = new JMenuItem("Reset Window Size");
		
		m_helpMenu = new JMenu("Help");
		m_helpCheckVersionMenuItem = new JMenuItem("Check for Updates");
		m_helpAboutMenuItem = new JMenuItem("About");
		
		m_fileNewMenuItem.addActionListener(this);
		m_fileOpenMenuItem.addActionListener(this);
		m_fileSaveMenuItem.addActionListener(this);
		m_fileSaveAsMenuItem.addActionListener(this);
		m_fileSaveAllMenuItem.addActionListener(this);
		m_fileImportMenuItem.addActionListener(this);
		m_fileExportMenuItem.addActionListener(this);
		m_fileCloseMenuItem.addActionListener(this);
		m_fileCloseAllMenuItem.addActionListener(this);
		m_fileExitMenuItem.addActionListener(this);
		m_settingsPluginDirectoryNameMenuItem.addActionListener(this);
		m_settingsConsoleLogFileNameMenuItem.addActionListener(this);
		m_settingsLogDirectoryNameMenuItem.addActionListener(this);
		m_settingsVersionFileURLMenuItem.addActionListener(this);
		m_settingsBackgroundColourMenuItem.addActionListener(this);
		m_settingsAutoScrollConsoleMenuItem.addActionListener(this);
		m_settingsMaxConsoleHistoryMenuItem.addActionListener(this);
		m_settingsLogConsoleMenuItem.addActionListener(this);
		m_settingsSupressUpdatesMenuItem.addActionListener(this);
		m_settingsAutoSaveSettingsMenuItem.addActionListener(this);
		m_settingsSaveSettingsMenuItem.addActionListener(this);
		m_settingsReloadSettingsMenuItem.addActionListener(this);
		m_settingsResetSettingsMenuItem.addActionListener(this);
		m_pluginsListLoadedMenuItem.addActionListener(this);
		m_pluginsLoadMenuItem.addActionListener(this);
		m_pluginsLoadAllMenuItem.addActionListener(this);
		m_pluginsAutoLoadMenuItem.addActionListener(this);
		m_buttonSizeMenuItem.addActionListener(this);
		m_paletteSpacingMenuItem.addActionListener(this);
		m_windowResetPositionMenuItem.addActionListener(this);
		m_windowResetSizeMenuItem.addActionListener(this);
		m_helpCheckVersionMenuItem.addActionListener(this);
		m_helpAboutMenuItem.addActionListener(this);
		
		m_fileMenu.add(m_fileNewMenuItem);
		m_fileMenu.add(m_fileOpenMenuItem);
		m_fileMenu.add(m_fileSaveMenuItem);
		m_fileMenu.add(m_fileSaveAsMenuItem);
		m_fileMenu.add(m_fileSaveAllMenuItem);
		m_fileMenu.add(m_fileImportMenuItem);
		m_fileMenu.add(m_fileExportMenuItem);
		m_fileMenu.add(m_fileCloseMenuItem);
		m_fileMenu.add(m_fileCloseAllMenuItem);
		m_fileMenu.add(m_fileExitMenuItem);
		
		m_settingsMenu.add(m_settingsPluginDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsConsoleLogFileNameMenuItem);
		m_settingsMenu.add(m_settingsLogDirectoryNameMenuItem);
		m_settingsMenu.add(m_settingsVersionFileURLMenuItem);
		m_settingsMenu.add(m_settingsBackgroundColourMenuItem);
		m_settingsMenu.add(m_settingsAutoScrollConsoleMenuItem);
		m_settingsMenu.add(m_settingsMaxConsoleHistoryMenuItem);
		m_settingsMenu.add(m_settingsLogConsoleMenuItem);
		m_settingsMenu.add(m_settingsSupressUpdatesMenuItem);
		m_settingsMenu.addSeparator();
		m_settingsMenu.add(m_settingsAutoSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsSaveSettingsMenuItem);
		m_settingsMenu.add(m_settingsReloadSettingsMenuItem);
		m_settingsMenu.add(m_settingsResetSettingsMenuItem);
		
		m_pluginsMenu.add(m_pluginsListLoadedMenuItem);
		m_pluginsMenu.add(m_pluginsLoadMenuItem);
		m_pluginsMenu.add(m_pluginsLoadAllMenuItem);
		m_pluginsMenu.add(m_pluginsAutoLoadMenuItem);
		
		m_windowMenu.add(m_buttonSizeMenuItem);
		m_windowMenu.add(m_paletteSpacingMenuItem);
		m_windowMenu.add(m_windowResetPositionMenuItem);
		m_windowMenu.add(m_windowResetSizeMenuItem);
		
		m_helpMenu.add(m_helpCheckVersionMenuItem);
		m_helpMenu.add(m_helpAboutMenuItem);
		
		m_menuBar.add(m_fileMenu);
		m_menuBar.add(m_settingsMenu);
		m_menuBar.add(m_pluginsMenu);
		m_menuBar.add(m_windowMenu);
		m_menuBar.add(m_helpMenu);
		
		m_frame.setJMenuBar(m_menuBar);
	}

	// initialize the gui components
	private void initComponents() {
		// initialize the main tabbed pane
		m_mainTabbedPane = new JTabbedPane();
		
		// initialize the console tab
		m_consoleText = new JTextArea();
		m_consoleFont = new Font("Verdana", Font.PLAIN, 14);
		m_consoleText.setFont(m_consoleFont);
		m_consoleText.setEditable(false);
		m_consoleText.setTransferHandler(m_transferHandler);
		m_consoleScrollPane = new JScrollPane(m_consoleText);
		m_mainTabbedPane.add(m_consoleScrollPane);
		
		m_mainTabbedPane.addTab("Console", null, m_consoleScrollPane, "Displays debugging information from the application.");
		
		m_mainTabbedPane.addChangeListener(this);
		
		m_frame.add(m_mainTabbedPane);
	}
	
	public JFrame getFrame() {
		return m_frame;
	}
	
	public TransferHandler getTransferHandler() {
		return m_transferHandler;
	}
	
	public void addPalette(PalettePanel palettePanel) {
		if(palettePanel == null) { return; }
		
		palettePanel.setTransferHandler(m_transferHandler);
		palettePanel.addPaletteChangeListener(PaletteEditor.instance);
		m_palettePanels.add(palettePanel);
		
		JScrollPane paletteImageScrollPane = new JScrollPane(palettePanel);
		paletteImageScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		int index = m_mainTabbedPane.getTabCount() - 1;
		m_mainTabbedPane.insertTab(palettePanel.getTabName(), null, paletteImageScrollPane, palettePanel.getTabDescription(), index);
		
		m_mainTabbedPane.setSelectedIndex(index);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				m_mainTabbedPane.revalidate();
				
				update();
			}
		});
	}
	
	public boolean unsavedChanges() {
		for(int i=0;i<m_palettePanels.size();i++) {
			if(m_palettePanels.elementAt(i).isChanged()) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean selectPalettePanel(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		PalettePanel p = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			p = getPalettePanelFrom(m_mainTabbedPane.getComponent(i));
			if(p == null) { continue; }
			
			if(palettePanel == p) {
				m_mainTabbedPane.setSelectedComponent(m_mainTabbedPane.getComponent(i));
				return true;
			}
		}
		return false;
	}

	protected PalettePanel getSelectedPalettePanel() {
		Component selectedComponent = m_mainTabbedPane.getSelectedComponent();
		if(selectedComponent == null || !(selectedComponent instanceof JScrollPane)) { return null; }
		JScrollPane selectedScrollPane = (JScrollPane) selectedComponent;
		JViewport selectedViewport = selectedScrollPane.getViewport();
		if(selectedViewport == null || selectedViewport.getComponentCount() < 1) { return null; }
		Component selectedScrollPaneComponent = selectedViewport.getComponent(0);
		if(selectedScrollPaneComponent == null || !(selectedScrollPaneComponent instanceof PalettePanel)) { return null; }
		return (PalettePanel) selectedScrollPaneComponent;
	}
	
	protected PalettePanel getPalettePanelFrom(Component component) {
		if(component == null || !(component instanceof JScrollPane)) { return null; }
		JScrollPane scrollPane = (JScrollPane) component;
		JViewport viewport = scrollPane.getViewport();
		if(viewport == null || viewport.getComponentCount() < 1) { return null; }
		Component scrollPaneComponent = viewport.getComponent(0);
		if(scrollPaneComponent == null || !(scrollPaneComponent instanceof PalettePanel)) { return null; }
		return (PalettePanel) scrollPaneComponent;
	}
	
	protected Component getTabComponentWith(PalettePanel palettePanel) {
		if(palettePanel == null) { return null; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof PalettePanel)) { continue; }
			if((PalettePanel) scrollPaneComponent == palettePanel) {
				return component;
			}
		}
		return null;
	}
	
	protected int indexOfTabComponentWith(PalettePanel palettePanel) {
		if(palettePanel == null) { return -1; }
		Component component = null;
		for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
			component = m_mainTabbedPane.getComponent(i);
			if(!(component instanceof JScrollPane)) { continue; }
			JScrollPane scrollPane = (JScrollPane) component;
			JViewport viewport = scrollPane.getViewport();
			if(viewport == null || viewport.getComponentCount() < 1) { continue; }
			Component scrollPaneComponent = viewport.getComponent(0);
			if(scrollPaneComponent == null || !(scrollPaneComponent instanceof PalettePanel)) { continue; }
			if(scrollPaneComponent == palettePanel) {
				m_mainTabbedPane.indexOfComponent(scrollPaneComponent);
			}
		}
		return -1;
	}
	
	public boolean promptNewPalette() {
		Vector<PalettePlugin> loadedPalettePlugins = PaletteEditor.pluginManager.getLoadedPlugins(PalettePlugin.class);
		if(loadedPalettePlugins.size() == 0) {
			String message = "No palette plugins found that support instantiation. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int pluginIndex = -1;
		Object choices[] = loadedPalettePlugins.toArray();
		Object value = JOptionPane.showInputDialog(m_frame, "Choose a palette type to create:", "Choose New Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(value == null) { return false; }
		for(int i=0;i<choices.length;i++) {
			if(choices[i] == value) {
				pluginIndex = i;
				break;
			}
		}
		if(pluginIndex < 0 || pluginIndex >= loadedPalettePlugins.size()) { return false; }
		
		Palette newPalette = null;
		try {
			newPalette = loadedPalettePlugins.elementAt(pluginIndex).getNewPaletteInstance(null);
		}
		catch(PaletteInstantiationException e) {
			String message = "Failed to create instance of \"" + loadedPalettePlugins.elementAt(pluginIndex).getName() + "\"!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Color fillColour = JColorChooser.showDialog(null, "Choose Fill Colour", Color.BLACK);
		if(fillColour == null) { return false; }
		
		if(!newPalette.fillAllWithColour(fillColour)) {
			String message = "Failed to fill palette with specified colour!";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Palette Fill Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SystemConsole.instance.writeLine(loadedPalettePlugins.elementAt(pluginIndex).getName() + " palette created successfully!");
		
		PalettePanel newPalettePanel = null;
		try { newPalettePanel = loadedPalettePlugins.elementAt(pluginIndex).getNewPalettePanelInstance(newPalette); }
		catch(PalettePanelInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Panel Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(newPalettePanel == null) {
			String message = "Failed to instantiate palette panel for \"" + loadedPalettePlugins.elementAt(pluginIndex).getName() + " plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!newPalettePanel.init()) {
			String message = "Failed to initialize palette panel for \"" + loadedPalettePlugins.elementAt(pluginIndex).getName() + "\" plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		newPalettePanel.addPaletteActionListener(this);
		
		addPalette(newPalettePanel);
		
		newPalettePanel.setPaletteNumber(PaletteEditor.getPaletteNumber());
		newPalettePanel.setChanged(true);
		
		return true;
	}
	
	public void promptLoadPalettes() {
		if(PaletteEditor.pluginManager.numberOfLoadedPlugins() == 0) {
			String message = "No palette plugins loaded. You must have at least one palette plugin loaded to open a palette file.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Palette Plugins Loaded", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Load Palette Files");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) { return; }
		
		loadPalettes(fileChooser.getSelectedFiles());
	}

	public int loadPalettes(File[] files) {
		if(files == null || files.length == 0) { return -1; }
		
		int numberOfPalettesLoaded = 0; 
		for(int i=0;i<files.length;i++) {
			if(files[i] == null) { continue; }
			
			if(loadPalette(files[i])) {
				numberOfPalettesLoaded++;
			}
		}
		
		if(files.length > 0) {
			int numberOfPalettesFailed = files.length - numberOfPalettesLoaded;
			if(numberOfPalettesLoaded == 0 && numberOfPalettesFailed > 0) {
				SystemConsole.instance.writeLine(numberOfPalettesFailed + " palette file" + (numberOfPalettesFailed == 1 ? "" : "s") + " failed to load, no palette files loaded.");
			}
			else if(numberOfPalettesLoaded > 1) {
				SystemConsole.instance.writeLine(numberOfPalettesLoaded + " palette files were loaded successfully" + (numberOfPalettesFailed == 0 ? "" : ", while " + numberOfPalettesFailed + " failed to load") + "!");
			}
		}
		
		return numberOfPalettesLoaded;
	}
	
	public boolean loadPalette(File file) {
		if(file == null || !file.exists()) {
			SystemConsole.instance.writeLine("File \"" + file.getName() + "\" does not exist.");
			return false;
		}
		
		for(int i=0;i<m_palettePanels.size();i++) {
			if(m_palettePanels.elementAt(i).isSameFile(file)) {
				selectPalettePanel(m_palettePanels.elementAt(i));
				
				String message = "Palette file \"" + (file == null ? "null" : file.getName()) +  "\" already loaded!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Already Loaded", JOptionPane.INFORMATION_MESSAGE);
				
				return true;
			}
		}
		
		String extension = Utilities.getFileExtension(file.getName());
		
		Vector<PalettePlugin> plugins = PaletteEditor.pluginManager.getPalettePluginsForFileFormat(extension);
		if(plugins == null || plugins.size() == 0) {
			String message = "No plugin found to load " + extension + " file type. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		PalettePlugin plugin = plugins.elementAt(0);
		if(plugins.size() > 1) {
			int pluginIndex = -1;
			Object choices[] = plugins.toArray();
			Object value = JOptionPane.showInputDialog(m_frame, "Found multiple plugins supporting this file format.\nChoose a plugin to open this file with:", "Choose Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
			if(value == null) { return false; }
			for(int i=0;i<choices.length;i++) {
				if(choices[i] == value) {
					pluginIndex = i;
					break;
				}
			}
			if(pluginIndex < 0 || pluginIndex >= plugins.size()) { return false; }
			
			plugin = plugins.elementAt(pluginIndex);
		}
		
		Palette palette = null;
		try { palette = plugin.getNewPaletteInstance(file); }
		catch(PaletteInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(palette == null) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileFormatsAsString() + ")\" plugin when attempting to read palette file: \"" + file.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!palette.load()) {
				String message = "Failed to load palette: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileFormatsAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while loading palette: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileFormatsAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(PaletteReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		SystemConsole.instance.writeLine("Palette file \"" + file.getName() +  "\" loaded successfully!");
		
		PalettePanel palettePanel = null;
		try { palettePanel = plugin.getNewPalettePanelInstance(palette); }
		catch(PalettePanelInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Panel Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(palettePanel == null) {
			String message = "Failed to instantiate palette panel for \"" + plugin.getName() + " plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		if(!palettePanel.init()) {
			String message = "Failed to initialize palette panel for \"" + plugin.getName() + "\" plugin.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		palettePanel.setPaletteNumber(PaletteEditor.getPaletteNumber());
		palettePanel.addPaletteActionListener(this);
		
		addPalette(palettePanel);
		
		return true;
	}
	
	public boolean saveSelectedPalette() {
		return savePalette(getSelectedPalettePanel(), false);
	}
	
	public boolean savePalette(PalettePanel palettePanel) {
		return savePalette(palettePanel, false);
	}
	
	public boolean savePalette(PalettePanel palettePanel, boolean copy) {
		if(palettePanel == null) { return false; }
		
		if(!palettePanel.isChanged() && !copy) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "No changes detected, save palette anyways?", "No Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.NO_OPTION) { return false; }
		}
		
		File paletteFile = palettePanel.getPalette().getFile();
		
		if(paletteFile == null) {
			return savePaletteAsNew(palettePanel);
		}
		
		try {
			if(palettePanel.save()) {
				SystemConsole.instance.writeLine("Palette successfully updated and saved to file: " + paletteFile.getName() + "!");
				
				update();
				
				return true;
			}
			else {
				String message = "Failed to update and save palette!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Save Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(PaletteWriteException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			return false;
		}
	}
	
	public boolean saveSelectedPaletteAsNew() {
		return savePaletteAsNew(getSelectedPalettePanel());
	}
	
	public boolean savePaletteAsNew(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		File paletteFile = palettePanel.getPalette().getFile();
		
		JFileChooser fileChooser = new JFileChooser(paletteFile == null ? System.getProperty("user.dir") : Utilities.getFilePath(paletteFile));
		fileChooser.setDialogTitle("Save Palette File As");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(paletteFile != null) {
			String fileName = paletteFile.getName();
			String extension = Utilities.getFileExtension(fileName);
			fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY") + (extension == null ? "" : "." + extension)));
		}
		else {
			String extension = palettePanel.getExtension();
			fileChooser.setSelectedFile(new File("NEW" + (extension == null ? "" : "." + extension)));
		}
		
		while(true) {
			if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
			
			if(fileChooser.getSelectedFile().exists()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The specified file already exists, are you sure you want to overwrite it?", "Overwrite File", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				else if(choice == JOptionPane.NO_OPTION) { continue; }
				
				break;
			}
			else {
				break;
			}
		}
		
		palettePanel.getPalette().setFile(fileChooser.getSelectedFile());
		
		return savePalette(palettePanel, true);
	}
	
	public void saveAllPalettes() {
		if(m_palettePanels.size() == 0) { return; }
		
		for(int i=0;i<m_palettePanels.size();i++) {
			savePalette(m_palettePanels.elementAt(i));
		}
		
		update();
	}
	
	public boolean importPaletteIntoSelectedPalette() {
		return importPaletteInto(getSelectedPalettePanel());
	}
	
	public boolean importPaletteInto(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		Palette palette = palettePanel.getPalette();
		if(palette == null) { return false; }
		
		JFileChooser fileChooser = new JFileChooser(palette.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(palette.getFile()));
		fileChooser.setDialogTitle("Import Palette File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
		if(!fileChooser.getSelectedFile().isFile() || !fileChooser.getSelectedFile().exists()) {
			String message = "Selected palette file \"" + fileChooser.getSelectedFile().getName() + "\" is not a file or does not exist.";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Invalid or Missing File", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		File selectedFile = fileChooser.getSelectedFile();
		String extension = Utilities.getFileExtension(selectedFile.getName());
		
		Vector<PalettePlugin> plugins = PaletteEditor.pluginManager.getPalettePluginsForFileFormat(extension);
		if(plugins == null || plugins.size() == 0) {
			String message = "No plugin found to import " + extension + " file type. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}

		PalettePlugin plugin = plugins.elementAt(0);
		if(plugins.size() > 1) {
			int pluginIndex = -1;
			Object choices[] = plugins.toArray();
			Object value = JOptionPane.showInputDialog(m_frame, "Found multiple plugins supporting this file format.\nChoose a plugin to import this file with:", "Choose Plugin", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
			if(value == null) { return false; }
			for(int i=0;i<choices.length;i++) {
				if(choices[i] == value) {
					pluginIndex = i;
					break;
				}
			}
			if(pluginIndex < 0 || pluginIndex >= plugins.size()) { return false; }
			
			plugin = plugins.elementAt(pluginIndex);
		}
		
		Palette importedPalette = null;
		try { importedPalette = plugin.getNewPaletteInstance(selectedFile); }
		catch(PaletteInstantiationException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(importedPalette == null) {
			String message = "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileFormatsAsString() + ")\" plugin when attempting to import palette file: \"" + selectedFile.getName() + "\".";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!importedPalette.load()) {
				String message = "Failed to import palette: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileFormatsAsString() + ")\".";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			String message = "Exception thrown while importing palette : \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileFormatsAsString() + "): " + e.getMessage();
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(PaletteReadException e) {
			SystemConsole.instance.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Importing Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int importedPaletteIndex = 0;
		if(importedPalette.numberOfPalettes() > 1) {
			if(palette.numberOfPalettes() == importedPalette.numberOfPalettes()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The palette you are importing has the same number of sub palettes, would you like to import all sub palettes?", "Import All Sub-Palettes", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				if(choice == JOptionPane.YES_OPTION) { importedPaletteIndex = -1; }
			}
			
			if(importedPaletteIndex != -1) {
				Vector<String> importPaletteDescriptions = importedPalette.getPaletteDescriptions();
				Object choices[] = new Object[importPaletteDescriptions.size()];
				for(int i=0;i<importPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + importPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to import from:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						importedPaletteIndex = i;
						break;
					}
				}
				if(importedPaletteIndex < 0 || importedPaletteIndex >= importPaletteDescriptions.size()) { return false; }
			}
		}
		
		int localPaletteIndex = 0;
		if(importedPaletteIndex == -1) {
			localPaletteIndex = -1;
		}
		else {
			if(palette.numberOfPalettes() > 1) {
				Vector<String> localPaletteDescriptions = palette.getPaletteDescriptions();
				Object choices[] = new Object[localPaletteDescriptions.size()];
				for(int i=0;i<localPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + localPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to import to:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						localPaletteIndex = i;
						break;
					}
				}
				if(localPaletteIndex < 0 || localPaletteIndex >= localPaletteDescriptions.size()) { return false; }
			}
		}
		
		Color importedColourData[] = importedPaletteIndex == -1 ? importedPalette.getAllColourData() : importedPalette.getColourData(importedPaletteIndex);
		
		boolean importSuccessful = false;
		if(localPaletteIndex == -1) {
			importSuccessful = palette.updateAllColourData(importedColourData);
		}
		else {
			importSuccessful = palette.updateColourData(localPaletteIndex, 0, importedColourData);
		}
		
		if(importSuccessful) {
			palettePanel.setChanged(true);
			
			SystemConsole.instance.writeLine("Palette file \"" + selectedFile.getName() +  "\" imported successfully!");
			
			update();
		}
		else {
			SystemConsole.instance.writeLine("Failed to import palette file \"" + selectedFile.getName() +  "\" imported successfully!");
		}
		
		return importSuccessful;
	}
	
	public boolean exportSelectedPalette() {
		return exportPalette(getSelectedPalettePanel());
	}
	
	public boolean exportPalette(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		Palette palette = palettePanel.getPalette();
		if(palette == null) { return false; }
		
		int paletteIndex = 0;
		if(palette.numberOfPalettes() > 1) {
			if(palette.numberOfPalettes() == palette.numberOfPalettes()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The palette you are exporting has multple sub palettes, would you like to export all of them?", "Export All Sub-Palettes", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				if(choice == JOptionPane.YES_OPTION) { paletteIndex = -1; }
			}
			
			if(paletteIndex != -1) {
				Vector<String> selectedPaletteDescriptions = palette.getPaletteDescriptions();
				Object choices[] = new Object[selectedPaletteDescriptions.size()];
				for(int i=0;i<selectedPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + selectedPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to export:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						paletteIndex = i;
						break;
					}
				}
				if(paletteIndex < 0 || paletteIndex >= selectedPaletteDescriptions.size()) { return false; }
			}
		}
		
		Vector<PalettePlugin> loadedInstantiablePlugins = PaletteEditor.pluginManager.getLoadedPalettePluginsExcludingFileFormat(palette.getExtension());
		if(loadedInstantiablePlugins.size() == 0) {
			String message = "No palette plugins found that support instantiation / exporting. Perhaps you forgot to load all plugins?";
			
			SystemConsole.instance.writeLine(message);
			
			JOptionPane.showMessageDialog(m_frame, message, "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int useSameExportSettings = -1;
		int pluginIndex = -1;
		int fileTypeIndex = 0;
		int numberOfPalettesExported = 0;
		int currentPaletteIndex = paletteIndex < 0 ? 0 : paletteIndex;
		while(true) {
			if(useSameExportSettings <= 0) {
				pluginIndex = -1;
				Object choices[] = loadedInstantiablePlugins.toArray();
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a palette type to export to:", "Choose Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						pluginIndex = i;
						break;
					}
				}
				if(pluginIndex < 0 || pluginIndex >= loadedInstantiablePlugins.size()) { return false; }
				
				fileTypeIndex = 0;
				if(loadedInstantiablePlugins.elementAt(pluginIndex).numberOfSupportedPaletteFileFormats() > 1) {
					choices = loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileFormats().toArray();
					value = JOptionPane.showInputDialog(m_frame, "Choose a palette file type to export to:", "Choose File Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
					if(value == null) { return false; }
					for(int i=0;i<choices.length;i++) {
						if(choices[i] == value) {
							fileTypeIndex = i;
							break;
						}
					}
					if(fileTypeIndex < 0 || fileTypeIndex >= loadedInstantiablePlugins.size()) { return false; }
				}
			}
			
			if(palette.numberOfPalettes() > 1 && useSameExportSettings < 0) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to use the same export settings for all sub-palettes?", "Use Same Export Settings?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(choice == JOptionPane.YES_OPTION) { useSameExportSettings = 1; }
				else if(choice == JOptionPane.NO_OPTION) { useSameExportSettings = 0; }
			}
			
			JFileChooser fileChooser = new JFileChooser(palette.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(palette.getFile()));
			fileChooser.setDialogTitle("Export Palette File");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			String extension = loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileFormat(fileTypeIndex);
			if(palette.getFile() != null) {
				String fileName = palette.getFile().getName();
				fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (palette.numberOfPalettes() > 1 ? "_" + (currentPaletteIndex + 1) : (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY"))  + "." + (Utilities.compareCasePercentage(fileName) < 0 ? extension.toLowerCase() : extension.toUpperCase())));
			}
			else {
				fileChooser.setSelectedFile(new File("NEW" + (palette.numberOfPalettes() > 1 ? "_" + (currentPaletteIndex + 1) : "") +  "." + extension));
			}
			
			while(true) {
				if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
				
				if(fileChooser.getSelectedFile().exists()) {
					int choice = JOptionPane.showConfirmDialog(m_frame, "The specified file already exists, are you sure you want to overwrite it?", "Overwrite File", JOptionPane.YES_NO_CANCEL_OPTION);
					if(choice == JOptionPane.CANCEL_OPTION) { return false; }
					else if(choice == JOptionPane.NO_OPTION) { continue; }
					
					break;
				}
				else {
					break;
				}
			}
			
			Palette newPalette = null;
			try {
				newPalette = loadedInstantiablePlugins.elementAt(pluginIndex).getNewPaletteInstance(fileChooser.getSelectedFile());
			}
			catch(PaletteInstantiationException e) {
				String message = "Failed to create instance of export file: \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " (" + loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileFormatsAsString() + ")!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
			
			if(!palettePanel.updatePaletteData()) {
				String message = "Failed to update palette data for source palette while attempting to export file: \"" + palette.getFile().getName() + "!";
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Update Palette Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
			newPalette.updateColourData(palette.getColourData(currentPaletteIndex));
			try {
				if(newPalette.save()) {
					SystemConsole.instance.writeLine("Palette successfully exported to new file: " + newPalette.getFile().getName() + "!");
					
					numberOfPalettesExported++;
				}
				else {
					String message = "Failed to export palette!";
					
					SystemConsole.instance.writeLine(message);
					
					JOptionPane.showMessageDialog(m_frame, message, "Export Failed", JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
			}
			catch(PaletteWriteException e) {
				SystemConsole.instance.writeLine(e.getMessage());
				
				return false;
			}
			
			if(paletteIndex >= 0 || currentPaletteIndex >= palette.numberOfPalettes() - 1) {
				break;
			}
			
			currentPaletteIndex++;
		}
		
		if(numberOfPalettesExported > 1) {
			SystemConsole.instance.writeLine("Successfully exported " + numberOfPalettesExported + " sub-palettes from palette: \"" + palette.getFile().getName() + "\".");
		}
		
		return true;
	}
	
	public boolean closeSelectedPalette() {
		return closePalette(getSelectedPalettePanel());
	}
	
	public boolean closePalette(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		Component tabComponent = getTabComponentWith(palettePanel);
		if(tabComponent == null) { return false; }
		m_mainTabbedPane.setSelectedComponent(tabComponent);
		
		if(palettePanel.isChanged()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to save your changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(choice == JOptionPane.CANCEL_OPTION) { return false; }
			if(choice == JOptionPane.YES_OPTION) {
				if(!saveSelectedPalette()) {
					return false;
				}
			}
		}
		
		
		m_mainTabbedPane.remove(tabComponent);
		int indexOfPalette = m_palettePanels.indexOf(palettePanel);
		m_palettePanels.remove(palettePanel);
		if(m_palettePanels.size() > 0) {
			m_mainTabbedPane.setSelectedComponent(getTabComponentWith(m_palettePanels.elementAt(indexOfPalette < m_palettePanels.size() ? indexOfPalette : indexOfPalette - 1)));
		}
		
		update();
		
		return true;
	}
	
	public boolean closeAllPalettes() {
		if(m_mainTabbedPane.getComponentCount() > 1) {
			m_mainTabbedPane.setSelectedComponent(m_mainTabbedPane.getComponent(m_mainTabbedPane.getComponentCount() - 2));
		}
		
		for(int i=m_palettePanels.size()-1;i>=0;i--) {
			if(!closePalette(m_palettePanels.elementAt(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	private void updateWindow() {
		m_settingsAutoScrollConsoleMenuItem.setSelected(SettingsManager.instance.autoScrollConsole);
		m_settingsLogConsoleMenuItem.setSelected(SettingsManager.instance.logConsole);
		m_settingsSupressUpdatesMenuItem.setSelected(SettingsManager.instance.supressUpdates);
		m_pluginsAutoLoadMenuItem.setSelected(SettingsManager.instance.autoLoadPlugins);
		m_settingsAutoSaveSettingsMenuItem.setSelected(SettingsManager.instance.autoSaveSettings);
		
		boolean paletteTabSelected = m_mainTabbedPane.getSelectedIndex() != m_mainTabbedPane.getTabCount() - 1;
		m_fileSaveMenuItem.setEnabled(paletteTabSelected);
		m_fileSaveAsMenuItem.setEnabled(paletteTabSelected);
		m_fileSaveAllMenuItem.setEnabled(m_palettePanels.size() > 0);
		m_fileImportMenuItem.setEnabled(paletteTabSelected);
		m_fileExportMenuItem.setEnabled(paletteTabSelected);
		m_fileCloseMenuItem.setEnabled(paletteTabSelected);
		m_fileCloseAllMenuItem.setEnabled(m_palettePanels.size() > 0);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PalettePanel palettePanel = null;
				for(int i=0;i<m_mainTabbedPane.getComponentCount();i++) {
					palettePanel = getPalettePanelFrom(m_mainTabbedPane.getComponentAt(i));
					if(palettePanel == null) { continue; }
					
					m_mainTabbedPane.setTitleAt(i, palettePanel.getTabName());
					m_mainTabbedPane.setToolTipTextAt(i, palettePanel.getTabDescription());
				}
			}
		});
		
		updateLayout();
		
		m_mainTabbedPane.revalidate();
	}
	
	public void updateLayout() {
		for(int i=0;i<m_palettePanels.size();i++) {
			m_palettePanels.elementAt(i).updateLayout();
		}
	}
	
	// update the server window
	public void update() {
		if(!m_initialized) { return; }
		
		// update and automatically scroll to the end of the text
		m_consoleText.setText(SystemConsole.instance.toString());
		
		if(SettingsManager.instance.autoScrollConsole) {
			JScrollBar hScrollBar = m_consoleScrollPane.getHorizontalScrollBar();
			JScrollBar vScrollBar = m_consoleScrollPane.getVerticalScrollBar();
			
			if(!hScrollBar.getValueIsAdjusting() && !vScrollBar.getValueIsAdjusting()) {
				hScrollBar.setValue(hScrollBar.getMinimum());
				vScrollBar.setValue(vScrollBar.getMaximum());
			}
		}
		
		m_updating = true;
		
		updateWindow();
		
		m_updating = false;
	}
	
	public void changeBackgroundColourPrompt() {
		Color newColour = JColorChooser.showDialog(null, "Choose background colour", SettingsManager.instance.backgroundColour);
		if(newColour == null) { return; }
		
		SettingsManager.instance.backgroundColour = newColour;
		
		update();
	}
	
	public void resetWindowPosition() {
		SettingsManager.instance.windowPositionX = SettingsManager.defaultWindowPositionX;
		SettingsManager.instance.windowPositionY = SettingsManager.defaultWindowPositionY;
		
		m_frame.setLocation(SettingsManager.instance.windowPositionX, SettingsManager.instance.windowPositionY);
	}
	
	public void resetWindowSize() {
		SettingsManager.instance.windowWidth = SettingsManager.defaultWindowWidth;
		SettingsManager.instance.windowHeight = SettingsManager.defaultWindowHeight;
		
		m_frame.setSize(SettingsManager.instance.windowWidth, SettingsManager.instance.windowHeight);
	}
	
	public void changePixelButtonSizePrompt() {
		// prompt for pixel button size
		String input = JOptionPane.showInputDialog(m_frame, "Please enter the desired pixel button size:", SettingsManager.instance.pixelButtonSize);
		if(input == null) { return; }
		
		// set the new pixel button size
		int pixelButtonSize = -1;
		try {
			pixelButtonSize = Integer.parseInt(input);
		}
		catch(NumberFormatException e2) {
			JOptionPane.showMessageDialog(m_frame, "Invalid pixel button size entered.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(pixelButtonSize >= 1 && pixelButtonSize <= 64) {
			SystemConsole.instance.writeLine("Pixel button size changed from " + SettingsManager.instance.pixelButtonSize + " to " + pixelButtonSize + ".");
			
			SettingsManager.instance.pixelButtonSize = pixelButtonSize;
			
			update();
		}
		else {
			SystemConsole.instance.writeLine("Pixel button size must be between 1 and 64.");
		}
	}
	
	public void changePaletteSpacingPrompt() {
		// prompt for palette spacing
		String input = JOptionPane.showInputDialog(m_frame, "Please enter the distance for palette spacing:", SettingsManager.instance.paletteSpacing);
		if(input == null) { return; }
		
		// set the new palette spacing
		int paletteSpacing = -1;
		try {
			paletteSpacing = Integer.parseInt(input);
		}
		catch(NumberFormatException e2) {
			JOptionPane.showMessageDialog(m_frame, "Invalid palette spacing distance entered.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(paletteSpacing >= 0 && paletteSpacing <= 128) {
			SystemConsole.instance.writeLine("Palette spacing changed from " + SettingsManager.instance.paletteSpacing + " to " + paletteSpacing + ".");
			
			SettingsManager.instance.paletteSpacing = paletteSpacing;
			
			update();
		}
		else {
			SystemConsole.instance.writeLine("Palette spacing must be between 0 and 128.");
		}
	}
	
	public void windowActivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	
	public void windowClosing(WindowEvent e) {
		if(e.getSource() == m_frame) {
			close();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(m_updating) { return; }
		
		// create new palette
		if(e.getSource() == m_fileNewMenuItem) {
			promptNewPalette();
		}
		// load palette
		else if(e.getSource() == m_fileOpenMenuItem) {
			promptLoadPalettes();
		}
		// save selected palette
		else if(e.getSource() == m_fileSaveMenuItem) {
			saveSelectedPalette();
		}
		// save selected palette as new palette
		else if(e.getSource() == m_fileSaveAsMenuItem) {
			saveSelectedPaletteAsNew();
		}
		// save all palettes
		else if(e.getSource() == m_fileSaveAllMenuItem) {
			saveAllPalettes();
		}
		// import palette
		else if(e.getSource() == m_fileImportMenuItem) {
			importPaletteIntoSelectedPalette();
		}
		// export palette
		else if(e.getSource() == m_fileExportMenuItem) {
			exportSelectedPalette();
		}
		// close current palette
		else if(e.getSource() == m_fileCloseMenuItem) {
			closeSelectedPalette();
		}
		// close all palettes
		else if(e.getSource() == m_fileCloseAllMenuItem) {
			closeAllPalettes();
		}
		// close the program
		else if(e.getSource() == m_fileExitMenuItem) {
			close();
		}
		// change the plugins folder name
		else if(e.getSource() == m_settingsPluginDirectoryNameMenuItem) {
			// prompt for the plugin directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the plugin directory name:", SettingsManager.instance.pluginDirectoryName);
			if(input == null) { return; }
			
			String newPluginDirectoryName = input.trim();
			if(newPluginDirectoryName.length() == 0) { return; }
			
			if(!newPluginDirectoryName.equalsIgnoreCase(SettingsManager.instance.pluginDirectoryName)) {
				SettingsManager.instance.pluginDirectoryName = newPluginDirectoryName;
			}
		}
		// change the console log file name
		else if(e.getSource() == m_settingsConsoleLogFileNameMenuItem) {
			// prompt for the console log file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the console log file name:", SettingsManager.instance.consoleLogFileName);
			if(input == null) { return; }
			
			String newConsoleLogFileName = input.trim();
			if(newConsoleLogFileName.length() == 0) { return; }
			
			if(!newConsoleLogFileName.equalsIgnoreCase(SettingsManager.instance.consoleLogFileName)) {
				SystemConsole.instance.resetConsoleLogFileHeader();
				
				SettingsManager.instance.consoleLogFileName = newConsoleLogFileName;
			}
		}
		// change the log directory name
		else if(e.getSource() == m_settingsLogDirectoryNameMenuItem) {
			// prompt for the log directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the log directory name:", SettingsManager.instance.logDirectoryName);
			if(input == null) { return; }
			
			String newLogDirectoryName = input.trim();
			if(newLogDirectoryName.length() == 0) { return; }
			
			if(!newLogDirectoryName.equalsIgnoreCase(SettingsManager.instance.logDirectoryName)) {
				SettingsManager.instance.logDirectoryName = newLogDirectoryName;
			}
		}
		else if(e.getSource() == m_settingsVersionFileURLMenuItem) {
			// prompt for the version file url
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the version file URL:", SettingsManager.instance.versionFileURL);
			if(input == null) { return; }
			
			String newVersionFileURL = input.trim();
			if(newVersionFileURL.length() == 0) { return; }
			
			if(!newVersionFileURL.equalsIgnoreCase(SettingsManager.instance.versionFileURL)) {
				SettingsManager.instance.versionFileURL = newVersionFileURL;
			}
		}
		else if(e.getSource() == m_settingsBackgroundColourMenuItem) {
			changeBackgroundColourPrompt();
		}
		// change the console auto scrolling
		else if(e.getSource() == m_settingsAutoScrollConsoleMenuItem) {
			SettingsManager.instance.autoScrollConsole = m_settingsAutoScrollConsoleMenuItem.isSelected();
		}
		// change the maximum number of elements the console can hold
		else if(e.getSource() == m_settingsMaxConsoleHistoryMenuItem) {
			// prompt for the maximum console history size
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the maximum console history size:", SettingsManager.instance.maxConsoleHistory);
			if(input == null) { return; }
			
			// set the new console history size
			int maxConsoleHistory = -1;
			try {
				maxConsoleHistory = Integer.parseInt(input);
			}
			catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(m_frame, "Invalid number entered for maximum console history.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(maxConsoleHistory > 1) {
				SettingsManager.instance.maxConsoleHistory = maxConsoleHistory;
			}
		}
		// change console logging
		else if(e.getSource() == m_settingsLogConsoleMenuItem) {
			SettingsManager.instance.logConsole = m_settingsLogConsoleMenuItem.isSelected();
		}
		// change update notification supressing
		else if(e.getSource() == m_settingsSupressUpdatesMenuItem) {
			SettingsManager.instance.supressUpdates = m_settingsSupressUpdatesMenuItem.isSelected();
		}
		else if(e.getSource() == m_settingsAutoSaveSettingsMenuItem) {
			SettingsManager.instance.autoSaveSettings = m_settingsAutoSaveSettingsMenuItem.isSelected();
		}
		else if(e.getSource() == m_settingsSaveSettingsMenuItem) {
			if(SettingsManager.instance.save()) {
				String message = "Successfully saved settings to file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String message = "Failed to save settings to file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource() == m_settingsReloadSettingsMenuItem) {
			if(SettingsManager.instance.load()) {
				update();
				
				String message = "Settings successfully loaded from file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String message = "Failed to load settings from file: " + SettingsManager.instance.settingsFileName;
				
				SystemConsole.instance.writeLine(message);
				
				JOptionPane.showMessageDialog(m_frame, message, "Settings Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource() == m_settingsResetSettingsMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset all settings?", "Reset All Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				SettingsManager.instance.reset();
				
				update();
				
				SystemConsole.instance.writeLine("All settings reset to default values");
			}
		}
		// display a list of loaded plugins
		else if(e.getSource() == m_pluginsListLoadedMenuItem) {
			PalettePluginManager.instance.displayLoadedPlugins();
		}
		// prompt for a plugin to load
		else if(e.getSource() == m_pluginsLoadMenuItem) {
			PalettePluginManager.instance.loadPluginPrompt();
		}
		// load all plugins
		else if(e.getSource() == m_pluginsLoadAllMenuItem) {
			PalettePluginManager.instance.loadPlugins();
		}
		// toggle auto-loading of plugins
		else if(e.getSource() == m_pluginsAutoLoadMenuItem) {
			SettingsManager.instance.autoLoadPlugins = m_pluginsAutoLoadMenuItem.isSelected();
			
			update();
		}
		// change the pixel button size
		else if(e.getSource() == m_buttonSizeMenuItem) {
			changePixelButtonSizePrompt();
		}
		// change the palette spacing
		else if(e.getSource() == m_paletteSpacingMenuItem) {
			changePaletteSpacingPrompt();
		}
		// reset the window position
		else if(e.getSource() == m_windowResetPositionMenuItem) {
			resetWindowPosition();
		}
		// reset the window size
		else if(e.getSource() == m_windowResetSizeMenuItem) {
			resetWindowSize();
		}
		// check program version
		else if(e.getSource() == m_helpCheckVersionMenuItem) {
			VersionChecker.checkVersion();
		}
		// display help message
		else if(e.getSource() == m_helpAboutMenuItem) {
			JOptionPane.showMessageDialog(m_frame, "Palette Editor Version " + PaletteEditor.VERSION + "\nCreated by Kevin Scroggins (a.k.a. nitro_glycerine)\nE-Mail: nitro404@gmail.com\nWebsite: http://www.nitro404.com", "About Palette Editor", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public boolean handlePaletteAction(PaletteAction action) {
		if(!PaletteAction.isvalid(action)) { return false; }
		
		switch(action.getAction()) {
			case Save:
				savePalette(action.getSource());
				break;
				
			case SaveAs:
				savePaletteAsNew(action.getSource());
				break;
				
			case Import:
				importPaletteInto(action.getSource());
				break;
				
			case Export:
				exportPalette(action.getSource());
				break;
				
			case Close:
				closePalette(action.getSource());
				break;
				
			default:
				return false;
		}
		
		return true;
	}
	
	public void stateChanged(ChangeEvent e) {
		if(m_updating) { return; }
		
		if(e.getSource() == m_mainTabbedPane) {
			if(m_mainTabbedPane.getSelectedIndex() >= 0 && m_mainTabbedPane.getSelectedIndex() < m_mainTabbedPane.getTabCount()) {
				update();
			}
		}
		
		for(int i=0;i<m_palettePanels.size();i++) {
			m_palettePanels.elementAt(i).updateLayout();
		}
	}
	
	public void componentShown(ComponentEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	
	public void componentResized(ComponentEvent e) {
		updateLayout();
	}
	
	public void close() {
		if(!closeAllPalettes()) {
			return;
		}
		
		// reset initialization variables
		m_initialized = false;
		
		SettingsManager.instance.windowPositionX = m_frame.getX();
		SettingsManager.instance.windowPositionY = m_frame.getY();
		SettingsManager.instance.windowWidth = m_frame.getWidth();
		SettingsManager.instance.windowHeight = m_frame.getHeight();
		
		// close the server
		PaletteEditor.instance.close();
		
		m_frame.dispose();
		
		System.exit(0);
	}
	
}
