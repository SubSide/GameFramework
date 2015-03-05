package subside.frameworks.gameframework.exceptions;

public class AlreadyIngameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8514713750719035797L;
	
	public AlreadyIngameException(){
		super("You are already in a game!");
	}
}
