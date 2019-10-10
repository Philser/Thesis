package predictor;

import java.util.List;

/**
 * Predictor for a HELib implementation to estimate runtime and decide whether bootstrapping is necessary.
 * @author Philip Kaiser
 *
 */
public class HELibPredictor extends Predictor {
	
	@Override
	public double predictRuntime(List<String> parsedTokens, CryptoContext context) {
		
		HELibContext helContext = (HELibContext) context;
		double mulTimeInS = Heuristics.getTimeCostsForMul(helContext.getModulo(), helContext.getPlaintextLifting());
		double addTimeInS = Heuristics.getTimeCostsForAdd(helContext.getModulo(), helContext.getPlaintextLifting());
		
		double bootstrapTimeInS = Heuristics.getTimeCostForRecrypt(helContext.getModulo(), 
				helContext.getLevels(), helContext.getPlaintextLifting());
		int bootstrapAmount = estimateNumberOfBootstrapping(parsedTokens, helContext);
		double initTime = Heuristics.getInitTimeCosts(helContext.getModulo(), needsBootstrapping);
		double prediction = 0.0;
		
		for(int i = 0; i < parsedTokens.size(); i++) {
			switch(parsedTokens.get((i))) {
			case "*":
				prediction += mulTimeInS;
				break;
			case "+":
			case "-":
				prediction += addTimeInS;
				break;
			default:
				break;
			}
		}
		
		prediction += initTime + bootstrapAmount * bootstrapTimeInS;		
		this.estimatedRuntimeInS = prediction;
		return prediction;		
	}

	/**
	 * Decides whether the evaluation of a function needs to include bootstrapping.
	 * @param parsedTokens Parsed tokens (variables, constants, operators)
	 * @param context The HELib context this evaluation is to be performed with.
	 * @return True, if bootstrapping is necessary.
	 */
	public boolean predictNeedsBootstrapping(List<String> parsedTokens, HELibContext context) {
		
		double mulCostInL = Heuristics.getLCostsForMul(context.getModulo(), context.getPlaintextLifting());
		double addCostInL = Heuristics.getLCostsForAdd(context.getModulo(), context.getPlaintextLifting());
		double levelBudget = context.getLevels();
		double uncertaintyFactor = 10; // Just to be sure with the prediction since the level cost sometimes vary slightly
		double overallLevelCosts = 0;

		for(int i=0; i < parsedTokens.size(); i++) {
			switch(parsedTokens.get(i)) {
			case "*":
				System.out.println("Found MUL. Costs: " + mulCostInL);
				overallLevelCosts += mulCostInL;
				break;
			case "+":
				System.out.println("Found ADD. Costs: " + addCostInL);
				overallLevelCosts += addCostInL;
				break;
			default:
				break;
			}
		}
		
//		if(overallLevelCosts >= levelBudget - uncertaintyFactor) {
//			System.out.println("Will need to bootstrap");
//			this.needsBootstrapping = true;
//			return true;
//		}
//		else {
//			System.out.println("No need to bootstrap");
//			this.needsBootstrapping = false;
//			return false;
//		}
		System.out.println("Will need to bootstrap");
		this.needsBootstrapping = true;
		return true;
	}

	private double predictLevelThreshold(List<String> operators, HELibContext context) {
		// TODO: Extract from heuristic
		double levelThreshold = 0;
		
		if(context.getModulo() < 10)
			levelThreshold = 500;
		else
			levelThreshold = 600;
		
		this.levelThreshold = levelThreshold;
		return levelThreshold;
	}
	
	
	private int estimateNumberOfBootstrapping(List<String> operators, HELibContext context) {
		
		return 0;
	}
	
	public String getBootstrappingExpression(String varName) {
		// TODO Auto-generated method stub
		return null;
	}
}