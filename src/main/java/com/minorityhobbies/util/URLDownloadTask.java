package com.minorityhobbies.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;

public class URLDownloadTask implements Callable<Boolean> {
	private final URL resource;
	private final File targetDir;
	
	public URLDownloadTask(URL resource, String targetDir) {
		super();
		this.resource = resource;
		this.targetDir = new File(targetDir);
	}
	
	public URLDownloadTask(URL resource, File targetDir) {
		super();
		this.resource = resource;
		this.targetDir = targetDir;
	}

	@Override
	public Boolean call() {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = resource.openStream();

			File filename = new File(resource.getFile());
			out = new FileOutputStream(targetDir + File.separator + filename.getName());
			byte[] b = new byte[1024 * 64];
			for (int read = 0; (read = in.read(b)) > 0;) {
				out.write(b, 0, read);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
