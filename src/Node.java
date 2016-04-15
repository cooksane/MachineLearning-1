
public class Node {

	public String attribute;
	public Node yes;
	public Node no;
	
	public Node(){
		this.attribute = null;
		this.yes = null;
		this.no = null;
	}
	public Node(String attribute){
		this.attribute = attribute;
		this.yes = null;
		this.no = null;
	}
}
