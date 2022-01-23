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
import the.bytecode.club.bytecodeviewer.api.*;
import the.bytecode.club.bytecodeviewer.decompilers.impl.FernFlowerDecompiler;
import the.bytecode.club.bytecodeviewer.gui.resourceviewer.viewer.ResourceViewer;

/**
 ** This is an Xposed Generator Plugin, used to aid Reverse-Engineering.
 **
 ** @author jowasp
 **/

public class XposedGenerator extends Plugin {
    
    private static final List<String> methodsNames = new ArrayList<>();
    private static final List<String> cleanMethodsNames = new ArrayList<>();
    private static String foundPckg;

    public XposedGenerator() {
    }

    @Override
    public void execute(List<ClassNode> classNodeList) {
        //Get actual file class content
        ResourceViewer viewer = BytecodeViewer.getActiveResource();

        if (viewer == null) {
            BytecodeViewer.showMessage("Open A Class First");
            return;
        }

        String className = viewer.getName();
        ClassNode classnode = BytecodeViewer.getCurrentlyOpenedClassNode();

        //Call XposedGenerator class
        ParseChosenFileContent(className, classnode);
    }

    public static void ParseChosenFileContent(String classname, ClassNode classNode) {
        try {
            //Parse content - Extract methods after APK /JAR has been extracted
            byte[] cont = ASMUtil.nodeToBytes(classNode);

            //Use one of the decompilers
            //TODO:Allow users to select other decompilers?
            FernFlowerDecompiler decompilefern = new FernFlowerDecompiler();

            //Decompile using Fern
            String decomp = decompilefern.decompileClassNode(classNode, cont);
            String[] xposedTemplateTypes = {"Empty", "Parameters", "Helper"};
            @SuppressWarnings({"unchecked", "rawtypes"})
            JComboBox xposedTemplateList = new JComboBox(xposedTemplateTypes);
            //Set results of parsed methods into a list
            List<String> methodsExtracted = ProcessContentExtractedClass(decomp);
            String packgExtracted = ProcessContentExtractedPackage(decomp);

            //Get a clean list
            List<String> cleanMethods;
            //clear list
            cleanMethods = ProcessCleanMethodsAll(methodsExtracted);
            if (!cleanMethods.isEmpty()) {
                JComboBox<String> cb = new JComboBox<>(cleanMethods.toArray(new String[0]));

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
                    Object cbItem = cb.getSelectedItem();
                    Object xPosedItem = xposedTemplateList.getSelectedItem();
                    System.out.println("SELECTED CLASS is" + cbItem);
                    if (cbItem != null && xPosedItem != null) {
                        String selectedClass = cbItem.toString();
                        String selectedXposedTemplate = xPosedItem.toString();

                        //WriteXposed Class with extracted data
                        try {
                            WriteXposedModule(selectedClass, packgExtracted, classname, selectedXposedTemplate);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error" + e);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Class Not Suitable");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error" + e);
        }

    }

    public static void WriteXposedModule(String functionToHook, String packageName, String classToHook,
                                         String template) {
        if (template != null && !template.equals("Empty")) {
            try {
                
                //TODO: Prompt save dialog
                File file = new File("./XposedClassTest.java");

                // if file doesn't exist, then create it
                if (!file.exists())
                    file.createNewFile();
                
                //Extract the package name only
                String packageNameOnly = packageName.substring(8, packageName.length() - 2).trim();
                String classToHookNameOnly = classToHook;
                if (classToHookNameOnly.endsWith(".class"))
                    classToHookNameOnly = classToHook.substring(0, classToHookNameOnly.length() - 6);

                String[] classClean = classToHookNameOnly.split("/");
                String[] functionSplitValues = functionToHook.split("\\s+");
                
                //select
                String onlyClass = classClean[classClean.length - 1];

                String onlyFunction = CleanUpFunction(functionSplitValues);

                //Write Xposed Class
                String XposedClassText = "package androidpentesting.com.xposedmodule;" + "\r\n" +
                                "import de.robv.android.xposed.IXposedHookLoadPackage;" + "\r\n" +
                                "\r\n" +
                                "import de.robv.android.xposed.XC_MethodHook;" + "\r\n" +
                                "import de.robv.android.xposed.XposedBridge;" + "\r\n" +
                                "import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;" + "\r\n" +
                                "import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;" + "\r\n" +
                                "\r\n" +
                                "public class XposedClassTest implements IXposedHookLoadPackage {" + "\r\n" + "\r\n" +
                                "   public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {" + "\r\n" + "\r\n" +
                                "		String classToHook = " + "\"" + packageNameOnly + "." + onlyClass + "\";" +
                                "\r\n" +
                                "		String functionToHook = " + "\"" + onlyFunction + "\";" + "\r\n" +
                                "		if (lpparam.packageName.equals(" + "\"" + packageNameOnly + "\"" + ")){" + "\r"
                                + "\n" +
                                "			XposedBridge.log(" + "\" Loaded app: \" " + " + lpparam.packageName);" +
                                "\r\n" + "\r\n" +
                                "			findAndHookMethod(" + "\"" + onlyClass + "\"" + ", lpparam.classLoader, " + " \"" + onlyFunction + "\"" + ", int.class," + "\r\n" +
                                "			new XC_MethodHook() {" + "\r\n" +
                                "			    @Override" + "\r\n" +
                                "		        protected void beforeHookedMethod(MethodHookParam param) throws "
                                + "Throwable {" + "\r\n" +
                                "		            //TO BE FILLED BY ANALYST" + "\r\n" +
                                "			    }" + "\r\n" +
                                "		    });" + "\r\n" +
                                "	    }" + "\r\n" +
                                "   }" + "\r\n" +
                                "}" + "\r\n";
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(XposedClassText);
                bw.write("\r\n");
                bw.close();

                JOptionPane.showMessageDialog(null, "Xposed Module Generated");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error" + e);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Empty Template Chosen, Did Not Generate");
        }
    }

    private static List<String> ProcessContentExtractedClass(String contentFile) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(contentFile);
            //@TODO : Improve patterns to match other excepts 'public'
            
            String regexclass = "public";
            Pattern pattern = Pattern.compile(regexclass, Pattern.CASE_INSENSITIVE);
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                // process the line
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    if (matcher.group() != null) {
                        System.out.println("find() found the pattern \"" + quote(line.trim()));
                        System.out.println("Function: " + CleanUpFunction(line.trim().split("\\s+")));
                        methodsNames.add(quote(line.trim()));
                    } else {
                        methodsNames.add("No methods found");
                    }
                }
            }

            if (methodsNames.isEmpty()) {
                methodsNames.add("No methods found");
            } else {
                return methodsNames;
            }
            return methodsNames;
        } finally {
            if (scanner != null)
                scanner.close();
        }
    }

    private static List<String> ProcessCleanMethodsAll(List<String> rawMethods) {
        for (String m : rawMethods) {
            //Exclude class declaration
            //TODO:add a list containing all possible types
            if (!m.contains("extends") && (!m.contains("implements") && (m.contains("(")))) {
                cleanMethodsNames.add(m);
            }
        }
        
        return cleanMethodsNames;
    }

    private static String CleanUpFunction(String[] rawFunction) {
        String onlyFunc = "functiondummy";
        for (String m : rawFunction) {
            if (m.contains("(")) {
                String[] split = m.split("\\(")[0].split(" ");
                return split[split.length - 1];
            }
        }

        return onlyFunc;
    }

    private static String ProcessContentExtractedPackage(String contentFile) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(contentFile);
            String regexPkg = "package";
            Pattern patternPkg = Pattern.compile(regexPkg, Pattern.CASE_INSENSITIVE);
            String line = scanner.nextLine();
            
            // process the line
            Matcher matcher = patternPkg.matcher(line);
            while (matcher.find()) {
                if (matcher.group() != null) {
                    System.out.println("find() found the pattern \"" + quote(line.trim()));
                    foundPckg = quote(line.trim());
                } else {
                    foundPckg = "";
                }
            }
            
            try
            {
                if (foundPckg == null || foundPckg.isEmpty())
                    foundPckg = "No Package Found";

            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null,
                        "Error - no package was found in the selected class: " + e);
            } finally {
                scanner.close();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Error" + e);
            if (scanner != null)
                scanner.close();
        } finally {
            if (scanner != null)
                scanner.close();
        }
        
        return foundPckg;
    }

    private static String quote(String aText) {
        String QUOTE = "'";
        return QUOTE + aText + QUOTE;
    }
}
