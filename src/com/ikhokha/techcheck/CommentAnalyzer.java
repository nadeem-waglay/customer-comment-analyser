package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentAnalyzer implements Callable {
	
	private File file;
	private Map<String, Pattern> patterns = new HashMap<String, Pattern>();
	
	
	public CommentAnalyzer(File file) {
		this.file = file;
		this.patterns.put("SHORTER_THAN_15", Pattern.compile("^.{0,14}$"));
		this.patterns.put("SHAKER_MENTIONS", Pattern.compile("shaker", Pattern.CASE_INSENSITIVE));
		this.patterns.put("MOVER_MENTIONS", Pattern.compile("mover", Pattern.CASE_INSENSITIVE));
		this.patterns.put("QUESTIONS", Pattern.compile("\\?"));
		this.patterns.put("SPAM", Pattern.compile("(http)|(www)", Pattern.CASE_INSENSITIVE));
	}
	
	public Map<String, Integer> call() {
		Map<String, Integer> resultsMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				for (String metric : this.patterns.keySet()) {
  					if (this.patterns.get(metric).matcher(line).find()) {
						incOccurrence(resultsMap, metric);
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error processing file: " + file.getAbsolutePath());
			e.printStackTrace();
		}
		
		return resultsMap;
		
	}
	
	/**
	 * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
	 * @param countMap the map that keeps track of counts
	 * @param key the key for the value to increment
	 */
	private void incOccurrence(Map<String, Integer> countMap, String key) {
		
		countMap.putIfAbsent(key, 0);
		countMap.put(key, countMap.get(key) + 1);
	}

}
