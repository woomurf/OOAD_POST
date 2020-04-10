package objects;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

class Log {
    Date date;
    int total_price;
    ArrayList<Payment> payments;
    ArrayList<Item> items;

    public Log(Date date, int total_price, ArrayList<Payment> payments, ArrayList<Item> items) {
        this.date = date;
        this.total_price = total_price;
        this.payments = payments;
        this.items = items;
    }
    
    public Date getDate() {
    	return date;
    }
    
    public int getTotalPrice() {
    	return total_price;
    }
    
    public ArrayList<Payment> getPayments(){
    	return payments;
    }
    
    public ArrayList<Item> getItems(){
    	return items;
    }

}