/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp_inference;
import java.io.IOException;
import java.util.List;
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.learningalgorithm.classification.dcd.DCDLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.classification.multiclassification.OneVsOneLearning;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsOneClassificationOutput;
import it.uniroma2.sag.kelp.predictionfunction.classifier.multiclass.OneVsOneClassifier;
import it.uniroma2.sag.kelp.utils.evaluation.MulticlassClassificationEvaluator;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.StringLabel;

/**
 *
 * @author giordanocristini
 */

public class NLP_inference {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        
        String filename_train="unper_snli_1.0_train.bigrams.TH_D.fix.kelp";
        String filename_test="snli_1.0_test.bigrams.TH_D.fix.kelp";
        
        //istanzio un simple dataset
        SimpleDataset training_set = new SimpleDataset();
        training_set.populate(filename_train);
        SimpleDataset test_set = new SimpleDataset();
        test_set.populate(filename_test);
        //test_set.populate("snli_1.0_dev.bigrams.TH_D.fix.kelp");
       // BinaryCSvmClassification svmSolver = new BinaryCSvmClassification();
        //Kernel kernel=new SmoothedPartialTreeKernel();
        //Kernel kernel = new SmoothedPartialTreeKernel(0.4f,0.4f,1.0f,0.6f,new LexicalStructureElementSimilarity(),"parse");
       // Kernel kernel = new PartialTreeKernel(0.4f,0.4f,1,"parse");
        DCDLearningAlgorithm dcd = new DCDLearningAlgorithm(1.0f, 1.0f, 10, "bigram");
      //  Kernel kernel = new LinearKernel();
        
//        svmSolver.setKernel(kernel);
//        svmSolver.setCn(1.0f);
//        svmSolver.setCp(1.0f);
        
        OneVsOneLearning classificator = new OneVsOneLearning();
        classificator.setBaseAlgorithm(dcd);
        List<Label> labels = training_set.getClassificationLabels();
        labels.remove(2);
        System.out.println(labels);
        
        classificator.setLabels(labels);
        classificator.learn(training_set);
        //calcolo l'accuracy sul test set
        OneVsOneClassifier f = classificator.getPredictionFunction();
        MulticlassClassificationEvaluator eval = new MulticlassClassificationEvaluator(labels);

        for(Example e:test_set.getExamples()){
            StringLabel l=(StringLabel) e.getLabels()[0];
            if(l.getClassName().equals("-"))
                continue;
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
