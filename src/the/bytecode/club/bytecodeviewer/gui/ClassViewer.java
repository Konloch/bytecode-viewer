package the.bytecode.club.bytecodeviewer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;

import static javax.swing.ScrollPaneConstants.*;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.ParagraphView;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.jhe.hexed.JHexEditor;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.decompilers.bytecode.ClassNodeDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.java.CFRDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.java.FernFlowerDecompiler;
import the.bytecode.club.bytecodeviewer.decompilers.java.ProcyonDecompiler;

/**
 * This represents the opened classfile.
 * 
 * @author Konloch
 * @author WaterWolf
 *
 */

public class ClassViewer extends JPanel {
	
	private boolean sourcePane = false, bytecodePane = false, hexPane = false;

	  /**
	   * Whoever wrote this function, THANK YOU!
	   * @param splitter
	   * @param proportion
	   * @return
	   */
	  public static JSplitPane setDividerLocation(final JSplitPane splitter,
	      final double proportion) {
	    if (splitter.isShowing()) {
	      if(splitter.getWidth() > 0 && splitter.getHeight() > 0) {
	        splitter.setDividerLocation(proportion);
	      }
	      else {
	        splitter.addComponentListener(new ComponentAdapter() {
	          @Override
	          public void componentResized(ComponentEvent ce) {
	            splitter.removeComponentListener(this);
	            setDividerLocation(splitter, proportion);
	          }
	        });
	      }
	    }
	    else {
	      splitter.addHierarchyListener(new HierarchyListener() {
	        @Override
	        public void hierarchyChanged(HierarchyEvent e) {
	          if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 &&
	              splitter.isShowing()) {
	            splitter.removeHierarchyListener(this);
	            setDividerLocation(splitter, proportion);
	          }
	        }
	      });
	    }
	    return splitter;
	  }

	private static final long serialVersionUID = -8650495368920680024L;
	ArrayList<MethodData> lnData = new ArrayList<MethodData>();
    String name;
    ClassNode cn;
    JSplitPane sp;
    JSplitPane sp2;
    public JPanel bytePanel = new JPanel(new BorderLayout());
    public JPanel decompPanel = new JPanel(new BorderLayout());

    public ClassViewer(final String name, final ClassNode cn) {
    	sourcePane = BytecodeViewer.viewer.sourcePane.isSelected();
    	bytecodePane = BytecodeViewer.viewer.bytecodePane.isSelected();
    	hexPane = BytecodeViewer.viewer.hexPane.isSelected();
        this.name = name;
        this.cn = cn;
        this.setName(name);
        this.setLayout(new BorderLayout());

        this.sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, decompPanel, bytePanel);
        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        JHexEditor hex = new JHexEditor(cw.toByteArray());
        JScrollPane penis;
        if(hexPane) {
        	penis = new JScrollPane(hex);
        } else {
        	penis = new JScrollPane();
        }
        penis.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        this.sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, penis);
        this.add(sp2, BorderLayout.CENTER);

        hex.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
    	hex.setSize(0, Integer.MAX_VALUE);
    	resetDivider();
		BytecodeViewer.viewer.setIcon(true);
		
        startPaneUpdater();
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
               resetDivider();
            }
        });
    }
    
    public void resetDivider() {
    	if(!sourcePane) {
            sp.setResizeWeight(0);
    	} else if(!bytecodePane) {
            sp.setResizeWeight(1);
    	} else {
            sp.setResizeWeight(0.5);
    	}
        if(hexPane) {
        	if(!sourcePane && !bytecodePane)
        		sp2 = setDividerLocation(sp2, 0);
        	else
        		sp2 = setDividerLocation(sp2, 0.7);
        } else {
            sp2 = setDividerLocation(sp2, 1);
        }
    }

    static FernFlowerDecompiler ff_dc = new FernFlowerDecompiler();
    static ProcyonDecompiler proc_dc = new ProcyonDecompiler();
    static CFRDecompiler cfr_dc = new CFRDecompiler();
    PaneUpdaterThread t;
    public void startPaneUpdater() {
        t = new PaneUpdaterThread() {
	        String s = "";
			@Override
			public void doShit() {
		        final String b = ClassNodeDecompiler.decompile(cn);
		        
		        if(BytecodeViewer.viewer.sourcePane.isSelected()) {
			        if(BytecodeViewer.viewer.decompilerGroup.isSelected(BytecodeViewer.viewer.fernflowerDec.getModel()))
			        	s = ff_dc.decompileClassNode(cn);
			        else if(BytecodeViewer.viewer.decompilerGroup.isSelected(BytecodeViewer.viewer.procyonDec.getModel()))
			        	s = proc_dc.decompileClassNode(cn);
			        else if(BytecodeViewer.viewer.decompilerGroup.isSelected(BytecodeViewer.viewer.cfrDec.getModel()))
			        	s = cfr_dc.decompileClassNode(cn);
		        }
		        
		        SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        RSyntaxTextArea bytecodeArea = new RSyntaxTextArea();
                        bytecodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        bytecodeArea.setCodeFoldingEnabled(true);
                        bytecodeArea.setAntiAliasingEnabled(true);
                        RTextScrollPane bytecodeSPane = new RTextScrollPane(bytecodeArea);
                        bytecodeArea.setText(b);
                    	
                        RSyntaxTextArea sourcecodeArea = new RSyntaxTextArea();
                        sourcecodeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                        sourcecodeArea.setCodeFoldingEnabled(true);
                        sourcecodeArea.setAntiAliasingEnabled(true);
                        RTextScrollPane sourcecodeSPane = new RTextScrollPane(sourcecodeArea);
                        sourcecodeArea.setText(s);

				        if(BytecodeViewer.viewer.bytecodePane.isSelected())
				        	bytePanel.add(bytecodeSPane);
				        if(BytecodeViewer.viewer.sourcePane.isSelected())
				        	decompPanel.add(sourcecodeSPane);

				        bytecodeArea.setCaretPosition(0);
				        sourcecodeArea.setCaretPosition(0);
						
						BytecodeViewer.viewer.setIcon(false);
                    }
		        });
			}
        	
        };
        t.start();
    }

    public static class MethodData {
        public String name, desc;
        public int srcLN, bytecodeLN;

        @Override
        public boolean equals(final Object o) {
            return equals((MethodData) o);
        }

        public boolean equals(final MethodData md) {
            return this.name.equals(md.name) && this.desc.equals(md.desc);
        }

        public String constructPattern() {
            final StringBuffer pattern = new StringBuffer();
            pattern.append(name + " *\\(");
            final org.objectweb.asm.Type[] types = org.objectweb.asm.Type
                    .getArgumentTypes(desc);
            pattern.append("(.*)");
            for (int i = 0; i < types.length; i++) {
                final Type type = types[i];
                final String clazzName = type.getClassName();
                pattern.append(clazzName.substring(clazzName.lastIndexOf(".") + 1)
                        + "(.*)");
            }
            pattern.append("\\) *\\{");
            return pattern.toString();
        }
    }

    class WrapEditorKit extends StyledEditorKit {
		private static final long serialVersionUID = 1719109651258205346L;
		ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    class WrapColumnFactory implements ViewFactory {
        public View create(final Element elem) {
            final String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ParagraphElementName))
                    return new NoWrapParagraphView(elem);
                else if (kind.equals(AbstractDocument.SectionElementName))
                    return new BoxView(elem, View.Y_AXIS);
                else if (kind.equals(StyleConstants.ComponentElementName))
                    return new ComponentView(elem);
                else if (kind.equals(StyleConstants.IconElementName))
                    return new IconView(elem);
            }

            // default to text display
            return new LabelView(elem);
        }
    }
    
    public class NoWrapParagraphView extends ParagraphView {
        public NoWrapParagraphView(final Element elem) {
            super(elem);
        }

        @Override
        public void layout(final int width, final int height) {
            super.layout(Short.MAX_VALUE, height);
        }

        @Override
        public float getMinimumSpan(final int axis) {
            return super.getPreferredSpan(axis);
        }
    }


}
