//in progress creating template for the project (movie dataset reading, masking, KNN for K=1)
import java.io.*;

public class Tests {

    // Use we use 'static' for all methods to keep things simple, so we can call those methods main

    static void Assert (boolean res) // We use this to test our results - don't delete or modify!
    {
        if(!res){
            System.out.print("Something went wrong.");
            System.exit(0);
        }
    }

    // Copy your vector operations here:

    static double [] mult(double a, double [] V) { // multiplying scalar and vector
        // add your code
        double [] Res = new double [V.length];

        for (int i = 0; i<V.length; i++) {
            Res[i] = a*V[i];
        }

        return Res;
    }

    static double [][] mult(double a, double [][]b){
        double [][] ans = new double[b.length][b[0].length];
        for (int i = 0; i<b.length; i++) {
            ans[i] = mult(a, b[i]);           // treating every row as a vector
        }
        return ans;
    }

    static double [] add(double a, double [] V) {
        // add your code
        double [] Res = new double [V.length];
        for (int i = 0; i < V.length; i++) {
            Res[i] = V[i] + a;
        }
        return Res;
    }
    static double [] sub(double a, double [] V) {
        // add your code
        double [] Res = new double [V.length];
        for (int i = 0; i < V.length; i++) {
            Res[i] = V[i] - a;
        }
        return Res;
    }

    static double [] add(double [] U, double [] V) {
        // add your code
        double [] Res = new double [V.length];
        for (int i = 0; i < V.length; i++) {
            Res[i] = V[i] + U[i];
        }
        return Res;
    }
    static double [] sub(double [] U, double [] V) {
        // add your code
        double [] Res = new double [V.length];
        for (int i = 0; i < V.length; i++) {
            Res[i] = U[i] - V[i];
        }
        return Res;
    }
    static double dot(double [] U, double [] V) {
        // add your code
        double Res = 0;

        for (int i = 0; i < V.length; i++) {
            Res += V[i] * U[i];
        }
        return Res;
    }

    // Finish implementations of matrix operations:

    static double [] dot(double [][] U, double [] V) {

        Assert(U[0].length == V.length);
        double[] ans = new double[U.length];
        // add some code here
        for (int i = 0; i < U.length; i++){
            ans[i] = dot(U[i], V);
        }
        return ans;
    }


    static double [][] add(double [][] a, double [][] b) {
        Assert(a.length == b.length);
        for (int i = 0;i < a.length; i++)
            Assert(a[i].length == b[i].length);
        double[][] ans = new double[a.length][a[0].length];
        for (int i = 0; i<a.length; i++) {
            ans[i] = add(a[i], b[i]);
        }

        return ans;
    }

    // ...

    static int NumberOfFeatures = 11;
    static double[] toFeatureVector(double id, String genre, double runtime, double year, double imdb, double rt, double budget, double boxOffice) {


        double[] feature = new double[NumberOfFeatures];

        switch (genre) { // We also use represent each movie genre as an integer number:

            // had to change this as the original template had the array index and the 1s swapped
            case "Action":  feature[0] = 1; break;
            case "Fantasy":   feature[1] = 1; break;
            case "Romance": feature[2] = 1; break;
            case "Sci-Fi": feature[3] = 1; break;
            case "Adventure": feature[4] = 1; break;
            case "Horror": feature[5] = 1; break;
            case "Comedy": feature[6] = 1; break;
            case "Thriller": feature[7] = 1; break;
            default: Assert(false);

        }

        // this is new went from 61 -> 66% (if movie has good reviews better change Alex will like it)
        feature [8] = ( rt >= 70 ? 1 : 0) ; // if rotten tomatoes >= 70% then == 1
        feature [9] = ( imdb >= 7 ? 1 : 0) ; // if imdb >= 7 then == 1

        // tried including box office didnt seem to make a difference

        // That is all. We don't use any other attributes for prediction.
        return feature;
    }

    // We are using the dot product to determine similarity:
    static double similarity(double[] u, double[] v) {
        return dot(u, v);
    }

    // We have implemented KNN classifier for the K=1 case only. You are welcome to modify it to support any K
    static int knnClassify(double[][] trainingData, int[] trainingLabels, double[] testFeature, int k) {

        int bestMatch = -1;
        double bestSimilarity = - Double.MAX_VALUE;  // We start with the worst similarity that we can get in Java.

        for (int i = 0; i < trainingData.length; i++) {
            double currentSimilarity = similarity(testFeature, trainingData[i]);
            if (currentSimilarity > bestSimilarity) {
                bestSimilarity = currentSimilarity;
                bestMatch = i;
            }
        }
        return trainingLabels[bestMatch];
    }


    static void loadData(String filePath, double[][] dataFeatures, int[] dataLabels) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int idx = 0;
            br.readLine(); // skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Assuming csv format: MovieID,Title,Genre,Runtime,Year,Lead Actor,Director,IMDB,RT(%),Budget,Box Office Revenue (in million $),Like it
                // look at original template the formatting and parsing was all kinds of messed up
                // changed most of the doubles to ints because they didnt need to be doubles i dont think
                double id = Double.parseDouble(values[0]);
                String title = values[1];
                String genre = values[2];
                int year = Integer.parseInt(values[3]);
                String director = values[4];
                String leadActor = values[5];
                int rt = Integer.parseInt(values[6]);
                double imdb = Double.parseDouble(values[7]);
                int boxOffice = Integer.parseInt(values[8]);
                int budget = Integer.parseInt(values[9]);
                int runtime = Integer.parseInt(values[10]);
                boolean likeit = Boolean.parseBoolean(values[11]);

                dataFeatures[idx] = toFeatureVector(id, genre, runtime, year, imdb, rt, budget, boxOffice);
                dataLabels[idx] = Integer.parseInt(values[11]); // Assuming the label is the last column and is numeric
                idx++;
            }
        }
    }

    public static void main(String[] args) {

        double[][] trainingData = new double[100][];
        int[] trainingLabels = new int[100];
        double[][] testingData = new double[100][];
        int[] testingLabels = new int[100];
        try {
            // You may need to change the path:
            loadData("/home/brian/IdeaProjects/cs259 final assignemnt stuff/src/training-set.csv", trainingData, trainingLabels);
            loadData("/home/brian/IdeaProjects/cs259 final assignemnt stuff/src/testing-set.csv", testingData, testingLabels);
        }
        catch (IOException e) {
            System.out.println("Error reading data files: " + e.getMessage());
            return;
        }

        // Compute accuracy on the testing set
        int correctPredictions = 0;

        int x;
        for (int i = 0; i < trainingData.length; i++) {
            x = knnClassify(trainingData, trainingLabels, testingData[i], 1);
            if (x == testingLabels[i]) {
                    correctPredictions++;
            }
        }

        double accuracy = (double) correctPredictions / testingData.length * 100;
        System.out.printf("A: %.2f%%\n", accuracy);

    }

}




