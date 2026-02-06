package vo;

public class FoodDTO {
       int food_idx; 
       int food_price; 
       int food_stock;
    public FoodDTO(){}
    
    public FoodDTO(String food_name, int food_price, int food_stock){
     this.food_name = food_name;
     this.food_price = food_price;
     this.food_stock = food_stock;
    }
    public FoodDTO(int food_idx, String food_name, int food_price, int food_stock){
        this.food_idx = food_idx;
        this.food_name = food_name;
        this.food_price = food_price;
        this.food_stock = food_stock;
    }
   public void setFood_idx(int food_idx) {
        this.food_idx = food_idx;
    }

       public void setFood_price(int food_price) {
           this.food_price = food_price;
       }

       public void setFood_stock(int food_stock) {
           this.food_stock = food_stock;
       }

   public int getFood_idx() {
        return food_idx;
    }

       public int getFood_price() {
           return food_price;
       }

       public int getFood_stock() {
           return food_stock;
       }

   String food_name;

	public String getFood_name() {
		return this.food_name;
	}

	public void setFood_name(String food_name) {
		this.food_name = food_name;
	}
}
