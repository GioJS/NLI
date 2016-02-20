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
import it.uniroma2.sag.kelp.data.dataset.selector.RandomExampleSelector;
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
import it.uniroma2.sag.kelp.utils.evaluation.MulticlassClassificationEvaluator;
import it.uniroma2.tk.TreeKernel;
import it.uniroma2.sag.kelp.data.example.ExamplePair;
import it.uniroma2.sag.kelp.kernel.KernelCombination;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryNuSvmClassification;
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
        String filename_train="snli_1.0_train.bigrams.TH_D.fix.kelp";
        String filename_test="snli_1.0_test.bigrams.TH_D.fix.kelp";
        //istanzio un simple dataset
        SimpleDataset training_set = new SimpleDataset();
        
        int tot_examples=training_set.getNumberOfExamples();
        int examples=(int) (tot_examples*0.01);
        training_set.populate(filename_train);
        RandomExampleSelector rand=new RandomExampleSelector(examples);
        
        //parser per estrarre alberi e label
        CSVParser parser=new CSVParser(filename_train);
        CSVElement pair=null;

//distributed tree
        int train_limit=10000;
        GenericDT dt=new GenericDT(0, 2048,true,true,1,   CircularConvolution.class);
        while((pair=parser.nextPair())!=null){
            //crea un example da aggiungere al dataset
              if(train_limit == 0)
                  break;
              if(pair.getLabel().equals("-"))
                  continue;
              SimpleExample ex=new SimpleExample();
              ex.addLabel(new StringLabel(pair.getLabel()));
              double[] dt1=dt.dt(pair.getT1());
              double[] dt2=dt.dt(pair.getT2());
              ex.addRepresentation("T1", new DenseVector(dt1));
              ex.addRepresentation("T2", new DenseVector(dt2));
             // ex.addRepresentation("Cos", new DenseVector(new double[]{ArrayMath.cosine(dt1, dt2)}));
              training_set.addExample(ex);

           train_limit--;
           System.out.println(10000-train_limit);
        }

        SimpleDataset test_set = new SimpleDataset();
        
        int test_limit=5000;
        parser=new CSVParser(filename_test);
        pair=null;
        
        while((pair=parser.nextPair())!=null){
            //crea un example da aggiungere al dataset
              if(test_limit==0)
                  break;
              if(pair.getLabel().equals("-"))
                  continue;
              SimpleExample ex=new SimpleExample();
              ex.addLabel(new StringLabel(pair.getLabel()));
              double[] dt1=dt.dt(pair.getT1());
              double[] dt2=dt.dt(pair.getT2());
              ex.addRepresentation("T1", new DenseVector(dt1));
              ex.addRepresentation("T2", new DenseVector(dt2));
             // ex.addRepresentation("Cos", new DenseVector(new double[]{ArrayMath.cosine(dt1, dt2)}));
              test_set.addExample(ex);

           test_limit--;
           System.out.println(5000-test_limit);
        }
        //inizializzo una svm con kernel lineare
        
        
        BinaryNuSvmClassification svmSolver = new BinaryNuSvmClassification();
        Kernel kernel=new KernelMultiplication();

        svmSolver.setKernel(kernel);
        svmSolver.setCn(1.0f);
        svmSolver.setCp(1.0f);

        OneVsOneLearning classificator = new OneVsOneLearning();
        classificator.setBaseAlgorithm(svmSolver);
        classificator.setLabels(training_set.getClassificationLabels());
        classificator.learn(training_set);
        //calcolo l'accuracy sul test set
        OneVsOneClassifier f = classificator.getPredictionFunction();
        MulticlassClassificationEvaluator eval = new MulticlassClassificationEvaluator(training_set.getClassificationLabels());

        for(Example e:test_set.getExamples()){
            OneVsOneClassificationOutput output=f.predict(e);
            System.out.println("Oracolo: "+e.getLabels()[0]);
            
            
            System.out.println("Predetto: "+output.getPredictedClasses().get(0)+" score: "+output.getScore(output.getPredictedClasses().get(0)));
            
            eval.addCount(e, output);

        }
        System.out.println("Mean F1: "
					+ eval.getPerformanceMeasure("MeanF1"));
			
			System.out.println("F1: "
					+ eval.getPerformanceMeasure("OverallF1"));
                  System.out.println("Accuracy: "+eval.getAccuracy());

    }
    
}
