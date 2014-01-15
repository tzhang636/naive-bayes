import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class NaiveBayes {

	private static HashMap<String, Double> featCounts = new HashMap<String, Double>();
	private static HashMap<String, Double> grCounts = new HashMap<String, Double>();
	private static HashMap<String, Double> leqCounts = new HashMap<String, Double>();
	private static HashMap<String, Double> grProbs = new HashMap<String, Double>();
	private static HashMap<String, Double> leqProbs = new HashMap<String, Double>();
	private static double grTotal = 0.0;
	private static double leqTotal = 0.0;
	private static double grTotalProb = 0.0;
	private static double leqTotalProb = 0.0;

	private static void initialize() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(
						"C:\\Users\\Thomas Zhang\\Documents\\Eclipse EE\\workspace\\cs446-hw5\\data\\values.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] strArray = line.split(",");
			featCounts.put(strArray[0], Double.parseDouble(strArray[1]));
			grCounts.put(strArray[0], 0.0);
			leqCounts.put(strArray[0], 0.0);
			grProbs.put(strArray[0], 0.0);
			leqProbs.put(strArray[0], 0.0);
		}
		br.close();
	}

	private static void count() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(
						"C:\\Users\\Thomas Zhang\\Documents\\Eclipse EE\\workspace\\cs446-hw5\\data\\train_data.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String[] strArray = line.split(",");
			String label = strArray[strArray.length - 1];
			if (label.equals(">50K")) {
				for (int i = 0; i < strArray.length - 1; ++i) {
					String feature = strArray[i];
					grCounts.put(feature, grCounts.get(feature) + 1);
				}
				++grTotal;
			} else { // <=50K
				for (int i = 0; i < strArray.length - 1; ++i) {
					String feature = strArray[i];
					leqCounts.put(feature, leqCounts.get(feature) + 1);
				}
				++leqTotal;
			}
		}
		br.close();
	}

	private static void computeProbs() {
		for (String feature : grCounts.keySet()) {
			grProbs.put(feature,
					(grCounts.get(feature) + 1) / (grTotal + featCounts.get(feature)));
			leqProbs.put(feature, 
					(leqCounts.get(feature) + 1) / (leqTotal + featCounts.get(feature)));
		}
		grTotalProb = grTotal / (grTotal + leqTotal);
		leqTotalProb = leqTotal / (grTotal + leqTotal);
	}

	private static double getAccuracy() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(
						"C:\\Users\\Thomas Zhang\\Documents\\Eclipse EE\\workspace\\cs446-hw5\\data\\test_data.txt"));
		String line;
		double correct = 0.0;
		double total = 0.0;
		while ((line = br.readLine()) != null) {
			String[] strArray = line.split(",");

			double grScore = grTotalProb;
			for (int i = 0; i < strArray.length - 1; ++i) {
				grScore *= grProbs.get(strArray[i]);
			}

			double leqScore = leqTotalProb;
			for (int i = 0; i < strArray.length - 1; ++i) {
				leqScore *= leqProbs.get(strArray[i]);
			}

			String predictedLabel;
			if (grScore > leqScore) {
				predictedLabel = ">50K";
			} else {
				predictedLabel = "<=50K";
			}

			String label = strArray[strArray.length - 1];
			if (predictedLabel.equals(label)) {
				++correct;
			}
			++total;
		}
		br.close();
		return correct / total;
	}

	public static void main(String[] args) throws IOException {
		initialize();
		count();
		computeProbs();
		double accuracy = getAccuracy();

		System.out.println("Pr(<=50K) = " + leqTotalProb);
		System.out.println("Pr(education = Bachelors | label = >50K) = "
				+ grProbs.get("Bachelors"));
		System.out.println("Accuracy = " + accuracy);
	}
}