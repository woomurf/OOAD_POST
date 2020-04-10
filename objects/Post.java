package objects;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class Post {
    int cash;
    Connection conn;
    Statement state;

    public Post(){
        try {
        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/post?serverTimezone=Asia/Seoul","root","2019db");
            state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT cash from sale_log order by id DESC");
            if( rs.next()) {
            	cash = rs.getInt("cash");
            }
        }
        catch(Exception e) {
        	System.out.println("MySQL Connection error");
        	e.printStackTrace();
        	cash = 0;
        }
        
        System.out.println("Post System On");
    }

    // 물건을 구매하는 함수, item 목록과 payment 목록을 입력받는다. 
    public void buy(ArrayList<Item> items, ArrayList<Payment> payments, int totalPrice){
    	
    	for(Item item : items) {
    		for (int i = 0; i < item.getNumber(); i++) {
    			item.minusRemain();
    		}
    		
    		try {
    			state.executeUpdate(String.format("UPDATE item SET remaining = %d WHERE id = %d", item.getRemain(), item.getId()));
    		}
    		catch(Exception e) {
    			System.out.println("update item remain fail");
    			e.printStackTrace();
    		}
    		
    	}
    	
    	Date date = new Date();
    	Log log = new Log(date, totalPrice, payments, items);
    	
    	this.queryLog(log);
    }

    // item list를 받아오는 함수 
    public ArrayList<Item> getItemList(){
    	ArrayList<Item> itemList = new ArrayList<Item>();
    	
    	try {
    		ResultSet rs = state.executeQuery("SELECT * FROM item");
    		while(rs.next()) {
    			int id = rs.getInt("id");
    			String name = rs.getString("name");
    			int price = rs.getInt("price");
    			String size = rs.getString("size");
    			int remain = rs.getInt("remaining");
    			
    			Item temp = new Item(id, name, size, price, remain, 0);
    			
    			itemList.add(temp);
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return itemList;
    }
    
    // item id가 유효한지 확인하는 함수
    public boolean checkItem(int itemid) {
    	
    	try {
    		ResultSet rs = state.executeQuery(String.format("SELECT * FROM item WHERE id = %d", itemid));
    		
    		if( rs.next()) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    
    	return false;
    }

    // item의 재고를 확인하는 함수
    public int getRemain(int itemid) {
    	
    	try {
    		ResultSet rs = state.executeQuery(String.format("SELECT * FROM item WHERE id = %d", itemid));
    		
    		if (rs.next()) {
    			int remain = rs.getInt("remaining");
    			return remain;
    		}
    		else {
    			return 0;
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return 0;
    }

    // 선택된 item List를 리턴하는 함수
    public ArrayList<Item> getSelectedItem(HashMap<Integer, Integer> items){
    	ArrayList<Item> selected = new ArrayList<Item>();
    	
    	items.forEach((id,number) -> {
    		try {
    			ResultSet rs = state.executeQuery(String.format("SELECT * FROM item WHERE id = %d", id));
    			if (rs.next()) {
    				Item item = new Item(id, rs.getString("name"), rs.getString("size"), rs.getInt("price"), rs.getInt("remaining"), number);
    				selected.add(item);
    			}
    			else {
    				System.out.println("Error getSelectedItem rs.next()");
    			}
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	});
    	
    	return selected;
    }

    // 선택된 item 들의 가격을 계산하는 함수 
    public int getTotalPrice(ArrayList<Item> itemList) {
    	int totalPrice = 0;
    	
    	for(Item item: itemList) {
    		int price = item.getPrice();
    		int number = item.getNumber();
    		totalPrice += price*number;
    	}
    	
    	return totalPrice;
    }
    
    // 쿠폰 번호  기간이 유효한지 검사하고, 유효하다면 제품의 가격을 유효하지 않다면 -1을 리턴 
    public int checkCoupon(int number) {
    	
    	try {
    		ResultSet rs = state.executeQuery(String.format("SELECT * FROM coupon WHERE coupon_number = %d", number));
    		if (rs.next()) {
    			int id = rs.getInt("id");
    			int price = rs.getInt("price");
    			java.sql.Date date = rs.getDate("expiration_date");
    			int used = rs.getInt("used");
    			Date _date = new Date(date.getTime());
    			Date now = new Date();
    			
    			if (now.compareTo(_date) == -1 && used == 0) {
    				state.executeUpdate(String.format("UPDATE coupon SET used = 1 WHERE id = %d", id));
    				return price;
    			}
    			else {
    				return -1;
    			}
    			
    		}
    		else {
    			return -1;
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	return -1;
    }
    
    // log 확인하는 함수 
    public void getLogs(LocalDateTime begin, LocalDateTime end) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
    	
    	String begin_ = begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	String end_ = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	
    	begin_ += " 00:00:00";
    	end_ += " 23:59:59";
    	
    	String sql = String.format("SELECT id, totalPrice, date FROM sale_log WHERE date BETWEEN '%s' AND '%s'", begin_, end_);
    	try {
    		ResultSet rs = state.executeQuery(sql);
    		while(rs.next()) {
    			int id = rs.getInt("id");
    			int totalPrice = rs.getInt("totalPrice");
    			Date date = rs.getDate("date");
    			Date time = rs.getTime("date");
    			String date_ = sdf.format(date);
    			String time_ = sdf2.format(time);
    			
    			System.out.println(String.format("ID : %d | totalPrice : %d | date : %s ", id, totalPrice, date_+time_));
    			
        	}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	
    }
    
    // Log를 입력하는 함수 
    private void queryLog(Log log){
    	try{
    		ArrayList<Item> items = log.getItems();
    		ArrayList<Payment> payments = log.getPayments();
    		
    		int totalPrice = log.getTotalPrice();
    		
    		Date date = log.getDate();
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String date_ = format.format(date);
    		
    		
    		// sale log 입력 
    		String sql = String.format("INSERT INTO sale_log(totalPrice, date) values(%d, '%s')", totalPrice, date_);
    		
    		state.executeUpdate(sql);
    		ResultSet rs = state.executeQuery(String.format("SELECT id FROM sale_log WHERE totalPrice=%d and date='%s'", totalPrice, date_));
    		
    		int sale_id = 0;
    		if (rs.next()) {
    			sale_id = rs.getInt("id");
    		}
    		else {
    			System.out.println("sale id 읽어오기 실패 ");
    			return; 
    		}
    		
    		// sale_item log 입력 
    		this.querySaleItem(items, sale_id);
    		
    		// payment, sale_payment log 입력 
    		this.queryPayment(payments, sale_id);
    		
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    }

    // sale_item table에 query를 입력하는 함수
    private void querySaleItem(ArrayList<Item> items, int sale_id){
    	
    	try {
    		for(Item item : items) {
    			int item_id = item.getId();
    			String sql = String.format("INSERT INTO sale_item(sale_id, item_id, number) values(%d, %d, %d)", sale_id, item_id, item.getNumber());
    			
    			state.executeUpdate(sql);
    		}
    	}
    	catch(Exception e) {
    		System.out.println("sale_item insert fail");
    		e.printStackTrace();
    	}
    	
    }

    // payment table에 query를 입력하는 함수
    // sale_payment table에도 query를 입력한다.
    private void queryPayment(ArrayList<Payment> payments, int sale_id){
    	try {
    		ResultSet rs;
    		
    		for(Payment payment : payments) {
        		
    			String p_method = payment.getPmethod();
    			int price = payment.getPrice();
    			
    			Date p_date = new Date();
    			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		String date_ = format.format(p_date);
   			
    			state.executeUpdate(String.format("INSERT INTO payment(sale_id, method, price, datetime) values(%d, '%s', %d, '%s')", sale_id, p_method, price, date_));
    		}
    	}
    	catch(Exception e) {
    		System.out.println("Payment insert fail");
    		e.printStackTrace();
    	}
    	
    }
    
    
}