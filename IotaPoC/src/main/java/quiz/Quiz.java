package quiz;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Quiz {
	
	
	private Map<String, Map<String, String>> answers;
	private static final Logger log = LoggerFactory.getLogger(Quiz.class);
	
	public Quiz() {
		answers = new LinkedHashMap<>();
	}
	
	public boolean addAnswer(String name, String question, String answer) {
		if(answers.containsKey(name)) {
			String prev = answers.get(name).put(question, answer);
			if(prev != null) {
				log.info("Overwrote old answer "+prev+" with "+answer+" for question "+question+" for player "+name);
			}else {
				log.info("Added answer "+answer+" for question "+question+ " for player " +name);
			}
		}else {
			answers.put(name, new LinkedHashMap<>());
			answers.get(name).put(question, answer);
			log.info("Added player "+name+" and answer "+answer+" for question "+question);
		}
		return true;
	}
	
	public String getAnswers() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Map<String, String>> e : answers.entrySet()) {
			sb.append("Player: "+e.getKey()+" Answers:\n");
			for(Entry<String, String> e2 : e.getValue().entrySet()) {
				sb.append(" Question "+e2.getKey()+": "+e2.getValue());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
}
