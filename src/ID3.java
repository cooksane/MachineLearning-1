import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.Iterator;


public class ID3 {
	
	public static HashSet<Recipe> parseRecipes(String file)
    {
		HashSet<Recipe> recipes = new HashSet<Recipe>();
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
        return recipes;
    }
	
	public static HashSet<String> parseIngredients(String filename) throws FileNotFoundException{
		HashSet<String> ingredients = new HashSet<String>();
		File file = new File(filename);
		Scanner scanner = new Scanner(file);

	    while(scanner.hasNextLine()){
	        ingredients.add(scanner.nextLine().trim().replace("\"",""));
	    }
	    return ingredients;
	}
	
//	public static Node buildTree(HashSet<String> ingredients, HashSet<Recipe> recipes){
//		String targetIngredient = getTargetIngredient(ingredients,recipes);
//		Node root = buildTree(ingredients, recipes, targetIngredient);
//		return root;
//	}
	
	public static Node buildTree(String ingredientsFile, String recipesFile) throws FileNotFoundException {
		HashSet<String> ingredients = parseIngredients(ingredientsFile);
		HashSet<Recipe> recipes = parseRecipes(recipesFile);
		return buildTree(ingredients, recipes);
	}
	
	public static Node buildTree(HashSet<String> ingredients, HashSet<Recipe> recipes){
		Node root = new Node();
		String cuisine = isSameCuisine(recipes);
		if (cuisine!=null){
			//System.out.println(cuisine);
			root.attribute = cuisine;
			return root;
		}
		if (ingredients.isEmpty()){
			//if number of predicting attributes is empty, find most common cuisine
			root.attribute = mostCommonCuisine(recipes);
			return root;
		} else {
			root.attribute = bestInfoGain(ingredients, recipes);

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
	
	public static String bestInfoGain(HashSet<String> ingredients, HashSet<Recipe> recipes){
		double entropy = getEntropy(recipes);
		double maxGain = -1.0 * Double.MAX_VALUE;
		String bestIngredient = "";
		for(String ingredient : ingredients) {
			HashSet<Recipe> yesRecipes = getSubset(true, ingredient, recipes);
			HashSet<Recipe> noRecipes = getSubset(false, ingredient, recipes);
			double yesEntropy = getEntropy(yesRecipes);
			double noEntropy = getEntropy(noRecipes);
			double gain = entropy - (((yesRecipes.size() / recipes.size()) * yesEntropy) +
					((noRecipes.size() / recipes.size()) * noEntropy));
			if(gain > maxGain) {
				maxGain = gain;
				bestIngredient = ingredient;
			}
		}
		return bestIngredient;
	}
	
	public static double getEntropy(HashSet<Recipe> recipes) {
		HashMap<String, Double> recipeCount = getRecipeCount(recipes);
		double entropy = 0;
		for(HashMap.Entry<String, Double> entry: recipeCount.entrySet()) {
			double p_i = entry.getValue();
			entropy += p_i * (Math.log(p_i) / Math.log(2));
		}
		return -1.0 * entropy;
	}
	
	public static HashMap<String, Double> getRecipeCount(HashSet<Recipe> recipes) {
		HashMap<String, Double> recipeCount = new HashMap<String, Double>();
		for (Recipe recipe: recipes){
			if (!recipeCount.containsKey(recipe.cuisine)){
				recipeCount.put(recipe.cuisine, 1.0);
			} else {
				recipeCount.put(recipe.cuisine, recipeCount.get(recipe.cuisine)+1);
			}
		}
		return recipeCount;
	}
	
	private static HashSet<Recipe> getSubset(boolean b, String attribute,
			HashSet<Recipe> recipes) {
		HashSet<Recipe> subSet = new HashSet<Recipe>();
		if(b) {
			for(Recipe recipe : recipes) {
				if(recipe.ingredients.contains(attribute)) {
					subSet.add(recipe);
				}
			}
		}
		if(!b) {
			for(Recipe recipe : recipes) {
				if(!recipe.ingredients.contains(attribute)) {
					subSet.add(recipe);
				}
			}
		}
		return subSet;
	}

	private static void printTree(Node root) {
		Queue<Node> q = new LinkedList<Node>();
		if(root == null)
			return;
		q.add(root);
		while(!q.isEmpty()) {
			Node n = (Node) q.remove();
			System.out.println(" " + n.attribute);
			if(n.yes != null)
				q.add(n.yes);
			if(n.no != null)
				q.add(n.no);
		}
	}
	public static void printTreeByLevel(Node root)
	{
        Queue<Node> currentLevel = new LinkedList<Node>();
        Queue<Node> nextLevel = new LinkedList<Node>();

        currentLevel.add(root);

        while (!currentLevel.isEmpty()) {
        	for(Node currentNode : currentLevel) {
                if (currentNode.yes != null) {
                    nextLevel.add(currentNode.yes);
                }
                if (currentNode.no != null) {
                    nextLevel.add(currentNode.no);
                }
                System.out.print(currentNode.attribute + " ");
            }
            System.out.println();
            currentLevel = nextLevel;
            nextLevel = new LinkedList<Node>();

        }
	}
	
	public static void guessCuisine(String filename, Node root) {
		Node storedRoot = root;
		HashSet<Recipe> recipes = parseRecipes(filename);
		for(Recipe recipe : recipes) {
			while(root.yes != null && root.no != null) {
				if(recipe.ingredients.contains(root.attribute)) {
					root = root.yes;
				} else {
					root = root.no;
				}
			}
			System.out.println(root.attribute);
			root = storedRoot;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Node root = buildTree("ingredients.txt", "training_data.csv");
		guessCuisine("test_data_sample.csv", root);
	}
	
//	public static String getTargetIngredient(HashSet<String> ingredients, HashSet<Recipe> recipes ){
//		//calculate entropy
//		double entropyRecipes = 0;
//		return null;
//		
//	}

}
