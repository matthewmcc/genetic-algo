import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Individual implements Comparable<Individual> {

	public List<Item> inventory;
	private Random ran = new Random();

	public float totalVolume;
	public float totalWeight;
	public float totalCost;
	public float profit;

	public Individual() {
		inventory  = new ArrayList<Item>();
		totalVolume = 0;
		totalWeight = 0;
		totalCost = 0;
		profit = 0;
	}

	/**
	 * Adds a new Item to the inventory and updates the total fields
	 * @param i, Item to be added to the inventory
	 */
	public void addItem(Item i) {
		inventory.add(i);
		totalVolume += i.volume;
		totalWeight += i.weight;
		totalCost += i.price;
		profit += (i.profit - i.price);
	}
	
	/**
	 * Returns the Item with index i from inventory
	 * @param i, index of item to be returned
	 * @return
	 */
	public Item getItem(int i) {
		return inventory.get(i);
	}

	/**
	 * Removes the last Item from the inventory
	 */
	public void removeLast() {
		if (inventory.size() != 0) {
			Item i = inventory.remove(inventory.size() - 1);
			totalVolume -= i.volume;
			totalWeight -= i.weight;
			totalCost -= i.price;
			profit -= (i.profit - i.price);
		}
	}
	
	/**
	 * Removes the Item at the index j and returns it
	 * @param j, index of Item to be removed
	 * @return Item remove for the inventory
	 */
	public Item removeAt(int j) {
		Item i = null;
		if (inventory.size() != 0) {
			i = inventory.remove(j);
			totalVolume -= i.volume;
			totalWeight -= i.weight;
			totalCost -= i.price;
			profit -= (i.profit - i.price);
		}
		return i;
	}
	
	/**
	 * Removes the Item at index j from the list if is a different Item then item
	 * @param j, index of the item to remove
	 * @param item, Item to compare the removed item to
	 * @return true if Item was removed, else false
	 */
	public boolean mutateRemove(int j, Item item) {
		Item i = null;
		if (inventory.size() != 0) {
			i = inventory.remove(j);
			if(i.equals(item)) {
				this.addItem(i);
				return false;
			}
			totalVolume -= i.volume;
			totalWeight -= i.weight;
			totalCost -= i.price;
			profit -= (i.profit - i.price);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a random Item and adds the Item i to the inventory
	 * @param i, Item to add
	 * @return	Item that was removed
	 */
	public Item mutate(Item i) {
		Item item = this.removeAt(ran.nextInt(inventory.size()));
		this.addItem(i);
		return item;
	}
	
	/**
	 * Checks if the Item i is in the inventory
	 * @param i, Item to check for
	 * @return true if the Item i in the inventory, else false
	 */
	public boolean contains(Item i){
		if (inventory.contains(i)) return true;
		else return false;
	}
	
	/**
	 * @return size of the inventory
	 */
	public int size() {
		return inventory.size();
	}
	
	/**
	 * @return random int that is a valid index of the inventory
	 */
	public int crossoverPoint() {
		return ran.nextInt(inventory.size());
	}
	
	/**
	 * Function used by Collections.sort to compare Individuals for sorting
	 */
	public int compareTo(Individual o) {
		if (o.profit < this.profit) return -1;
		if (o.profit > this.profit) return 1;
		
		if (o.totalCost < this.totalCost) return -1;
		if (o.totalCost > this.totalCost) return 1;
		
		if (o.totalWeight < this.totalWeight) return -1;
		if (o.totalWeight > this.totalWeight) return 1;
		
		if (o.totalVolume < this.totalVolume) return -1;
		if (o.totalVolume > this.totalVolume) return 1;
		
		return 0;
	}
	
	/**
	 *  Checks if two individuals are the same
	 * @param ind
	 * @return
	 */
	public boolean equals(Individual ind) {
		if(ind.size() != this.size() || ind.profit != this.profit ||
				ind.totalWeight != this.totalWeight || ind.totalVolume != this.totalVolume ||
				ind.totalCost != this.totalCost) return false;
		
		return true;
	}
	
	/**
	 * Creates a new individual, copys the contents of this inventory to the new
	 * individuals inventory and return the new individual.
	 * @return
	 */
	public Individual copy() {
		Individual i = new Individual();
		for(Item it : this.inventory) {
			i.addItem(it);
		}
		return i;
	}
	
	/**
	 * Rounds all the float fields to stop floating point errors effecting the 
	 * duplication finder
	 */
	public void roundValues() {
		totalCost = round(totalCost, 2);
		totalWeight = round(totalWeight, 2);
		totalVolume = round(totalVolume, 2);
		profit = round(profit, 2);
	}
	
	/**
     * Rounds to certain number of decimals
     * 
     * @param d
     * @param decimalPlace
     * @return
     */
    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
