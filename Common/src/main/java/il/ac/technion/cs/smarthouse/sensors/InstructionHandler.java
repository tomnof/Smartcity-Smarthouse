package il.ac.technion.cs.smarthouse.sensors;

/**
 * @author Yarden
 * @author Inbal Zukerman
 * @since 31.3.17
 */
public interface InstructionHandler {
	/**
	 * Acts due to an instruction.
	 * 
	 * @return <code>true</code> if the action was successful,
	 *         <code>false</code> otherwise
	 */
	boolean applyInstruction(String path, String instruction);
}