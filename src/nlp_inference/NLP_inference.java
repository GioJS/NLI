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
import java.util.List;
import it.uniroma2.dtk.op.convolution.CircularConvolution;
import it.uniroma2.dtk.op.convolution.ShuffledCircularConvolution;
import it.uniroma2.dtk.op.product.GammaProduct;
import it.uniroma2.util.math.ArrayMath;
/**
 *
 * @author giordanocristini
 */
public class NLP_inference {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        // TODO code application logic here
        //leggere dal csv separato da tab (\t)
        //per ogni riga estrarre i due alberi parentesizzati
        //generare gli alberi
        //generare i dt
        //dot product tra dt
        //abbiamo similitudine tra le due frasi
        //classificare se le due frasi sono : neutrali, implicanti, in contraddizione
        //rispetto alle label del file calcolare accuracy (VP+NP)/(P+N) dove P ed N sono positivi e negativi in tot
        String filename="snli_1.0_dev.txt";
        CSVParser parser=new CSVParser(filename);
        List<Tree> pair=null;
        GenericDT dt=new GenericDT(0, 4096,1, new CircularConvolution());
        while(!(pair=parser.nextPair()).isEmpty()){
            Tree t1=pair.get(0);
            Tree t2=pair.get(1);
            double[] dt1=dt.dt(t1);
            double[] dt2=dt.dt(t2);
            System.out.println(ArrayMath.cosine(dt1, dt2));
            // TreeKernel tk=new TreeKernel();
           // System.out.println(TreeKernel.value(t2, t1));
        }
    }
    
}
