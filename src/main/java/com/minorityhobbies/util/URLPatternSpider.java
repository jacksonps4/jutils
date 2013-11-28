package com.minorityhobbies.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class URLPatternSpider implements Callable<Boolean> {
	private final String urlPattern;
	private final List<Object> parameters;
	private final File targetDir;
	
	public URLPatternSpider(String urlPattern, List<Object> parameters,
			File targetDir) {
		super();
		this.urlPattern = urlPattern;
		this.parameters = parameters;
		this.targetDir = targetDir;
	}
	
	public URLPatternSpider(String urlPattern, List<Object> parameters,
			String targetDir) {
		this(urlPattern, parameters, new File(targetDir));
	}

	@Override
	public Boolean call() throws IOException, InterruptedException, ExecutionException {
		ExecutorService executor = null;
		try {
			executor = Executors.newFixedThreadPool(6);
			CompletionService<Boolean> cs = new ExecutorCompletionService<Boolean>(executor);
			
			URLPatternFactory upf = new URLPatternFactory(urlPattern, parameters);
			for (URL url : upf) {
				cs.submit(new URLDownloadTask(url, targetDir));
			}

			for (Future<Boolean> result = null; (result = cs.take()) != null; ) {
				if (!result.get()) {
					return false;
				}
			}
			
			return true;
		} finally {
			if (executor != null) {
				executor.shutdownNow();
			}
		}
	}
}
