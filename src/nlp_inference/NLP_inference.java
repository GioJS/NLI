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
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.kernel.KernelCombination;
import it.uniroma2.sag.kelp.kernel.tree.SmoothedPartialTreeKernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.classification.libsvm.BinaryNuSvmClassification;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.kernel.pairs.UncrossedPairwiseProductKernel;
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
        BinaryNuSvmClassification svmSolver = new BinaryNuSvmClassification();
        //Kernel kernel=new SmoothedPartialTreeKernel();
        //Kernel kernel = new PartialTreeKernel(0.4f, 0.4f, 1, "parse");
        Kernel kernel = new KernelMultiplication();
        svmSolver.setKernel(kernel);
        svmSolver.setCn(1.0f);
        svmSolver.setCp(1.0f);

        OneVsOneLearning classificator = new OneVsOneLearning();
        classificator.setBaseAlgorithm(svmSolver);
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
