package objects;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.HashMap;
import java.util.InputMismatchException;


public class Main{
	
    public static void main(String[] args){   
    	ConsolePrint cp = new ConsolePrint();
    	Post post = new Post();
    	String select;
    	ArrayList<Item> ItemList = new ArrayList<Item>();
    	ArrayList<Payment> payments = new ArrayList<Payment>();
    	int state = 0;
    	int totalPrice = 0;
    	int totalPrice_ = 0;
    	
    	while(true) {
    		
    		// 시작 화면
    		if (state == 0) {
        		select = cp.initial();
        		state = Integer.parseInt(select);
        		if (state == 5) {
        			state = -1;
        		}
        		else if (state == 4) {
        			state = 6;
        		}
        	}
    		// 메뉴보기
    		else if (state == 1) {
    			select = cp.getMenu(post); 
    			if(select.equals("1")) {
    				state = 4;
    			}
    			else if (select.equals("2")) {
    				state = 0;
    			}
    		}
    		// 선택한 제품 보기
    		else if (state == 2) {
    			int result = cp.getSelectedItem(ItemList, totalPrice);
    			if( result == 1) {
    				state = 3;
    			}
    			else if (result == 2) {
    				ItemList.clear();
    				totalPrice = 0;
    				state = 0;
    			}
    			else if (result == 3) {
    				state = 0;
    			}
    			
    		}
    		// 결제하기
    		else if (state == 3) {
    			int result = cp.paymentView(ItemList, totalPrice);
    			
    			if( result == 4) {
    				state = 0;
    				continue;
    			}
    			else if (result == 1) {
    				Payment payment = cp.cash(totalPrice);
    				if (payment.getPrice() != -1) {
    					payments.add(payment);
        				totalPrice -= payment.getPrice();
    				}
    				
    			}
    			else if (result == 2) {
    				Payment payment = cp.card(totalPrice);
    				if (payment.getPrice() != -1) {
    					payments.add(payment);
        				totalPrice -= payment.getPrice();
    				}
    			}
    			else if (result == 3) {
    				Payment payment = cp.coupon(post);
    				if(payment.getPrice() != 0) {
    					payments.add(payment);
    					
    					if (totalPrice < payment.getPrice()) {
    						totalPrice = 0;
    					}
    					else {
    						totalPrice -= payment.getPrice();
    					}
    				}
    			}
    			
    			if (totalPrice == 0) {
    				state = 5;
    			}
    		}
    		// 제품 선택하기
    		else if (state == 4) {
    			ItemList = cp.selectItem(post, ItemList);
    			totalPrice = post.getTotalPrice(ItemList);
    			totalPrice_ = totalPrice;
    			state = 1;
    		}
    		// 마지막 화면
    		else if (state == 5) {
    			cp.finish(post, ItemList, payments, totalPrice_);
    			ItemList.clear();
    			state = 0;
    		}
    		// 오늘의 기록
    		else if (state == 6) {
    			int result = cp.todayLog(post);
    			state = 0;
    		}
    		// 종료
    		else if (state == -1) {
    			System.out.println("POST System을 종료합니다.");
    			break;
    		}
    		
    	}
    	
    }
	
}

class ConsolePrint {
	Scanner sc;
	
	public ConsolePrint() {
		sc = new Scanner(System.in);
	}
	
	public void close() {
		sc.close();
	}
	
	// 메뉴 목록 화면
	public String getMenu(Post post) {
    	ArrayList<Item> items = post.getItemList();
    	
    	System.out.println("메뉴판");
    	System.out.println("=============================================================");
    	for(Item item : items) {
    		item.print();
    	}
    	System.out.println("=============================================================");
    	System.out.println("1. 제품 선택하기");
    	System.out.println("2. 나가기");
    	
    	String input = sc.next();
    	
    	while(input.equals("1") && input.equals("2")) {
    		System.out.println("잘못 선택하셨습니다. 다시 선택해주세요.");
    		input = sc.next();
    	}
    	
    	return input;
    	
    }
	
	// 시작 화면
	public String initial() {
    	System.out.println("옵션을 선택해주세요.");
    	System.out.println("1. 메뉴보기");
    	System.out.println("2. 선택한 제품 보기");
    	System.out.println("3. 결제하기");
    	System.out.println("4. 오늘의 기록 보기");
    	System.out.println("5. 나가기");
    	
    	String input = sc.next();
    	
    	while(input.equals("1") && input.equals("2") && input.equals("3") && input.equals("4") && input.equals("5")) {
    		System.out.println("잘못 선택하셨습니다. 다시 선택해주세요.");
    		input = sc.next();
    	}

    	return input;
    }
	
	// 메뉴를 고르는 화면
	public ArrayList<Item> selectItem(Post post, ArrayList<Item> Items) {
		
		int itemid = 0;
		int numOfitem = 0;
		
		boolean first = true;
		boolean check = false;
		boolean stop = false;
		boolean checkremain = false;
		
		HashMap<Integer, Integer> ItemList = new HashMap<Integer, Integer>();
	
		for (Item item: Items) {
			ItemList.put(item.getId(), item.getNumber());
		}
		
		while(!stop) {
			check = false;
			checkremain = false;
			first = true;
			while(!stop && !check) {
				if (first) {
					System.out.println("선택하실 제품 번호를 입력해주세요. (모두 선택하셨으면 -1을 입력해주세요.) : ");
					first = false;
				}
				else {
					System.out.println("선택하신 제품의 번호가 유효하지 않습니다. 다시 입력해주세요. : ");
				}
				while(true) {
					try {
						itemid = sc.nextInt();
						break;
					}
					catch( InputMismatchException e) {
						sc = new Scanner(System.in);
						System.out.println("번호를 입력해주세요!");
					}
				}
				
				if(itemid == -1) {
					Items = post.getSelectedItem(ItemList);
					return Items;
					
				}
				check = post.checkItem(itemid);
			}
			
			first = true;
			while(!stop && !checkremain) {
				if (first) {
					System.out.println("제품의 갯수를 입력해주세요 : ");
					first = false;
				}
				else {
					System.out.println("제품의 갯수가 재고보다 많습니다. 다시 입력해주세요. : ");
				}
				
				while(true) {
					try {
						numOfitem = sc.nextInt();
						if (numOfitem > 0) {
							break;
						}
						else {
							System.out.println("1 이상의 숫자를 입력해주세요.");
						}
						
					}
					catch( InputMismatchException e) {
						System.out.println("숫자를 입력해주세요!");
						sc = new Scanner(System.in);
					}
				}
				
				int remain = post.getRemain(itemid);
				if (numOfitem <= remain ) {
					checkremain = true;
				}
				
			}
			if( ItemList.get(itemid) != null) {
				int num = ItemList.get(itemid);
				ItemList.put(itemid, num+numOfitem);
			}
			else {
				ItemList.put(itemid, numOfitem);
			}
			
		}
		
		Items = post.getSelectedItem(ItemList);
		return Items;

	}
	
	// 선택된 메뉴 화면
	public int getSelectedItem(ArrayList<Item> selected, int totalPrice ) {
		int input = 0;
		System.out.println("선택하신 제품들 입니다.");
		System.out.println("=============================================================");
		for(Item item : selected) {
			item.printNumber();
		}
		System.out.println("=============================================================");
		System.out.println(String.format("총 가격 : %d", totalPrice));
		
		System.out.println("1. 결제하기");
		System.out.println("2. 장바구니 비우기");
		System.out.println("3. 나가기");
		
		while(true) {
			try {
				input = sc.nextInt();
				if(input != 1 && input != 2 && input != 3) {
					System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
				}
				else {
					break;
				}
				
			}
			catch( InputMismatchException e) {
				sc = new Scanner(System.in);
				System.out.println("숫자를 입력해주세요!");
				
			}
			
			
		}
		
		
		
		return input;
	}
	
	// 결제화면
	public int paymentView(ArrayList<Item> selected, int totalPrice) {
		int input = 0;
		
		if (totalPrice == 0) {
			System.out.println("결제하실 품목이 없습니다.");
			System.out.println("1. 나가기");
			
			while(true) {
				
				try {
					input = sc.nextInt();
					break;
				}
				catch( InputMismatchException e) {
					sc = new Scanner(System.in);
					System.out.println("숫자를 입력해주세요");
				}
			}
			
			return 4;
		}
		
		
		System.out.println("결제하실 목록 입니다.");
		System.out.println("=============================================================");
		for(Item item : selected) {
			item.printNumber();
		}
		System.out.println("=============================================================");
		System.out.println(String.format("결제해야 할 가격 : %d", totalPrice));
		
		System.out.println("1. 현금");
		System.out.println("2. 카드");
		System.out.println("3. 쿠폰");
		System.out.println("4. 나가기");
		
		
		while(true) {
			try {
				input = sc.nextInt();
				if(input != 1 && input != 2 && input != 3 && input != 4) {
					System.out.println("입력이 잘못되었습니다. 다시 입력해주세요.");
				}
				else {
					break;
				}
			}
			catch ( InputMismatchException e) {
				System.out.println("숫자를 입력해주세요!");
				sc = new Scanner(System.in);
			}
		}
		
		
		
		
		return input;
	}
	
	// 쿠폰
	public Payment coupon(Post post){
		System.out.println("쿠폰 번호를 입력해주세요.");
		System.out.println("만약 해당하는 물품이 없을 경우 현금처리되어 계산됩니다.");
		System.out.println("-1을 입력하시면 되돌아갑니다.");
		
		Payment p = new Payment("coupon", 0);
		boolean stop = false;
		int gift_num = 0;
		while(!stop) {
			
			try {
				gift_num = sc.nextInt();
				stop = true;
			}
			catch( InputMismatchException e) {
				System.out.println("숫자를 입력해 주십시오.");
				sc = new Scanner(System.in);
			}
			
			if (stop && gift_num == -1) {
				return p;
			}
			else if (stop) {
				int price = post.checkCoupon(gift_num);
				if(price == -1) {
					System.out.println("쿠폰 번호 또는 기간이 유효하지 않습니다. 번호를 다시 입력해주십시오.");
					stop = false;
				}
				else {
					p = new Payment("coupon", price);
				}
				
			}
		}
	
		return p;
	}
	
	// 현금결제
	public Payment cash(int totalPrice) {
		int payment = this.payment(totalPrice);
		
		Payment p = new Payment("cash", payment);
		
		return p;
	}

	// 카드결제
	public Payment card(int totalPrice) {
		int payment = this.payment(totalPrice);
		
		Payment p = new Payment("card", payment);
		
		return p;
	}
	
	// 결제
	public int payment(int totalPrice){
		boolean stop = false;
		System.out.println("결제하실 금액을 입력해주세요.");
		System.out.println("-1을 입력하시면 되돌아갑니다.");
		int payment = 0;
		while(!stop) {
			try {
				payment = sc.nextInt();
				System.out.println(String.format("입력한 가격 : %d", payment));
				stop = true;
			}
			catch(Exception e) {
				System.out.println("금액을 숫자로 입력해주십시오.");
				sc = new Scanner(System.in);
			}
			
			if (stop && totalPrice < payment) {
				System.out.println("입력하신 금액이 총 가격보다 많습니다. 다시 입력해주십시오.");
				stop = false;
			}
			else if (stop && payment == -1) {
				return payment;
			}
			else if (stop && payment < 0) {
				System.out.println("입력하신 금액이 올바르지 않습니다. 다시 입력해주십시오.");
				stop = false;
			}
		}
		
		return payment;
	}

	// 마지막 화면
	public void finish(Post post, ArrayList<Item> ItemList, ArrayList<Payment> payments, int totalPrice) {
		System.out.println("결제해주셔서 감사합니다!");
		
		post.buy(ItemList, payments, totalPrice);
		
		
		
	}
 
	// 오늘의 기록
	public int todayLog(Post post) {
		LocalDateTime begin = LocalDateTime.now();
		LocalDateTime end = begin.plusDays(1);
		
		
		Calendar c = Calendar.getInstance();
		
		
		post.getLogs(begin, end);
		
		System.out.println("1. 돌아가기");
		
		int input = sc.nextInt();
		
		while(input != 1) {
			System.out.println("올바르지 않은 입력입니다.");
			input = sc.nextInt();
		}
		
		return input;
	}
}














