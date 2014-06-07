package gui;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import exception.*;
import settings.*;
import utilities.*;
import palette.*;

public class PalettePanel extends JPanel implements Scrollable, ActionListener, MouseListener, Updatable {
	
	private int m_paletteNumber;
	private Palette m_palette;
	private PixelButton m_buttons[];
	private Dimension m_dimensions;
	private Vector<PaletteChangeListener> m_paletteChangeListeners;
	private boolean m_changed;
	private boolean m_initialized;
	
	public static final int PALETTE_SPACING = PixelButton.BUTTON_SIZE * 2;
	
	private JPopupMenu m_palettePanelPopupMenu;
	private JMenuItem m_savePopupMenuItem;
	private JMenuItem m_saveAsPopupMenuItem;
	private JMenuItem m_importPopupMenuItem;
	private JMenuItem m_exportPopupMenuItem;
	private JMenuItem m_closePopupMenuItem;
	private JMenuItem m_canelPopupMenuItem;
	
	private static final long serialVersionUID = -7026896833349413736L;

	public PalettePanel() {
		this(null);
	}
	
	public PalettePanel(Palette palette) {
		setLayout(null);
		setBackground(SettingsManager.defaultBackgroundColour);
		
		m_paletteNumber = PaletteManager.getPaletteNumber();
		m_dimensions = new Dimension(Palette.PALETTE_WIDTH * PixelButton.BUTTON_SIZE, Palette.PALETTE_HEIGHT * PixelButton.BUTTON_SIZE);
		m_paletteChangeListeners = new Vector<PaletteChangeListener>();
		m_changed = false;
		m_initialized = false;
		
		setPalette(palette);
		
		initPopupMenu();
		
		addMouseListener(this);
	}
	
	public void initPopupMenu() {
		m_palettePanelPopupMenu = new JPopupMenu();
		
		m_savePopupMenuItem = new JMenuItem("Save");
		m_saveAsPopupMenuItem = new JMenuItem("Save As");
		m_importPopupMenuItem = new JMenuItem("Import");
		m_exportPopupMenuItem = new JMenuItem("Export");
		m_closePopupMenuItem = new JMenuItem("Close");
		m_canelPopupMenuItem = new JMenuItem("Cancel");
		
		m_savePopupMenuItem.addActionListener(this);
		m_saveAsPopupMenuItem.addActionListener(this);
		m_importPopupMenuItem.addActionListener(this);
		m_exportPopupMenuItem.addActionListener(this);
		m_closePopupMenuItem.addActionListener(this);
		m_canelPopupMenuItem.addActionListener(this);
		
		m_palettePanelPopupMenu.add(m_savePopupMenuItem);
		m_palettePanelPopupMenu.add(m_saveAsPopupMenuItem);
		m_palettePanelPopupMenu.add(m_importPopupMenuItem);
		m_palettePanelPopupMenu.add(m_exportPopupMenuItem);
		m_palettePanelPopupMenu.add(m_closePopupMenuItem);
		m_palettePanelPopupMenu.addSeparator();
		m_palettePanelPopupMenu.add(m_canelPopupMenuItem);
	}
	
	public int getPaletteNumber() {
		return m_paletteNumber;
	}
	
	public String getTabName() {
		String fileName = m_palette.getFile() == null ? null : m_palette.getFile().getName();
		return fileName == null ? "NEW " + m_palette.getType() + " *" : fileName + (m_changed ? " *" : "");
	}
	
	public String getTabDescription() {
		String fileName = m_palette.getFile() == null ? null : m_palette.getFile().getName();
		return "Palette " + m_paletteNumber + (fileName == null ? "" : " (" + fileName + ")");
	}
	
	public Palette getPalette() {
		return m_palette;
	}
	
	public String getExtension() {
		return m_palette.getExtension();
	}
	
	public boolean isChanged() {
		return m_changed;
	}
	
	public void setChanged(boolean changed) {
		m_changed = changed;
		
		if(m_changed) {
			handlePaletteChange();
		}
	}
	
	public boolean isSameFile(File file) {
		if(file == null || m_palette.getFile() == null) { return false; }
		
		File localCanonicalFile = null;
		File externalCanonicalFile = null;
		try {
			localCanonicalFile = m_palette.getFile().getCanonicalFile();
			externalCanonicalFile = file.getCanonicalFile();
			
			return localCanonicalFile.equals(externalCanonicalFile);
		}
		catch(IOException e) {
			return m_palette.getFile().equals(file);
		}
	}

	public int numberOfPaletteChangeListeners() {
		return m_paletteChangeListeners.size();
	}
	
	public PaletteChangeListener getPaletteChangeListener(int index) {
		if(index < 0 || index >= m_paletteChangeListeners.size()) { return null; }
		return m_paletteChangeListeners.elementAt(index);
	}
	
	public boolean hasPaletteChangeListener(PaletteChangeListener a) {
		return m_paletteChangeListeners.contains(a);
	}
	
	public int indexOfPaletteChangeListener(PaletteChangeListener a) {
		return m_paletteChangeListeners.indexOf(a);
	}
	
	public boolean addPaletteChangeListener(PaletteChangeListener a) {
		if(a == null || m_paletteChangeListeners.contains(a)) { return false; }
		
		m_paletteChangeListeners.add(a);
		
		return true;
	}
	
	public boolean removePaletteChangeListener(int index) {
		if(index < 0 || index >= m_paletteChangeListeners.size()) { return false; }
		m_paletteChangeListeners.remove(index);
		return true;
	}
	
	public boolean removePaletteChangeListener(PaletteChangeListener a) {
		if(a == null) { return false; }
		return m_paletteChangeListeners.remove(a);
	}
	
	public void clearPaletteChangeListeners() {
		m_paletteChangeListeners.clear();
	}
	
	public void handlePaletteChange() {
		for(int i=0;i<m_paletteChangeListeners.size();i++) {
			m_paletteChangeListeners.elementAt(i).notifyPaletteChanged(this);
		}
	}
	
	private void setPalette(Palette palette) {
		m_palette = palette;
		
		if(m_buttons != null) {
			for(int i=0;i<m_buttons.length;i++) {
				remove(m_buttons[i]);
			}
			m_buttons = null;
		}
		
		if(m_palette == null) {
			m_initialized = false;
		}
		else {
			int numberOfPixels = Palette.NUMBER_OF_COLOURS * m_palette.numberOfPalettes();
			int pixelIndex = 0;
			m_buttons = new PixelButton[numberOfPixels];
			for(int p=0;p<m_palette.numberOfPalettes();p++) {
				for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
					for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
						pixelIndex = (p * Palette.NUMBER_OF_COLOURS) + (j * Palette.PALETTE_WIDTH) + i;
						m_buttons[pixelIndex] = new PixelButton(m_palette.getPixel(p, i, j), i, j, p);
						m_buttons[pixelIndex].addActionListener(this);
						add(m_buttons[pixelIndex]);
					}
				}
			}
			
			m_initialized = true;
		}
		
		updateLayout();
	}
	
	public boolean updatePaletteData() {
		if(!m_initialized) { return false; }
		
		int numberOfPalettes = m_palette.numberOfPalettes();
		Color colourData[] = new Color[Palette.NUMBER_OF_COLOURS * numberOfPalettes];
		int pixelIndex = 0;
		for(int p=0;p<numberOfPalettes;p++) {
			for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
				for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
					pixelIndex = (p * Palette.NUMBER_OF_COLOURS) + (j * Palette.PALETTE_WIDTH) + i;
					colourData[pixelIndex] = m_buttons[pixelIndex].getBackground();
				}
			}
		}
		
		return m_palette.updateAllColourData(colourData);
	}
	
	public boolean updatePixelButtons() {
		if(!m_initialized) { return false; }
		
		for(int p=0;p<m_palette.numberOfPalettes();p++) {
			for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
				for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
					m_buttons[(p * Palette.NUMBER_OF_COLOURS) + (j * Palette.PALETTE_WIDTH) + i].setBackground(m_palette.getPixel(p, i, j));
				}
			}
		}
		
		return true;
	}
	
	public boolean save() throws PaletteWriteException {
		return save(true);
	}
	
	public boolean save(boolean update) throws PaletteWriteException {
		if(!m_initialized) { return false; }
		if(update && !updatePaletteData()) { return false; }
		boolean saved = m_palette.save();
		if(saved) { setChanged(false); }
		return saved;
	}
	
	public void update() {
		if(!m_initialized) { return; }
		
		setBackground(PaletteManager.settings.backgroundColour);
		
		repaint();
		revalidate();
	}
	
	public void updateLayout() {
		if(!m_initialized) { return; }
		
		setBackground(PaletteManager.settings.backgroundColour);
		
		Component parent = getParent();
		int parentWidth = parent == null ? 0 : parent.getWidth();
		int parentHeight = parent == null ? 0 : parent.getHeight();
		int buttonSize = PaletteManager.settings.pixelButtonSize;
		int paletteSpacing = PaletteManager.settings.paletteSpacing;
		int paletteWidth = Palette.PALETTE_WIDTH * buttonSize;
		int paletteHeight = Palette.PALETTE_HEIGHT * buttonSize;
		int numberOfHorizontalPalettes = 1 + (parent == null ? 1 : (int) (Math.floor((float) (parentWidth - paletteWidth) / (float) (paletteWidth + paletteSpacing))));
		int numberOfVerticalPalettes = (int) Math.ceil((float) m_palette.numberOfPalettes() / (float) numberOfHorizontalPalettes);
		int newWidth = paletteWidth + (m_palette.numberOfPalettes() > 1 ? paletteWidth + paletteSpacing : 0);
		int newHeight = (numberOfVerticalPalettes * paletteHeight) + ((numberOfVerticalPalettes - 1) * paletteSpacing);
		m_dimensions = new Dimension(parentWidth > newWidth ? parentWidth : newWidth, parentHeight > newHeight ? parentHeight : newHeight);
		int numberOfPalettes = m_palette.numberOfPalettes();
		int paletteSize = Palette.PALETTE_WIDTH * Palette.PALETTE_HEIGHT;
		int horizontalSpacing = (Palette.PALETTE_WIDTH * buttonSize) + paletteSpacing;
		int verticalSpacing = (Palette.PALETTE_HEIGHT * buttonSize) + paletteSpacing;
		int x = 0;
		int y = 0;
		int pixelIndex = 0;
		for(int p=0;p<numberOfPalettes;p++) {
			for(int j=0;j<Palette.PALETTE_HEIGHT;j++) {
				for(int i=0;i<Palette.PALETTE_WIDTH;i++) {
					pixelIndex = (p * paletteSize) + (j * Palette.PALETTE_WIDTH) + i;
					m_buttons[pixelIndex].setLocation((i * buttonSize) + (x * horizontalSpacing), (j * buttonSize) + (y * verticalSpacing));
					m_buttons[pixelIndex].setPreferredSize(new Dimension(buttonSize, buttonSize));
					m_buttons[pixelIndex].setSize(buttonSize, buttonSize);
					m_buttons[pixelIndex].setBackground(m_palette.getPixel(p, i, j));
				}
			}
			
			if(x >= numberOfHorizontalPalettes - 1) {
				y++;
				x = 0;
			}
			else {
				x++;
			}
		}
		
		revalidate();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(!m_initialized || e == null || e.getSource() == null) { return; }
		
		if(e.getSource() instanceof PixelButton) {
			PixelButton pixelButton = (PixelButton) e.getSource();
			if(pixelButton.chooseColour()) {
				m_palette.updatePixel(pixelButton.getPixelX(), pixelButton.getPixelY(), pixelButton.getBackground(), pixelButton.getPaletteIndex());
				
				setChanged(true);
			}
		}
		else if(e.getSource() == m_savePopupMenuItem) {
			PaletteManager.paletteEditorWindow.saveSelectedPalette();
		}
		else if(e.getSource() == m_saveAsPopupMenuItem) {
			PaletteManager.paletteEditorWindow.saveSelectedPaletteAsNew();
		}
		else if(e.getSource() == m_importPopupMenuItem) {
			PaletteManager.paletteEditorWindow.importPalette();
		}
		else if(e.getSource() == m_exportPopupMenuItem) {
			PaletteManager.paletteEditorWindow.exportPalette();
		}
		else if(e.getSource() == m_closePopupMenuItem) {
			PaletteManager.paletteEditorWindow.closeSelectedPalette();
		}
	}
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }
	
	public void mouseReleased(MouseEvent e) {
		if(!m_initialized) { return; }
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			m_palettePanelPopupMenu.show(this, e.getX(), e.getY());
		}
	}
	
	public Dimension getPreferredSize() {
		return m_dimensions;
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		int currentPosition = 0;
		if(orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		}
		else {
			currentPosition = visibleRect.y;
		}
        
		int maxUnitIncrement = 40;
		if(direction < 0) {
			int newPosition = currentPosition -
							  (currentPosition / maxUnitIncrement)
                              * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        }
		else {
            return ((currentPosition / maxUnitIncrement) + 1)
                   * maxUnitIncrement
                   - currentPosition;
        }
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if(orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - 5;
		}
		else {
			return visibleRect.height - 5;
		}
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
}
