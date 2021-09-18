import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.objectweb.asm.tree.ClassNode;
import the.bytecode.club.bytecodeviewer.*;
import the.bytecode.club.bytecodeviewer.util.*;
import the.bytecode.club.bytecodeviewer.api.*;
import the.bytecode.club.bytecodeviewer.decompilers.impl.FernFlowerDecompiler;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;

/**
 * @author jowasp
 */
public class XposedGenerator extends Plugin
{
    //PRIVATE
    private static List<String> methodsNames = new ArrayList<String>();
    private static List<String> cleanMethodsNames = new ArrayList<String>();
    private static String foundpckg;
	
	public XposedGenerator() {}
    
    @Override
    public void execute(ArrayList<ClassNode> classNodeList)
    {
        //Get actual file class content
        ResourceViewer viewer = BytecodeViewer.getActiveResource();
        
		if(viewer == null)
		{
		    BytecodeViewer.showMessage("Open A Class First");
			return;
		}
		
        String className = viewer.getName();
        String containerName = viewer.name;
        ClassNode classnode = BytecodeViewer.getCurrentlyOpenedClassNode();
        
        //Call XposedGenerator class
        ParseChosenFileContent(className,containerName,classnode);
    }

    public static void ParseChosenFileContent(String classname, String containerName, ClassNode classNode)
    {
        try
        {
            //Parse content - Extract methods after APK /JAR has been extracted
            byte[] cont = ASMUtil.nodeToBytes(classNode);
            
            //Use one of the decompilers
            //TODO:Allow users to select other decompilers?
            FernFlowerDecompiler decompilefern = new FernFlowerDecompiler();

            //Decompile using Fern
            String decomp  = decompilefern.decompileClassNode(classNode, cont);
            String[] xposedTemplateTypes = {"Empty","Parameters","Helper"};
            @SuppressWarnings({ "unchecked", "rawtypes" })
            JComboBox xposedTemplateList = new JComboBox(xposedTemplateTypes);
            //Set results of parsed methods into an a list
            List<String> methodsExtracted = ProcessContentExtractedClass(decomp);
            String packgExtracted = ProcessContentExtractedPackage(decomp);
            System.out.println("PACKAGE NAME: " +packgExtracted);

            //Get a clean list
            List<String> cleanMethods = null;
            //clear list
            cleanMethods = ProcessCleanMethodsAll(methodsExtracted);
            if (!cleanMethods.isEmpty())
            {
                JComboBox<String> cb = new JComboBox<>(cleanMethods.toArray(new String[cleanMethods.size()]));

                //Add Panel elements
                //Start Panel
                JPanel myPanel = new JPanel();
                myPanel.add(Box.createHorizontalStrut(15));
                myPanel.add(xposedTemplateList);
                myPanel.add(cb);

                //output methods to pane box
                int result = JOptionPane.showConfirmDialog(null, myPanel,
                        "Choose Template and Method for Xposed Module", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    //Read Chosen Class
                    System.out.println("SELECTED CLASS is" + cb.getSelectedItem());
                    String selectedClass = cb.getSelectedItem().toString();
                    String selectedXposedTemplate = xposedTemplateList.getSelectedItem().toString();

                    //WriteXposed Class with extracted data
                    try{
                        WriteXposedModule(selectedClass, packgExtracted, classname, selectedXposedTemplate);
                    }
                    catch(IllegalArgumentException e)
                    {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Error" + e.toString());
                    }
                }
            }
			else
			{
                JOptionPane.showMessageDialog(null,"Class Not Suitable");
			}
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Error" + e.toString());
        }

    }

    public static void WriteXposedModule(String functionToHook, String packageName, String classToHook, String template) {
		System.out.println("TEMPLATE: " + template);
        if (template != null && !template.equals("Empty"))
        {
            try {
                //TODO: Prompt save dialog
                File file = new File("./XposedClassTest.java");

                // if file doesn't exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }
                //Extract the package name only
                String packageNameOnly = packageName.substring(8,packageName.length() - 2 ).trim();
                String classToHookNameOnly = classToHook;
                if(classToHookNameOnly.endsWith(".class"))
                    classToHookNameOnly = classToHook.substring(0, classToHookNameOnly.length() - 6);
                
                String[] classClean = classToHookNameOnly.split("\\/");
                String[] functionSplitValues = functionToHook.split("\\s+");
                //select
                String onlyClass = classClean[classClean.length-1];
                //String onlyFunctionParateses = functionSplitValues[functionSplitValues.length-2];

                String onlyFunction = CleanUpFunction(functionSplitValues);
                //String functionToHookOnly = "dummy function";
                System.out.println(onlyClass);
                System.out.println(packageNameOnly);

                //Write Xposed Class
                String XposedClassText =
                        "package androidpentesting.com.xposedmodule;"+ "\r\n" +
                                "import de.robv.android.xposed.IXposedHookLoadPackage;" + "\r\n" +
                                "\r\n" +
                                "import de.robv.android.xposed.XC_MethodHook;" +"\r\n" +
                                "import de.robv.android.xposed.XposedBridge;" +"\r\n" +
                                "import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;"+"\r\n" +
                                "import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;"+"\r\n" +"\r\n" +
                                "public class XposedClassTest implements IXposedHookLoadPackage {"+"\r\n" +"\r\n" +
                                "   public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {" + "\r\n" +"\r\n" +
                                "		String classToHook = " + "\"" + packageNameOnly + "." + onlyClass + "\";" + "\r\n" +
                                "		String functionToHook = "+"\""+ onlyFunction+"\";"+"\r\n" +
                                "		if (lpparam.packageName.equals("+"\""+packageNameOnly+ "\""+")){"+ "\r\n" +
                                "			XposedBridge.log(" + "\" Loaded app: \" " + " + lpparam.packageName);"+ "\r\n" +"\r\n" +
                                "			findAndHookMethod("+"\""+onlyClass+"\"" + ", lpparam.classLoader, "+" \"" +onlyFunction + "\""+", int.class,"+ "\r\n" +
                                "			new XC_MethodHook() {"+ "\r\n" +
                                "			    @Override"+ "\r\n" +
                                "		        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {"+ "\r\n" +
                                "		            //TO BE FILLED BY ANALYST"+ "\r\n" +
                                "			    }"+ "\r\n" +
                                "		    });"+"\r\n" +
                                "	    }"+ "\r\n" +
                                "   }"+ "\r\n" +
                                "}"+ "\r\n"
                        ;
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(XposedClassText);
                bw.write("\r\n");
                bw.close();

                System.out.println("Done");
                JOptionPane.showMessageDialog(null,"Xposed Module Generated");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,"Error" + e.toString());
                e.printStackTrace();
            }
        }
		else
		{
			JOptionPane.showMessageDialog(null,"Empty Template Chosen, Did Not Generate");
		}
    }

    private static List <String> ProcessContentExtractedClass(String contentFile){
        Scanner scanner = null;
        try{
            scanner = new Scanner(contentFile);
            //@TODO : Improve patterns to match other excepts 'public'
            String regexclass = "public";
            //String regexPkg = "package";
            Pattern pattern = Pattern.compile(regexclass, Pattern.CASE_INSENSITIVE);
            //Pattern patternVoid = Pattern.compile(regexVoid , Pattern.CASE_INSENSITIVE);
            // Pattern patternPkg = Pattern.compile(regexPkg , Pattern.CASE_INSENSITIVE);
            //scanner.useDelimiter(";");
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                // process the line
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()){

                    if (matcher.group() != null)
                    {
                        System.out.println("find() found the pattern \"" + quote(line.trim()));
                        System.out.println("Function: " + CleanUpFunction(line.trim().split("\\s+")));
                        methodsNames.add(quote(line.trim()));
                    }
                    else
                    {
                        methodsNames.add("No methods found");
                    }

                }

            }

            if (methodsNames.isEmpty())
            {
                methodsNames.add("No methods found");
            }
            else
            {
                return methodsNames;
            }
            return methodsNames;
        }
        finally {
            if (scanner!=null)
                scanner.close();
        }
    }

    private static List <String> ProcessCleanMethodsAll(List<String> rawMethods)
    {
        for (String m:rawMethods)
        {
            //Exclude class declaration
            //TODO:add a list containing all possible types
            if (m.contains("extends") || (m.contains("implements") || (!m.contains("("))))
            {
                continue;
            }
            else
            {
                cleanMethodsNames.add(m);
            }

        }
        return cleanMethodsNames;


    }

    private static String CleanUpFunction(String[] rawFunction)
    {
        String onlyFunc = "functiondummy";
        for (String m:rawFunction)
        {
            if(m.contains("("))
            {
                String[] split = m.split("\\(")[0].split(" ");
                return split[split.length-1];
            }
            else
            {
                continue;
            }
        }

        return onlyFunc;

    }

    private static String ProcessContentExtractedPackage(String contentFile){
        Scanner scanner = null;
        try {
            scanner = new Scanner(contentFile);
            String regexPkg = "package";
            Pattern patternPkg = Pattern.compile(regexPkg , Pattern.CASE_INSENSITIVE);
            String line = scanner.nextLine();
            // process the line
            Matcher matcher = patternPkg.matcher(line);
            while (matcher.find()){

                if (matcher.group() != null)
                {
                    System.out.println("find() found the pattern \"" + quote(line.trim())) ;
                    foundpckg  = quote(line.trim());

                }
                else
                {
                    foundpckg  = "";
                }
            }
            try
            //
            {
                if (foundpckg == null || foundpckg.isEmpty())
                    foundpckg  = "No Package Found";

            }
            catch(NullPointerException e)
            {
                JOptionPane.showMessageDialog(null,"Error - no package was found in the selected class: " + e.toString());
            }
            finally
            {
                if(scanner != null)
                    scanner.close();
            }
        }
        catch(IllegalArgumentException e)
        {
            JOptionPane.showMessageDialog(null,"Error" + e.toString());
            if(scanner != null)
                scanner.close();
        }
        finally
        {
            if(scanner != null)
                scanner.close();
        }
        return foundpckg;

    }

    private static String quote(String aText){
        String QUOTE = "'";
        return QUOTE + aText + QUOTE;
    }
}
