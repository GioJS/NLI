/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp_inference;
import it.uniroma2.dtk.dt.GenericDT;
import it.uniroma2.util.tree.Tree;
import it.uniroma2.tk.TreeKernel;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author giordanocristini
 */
public class NLP_inference {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        //leggere dal csv separato da tab (\t)
        //per ogni riga estrarre i due alberi parentesizzati
        //generare gli alberi
        //generare i dt
        //dot product tra dt
        //abbiamo similitudine tra le due frasi
        String filename="C:\\Users\\Caterina\\Documents\\NetBeansProjects\\NLI\\snli_1.0_dev.txt";
        CSVParser parser=new CSVParser(filename);
        
        parser.parse();
    }
    
}
