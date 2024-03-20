/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author ASUS
 */
public class OrderDetail {
    private int order_id,racket_id;
    private int quantity;
    private float price;

    public OrderDetail() {
    }

    public OrderDetail(int order_id, int racket_id, int quantity, float price) {
        this.order_id = order_id;
        this.racket_id = racket_id;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getRacket_id() {
        return racket_id;
    }

    public void setRacket_id(int racket_id) {
        this.racket_id = racket_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
    
    
}
