/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp_inference;
import it.uniroma2.dtk.dt.GenericDT;
import it.uniroma2.util.tree.Tree;
import java.io.IOException;
import java.util.List;
import it.uniroma2.dtk.op.convolution.CircularConvolution;
import it.uniroma2.dtk.op.product.GammaProduct;
import it.uniroma2.util.math.ArrayMath;
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.data.representation.vector.DenseVector;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.standard.KernelMultiplication;
import it.uniroma2.sag.kelp.kernel.tree.PartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.standard.LinearKernelCombination;
import it.uniroma2.sag.kelp.kernel.tree.SubTreeKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryCSvmClassification;
import it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification.MultiLabelClassificationLearning;
import it.uniroma2.sag.kelp.learningalgorithm.classification.perceptron.KernelizedPerceptron;
import it.uniroma2.sag.kelp.learningalgorithm.classification.perceptron.LinearPerceptron;
import it.uniroma2.sag.kelp.predictionfunction.classifier.ClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.MultiLabelClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.MultiLabelClassifier;
import it.uniroma2.sag.kelp.learningalgorithm.classification.perceptron.Perceptron;
import it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification.OneVsAllLearning;
import it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification.OneVsOneLearning;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsAllClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsAllClassifier;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsOneClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsOneClassifier;
import it.uniroma2.tk.TreeKernel;
import it.uniroma2.sag.kelp.data.example.ExamplePair;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
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
        //classificare se le due frasi sono : neutrali ~ 0, implicanti ~ 1, in contraddizione ~ -1
        //rispetto alle label del file calcolare accuracy (VP+NP)/(P+N) dove P ed N sono positivi e negativi in tot
        //file snli dove abbiamo alberi e annotazione
        String filename="snli_1.0_dev.txt";
        //istanzio un simple dataset
        SimpleDataset dataset = new SimpleDataset();
        
        //dataset.populate(filename);
//        SimpleDataset[] train_test=dataset.split(0.6f);
//        SimpleDataset training_set=train_test[0];
//        SimpleDataset test_set=train_test[1];
//        System.out.println(training_set.getNumberOfExamples());
//        System.out.println(test_set.getNumberOfExamples());
        //parser per estrarre alberi e label
        CSVParser parser=new CSVParser(filename);
        CSVElement pair=null;
        
//        double threshold1=0.7;
//        double threshold2=-0.5;
//        Statistics impl=new Statistics();
//        Statistics contr=new Statistics();
//        Statistics neutrals=new Statistics();
//distributed tree
        GenericDT dt=new GenericDT(0, 2048,true,true,1,   CircularConvolution.class);
        while((pair=parser.nextPair())!=null){
            //crea un example da aggiungere al dataset
              if(pair.getLabel().equals("-"))
                  continue;
              SimpleExample ex=new SimpleExample();
              ex.addLabel(new StringLabel(pair.getLabel()));
              double[] dt1=dt.dt(pair.getT1());
              double[] dt2=dt.dt(pair.getT2());
              ex.addRepresentation("T1", new DenseVector(dt1));
              ex.addRepresentation("T2", new DenseVector(dt2));
              ex.addRepresentation("Cos", new DenseVector(new double[]{ArrayMath.cosine(dt1, dt2)}));
              dataset.addExample(ex);
//            double[] dt1=dt.dt(pair.getT1());
//            double[] dt2=dt.dt(pair.getT2());
//            
//            double cosine=ArrayMath.cosine(dt1, dt2);
//            
//            if(pair.getLabel().equals("-"))
//                continue;
//            System.out.println("NLI: "+pair.getLabel());
//            System.out.print("NLP_I: ");
//            if(cosine>=threshold1){
//                System.out.println("imply");
//                impl.incCouples();
//                if(pair.getLabel().equals("entailment"))
//                    impl.incCount();
//            }else if(cosine>=threshold2){
//                System.out.println("neutral");
//                neutrals.incCouples();
//                if(pair.getLabel().equals("neutral"))
//                    neutrals.incCount();
//            }else{
//                System.out.println("contradiction");
//                contr.incCouples();
//                if(pair.getLabel().equals("contradiction"))
//                    contr.incCount();
//            }
            // TreeKernel tk=new TreeKernel();
           // System.out.println(TreeKernel.value(t2, t1));
        }
//        System.out.println("Accuracy entailments: "+impl.frequency());
//        System.out.println("Accuracy contradictions: "+contr.frequency());
//        System.out.println("Accuracy neutrals: "+neutrals.frequency());
          //System.out.println(dataset.getNextExample());
          //System.out.println(dataset.getNumberOfExamples());
        //spezzo il dataset in training e test set (60-40)
        SimpleDataset[] train_test=dataset.split(0.6f);
        SimpleDataset training_set=train_test[0];
        SimpleDataset test_set=train_test[1];
        System.out.println(training_set.getNumberOfExamples());
        System.out.println(test_set.getNumberOfExamples());
        //inizializzo una svm con kernel lineare
        //BinaryCSvmClassification svmSolver = new BinaryCSvmClassification();
        Kernel kernel=new KernelMultiplication();
        KernelizedPerceptron p=new KernelizedPerceptron();
        p.setKernel(kernel);
       // p.setLabels(dataset.getClassificationLabels());
        
       // svmSolver.setKernel(kernel);
        //svmSolver.setCn(1.0f);
        //svmSolver.setCp(1.0f);
        //istanzio un multiclass classificator che sfrutta il classificatore binario
        //MultiLabelClassificationLearning classificator=new MultiLabelClassificationLearning();
        OneVsOneLearning classificator = new OneVsOneLearning();
        classificator.setBaseAlgorithm(p);
        classificator.setLabels(dataset.getClassificationLabels());
        classificator.learn(training_set);
        //calcolo l'accuracy sul test set
        OneVsOneClassifier f = classificator.getPredictionFunction();
        
        int correct = 0;
        int howmany = test_set.getNumberOfExamples();
        for(Example e:test_set.getExamples()){
            OneVsOneClassificationOutput output=f.predict(e);
            System.out.println("Oracolo: "+e.getLabels()[0]);
            
            if(output.getPredictedClasses()==null)
                continue;
            System.out.println("Predetto: "+output.getPredictedClasses().get(0)+" score: "+output.getScore(output.getPredictedClasses().get(0)));
            
            if(!output.getPredictedClasses().isEmpty())
                if(e.isExampleOf(output.getPredictedClasses().get(0)))
                    correct++;
            //System.out.println();
        }
        float accuracy=correct/(float)howmany;
        System.out.println("accuracy: "+accuracy);
    }
    
}
