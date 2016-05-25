/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.releng.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class JavadocPackageListTask extends Task
{

	private static final FilenameFilter JAVA_FILE_FILTER = new FilenameFilter() {
		public boolean accept (File parent, String file) {
			return file.endsWith(".java");
		}
	};
	
	private static final FilenameFilter DIR_FILTER = new FilenameFilter() {
		public boolean accept (File parent, String file) {
			return new File(parent, file).isDirectory();
		}
	};
	
	private String[] sourceDirs;
	private File baseDir;
	private File outputFile;
	
	private LinkedList<String> packages = new LinkedList<String>();
	private LinkedList<File> dirStack = new LinkedList<File>();
	
	public JavadocPackageListTask() {
	}

	public void setSourceDirectories (String dirs) {
		sourceDirs = dirs.split(";");
	}
	
	public void setBaseDir (File baseDir) {
		this.baseDir = baseDir;
	}
	
	public void setOutputFile (File outputFile) {
		this.outputFile = outputFile;
	}
	
	public void execute () throws BuildException {
		for (String sourceDir : sourceDirs) {
			processDirectory(baseDir, sourceDir);
		}
		
		try {
			PrintWriter pw = new PrintWriter(outputFile);
			for (String pack : packages) {
				pw.println(pack);
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void processDirectory (File parent, String dirName) {
		File dir = new File(parent, dirName);
		System.err.println("processing directory: " + dir.getAbsolutePath());
		File[] javaFiles = dir.listFiles(JAVA_FILE_FILTER);
		File[] directories = dir.listFiles(DIR_FILTER);
		
		if (javaFiles.length > 0) {
			addPackage(dir);
		}
		for (File newDir : directories) {
			dirStack.addLast(newDir);
			processDirectory(dir, newDir.getName());
			dirStack.removeLast();
		}
	}
	
	protected void addPackage (File dir) {
		StringBuffer sb = new StringBuffer();
		for (File seg : dirStack) {
			sb.append(seg.getName());
			sb.append('.');
		}
		sb.deleteCharAt(sb.length() - 1);
		packages.add(sb.toString());
	}
	
	public static void main(String[] args) {
		JavadocPackageListTask jplt = new JavadocPackageListTask();
		jplt.setBaseDir(new File("c:/Eclipse3.4/eclipse/a11y-1.0.0"));
		//jplt.setOutputFile(new File(args[1]));
		jplt.setSourceDirectories("org.a11y.utils.accprobe.core/src;org.a11y.utils.accprobe.core.model/src");
		jplt.execute();
	}

}
