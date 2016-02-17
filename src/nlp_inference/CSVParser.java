/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp_inference;
import it.uniroma2.util.tree.Tree;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 *
 * @author Caterina
 */
class CSVElement{
    private Tree t1;
    private Tree t2;
    private String label;

    public CSVElement(Tree t1, Tree t2, String label) {
        this.t1 = t1;
        this.t2 = t2;
        this.label = label;
    }

    public Tree getT1() {
        return t1;
    }

    public void setT1(Tree t1) {
        this.t1 = t1;
    }

    public Tree getT2() {
        return t2;
    }

    public void setT2(Tree t2) {
        this.t2 = t2;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
public class CSVParser {
    
    String file;
    BufferedReader f;
    public CSVParser(String file) throws FileNotFoundException, IOException{
        this.file=file;
        f=new BufferedReader(new FileReader(file));
        f.readLine();
    }
    
   
    
    public CSVElement nextPair() throws IOException, Exception{
         String line="";
         CSVElement pair=null;
         if((line=f.readLine()) != null){
                String[] tokens=line.split("\t");
                pair=new CSVElement(Tree.fromPennTree(tokens[3]), Tree.fromPennTree(tokens[4]), tokens[0]);
         
        }
        return pair;
    }
}
