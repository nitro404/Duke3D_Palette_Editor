package exception;

public class PalettePanelInstantiationException extends Exception {
	
	private static final long serialVersionUID = -1806724280312994674L;
	
	public PalettePanelInstantiationException() {
		super();
	}
	
	public PalettePanelInstantiationException(String message) {
		super(message);
	}
	
	public PalettePanelInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PalettePanelInstantiationException(Throwable cause) {
		super(cause);
	}
	
}
