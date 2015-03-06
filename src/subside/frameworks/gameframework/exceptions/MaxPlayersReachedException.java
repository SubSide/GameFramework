package subside.frameworks.gameframework.exceptions;

public class MaxPlayersReachedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 466358682679021566L;

	public MaxPlayersReachedException(){
		super("This game already has its max players!");
	}
}
