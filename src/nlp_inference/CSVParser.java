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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Caterina
 */
public class CSVParser {
    
    String file;
    BufferedReader f;
    public CSVParser(String file) throws FileNotFoundException{
        this.file=file;
        f=new BufferedReader(new FileReader(file));
    }
    
   
    
    public List<Tree> nextPair() throws IOException, Exception{
         String line="";
         List<Tree> pair=new ArrayList<>();
         if((line=f.readLine()) != null){
                String[] tokens=line.split("\t");
                pair.add(Tree.fromPennTree(tokens[3]));
                pair.add(Tree.fromPennTree(tokens[4]));
         }
         return pair;
    }
}
