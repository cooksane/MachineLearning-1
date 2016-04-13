
public class Node {

	public String attribute;
	public Node yes;
	public Node no;
	
	public Node(){
		this.attribute = null;
		this.yes = new Node();
		this.no = new Node();
	}
	public Node(String attribute){
		this.attribute = attribute;
		this.yes = new Node();
		this.no = new Node();
	}
}
