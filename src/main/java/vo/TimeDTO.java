package vo;

public class TimeDTO {
    
    int hour;
    int price;

    public TimeDTO(){}

    public TimeDTO(int hour, int price){
        this.hour = hour;
        this.price = price;
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
