import com.neuronrobotics.bowlerstudio.scripting.*

import java.nio.file.Files
import java.nio.file.Paths

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import com.neuronrobotics.bowlerstudio.tabs.LocalFileScriptTab;

println "Loading new DSL..."

ScriptingEngine.addScriptingLanguage(new IScriptingLanguage() {
	java.lang.String getDefaultContents(){
		return "cube([1,1,1]);"
	}
	/**
	 * This interface is for adding additional language support. 
	 * @param code file content of the code to be executed
	 * @param args the incoming arguments as a list of objects
	 * @return the objects returned form the code that ran
	 */
	public  Object inlineScriptRun(File code, ArrayList<Object> args) throws Exception{
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(code.getAbsolutePath())));
		String hash = data.hashCode();
		String filename = code.getName().split("\\.")[0]
		File cache = new File(code.getParentFile().getAbsolutePath()+"/"+filename+"-cache/")
		println cache.getAbsolutePath()
		if(!cache.exists())
			if(!cache.mkdir())
				throw new RuntimeException("Failed to create the cache direcory")
		File groovy = new File(cache.getAbsolutePath()+"/"+filename+".groovy")
		File hashfile = new File(cache.getAbsolutePath()+"/hash.txt")
		boolean matchingHash=false;
		if(hashfile.exists()) {
			String currentHash = new String(Files.readAllBytes(Paths.get(hashfile.getAbsolutePath())));
			if(currentHash.contains(hash)) {
				matchingHash=true;
			}
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(hashfile.getAbsolutePath()));
		writer.write(hash);
		writer.close();
		if(!matchingHash){
			if(!groovy.exists())
				groovy.createNewFile();
			String groovyContents = "return new Cube(1).toCSG()"
			
			// generate real code here
			
			BufferedWriter w = new BufferedWriter(new FileWriter(groovy.getAbsolutePath()));
			w.write(groovyContents);
			w.close();
		}
		println groovy
		def ret= ScriptingEngine.inlineScriptRun(groovy, args,"Groovy");;
		println ret.getClass()
		return ret
	}
	
	/**
	 * This interface is for adding additional language support. 
	 * @param code the text content of the code to be executed
	 * @param args the incoming arguments as a list of objects
	 * @return the objects returned form the code that ran
	 * @throws Exception 
	 */
	public  Object inlineScriptRun(String code, ArrayList<Object> args) throws Exception{
		File workspace = ScriptingEngine.getWorkspace()
		File tmp = new File(workspace.getAbsolutePath()+"/"+code.hashCode()+".scad")
		if (!tmp .exists()) {
			tmp .createNewFile();
		}
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(tmp));
			output.write(code);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				output.close();
			}
		}
		
		return inlineScriptRun(tmp,args);
	}
	
	/**
	 * Returns the HashMap key for this language
	 * @return
	 */
	public  String getShellType(){
		return "OpenSCAD"
	}
	/**
	 * Returns the list of supported file extentions
	 * Convention is to provide just the leters that make up the file extention
	 * @return
	 */
	public  ArrayList<String> getFileExtenetion(){
		return ["scad","SCAD"]
	}
	
	/**
	 * This function returns if this is a binary file or a text file
	 * @return true if the file is a text file.
	 */
	public boolean getIsTextFile(){
		return true;
	}
	
})
//Set the file associate for syntax highlighting in the text editor
// for options see: https://github.com/bobbylight/RSyntaxTextArea/blob/master/src/main/java/org/fife/ui/rsyntaxtextarea/SyntaxConstants.java
LocalFileScriptTab.setExtentionSyntaxType("OpenSCAD",SyntaxConstants.SYNTAX_STYLE_LISP)

ScriptingEngine.gitScriptRun("https://github.com/adamjvr/skully.git", "src_3d/skull.scad")
















