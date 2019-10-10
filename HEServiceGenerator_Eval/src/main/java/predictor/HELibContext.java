package predictor;

/**
 * Represents an HELib context.
 * @author Philip Kaiser
 *
 */
public class HELibContext extends CryptoContext {
	
	private double levels; // L
	private int modulo; // m
	private int plaintextBase; // p
	private int plaintextLifting; // r (plaintext space = p^r)
	private int security; // k
	
	/**
	 * Represents an HELib context.
	 * @param levels Amount of levels (bits)
	 * @param modulo Polynomial modulo
	 * @param plaintextBase Base of the plaintext p
	 * @param plaintextLifting Lifting of the plaintext - The plaintext space is p^r
	 * @param security Security level (bits)
	 */
	public HELibContext(double levels, int modulo, int plaintextBase, int plaintextLifting, int security) {
		this.levels = levels;
		this.modulo = modulo;
		this.plaintextBase = plaintextBase;
		this.plaintextLifting = plaintextLifting;
		this.security = security;
	}
	
	public double getLevels() {
		return levels;
	}
	public void setLevels(double levels) {
		this.levels = levels;
	}
	public int getModulo() {
		return modulo;
	}
	public void setModulo(int modulo) {
		this.modulo = modulo;
	}
	public int getPlaintextBase() {
		return plaintextBase;
	}
	public void setPlaintextBase(int plaintextBase) {
		this.plaintextBase = plaintextBase;
	}
	public int getPlaintextLifting() {
		return plaintextLifting;
	}
	public void setPlaintextLifting(int plaintextLifting) {
		this.plaintextLifting = plaintextLifting;
	}
	public int getSecurity() {
		return security;
	}
	public void setSecurity(int security) {
		this.security = security;
	}
}
