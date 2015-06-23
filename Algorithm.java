import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Algorithm {

	/**
	// 	Runs a genetic algorithm to find the best cargo load to bring on a boat for profit
	//	@param args contains three command line arguments
	//	the first is the maximum available volume, 
	//	the second is the maximum weight of goods that can be taken aboard, 
	// 	and the third is the maximum purchase price of the entire cargo that the owner can afford. 
	 * @throws FileNotFoundException 
	 **/
	public static void main(String[] args) throws FileNotFoundException {
		
		// Reads in arguments from the commandline
		if(args.length != 5) {
			System.out.println("Usage: <Max Available Volume> <Max Available Weight> <Max Purchase Price>" +
					" <Runtime> <Items Filename>");
			System.exit(-1);
		}

		float[] constraints = new float[3];		
		constraints[0]	= Float.parseFloat(args[0]);
		constraints[1] = Float.parseFloat(args[1]);
		constraints[2] = Float.parseFloat(args[2]);
		float timeGiven = Float.parseFloat(args[3]);
		// convert time allowed to nanotime
		float timeAllowed = (float) (Float.parseFloat(args[3]) * Math.pow(10, 9));
		int populationSize = Integer.parseInt(args[3]) * 7;
		String itemsFilename = args[4];
		
		List<Item> items = new ArrayList<Item>();
		items = parseItems(itemsFilename);
		
		Genetics g = new Genetics(populationSize, constraints);
		g.computable(items);
		g.generatePopulation(items);
		
		System.out.println("Running genetic algorithm on the " + itemsFilename + " data set"
								+ '\n' + "Volume constraint: " + constraints[0] 
								+ " Weight constraint: " + constraints[1] 
								+ " Price constraint: " + constraints[2]
								+ '\n' + "Algorithm will run for " + timeGiven + " seconds" 
								+ ", with a population size of " + populationSize
								+ '\n' + "Please wait patiently...");
		
		long startTime = System.nanoTime();
		while (g.run) {
			long runtime = System.nanoTime();
			g.generation();
			if (runtime - startTime > timeAllowed) 
				g.run = false;
		}
		
		
		Individual eliteAlphaPrime = g.getBest();
		
		// TODO:
		g.populationSelection();
		
		for (int i = 0; i < populationSize; i++) {
			System.out.println(g.population.get(i).profit + " " + g.population.get(i).totalVolume + 
					" " + g.population.get(i).totalWeight + " " + g.population.get(i).totalCost);
		}
		// TODO:
		
		System.out.println("EliteAlphaPrimes Profit = " + eliteAlphaPrime.profit + ", Completed " + g.cyclesCompleted + " cycles");
		System.out.println(eliteAlphaPrime.totalVolume + " " + eliteAlphaPrime.totalWeight + " " + eliteAlphaPrime.totalCost);
	}

	/**
	 * Reads the lines from the text file and converts them to Items
	 * @param filename, name of the text file to parse
	 * @return ArrayList<Item> contains all the Items parsed in from the text file
	 * @throws FileNotFoundException
	 */
	public static List<Item> parseItems(String filename) throws FileNotFoundException {
		File f = new File(filename);
		Scanner sc = new Scanner(f);
		List<Item> il = new ArrayList<Item>();
		float[] fa = new float[4];

		while(sc.hasNext()) {
			String[] split = sc.nextLine().split(" ");
			
			for (int i = 0; i < split.length; i++) {
				fa[i] = Float.parseFloat(split[i]);
			}

			Item it = new Item(fa[0], fa[1], fa[2], fa[3]);
			il.add(it);
		}

		sc.close();
		return il;
	}
}
