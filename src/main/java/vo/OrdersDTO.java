package vo;

public class OrdersDTO {
   int od_idx;
  int  mem_idx;
  int seat_idx;
  int food_idx;
  String mem_name;
  String food_name;
  int food_stock;
    
  public OrdersDTO(){}

  public String getMem_name() {
      return this.mem_name;
  }

  public void setMem_name(String mem_name) {
      this.mem_name = mem_name;
  }

  public String getFood_name() {
      return this.food_name;
  }

  public void setFood_name(String food_name) {
      this.food_name = food_name;
  }

  public int getFood_stock() {
      return this.food_stock;
  }

  public void setFood_stock(int food_stock) {
      this.food_stock = food_stock;
  }
  


  public int getOd_idx() {
    return od_idx;
  }
  public void setOd_idx(int od_idx) {
    this.od_idx = od_idx;
  }
  public int getMem_idx() {
    return mem_idx;
  }
  public void setMem_idx(int mem_idx) {
    this.mem_idx = mem_idx;
  }
  public int getSeat_idx() {
    return seat_idx;
  }
  public void setSeat_idx(int seat_idx) {
    this.seat_idx = seat_idx;
  }
  public int getFood_idx() {
    return food_idx;
  }
  public void setFood_idx(int food_idx) {
    this.food_idx = food_idx;
  }
    
}
