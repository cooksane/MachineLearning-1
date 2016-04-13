import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.Iterator;


public class ID3 {

	public static HashSet<Recipe> recipes = new HashSet<Recipe>();
	public static HashSet<String> ingredients = new HashSet<String>();
	
	public static void parseRecipes(String file)
    {
        //Input file which needs to be parsed
        String fileToParse = file;
        BufferedReader fileReader = null;
         
        final String DELIMITER = ",";
        try
        {
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileToParse));
             
            //Read the file line by line
            while ((line = fileReader.readLine()) != null) 
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);
              
                Recipe recipe = new Recipe();
                recipe.cuisine = tokens[1].trim().replace("\"","");
                for(int i=2; i<tokens.length;i++)
                {
                	recipe.ingredients.add(tokens[i].trim().replace("\"", ""));
                }
                recipes.add(recipe);
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally
        {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public static void parseIngredients(String file){
		Scanner scanner = new Scanner(file);

	    while(scanner.hasNext()){
	        ingredients.add(scanner.nextLine().trim().replace("\"",""));
	    }
	}
	
//	public static Node buildTree(HashSet<String> ingredients, HashSet<Recipe> recipes){
//		String targetIngredient = getTargetIngredient(ingredients,recipes);
//		Node root = buildTree(ingredients, recipes, targetIngredient);
//		return root;
//	}
	
	public static Node buildTree(HashSet<String> ingredients, HashSet<Recipe> recipes){
		Node root = new Node();
		String cuisine = isSameCuisine(recipes);
		if (cuisine!=null){
			root.attribute = cuisine;
			return root;
		}
		if (ingredients.isEmpty()){
			//if number of predicting attributes is empty, find most common cuisine
			root.attribute = mostCommonCuisine(recipes);
			return root;
		} else {
			root.attribute = bestEntropyGain(ingredients, recipes);

			HashSet<Recipe> yesRecipes = getSubset(true, root.attribute, recipes);
			if (yesRecipes.isEmpty()){
				root.yes = new Node(mostCommonCuisine(recipes));
			} else {
				ingredients.remove(root.attribute);
				root.yes = buildTree(ingredients, yesRecipes);
			}
			
			HashSet<Recipe> noRecipes = getSubset(false, root.attribute, recipes);
			if (noRecipes.isEmpty()){
				root.no = new Node(mostCommonCuisine(recipes));
			} else {
				ingredients.remove(root.attribute);
				root.no = buildTree(ingredients, noRecipes);
			}
		}
		return root;
	}
	
	public static String mostCommonCuisine(HashSet<Recipe> recipes){
		String cuisine="";
		int maxCount=0;
		HashMap<String, Integer> mostCommon = new HashMap<String, Integer>();
		for (Recipe recipe: recipes){
			if (!mostCommon.containsKey(recipe.cuisine)){
				mostCommon.put(recipe.cuisine, 1);
			} else {
				mostCommon.put(recipe.cuisine, mostCommon.get(recipe.cuisine)+1);
			}
		}
		for (HashMap.Entry<String,Integer> entry: mostCommon.entrySet()){
			if (entry.getValue()>maxCount){
				maxCount = entry.getValue();
				cuisine = entry.getKey();
			}
		}
		return cuisine;
	}
	
	public static String isSameCuisine(HashSet<Recipe> recipes){
		String cuisine = "";
		for (Recipe recipe : recipes){
			if (cuisine.equals("")){
				cuisine = recipe.cuisine;
			}
			if (!recipe.cuisine.equals(cuisine)){
				return null;
			}
		}
		return cuisine;
	}
	
	public static String bestEntropyGain(HashSet<String> ingredients, HashSet<Recipe> recipes){
		return null;
	}
	
	private static HashSet<Recipe> getSubset(boolean b, String attribute,
			HashSet<Recipe> recipes2) {
		// TODO Auto-generated method stub
		return null;
	}

	
//	public static String getTargetIngredient(HashSet<String> ingredients, HashSet<Recipe> recipes ){
//		//calculate entropy
//		double entropyRecipes = 0;
//		return null;
//		
//	}

}
