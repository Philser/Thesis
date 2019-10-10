package predictor;

import java.util.List;

public abstract class Predictor {
	
	protected double levelThreshold = 0.0;
	protected boolean needsBootstrapping = false;
	protected double estimatedRuntimeInS = 0;
	protected CryptoContext context;
	
	public abstract double predictRuntime(List<String> parsedTokens, CryptoContext context);
		
	public boolean needsBootstrapping() {
		return needsBootstrapping;
	}
	
	public double getLevelThreshold() {
		return levelThreshold;
	}
	
	public double getEstimatedRuntime() {
		return estimatedRuntimeInS;
	}


	public CryptoContext getContext() {
		return context;
	}
}