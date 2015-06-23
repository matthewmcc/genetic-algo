import java.math.BigDecimal;

public class Item {
	
	public float volume; 
	public float weight; 
	public float profit;
	public float price;
	
	public Item(float volume, float weight, float price, float profit) {
		this.volume = volume; 
		this.weight = weight; 
		this.profit = profit;
		this.price = price;
		
		roundValues();
	}
	
	/**
	 * Checks if 2 Items are the same
	 * @param item, Item to check
	 * @return true if Item's are the same, else false
	 */
	public boolean equals(Item item) {
		if (this.volume == item.volume && this.weight == item.weight && 
				this.profit == item.profit && this.price == item.price)
			return true;
		
		return false;
	}
	
	/**
	 * Rounds all the float fields to stop floating point errors effecting the 
	 * duplication finder
	 */
	public void roundValues() {
		price = round(price, 2);
		weight = round(weight, 2);
		volume = round(volume, 2);
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
