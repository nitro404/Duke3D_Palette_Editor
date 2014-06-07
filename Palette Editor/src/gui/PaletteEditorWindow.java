package gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import exception.*;
import utilities.*;
import settings.*;
import palette.*;
import version.*;

public class PaletteEditorWindow implements WindowListener, ComponentListener, ChangeListener, ActionListener, Updatable {
	
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
		
		m_frame.setLocation(PaletteManager.settings.windowPositionX, PaletteManager.settings.windowPositionY);
		m_frame.setSize(PaletteManager.settings.windowWidth, PaletteManager.settings.windowHeight);
		
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
		palettePanel.addPaletteChangeListener(PaletteManager.instance);
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
		Vector<PalettePlugin> loadedInstantiablePlugins = PaletteManager.pluginManager.getLoadedInstantiablePlugins();
		if(loadedInstantiablePlugins.size() == 0) {
			PaletteManager.console.writeLine("No palette plugins found that support instantiation. Perhaps you forgot to load all plugins?");
			
			JOptionPane.showMessageDialog(m_frame, "No palette plugins found that support instantiation. Perhaps you forgot to load all plugins?", "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int pluginIndex = -1;
		Object choices[] = loadedInstantiablePlugins.toArray();
		Object value = JOptionPane.showInputDialog(m_frame, "Choose a palette type to create:", "Choose New Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		if(value == null) { return false; }
		for(int i=0;i<choices.length;i++) {
			if(choices[i] == value) {
				pluginIndex = i;
				break;
			}
		}
		if(pluginIndex < 0 || pluginIndex >= loadedInstantiablePlugins.size()) { return false; }
		
		Palette newPalette = null;
		try {
			newPalette = loadedInstantiablePlugins.elementAt(pluginIndex).getPaletteInstance(null);
		}
		catch(PaletteInstantiationException e) {
			PaletteManager.console.writeLine("Failed to create instance of \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + "\"!");
			
			JOptionPane.showMessageDialog(m_frame, "Failed to create instance of \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + "\"!", "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Color fillColour = JColorChooser.showDialog(null, "Choose Fill Colour", Color.BLACK);
		if(fillColour == null) { return false; }
		
		if(!newPalette.fillAllWithColour(fillColour)) {
			PaletteManager.console.writeLine("Failed to fill palette with specified colour!");
			
			JOptionPane.showMessageDialog(m_frame, "Failed to fill palette with specified colour!", "Palette Fill Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		PaletteManager.console.writeLine(loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " palette created successfully!");
		
		PalettePanel newPalettePanel = new PalettePanel(newPalette);
		
		addPalette(newPalettePanel);
		
		newPalettePanel.setChanged(true);
		
		return true;
	}
	
	public void promptLoadPalettes() {
		JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setDialogTitle("Load Palette Files");
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
				PaletteManager.console.writeLine(numberOfPalettesFailed + " palette file" + (numberOfPalettesFailed == 1 ? "" : "s") + " failed to load, no palette files loaded.");
			}
			else if(numberOfPalettesLoaded > 1) {
				PaletteManager.console.writeLine(numberOfPalettesLoaded + " palette files were loaded successfully" + (numberOfPalettesFailed == 0 ? "" : ", while " + numberOfPalettesFailed + " failed to load") + "!");
			}
		}
		
		return numberOfPalettesLoaded;
	}
	
	public boolean loadPalette(File file) {
		if(file == null || !file.exists()) {
			PaletteManager.console.writeLine("File \"" + file.getName() + "\" does not exist.");
			return false;
		}
		
		for(int i=0;i<m_palettePanels.size();i++) {
			if(m_palettePanels.elementAt(i).isSameFile(file)) {
				selectPalettePanel(m_palettePanels.elementAt(i));
				
				PaletteManager.console.writeLine("Palette file \"" + (file == null ? "null" : file.getName()) +  "\" already loaded!");
				
				JOptionPane.showMessageDialog(m_frame, "Palette file \"" + (file == null ? "null" : file.getName()) +  "\" already loaded!", "Already Loaded", JOptionPane.INFORMATION_MESSAGE);
				
				return true;
			}
		}
		
		String extension = Utilities.getFileExtension(file.getName());
		
		PalettePlugin plugin = PaletteManager.pluginManager.getPluginForFileType(extension);
		if(plugin == null) {
			PaletteManager.console.writeLine("No plugin found to load " + extension + " file type. Perhaps you forgot to load all plugins?");
			
			JOptionPane.showMessageDialog(m_frame, "No plugin found to load " + extension + " file type. Perhaps you forgot to load all plugins?", "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Palette palette = null;
		try { palette = plugin.getPaletteInstance(file); }
		catch(PaletteInstantiationException e) {
			PaletteManager.console.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(palette == null) {
			PaletteManager.console.writeLine("Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\" plugin when attempting to read palette file: \"" + file.getName() + "\".");
			
			JOptionPane.showMessageDialog(m_frame, "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\" plugin when attempting to read palette file: \"" + file.getName() + "\".", "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!palette.load()) {
				PaletteManager.console.writeLine("Failed to load palette: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\".");
				
				JOptionPane.showMessageDialog(m_frame, "Failed to load palette: \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\".", "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			PaletteManager.console.writeLine("Exception thrown while loading palette : \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + "): " + e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, "Exception thrown while loading palette : \"" + file.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + "): " + e.getMessage(), "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(PaletteReadException e) {
			PaletteManager.console.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		PaletteManager.console.writeLine("Palette file \"" + file.getName() +  "\" loaded successfully!");
		
		addPalette(new PalettePanel(palette));
		
		return true;
	}
	
	public boolean saveSelectedPalette() {
		return savePalette(getSelectedPalettePanel());
	}
	
	public boolean savePalette(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		if(!palettePanel.isChanged()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "No changes detected, save palette anyways?", "No Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.NO_OPTION) { return false; }
		}
		
		File paletteFile = palettePanel.getPalette().getFile();
		
		if(paletteFile == null) {
			return savePaletteAsNew(palettePanel);
		}
		
		try {
			if(palettePanel.save()) {
				PaletteManager.console.writeLine("Palette successfully updated and saved to file: " + paletteFile.getName() + "!");
				
				update();
				
				return true;
			}
			else {
				PaletteManager.console.writeLine("Failed to update and save palette!");
				
				JOptionPane.showMessageDialog(m_frame, "Failed to update and save palette!", "Save Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(PaletteWriteException e) {
			PaletteManager.console.writeLine(e.getMessage());
			
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
		
		return savePalette(palettePanel);
	}
	
	public void saveAllPalettes() {
		if(m_palettePanels.size() == 0) { return; }
		
		for(int i=0;i<m_palettePanels.size();i++) {
			savePalette(m_palettePanels.elementAt(i));
		}
		
		update();
	}
	
	public boolean importPalette() {
		PalettePanel selectedPalettePanel = getSelectedPalettePanel();
		if(selectedPalettePanel == null) { return false; }
		Palette selectedPalette = selectedPalettePanel.getPalette();
		
		JFileChooser fileChooser = new JFileChooser(selectedPalette.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(selectedPalette.getFile()));
		fileChooser.setDialogTitle("Import Palette File");
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) { return false; }
		if(!fileChooser.getSelectedFile().isFile() || !fileChooser.getSelectedFile().exists()) {
			PaletteManager.console.writeLine("Selected palette file \"" + fileChooser.getSelectedFile().getName() + "\" is not a file or does not exist.");
			
			JOptionPane.showMessageDialog(m_frame, "Selected palette file \"" + fileChooser.getSelectedFile().getName() + "\" is not a file or does not exist.", "Invalid or Missing File", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		File selectedFile = fileChooser.getSelectedFile();
		String extension = Utilities.getFileExtension(selectedFile.getName());
		
		PalettePlugin plugin = PaletteManager.pluginManager.getPluginForFileType(extension);
		if(plugin == null) {
			PaletteManager.console.writeLine("No plugin found to import " + extension + " file type. Perhaps you forgot to load all plugins?");
			
			JOptionPane.showMessageDialog(m_frame, "No plugin found to import " + extension + " file type. Perhaps you forgot to load all plugins?", "No Plugin Found", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		Palette importedPalette = null;
		try { importedPalette = plugin.getPaletteInstance(selectedFile); }
		catch(PaletteInstantiationException e) {
			PaletteManager.console.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		if(importedPalette == null) {
			PaletteManager.console.writeLine("Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\" plugin when attempting to import palette file: \"" + selectedFile.getName() + "\".");
			
			JOptionPane.showMessageDialog(m_frame, "Failed to instantiate \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\" plugin when attempting to import palette file: \"" + selectedFile.getName() + "\".", "Plugin Instantiation Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		try {
			if(!importedPalette.load()) {
				PaletteManager.console.writeLine("Failed to import palette: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\".");
				
				JOptionPane.showMessageDialog(m_frame, "Failed to import palette: \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + ")\".", "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
		}
		catch(HeadlessException e) {
			PaletteManager.console.writeLine("Exception thrown while importing palette : \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + "): " + e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, "Exception thrown while importing palette : \"" + selectedFile.getName() + "\" using plugin: \"" + plugin.getName() + " (" + plugin.getSupportedPaletteFileTypesAsString() + "): " + e.getMessage(), "Palette Loading Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		catch(PaletteReadException e) {
			PaletteManager.console.writeLine(e.getMessage());
			
			JOptionPane.showMessageDialog(m_frame, e.getMessage(), "Palette Importing Failed", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int importedPaletteIndex = 0;
		if(importedPalette.numberOfPalettes() > 1) {
			if(selectedPalette.numberOfPalettes() == importedPalette.numberOfPalettes()) {
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
			if(selectedPalette.numberOfPalettes() > 1) {
				Vector<String> localPaletteDescriptions = selectedPalette.getPaletteDescriptions();
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
			importSuccessful = selectedPalette.updateAllColourData(importedColourData);
		}
		else {
			importSuccessful = selectedPalette.updateColourData(localPaletteIndex, 0, importedColourData);
		}
		
		if(importSuccessful) {
			selectedPalettePanel.setChanged(true);
			
			PaletteManager.console.writeLine("Palette file \"" + selectedFile.getName() +  "\" imported successfully!");
			
			update();
		}
		else {
			PaletteManager.console.writeLine("Failed to import palette file \"" + selectedFile.getName() +  "\" imported successfully!");
		}
		
		return importSuccessful;
	}
	
	public boolean exportPalette() {
		PalettePanel selectedPalettePanel = getSelectedPalettePanel();
		if(selectedPalettePanel == null) { return false; }
		Palette selectedPalette = selectedPalettePanel.getPalette();
		
		int selectedPaletteIndex = 0;
		if(selectedPalette.numberOfPalettes() > 1) {
			if(selectedPalette.numberOfPalettes() == selectedPalette.numberOfPalettes()) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "The palette you are exporting has multple sub palettes, would you like to export all of them?", "Export All Sub-Palettes", JOptionPane.YES_NO_CANCEL_OPTION);
				if(choice == JOptionPane.CANCEL_OPTION) { return false; }
				if(choice == JOptionPane.YES_OPTION) { selectedPaletteIndex = -1; }
			}
			
			if(selectedPaletteIndex != -1) {
				Vector<String> selectedPaletteDescriptions = selectedPalette.getPaletteDescriptions();
				Object choices[] = new Object[selectedPaletteDescriptions.size()];
				for(int i=0;i<selectedPaletteDescriptions.size();i++) {
					choices[i] = new String((i+1) + ": " + selectedPaletteDescriptions.elementAt(i));
				}
				Object value = JOptionPane.showInputDialog(m_frame, "Choose a sub palette to export:", "Choose Sub-Palette Type", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
				if(value == null) { return false; }
				for(int i=0;i<choices.length;i++) {
					if(choices[i] == value) {
						selectedPaletteIndex = i;
						break;
					}
				}
				if(selectedPaletteIndex < 0 || selectedPaletteIndex >= selectedPaletteDescriptions.size()) { return false; }
			}
		}
		
		Vector<PalettePlugin> loadedInstantiablePlugins = PaletteManager.pluginManager.getLoadedInstantiablePluginsExcluding(selectedPalette.getExtension());
		if(loadedInstantiablePlugins.size() == 0) {
			PaletteManager.console.writeLine("No palette plugins found that support instantiation / exporting. Perhaps you forgot to load all plugins?");
			
			JOptionPane.showMessageDialog(m_frame, "No palette plugins found that support instantiation / exporting. Perhaps you forgot to load all plugins?", "No Plugins", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		int useSameExportSettings = -1;
		int pluginIndex = -1;
		int fileTypeIndex = 0;
		int numberOfPalettesExported = 0;
		int currentPaletteIndex = selectedPaletteIndex < 0 ? 0 : selectedPaletteIndex;
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
				if(loadedInstantiablePlugins.elementAt(pluginIndex).numberOfSupportedPaletteFileTypes() > 1) {
					choices = loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileTypes().toArray();
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
			
			if(selectedPalette.numberOfPalettes() > 1 && useSameExportSettings < 0) {
				int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to use the same export settings for all sub-palettes?", "Use Same Export Settings?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(choice == JOptionPane.YES_OPTION) { useSameExportSettings = 1; }
				else if(choice == JOptionPane.NO_OPTION) { useSameExportSettings = 0; }
			}
			
			JFileChooser fileChooser = new JFileChooser(selectedPalette.getFile() == null ? System.getProperty("user.dir") : Utilities.getFilePath(selectedPalette.getFile()));
			fileChooser.setDialogTitle("Export Palette File");
			String extension = loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileType(fileTypeIndex);
			if(selectedPalette.getFile() != null) {
				String fileName = selectedPalette.getFile().getName();
				fileChooser.setSelectedFile(new File(Utilities.getFileNameNoExtension(fileName) + (selectedPalette.numberOfPalettes() > 1 ? "_" + (currentPaletteIndex + 1) : (Utilities.compareCasePercentage(fileName) < 0 ? "_copy" : "_COPY"))  + "." + (Utilities.compareCasePercentage(fileName) < 0 ? extension.toLowerCase() : extension.toUpperCase())));
			}
			else {
				fileChooser.setSelectedFile(new File("NEW" + (selectedPalette.numberOfPalettes() > + 1 ? "_" + (currentPaletteIndex + 1) : "") +  "." + extension));
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
				newPalette = loadedInstantiablePlugins.elementAt(pluginIndex).getPaletteInstance(fileChooser.getSelectedFile());
			}
			catch(PaletteInstantiationException e) {
				PaletteManager.console.writeLine("Failed to create instance of export file: \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " (" + loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileTypesAsString() + ")!");
				
				JOptionPane.showMessageDialog(m_frame, "Failed to create instance of export file: \"" + loadedInstantiablePlugins.elementAt(pluginIndex).getName() + " (" + loadedInstantiablePlugins.elementAt(pluginIndex).getSupportedPaletteFileTypesAsString() + ")!", "Instantiation Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
			
			if(!selectedPalettePanel.updatePaletteData()) {
				PaletteManager.console.writeLine("Failed to update palette data for source palette while attempting to export file: \"" + selectedPalette.getFile().getName() + "!");
				
				JOptionPane.showMessageDialog(m_frame, "Failed to update palette data for source palette while attempting to export file: \"" + selectedPalette.getFile().getName() + "!", "Update Palette Failed", JOptionPane.ERROR_MESSAGE);
				
				return false;
			}
			newPalette.updateColourData(selectedPalette.getColourData(currentPaletteIndex));
			try {
				if(newPalette.save()) {
					PaletteManager.console.writeLine("Palette successfully exported to new file: " + newPalette.getFile().getName() + "!");
					
					numberOfPalettesExported++;
				}
				else {
					PaletteManager.console.writeLine("Failed to export palette!");
					
					JOptionPane.showMessageDialog(m_frame, "Failed to export palette!", "Export Failed", JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
			}
			catch(PaletteWriteException e) {
				PaletteManager.console.writeLine(e.getMessage());
				
				return false;
			}
			
			if(selectedPaletteIndex >= 0 || currentPaletteIndex >= selectedPalette.numberOfPalettes() - 1) {
				break;
			}
			
			currentPaletteIndex++;
		}
		
		if(numberOfPalettesExported > 1) {
			PaletteManager.console.writeLine("Successfully exported " + numberOfPalettesExported + " sub-palettes from palette: \"" + selectedPalette.getFile().getName() + "\".");
		}
		
		return true;
	}
	
	public boolean closeSelectedPalette() {
		return closePalette(getSelectedPalettePanel());
	}
	
	public boolean closePalette(PalettePanel palettePanel) {
		if(palettePanel == null) { return false; }
		
		if(palettePanel.isChanged()) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Would you like to save your changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(choice == JOptionPane.CANCEL_OPTION) { return false; }
			if(choice == JOptionPane.YES_OPTION) {
				if(!saveSelectedPalette()) {
					return false;
				}
			}
		}
		
		Component tabComponent = getTabComponentWith(palettePanel);
		if(tabComponent == null) { return false; }
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
		
		for(int i=0;i<m_palettePanels.size();i++) {
			if(!closePalette(m_palettePanels.elementAt(i))) {
				return false;
			}
			
			i--;
		}
		
		return true;
	}
	
	private void updateWindow() {
		m_settingsAutoScrollConsoleMenuItem.setSelected(PaletteManager.settings.autoScrollConsole);
		m_settingsLogConsoleMenuItem.setSelected(PaletteManager.settings.logConsole);
		m_settingsSupressUpdatesMenuItem.setSelected(PaletteManager.settings.supressUpdates);
		m_pluginsAutoLoadMenuItem.setSelected(PaletteManager.settings.autoLoadPlugins);
		m_settingsAutoSaveSettingsMenuItem.setSelected(PaletteManager.settings.autoSaveSettings);
		
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
		m_consoleText.setText(PaletteManager.console.toString());
		
		if(PaletteManager.settings.autoScrollConsole) {
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
		Color newColour = JColorChooser.showDialog(null, "Choose background colour", PaletteManager.settings.backgroundColour);
		if(newColour == null) { return; }
		
		PaletteManager.settings.backgroundColour = newColour;
		
		update();
	}
	
	public void resetWindowPosition() {
		PaletteManager.settings.windowPositionX = SettingsManager.defaultWindowPositionX;
		PaletteManager.settings.windowPositionY = SettingsManager.defaultWindowPositionY;
		
		m_frame.setLocation(PaletteManager.settings.windowPositionX, PaletteManager.settings.windowPositionY);
	}
	
	public void resetWindowSize() {
		PaletteManager.settings.windowWidth = SettingsManager.defaultWindowWidth;
		PaletteManager.settings.windowHeight = SettingsManager.defaultWindowHeight;
		
		m_frame.setSize(PaletteManager.settings.windowWidth, PaletteManager.settings.windowHeight);
	}
	
	public void changePixelButtonSizePrompt() {
		// prompt for pixel button size
		String input = JOptionPane.showInputDialog(m_frame, "Please enter the desired pixel button size:", PaletteManager.settings.pixelButtonSize);
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
			PaletteManager.console.writeLine("Pixel button size changed from " + PaletteManager.settings.pixelButtonSize + " to " + pixelButtonSize + ".");
			
			PaletteManager.settings.pixelButtonSize = pixelButtonSize;
			
			update();
		}
		else {
			PaletteManager.console.writeLine("Pixel button size must be between 1 and 64.");
		}
	}
	
	public void changePaletteSpacingPrompt() {
		// prompt for palette spacing
		String input = JOptionPane.showInputDialog(m_frame, "Please enter the distance for palette spacing:", PaletteManager.settings.paletteSpacing);
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
			PaletteManager.console.writeLine("Palette spacing changed from " + PaletteManager.settings.paletteSpacing + " to " + paletteSpacing + ".");
			
			PaletteManager.settings.paletteSpacing = paletteSpacing;
			
			update();
		}
		else {
			PaletteManager.console.writeLine("Palette spacing must be between 0 and 128.");
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
			importPalette();
		}
		// export palette
		else if(e.getSource() == m_fileExportMenuItem) {
			exportPalette();
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
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the plugin directory name:", PaletteManager.settings.pluginDirectoryName);
			if(input == null) { return; }
			
			String newPluginDirectoryName = input.trim();
			if(newPluginDirectoryName.length() == 0) { return; }
			
			if(!newPluginDirectoryName.equalsIgnoreCase(PaletteManager.settings.pluginDirectoryName)) {
				PaletteManager.settings.pluginDirectoryName = newPluginDirectoryName;
			}
		}
		// change the console log file name
		else if(e.getSource() == m_settingsConsoleLogFileNameMenuItem) {
			// prompt for the console log file name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the console log file name:", PaletteManager.settings.consoleLogFileName);
			if(input == null) { return; }
			
			String newConsoleLogFileName = input.trim();
			if(newConsoleLogFileName.length() == 0) { return; }
			
			if(!newConsoleLogFileName.equalsIgnoreCase(PaletteManager.settings.consoleLogFileName)) {
				PaletteManager.console.resetConsoleLogFileHeader();
				
				PaletteManager.settings.consoleLogFileName = newConsoleLogFileName;
			}
		}
		// change the log directory name
		else if(e.getSource() == m_settingsLogDirectoryNameMenuItem) {
			// prompt for the log directory name
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the log directory name:", PaletteManager.settings.logDirectoryName);
			if(input == null) { return; }
			
			String newLogDirectoryName = input.trim();
			if(newLogDirectoryName.length() == 0) { return; }
			
			if(!newLogDirectoryName.equalsIgnoreCase(PaletteManager.settings.logDirectoryName)) {
				PaletteManager.settings.logDirectoryName = newLogDirectoryName;
			}
		}
		else if(e.getSource() == m_settingsVersionFileURLMenuItem) {
			// prompt for the version file url
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the version file URL:", PaletteManager.settings.versionFileURL);
			if(input == null) { return; }
			
			String newVersionFileURL = input.trim();
			if(newVersionFileURL.length() == 0) { return; }
			
			if(!newVersionFileURL.equalsIgnoreCase(PaletteManager.settings.versionFileURL)) {
				PaletteManager.settings.versionFileURL = newVersionFileURL;
			}
		}
		else if(e.getSource() == m_settingsBackgroundColourMenuItem) {
			changeBackgroundColourPrompt();
		}
		// change the console auto scrolling
		else if(e.getSource() == m_settingsAutoScrollConsoleMenuItem) {
			PaletteManager.settings.autoScrollConsole = m_settingsAutoScrollConsoleMenuItem.isSelected();
		}
		// change the maximum number of elements the console can hold
		else if(e.getSource() == m_settingsMaxConsoleHistoryMenuItem) {
			// prompt for the maximum console history size
			String input = JOptionPane.showInputDialog(m_frame, "Please enter the maximum console history size:", PaletteManager.settings.maxConsoleHistory);
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
				PaletteManager.settings.maxConsoleHistory = maxConsoleHistory;
			}
		}
		// change console logging
		else if(e.getSource() == m_settingsLogConsoleMenuItem) {
			PaletteManager.settings.logConsole = m_settingsLogConsoleMenuItem.isSelected();
		}
		// change update notification supressing
		else if(e.getSource() == m_settingsSupressUpdatesMenuItem) {
			PaletteManager.settings.supressUpdates = m_settingsSupressUpdatesMenuItem.isSelected();
		}
		else if(e.getSource() == m_settingsAutoSaveSettingsMenuItem) {
			PaletteManager.settings.autoSaveSettings = m_settingsAutoSaveSettingsMenuItem.isSelected();
		}
		else if(e.getSource() == m_settingsSaveSettingsMenuItem) {
			if(PaletteManager.settings.save()) {
				PaletteManager.console.writeLine("Successfully saved settings to file: " + PaletteManager.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Successfully saved settings to file: " + PaletteManager.settings.settingsFileName, "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				PaletteManager.console.writeLine("Failed to save settings to file: " + PaletteManager.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to save settings to file: " + PaletteManager.settings.settingsFileName, "Settings Not Saved", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource() == m_settingsReloadSettingsMenuItem) {
			if(PaletteManager.settings.load()) {
				update();
				
				PaletteManager.console.writeLine("Settings successfully loaded from file: " + PaletteManager.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Settings successfully loaded from file: " + PaletteManager.settings.settingsFileName, "Settings Loaded", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				PaletteManager.console.writeLine("Failed to load settings from file: " + PaletteManager.settings.settingsFileName);
				
				JOptionPane.showMessageDialog(m_frame, "Failed to load settings from file: " + PaletteManager.settings.settingsFileName, "Settings Not Loaded", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getSource() == m_settingsResetSettingsMenuItem) {
			int choice = JOptionPane.showConfirmDialog(m_frame, "Are you sure you wish to reset all settings?", "Reset All Settings", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if(choice == JOptionPane.YES_OPTION) {
				PaletteManager.settings.reset();
				
				update();
				
				PaletteManager.console.writeLine("All settings reset to default values");
			}
		}
		// display a list of loaded plugins
		else if(e.getSource() == m_pluginsListLoadedMenuItem) {
			PaletteManager.instance.displayLoadedPlugins();
		}
		// prompt for a plugin to load
		else if(e.getSource() == m_pluginsLoadMenuItem) {
			PaletteManager.instance.loadPluginPrompt();
		}
		// load all plugins
		else if(e.getSource() == m_pluginsLoadAllMenuItem) {
			PaletteManager.instance.loadPlugins();
		}
		// toggle auto-loading of plugins
		else if(e.getSource() == m_pluginsAutoLoadMenuItem) {
			PaletteManager.settings.autoLoadPlugins = m_pluginsAutoLoadMenuItem.isSelected();
			
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
			JOptionPane.showMessageDialog(m_frame, "Palette Editor Version " + PaletteManager.VERSION + "\nCreated by Kevin Scroggins (a.k.a. nitro_glycerine)\nE-Mail: nitro404@gmail.com\nWebsite: http://www.nitro404.com", "About Palette Editor", JOptionPane.INFORMATION_MESSAGE);
		}
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
		
		PaletteManager.settings.windowPositionX = m_frame.getX();
		PaletteManager.settings.windowPositionY = m_frame.getY();
		PaletteManager.settings.windowWidth = m_frame.getWidth();
		PaletteManager.settings.windowHeight = m_frame.getHeight();
		
		// close the server
		PaletteManager.instance.close();
		
		m_frame.dispose();
		
		System.exit(0);
	}
	
}
