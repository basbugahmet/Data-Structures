import java.util.ArrayList;

public class Vertex {


	private Integer integerIndex; //this is the integer number that helps us when we are inserting vertices
	private String valueOfVertex; //this is the name of vertex
	private ArrayList<Vertex> neighbors; //this is the arraylist the we will keep neighbors of the vertex
	private double betweenness = 0; //default 0
	private double closeness = 0; //default 0
	private boolean initialized = false; //when we add vertex to the graph we will make it true


	public Vertex(String valueOfVertex) { //constructor
		this.valueOfVertex = valueOfVertex;
		neighbors = new ArrayList<Vertex>();
	}

	public Integer getIntegerIndex() { //it is used in shortest path function
		return integerIndex;
	}

	public void setIntegerIndex(Integer integerIndex) { //for setting integer number that corresponds index here
		this.integerIndex = integerIndex;
	}

	public double getBetweenness() { //it is used in shortest path function
		return betweenness;
	}

	public void setBetweenness(double betweenness) { //it is used in shortest path function
		this.betweenness = betweenness;
	}

	public void makeInitialized(){ //when we add vertex to the graph we will make it true
		initialized = true;
	}

	public boolean isInitialized() {//it is used when printing results
		return initialized;
	}

	public double getCloseness() {//it is used in shortest path function
		return closeness;
	}

	public double getFinalCloseness() { //it is used when printing results
		if(closeness == 0){
			return 0;
		}
		return 1/closeness;
	}

	public double getFinalBetweenness() { //it is used when printing results
		if(betweenness == 0){
			return 0;
		}
		return betweenness;
	}

	public void setCloseness(double closeness) {//it is used in shortest path function
		this.closeness = closeness;
	}

	public void addNeighbor(Vertex aNeighbor) {//it is used for inserting part
		neighbors.add(aNeighbor);
	}

	public ArrayList<Vertex> getNeighbors() {//it is used for inserting part
		return neighbors;
	}

	public String getValueOfVertex() { //it is used for inserting part and several parts
		return valueOfVertex;
	}

	public void setName(String name) {//it is used for inserting part
		this.valueOfVertex = name;
	}

}
