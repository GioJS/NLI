/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp_inference;

/**
 *
 * @author Caterina
 */
public class Statistics {
    
    private int count;
    private int couples;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCouples() {
        return couples;
    }

    public void setCouples(int couples) {
        this.couples = couples;
    }
    
    public Statistics() {
        this.count=0;
        this.couples=0;
    }
    
    public void incCount(){
        count++;
    }
    
    public void incCouples(){
        couples++;
    }
    
    public double frequency(){
        return count/(double)couples;
    }
    
}
