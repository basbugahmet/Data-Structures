import java.util.ArrayList;

public interface GraphInterface {


    public void addEdge(String source, String destination,int indexSource,int indexDestination);

    public void getShortestPath(Vertex source, Vertex destination);

    public int size();

    public ArrayList<Vertex> getAdjacencyList();

    public boolean isEmpty();


}
