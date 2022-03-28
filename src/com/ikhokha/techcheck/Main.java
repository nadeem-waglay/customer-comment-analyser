package com.ikhokha.techcheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;


public class Main {

	public static void main(String[] args) {
		
		Map<String, Integer> totalResults = new HashMap<>();
				
		final File docPath = new File("../docs");
		final File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		final CompletionService<Map<String, Integer>> service = new ExecutorCompletionService<Map<String, Integer>>(executor);
		
		for (File commentFile : commentFiles) {

			final Callable<Map<String, Integer>> commentAnalyzer = new CommentAnalyzer(commentFile);
			service.submit(commentAnalyzer);
						
		}

		executor.shutdown();

		try {
			for (int i = 0; i < commentFiles.length; i++) {
    			final Future<Map<String, Integer>> serviceResult = service.take();
				addReportResults(serviceResult.get(), totalResults);
  			}
		} catch (ExecutionException | InterruptedException e) { 
			e.printStackTrace();
		}
		
		System.out.println("RESULTS\n=======");
		totalResults.forEach((k,v) -> System.out.println(k + " : " + v));
	}
	
	/**
	 * This method adds the result counts from a source map to the target map 
	 * @param source the source map
	 * @param target the target map
	 */
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			Integer total = target.get(entry.getKey()) == null ? entry.getValue() : target.get(entry.getKey()) + entry.getValue();
			target.put(entry.getKey(), total);
		}
		
	}

}
