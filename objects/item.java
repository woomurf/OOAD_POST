package objects;

class Item{
	int id;
    String name;
    int price;
    String size;
    int remain;
    int number;

    public Item(int id, String name, String size, int price, int remain, int number){
    	this.id = id;
        this.name = name;
        this.size = size;
        this.price = price;
        this.remain = remain;
        this.number = number;
    }

    public int getPrice(){
        return this.price;
    }
    
    public int getId() {
    	return this.id;
    }
    
    public void minusRemain() {
    	this.remain -= 1;
    }
    
    public int getRemain() {
    	return this.remain;
    }
    
    public int getNumber() {
    	return this.number;
    }
    
    public void print() {
    	System.out.println(String.format("%d. 제품명 : %s | 크기 : %s | 가격 : %d | 재고 : %d", id, name, size, price, remain));
    }
    
    public void printNumber() {
    	System.out.println(String.format("%d. 제품명 : %s | 크기 : %s | 가격 : %d | 수량 : %d", id, name, size, price, number));
    }
    
    
}