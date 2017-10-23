import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Perform a battle between the Autobots and the Decepticons
 * 
 * Takes input that describes a group of Transformers and based on that group displays:
 * 
 *	a. The number of battles
 *	b. The winning team
 *	c. The surviving members of the losing team 
 */
public class TransformerBattle {
	
	/**
	 * Application entry point.
	 * @param args array of command line parameters
	 */
	public static void main(String[] args) {
		TransformerBattle app = new TransformerBattle();
		app.run(args);
	}
	
	/**
	 * Run application.
	 * @param args array of command line parameters
	 */
	public void run(String[] args) {
		TransFormer[] transFormers = argsToTransFormersArray(args);
		String msg;
		if (transFormers == null) {
			msg = getErrorMsg();
		}
		else {
			msg = battle(transFormers);
		}
		System.out.println(msg);
	}
	
	/**
	 * Returns last error message.
	 * @return error message
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	
	/**
	 * Converts arguments to TransFormer array. 
	 * @param args array of command line parameters
	 * @return array of TransFormers or null
	 */
	public TransFormer[] argsToTransFormersArray(String[] args) {
		if ((args != null) && (args.length == 1)) {
			try {
				List<String> records = new ArrayList<String>();
				BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			    String line;
			    while ((line = reader.readLine()) != null) {
			      records.add(line);
			    }
			    reader.close();
			    
			    if (records.size() > 0) {
			    		TransFormer[] transFormers = new TransFormer[records.size()];
			    		int i = 0;
			    		for (String s : records) {
			    			transFormers[i++] = new TransFormer(s);
			    		}
			    		return transFormers;
			    }  
			}
			catch (Exception e) {
				errorMsg = "Couldn't read file " + args[0];
			}
		}
		else {
			errorMsg = "Invalid number of parameters";
		}
		return null;
	}
	
	/**
	 * Start battle. 
	 * @param transformers array of transformers
	 * @return battle results as a string
	 */
	public String battle(TransFormer[] transformers) {
		List<TransFormer> autobots = new ArrayList<TransFormer>();
		List<TransFormer> decepticons = new ArrayList<TransFormer>();
		
		// Separate transformers
		for (TransFormer tf : transformers) {
			switch (tf.allegiance) {
				case Autobot:
					autobots.add(tf);
					break;
				case Decepticons:
					decepticons.add(tf);
					break;
			}
		}
		
		// Sort by rank in descending order
		Comparator<TransFormer> comparator = new Comparator<TransFormer>() {
		    @Override
		    public int compare(TransFormer left, TransFormer right) {
		        return right.rank - left.rank; 
		    }
		};
		Collections.sort(autobots, comparator);
		Collections.sort(decepticons, comparator);
		
		// Start fighting
		List<TransFormer> autobotsToRemove = new ArrayList<TransFormer>();
		List<TransFormer> decepticonsToRemove = new ArrayList<TransFormer>();
		int count = Math.min(autobots.size(), decepticons.size());
		int fightCount = 0;
		for (int i = 0; i < count; i++) {
			TransFormer autobot = autobots.get(i);
			TransFormer decepticon = decepticons.get(i);
			
			fightCount++;
			
			// Any Transformer named Optimus Prime or Predaking wins his fight automatically 
			// regardless of any other criteria. In the event either of the above face each other 
			// (or a duplicate of each other), the game immediately ends with all competitors destroyed
			if ((autobot.name.equalsIgnoreCase("Optimus Prime")) 
					&& (decepticon.name.equalsIgnoreCase("Predaking"))) {
				autobots.clear();
				decepticons.clear();
				break;
			}
			else if (autobot.name.equalsIgnoreCase("Optimus Prime")) {
				decepticonsToRemove.add(decepticon);
			}
			else if (decepticon.name.equalsIgnoreCase("Predaking")) {
				autobotsToRemove.add(autobot);
			}
			
			// If any fighter is down 4 or more points of courage and 3 or more points of strength
			// to their opponent, the opponent automatically wins the face-off regardless of
			// overall rating (opponent has ran away)
			else if ((autobot.courage >= (decepticon.courage + 4)) 
					&& (autobot.strength >= (decepticon.strength + 3))) {
				decepticonsToRemove.add(decepticon);
			}
			else if ((decepticon.courage >= (autobot.courage + 4)) 
					&& (decepticon.strength >= (autobot.strength + 3))) {
				autobotsToRemove.add(autobot);
			}
			
			// If one of the fighters is 3 or more points of skill above their opponent, they win
			// the fight regardless of overall rating
			else if (autobot.skill >= (decepticon.skill + 3)) {
				decepticonsToRemove.add(decepticon);
			}
			else if (decepticon.skill >= (autobot.skill + 3)) {
				autobotsToRemove.add(autobot);
			}
			
			// The winner is the Transformer with the highest overall rating
			else if (autobot.getOverallRating() > decepticon.getOverallRating()) {
				decepticonsToRemove.add(decepticon);
			}
			else if (decepticon.getOverallRating() > autobot.getOverallRating()) {
				autobotsToRemove.add(autobot);
			}
			
			// In the event of a tie, both Transformers are considered destroyed
			else {
				autobotsToRemove.add(autobot);
				decepticonsToRemove.add(decepticon);
			}
		}
		autobots.removeAll(autobotsToRemove);
		decepticons.removeAll(decepticonsToRemove);
		int autobotWins = decepticonsToRemove.size();
		int decepticonWins = autobotsToRemove.size();
		
		// Build the results of the battle into a string
		StringBuilder sb = new StringBuilder();
		
		sb.append(fightCount);
		sb.append(" battle(s)"); 
		sb.append(nl);
		
		sb.append("Winning team");
		if (autobotWins > decepticonWins) {
			sb.append(" (Autobots):");
			for (TransFormer tf : autobots) {
				sb.append(" "); sb.append(tf.name);
			}
		}
		else if (decepticonWins > autobotWins) {
			sb.append(" (Decepticons):");
			for (TransFormer tf : decepticons) {
				sb.append(" "); sb.append(tf.name);
			}
		}
		else {
			sb.append(" : It's a draw");
		}
		sb.append(nl);
		
		if (autobotWins < decepticonWins) {
			sb.append("Survivors from the losing team (Autobots):");
			for (TransFormer tf : autobots) {
				sb.append(" "); sb.append(tf.name);
			}
			sb.append(nl);
		}
		else if (decepticonWins < autobotWins) {
			sb.append("Survivors from the losing team (Decepticons):");
			for (TransFormer tf : decepticons) {
				sb.append(" "); sb.append(tf.name);
			}
			sb.append(nl);
		}
		
		return sb.toString();
	}
	
	/**
	 * Class describes a Transformer.
	 */
	class TransFormer {
		
		String name;
		Allegiance allegiance;
		
		int strength;
		int intelligence;
		int speed;
		int endurance;
		int rank;
		int courage;
		int firepower;
		int skill;
		
		/**
		 * Creates a TransFormer from data string. This constructor could throw
		 * an exception if data is not valid. 
		 * @param data transformer description
		 */
		TransFormer(String data) {
			String[] parts = data.split(",");
			
			name = parts[0].trim();
			switch (parts[1].trim()) {
				case "A":
					allegiance = Allegiance.Autobot;
					break;
				default:
					allegiance = Allegiance.Decepticons;
					break;
			}
			strength = Integer.parseInt(parts[2].trim());
			intelligence = Integer.parseInt(parts[3].trim());
			speed = Integer.parseInt(parts[4].trim());
			endurance = Integer.parseInt(parts[5].trim());
			rank = Integer.parseInt(parts[6].trim());
			courage = Integer.parseInt(parts[7].trim());
			firepower = Integer.parseInt(parts[8].trim());
			skill = Integer.parseInt(parts[9].trim());
		}
		
		/**
		 * Returns the overall rating of a Transformer.
		 * @return overall rating
		 */
		int getOverallRating() {
			return strength + intelligence + speed + endurance + firepower;
		}	
	}
	
	private enum Allegiance { Autobot, Decepticons }
	private String nl = System.lineSeparator();
	private String errorMsg = "";
}
