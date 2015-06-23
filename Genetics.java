import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Genetics {

	Random ran = new Random();
	List<Individual> population;
	List<Item> items;
	List<Item> removedItems;
	float[] contraints;
	boolean run = true;
	int popSize, cyclesCompleted;	// Size of the population
	double mutationRate;	// Rate at which the mutation function is called

	public Genetics(int p, float[] c) {
		popSize = p;
		contraints = c;
		removedItems = new ArrayList<Item>();
		mutationRate = 0.15;
		cyclesCompleted = 0;
		population = new ArrayList<Individual>();
	}

	/**
	 * Creates a population of individuals by randomly inserting items until the constraints  are met
	 * @param li
	 */
	public void generatePopulation(List<Item> li) {
		items = li;		

		// Add items to individuals to the population until they break constraints
		while (population.size() < popSize) {

			Individual ind = new Individual();

			// add items until one of the constraints is broken then remove the item that overloaded the Individual
			while(!breaksConstraints(ind)){				
				ind.addItem(getRandom());				
			}				

			ind.removeLast();

			ind.roundValues();
			if(ind.size() != 0)
				population.add(ind);

			for (Item i : removedItems){
				items.add(i);
			}
			removedItems.clear();
		}				
	}		

	/**
	 * Gets a random item from the complete inventory and returns it
	 * @return
	 */
	private Item getRandom() {		
		// Assumes list of items in inventory on hand no duplicates
		int index = ran.nextInt(items.size());
		Item i = items.remove(index);
		removedItems.add(i);
		return i;		
	}

	/**
	 * If the current individual is breaking the constraints given by the algorithm true is
	 * returned, else false
	 * @param ind
	 * @return
	 */
	private boolean breaksConstraints(Individual ind) {		
		if (contraints[0] < ind.totalVolume || 
				contraints[1] < ind.totalWeight || 
				contraints[2] < ind.totalCost) {
			return true;
		}
		return false;
	}

	/**
	 * Runs one cycle of the algorithm
	 */
	public void generation() {
		populationSelection();
		crossoverPopulation();
		while(probabilityBool(mutationRate))
			mutatePopulation();

		cyclesCompleted++;
	}

	/**
	 * Sorts the population and returns the individual with the highest profit
	 * @return
	 */
	public Individual getBest() {
		System.out.println("Size of inventory = " + population.get(0).size());
		return population.get(0);
	}

	/**
	 *	Sorts the population then culls all weak individuals to reduce the population back to popSize
	 */
	public void populationSelection() {

		// Sorts the population by the compareTo function implemented in Individual
		Collections.sort(population);

		// Removes duplicates from the population before they mates with each other
		Individual a, b;
		for(int i = 0; i < population.size() - 1 && run; i++) {
			a = population.get(i);
			b = population.get(i + 1);
			if(a.equals(b)) {
				population.remove(i);
				i--;
			}
		}

		// Reduces the population to popSize 
		while (population.size() > popSize) {
			population.remove(population.size() - 1);
		}
		
		// Checks an errors hasn't occurred with constraint sizes
		if (population.size() < popSize) {
			System.out.println("Population converged, program terminated with no result " +
					'\n' + "	Please increase constraints and retry");
			System.exit(-1);
		}
	}

	/**
	 * Increases the population size to the size of the variable mate by a single crossover function
	 */
	private void crossoverPopulation() {
		Individual[] parents = new Individual[2];
		Individual child = new Individual();
		int crossover, r1, r2, mate = popSize * 2;
		Item k;


		// Adds new individuals to the population until the population has doubled in size
		while(population.size() < mate && run) {
			r1 = ran.nextInt(popSize);
			parents[0] = population.get(r1);

			while (true) {
				r2 = ran.nextInt(popSize);
				if(r1 != r2) {
					break;
				}
			}
			parents[1] = population.get(r2);

			child = new Individual();

			// Computes a random crossover index
			if (parents[0].size() < parents[1].size()) 
				crossover = parents[0].crossoverPoint();			
			else 
				crossover = parents[1].crossoverPoint();			

			for (int j = 0; j < crossover; j++) {
				child.addItem(parents[0].getItem(j));
			}

			for (int j = crossover; j < parents[1].size(); j++) {
				if(!child.inventory.contains(parents[1].getItem(j)))
					child.addItem(parents[1].getItem(j));
			}

			// If after a crossover the child doesn't break constraints new items are added...
			// ..until constraints are broken
			while(!breaksConstraints(child)) {
				k = items.get(ran.nextInt(items.size()));
				if (!child.contains(k)){					
					child.addItem(k);
				}
			}

			// If after crossover a child breaks constraints a random item is removed until constraints..
			// ...hold
			while (breaksConstraints(child)) {
				child.removeAt(ran.nextInt(child.size()));
			}

			// If after crossover a child is different from it's parents it's added to the population 
			if (!child.equals(parents[0]) && !child.equals(parents[1])) {
				child.roundValues();
				if(child.size() != 0) 
					population.add(child);
			}
		}	
	}

	/** 
	 * Creates a new individual by mutating an individual from the population and
	 * adds the mutated individual to the list 
	 */
	private void mutatePopulation() {

		Individual mutant = new Individual();
		Item k = null;

		// Pick the Individual to mutate
		mutant = population.get(ran.nextInt(popSize)).copy();

		while (run) {
			k = items.get(ran.nextInt(items.size()));
			if (!mutant.contains(k)){					
				mutant.mutate(k);
				break;
			}
		}

		// Tries to remove items from mutant 50 times if the mutant breaks...
		// ...constraints
		for(int i = 0; i < 50 && breaksConstraints(mutant); i++) {
			if(mutant.mutateRemove(ran.nextInt(mutant.size()), k))
				break;
		}		

		// Adds mutant to population if it doesn't break the constraints
		mutant.roundValues();
		if(mutant.size() != 0 && !breaksConstraints(mutant)) 
			population.add(mutant);	
	}	

	/**
	 * Rounds all float fields of the population to stop floating point errors effecting
	 * the populations diversity
	 */
	public void roundPopulation() {
		for(Individual i : population) {
			i.roundValues();
		}
	}

	/**
	 * Returns a boolean depending of the probability given
	 * @param prob
	 * @return
	 */
	public boolean probabilityBool(double prob) {
		return prob > ran.nextDouble();
	}

	/**
	 * Checks if the problem is computable for a genetic algorithm  
	 */
	public void computable(List<Item> items) {
		Individual ind = new Individual();
		int i = 0;

		for (; i < items.size() - 1; i++) {
			ind.addItem(items.get(i));
			if(breaksConstraints(ind))
				break;
		}

		// If these constraints are meet the problem is computable
		if (i > 0 && i < items.size() -1) return;

		// Checks if all items fit within the constraints
		if (i == items.size() - 1) {
			System.out.println("All items fit within the constraints " +
					'\n' + "Total profit = " + ind.profit);
			System.exit(1);
		}

		// Checks if any items can fit in the inventory without breaking constraints
		for (int j = 0; j < items.size(); j++) {
			ind.inventory.clear();
			ind.addItem(items.get(j));
			if(!breaksConstraints(ind)) return;
		}

		System.out.println("Constraints are to small for any item to fit in the inventory");
		System.exit(1);

	}
}
