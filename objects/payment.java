package objects;
class Payment{
    String p_method;
    int price;

    public Payment(String p_method, int price){
        this.p_method = p_method;
        this.price = price;
    }

    public int getPrice(){
        return price;
    }

    public String getPmethod(){
        return p_method;
    }
}