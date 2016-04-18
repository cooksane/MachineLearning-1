import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Recipe {
	
	public String cuisine;
	public ArrayList<String> ingredients;
	public int id;
	
	public Recipe(){
		this.cuisine = null;
		this.ingredients = new ArrayList<String>();
		this.id = 0;
	}


}
