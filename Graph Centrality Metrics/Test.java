import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
public class Test {


    //In this program I used different algorithms for integers and other types in order to make it faster.
    //So there is some function that determine the dataset consist of integers or other.
    //According to result it will add vertices differently.

    //TIME FOR KARATE_CLUB_NETWORK.TXT >>>>> average 0.1 seconds
    //TIME FOR FACEBOOK_SOCIAL_NETWORK.TXT >>>>> average 160 seconds

    //Apart from that since there are shortest paths that are more than one my program select different path it may effect the betweenness value

    public static void main(String[] args) throws IOException {

        String pathKarate = "src//karate_club_network.txt";
        boolean type = readAndGetType(pathKarate);
        Graph graphKarate = new Graph(readAndDetermineSize(pathKarate,type));
        readAndAdd(pathKarate, graphKarate,type);


        String pathFacebook = "src//facebook_social_network.txt";
        boolean type2 =readAndGetType(pathFacebook);
        Graph graphFacebook = new Graph(readAndDetermineSize(pathFacebook,type2));
        readAndAdd(pathFacebook, graphFacebook,type2);



        System.out.println("2020510135 Ahmet Başbuğ");

        graphIterator(graphKarate);
        System.out.print("Zachary Karate Club Network -");
        printBetweenness(graphKarate);
        System.out.print("Zachary Karate Club Network -");
        printCloseness(graphKarate);

        graphIterator(graphFacebook);
        System.out.print("Facebook Social Network -");
        printBetweenness(graphFacebook);
        System.out.print("Facebook Social Network -");
        printCloseness(graphFacebook);

    }

    //this is the function for inserting reading and inserting elements to the graph
    public static void readAndAdd(String path, Graph graph2, boolean isInt) throws IOException {

        ArrayList<String> arr = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(" ");
            if(!isInt){//if the numbers are integers
                if(!arr.contains(values[0])){
                    arr.add(values[0]);
                }
                if(!arr.contains(values[1])){
                    arr.add(values[1]);
                }
                int indexS = arr.indexOf(values[0]);//corresponding value for index, it depends on order of inserting
                int indexD = arr.indexOf(values[1]);//corresponding value for index, it depends on order of inserting

                //we will use this values when addEdge, getShortestPath in Graph class.
                graph2.addEdge((values[0]),(values[1]),indexS,indexD);
            }else{//other type
                graph2.addEdge((values[0]),(values[1]),Integer.parseInt(values[0]),Integer.parseInt(values[1]));
            }
        }
        br.close();
    }

    //this is the function that determines the data type of elements in dataset.
    public static boolean readAndGetType(String path) throws IOException {
        Boolean isInt = true;
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(" ");

            if(!isInteger(values[0]) || !isInteger(values[1])){
                isInt = false;
            }

        }
        br.close();
        return isInt;
    }


    //since we keep all elements in arraylist and achieve them according to the indices we need to add default vertex elements.
    //so we need to determine size of it, we will use it in constructor.
    public static int readAndDetermineSize(String path, boolean isInt) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        HashSet<String> hs = new HashSet<>();
        int size = 0;
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(" ");

            if(!isInt){
                hs.add(values[0]);
                hs.add(values[1]);
                size= hs.size();
            }else{
                if(Integer.parseInt(values[0])> size){
                    size = Integer.parseInt(values[0]);
                }
                if(Integer.parseInt(values[1])> size){
                    size = Integer.parseInt(values[1]);
                }
            }
        }
        br.close();
        return size+1;
    }


    //this is the iterator that sends all vertices in this graph to getShortestPath function.
    public static void graphIterator(Graph graph2) throws IOException {
        //Here, if we send values it will affects the execution time because there are two loops here
        //I only sends numbers that are in the adj list
        //apart from that, since this graph is undirected, we do not need to check again vertices from end to beginning
        //In the other words, if we send 1-4, not send 4-1
        //if we send, it will affects time comp. bad and also changes the betweenness value.(2x)
        int i = 0;
        int j = 0;
        for(Vertex eachVertex : graph2.getAdjacencyList()){
            if(eachVertex.isInitialized()){
                for(i = j; i <graph2.getAdjacencyList().size(); i++){
                    Vertex eachVertex2 =  graph2.getAdjacencyList().get(i);
                    if(eachVertex2.isInitialized()){
                        graph2.getShortestPath(eachVertex,eachVertex2);
                    }
                }
            }
            j+=1;

        }
    }


    //this is the function that shows max betweenness value and vertex.
    public static void printBetweenness(Graph graph2) throws IOException {
        double maxBetweenness = 0;
        Vertex maxBetweennessVertex = null;
        for(Vertex eachVertex : graph2.getAdjacencyList()){
            if(eachVertex.isInitialized()){
                //System.out.println("Vertex: " + eachVertex.getValueOfVertex() + " Betweenness Centrality: " + eachVertex.getFinalBetweenness());
                if(eachVertex.getFinalBetweenness()>maxBetweenness){
                    maxBetweenness = eachVertex.getFinalBetweenness();
                    maxBetweennessVertex = eachVertex;
                }
            }
        }
        if(maxBetweennessVertex == null){
            System.out.println("The Highest Node for Betweenness: " + "null" +  " and the value: " + "null");
        }else{
            System.out.println("The Highest Node for Betweenness: " + maxBetweennessVertex.getValueOfVertex() +  " and the value: " + maxBetweenness);
        }

    }

    //this is the function that shows max closeness value and vertex.
    public static void printCloseness(Graph graph2) throws IOException {
        double maxCloseness = 0;
        Vertex maxClosenessVertex = null;
        for(Vertex eachVertex : graph2.getAdjacencyList()){
            if(eachVertex.isInitialized()){
                //System.out.println("Vertex: " + eachVertex.getValueOfVertex() + " Closeness Centrality: " + eachVertex.getFinalCloseness());
                if(eachVertex.getFinalCloseness()>maxCloseness){
                    maxCloseness = eachVertex.getFinalCloseness();
                    maxClosenessVertex = eachVertex;
                }
            }
        }

        if(maxClosenessVertex == null){
            System.out.println("The Highest Node for Closeness: " + "null" +  " and the value: " + "null");
        }else{
            System.out.println("The Highest Node for Closeness: " + maxClosenessVertex.getValueOfVertex() +  " and the value: " + maxCloseness);
        }

    }

    //this is the function for determining the element is integer or not.
    public static boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int number = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}





