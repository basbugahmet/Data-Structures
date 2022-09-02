import java.util.*;
public class Graph implements GraphInterface{


    ArrayList<Vertex> adjacencyList =  new ArrayList<Vertex>(); //this is the list that we keep all vertices

    public Graph(int size) { //this is the constructor, it takes one parameter. According to the vertex number it will create empty vertices
        for (int i = 0; i < size; i++) {
            adjacencyList.add(new Vertex("-1"));
        }
    }

    //this is the function to add edge for a vertex.
    //the logic in adding edge, if the elements are integer type then the indexSource and indexDestination will be themselves.
    //otherwise the integerIndex of them will be the number that is insertion order number.
    @Override
    public void addEdge(String source, String destination,int indexSource,int indexDestination)
    {
        Vertex sourceV = new Vertex(source); //creating Source vertex
        Vertex destinationV = new Vertex(destination);//creating Destination vertex

        //checking for a neighbor not to be added again
        boolean addBefore = false;
        for(Vertex eachNeig : adjacencyList.get(indexSource).getNeighbors()){
            if(Objects.equals(eachNeig.getValueOfVertex(), destination)){
                addBefore = true;
                break;
            }
        }


        //below we will do adding operations. The logic here is that, as default the Vertices in the list was
        //Vertex("-1") but here we will change the values of them in order to set new values.

        if(!addBefore){
            adjacencyList.get(indexSource).addNeighbor(destinationV);  //adding neighbor
            destinationV.setIntegerIndex(indexDestination); //equalize the integer value of neighboring vertex
            adjacencyList.get(indexSource).setName(source);//setting name
            adjacencyList.get(indexSource).makeInitialized();//at the beginning the vertex in this index was default whose value is "-1"
            //we will make it initialized.
            adjacencyList.get(indexSource).setIntegerIndex(indexSource); //equalize the integer value of the source vertex

            adjacencyList.get(indexDestination).addNeighbor(sourceV);//adding neighbor
            sourceV.setIntegerIndex(indexSource);//equalize the integer value of neighboring vertex
            adjacencyList.get(indexDestination).setName(destination);//setting name
            adjacencyList.get(indexDestination).makeInitialized();//at the beginning the vertex in this index was default whose value is "-1"
            //we will make it initialized.
            adjacencyList.get(indexDestination).setIntegerIndex(indexDestination);//equalize the integer value of the destination vertex
        }

    }

    //This method will give us shortest path. It takes two parameter source vertex and destination vertex.
    @Override
    public void getShortestPath(Vertex source, Vertex destination)
    {

        //we will determine the place of vertices according to integerIndex
        int valueOfSource = source.getIntegerIndex();
        int valueOfDestionation = destination.getIntegerIndex();

        int[] predecessorArray = new int[adjacencyList.size()];
        int[] distanceArray = new int[adjacencyList.size()];
        boolean visited[] = new boolean[adjacencyList.size()];


        for (int i = 0; i < adjacencyList.size(); i++) {
            visited[i] = false; //making all other indices false, marking them unvisited.
            distanceArray[i] = Integer.MAX_VALUE;//making default distance max
            predecessorArray[i] = -1; //default value -1
        }

        Stack<Vertex> stack = new Stack<>();//stack that we will keep vertices when we are searching path below.
        visited[valueOfSource] = true;//signing source as visited
        distanceArray[valueOfSource] = 0;//signing distance of itself as 0
        stack.add(source); //I used here stack because its time complexity for inserting and deleting is O(1)

        boolean found = false;

        while (!found && !stack.isEmpty()) { //the process will continue up to stack is empty and object is found
            Vertex currentVertex = stack.remove(0);//since the logic in the stack is LIFO, I remove object which is at the 0 index.
            int i = 0;
            while(!found && i < adjacencyList.get(currentVertex.getIntegerIndex()).getNeighbors().size()){
                int neighbor = adjacencyList.get(currentVertex.getIntegerIndex()).getNeighbors().get(i).getIntegerIndex(); //we will visit all neighbors
                if (!visited[neighbor]) {
                    visited[neighbor] = true;//signing as visited
                    distanceArray[neighbor] = distanceArray[currentVertex.getIntegerIndex()] + 1;
                    predecessorArray[neighbor] = currentVertex.getIntegerIndex();
                    Vertex neighborWillBeAdded = adjacencyList.get(currentVertex.getIntegerIndex()).getNeighbors().get(i);
                    stack.add(neighborWillBeAdded);
                    if (Objects.equals(neighborWillBeAdded.getValueOfVertex(), destination.getValueOfVertex()))
                        found = true;
                }
                i+=1;
            }
        }

        if(!found){ //if found false, then there is no path return empty.
            return;
        }



        //in the below if the elements in dataset are integer than the integerIndex of them will be themselves
        //so the path will consist of vertices actually.
        //however if the vertices are not integer then the path will consist of their integerIndex values.

        LinkedList<Integer> path = new LinkedList<Integer>(); //this is the path that we keep integerIndex of vertices
        path.add(valueOfDestionation);
        while (predecessorArray[valueOfDestionation] != -1) {
            path.add(predecessorArray[valueOfDestionation]);
            valueOfDestionation = predecessorArray[valueOfDestionation];
        }

        //path.size()-1 will give us path length
        source.setCloseness(source.getCloseness()+ (path.size()-1));
        destination.setCloseness(destination.getCloseness()+ (path.size()-1));

        for (int i = 1; i < path.size()-1; i++) {
            //achieving vertices in the path except first and last vertices.(starting from i=1)
            //by doing it, we can determine betweenness value for each vertex in the path.
            //path.get(i) >>> will give us integerIndex, and with this we can achieve the vertex in the adj list.
            adjacencyList.get(path.get(i)).setBetweenness(adjacencyList.get(path.get(i)).getBetweenness()+1);
        }


    }

    @Override
    public int size(){ //returns the size of adj list.
        return adjacencyList.size();
    }

    @Override
    public ArrayList<Vertex> getAdjacencyList(){ //return adj list.
        return adjacencyList;
    }

    @Override
    public boolean isEmpty(){ //at the beginning the vertices which is default was not initialized we will determine the empty or not, according to
                                //initialized situation.
        boolean empty = true;
        for(Vertex eachVertex : adjacencyList){
            if (eachVertex.isInitialized()) {
                empty = false;
                break;
            }
        }
        return empty;
    }
}
