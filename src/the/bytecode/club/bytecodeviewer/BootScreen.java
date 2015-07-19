package the.bytecode.club.bytecodeviewer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.io.FileUtils;

/**
 * Automatic updater for BCV libraries
 * 
 * @author Konloch
 *
 */

public class BootScreen extends JFrame {

	private static final long serialVersionUID = -1098467609722393444L;

	private static boolean FIRST_BOOT = false;

	private JProgressBar progressBar = new JProgressBar();

	public BootScreen() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImages(Resources.iconList);

		int i = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		if(i >= 840)
			setSize(new Dimension(600, 800));
		else if(i >= 640)
			setSize(new Dimension(500, 600));
		else if(i >= 440)
			setSize(new Dimension(400, 400));
		else
			setSize(Toolkit.getDefaultToolkit().getScreenSize());

		setTitle("Bytecode Viewer Boot Screen - Starting Up");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 24;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(scrollPane, gbc_scrollPane);

		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditorKit(new HTMLEditorKit());

		editorPane.setText(convertStreamToString(BytecodeViewer.class.getClassLoader().getResourceAsStream("resources/intro.html")));

		scrollPane.setViewportView(editorPane);

		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 24;
		getContentPane().add(progressBar, gbc_progressBar);
		this.setLocationRelativeTo(null);
	}

	static String convertStreamToString(java.io.InputStream is) throws IOException {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		String string =  s.hasNext() ? s.next() : "";
		is.close();
		s.close();
		return string;
	}

	public void DO_FIRST_BOOT(String args[], int CLI) {
		if(CLI == -1)
			return;

		if(CLI == 1)
			this.setVisible(true);

		if(FIRST_BOOT)
			return;

		FIRST_BOOT = true;

		setTitle("Bytecode Viewer Boot Screen - Checking Libraries...");
		System.out.println("Checking Libraries...");

		try {
			int completedCheck = 0;
			setTitle("Bytecode Viewer Boot Screen - Checking Krakatau...");
			System.out.println("Checking krakatau");

			File krakatauZip = null;
			for(File f : new File(BytecodeViewer.libsDirectory).listFiles()) {
				if(f.getName().toLowerCase().startsWith("krakatau-")) {
					BytecodeViewer.krakatauVersion = f.getName().split("-")[1].split("\\.")[0];
					krakatauZip = f;
				}
			}

			for(File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
				if(f.getName().toLowerCase().startsWith("krakatau_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.krakatauVersion)) {
					setTitle("Bytecode Viewer Boot Screen - Removing Outdated " + f.getName() + "...");
					System.out.println("Removing oudated " + f.getName());
					try {
						FileUtils.deleteDirectory(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			BytecodeViewer.krakatauWorkingDirectory = BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion + BytecodeViewer.fs + "Krakatau-master";		
			File krakatauDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "krakatau_" + BytecodeViewer.krakatauVersion);
			if(!krakatauDirectory.exists()) {
				try {
					setTitle("Bytecode Viewer Boot Screen - Updating to "+krakatauDirectory.getName()+"...");
					ZipUtils.unzipFilesToPath(krakatauZip.getAbsolutePath(), krakatauDirectory.getAbsolutePath());
					System.out.println("Updated to krakatau v" + BytecodeViewer.krakatauVersion);
				} catch(Exception e) {
					BytecodeViewer.showMessage("ERROR: There was an issue unzipping Krakatau decompiler (possibly corrupt). Restart BCV."+BytecodeViewer.nl+
							"If the error persists contact @Konloch.");
					new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
					krakatauZip.delete();
				}
			}

			completedCheck++;
			progressBar.setValue(completedCheck);


			setTitle("Bytecode Viewer Boot Screen - Checking Enjarify...");
			System.out.println("Checking enjarify");

			File enjarifyZip = null;
			for(File f : new File(BytecodeViewer.libsDirectory).listFiles()) {
				if(f.getName().toLowerCase().startsWith("enjarify-")) {
					BytecodeViewer.enjarifyVersion = f.getName().split("-")[1].split("\\.")[0];
					enjarifyZip = f;
				}
			}

			for(File f : new File(BytecodeViewer.getBCVDirectory()).listFiles()) {
				if(f.getName().toLowerCase().startsWith("enjarify_") && !f.getName().split("_")[1].split("\\.")[0].equals(BytecodeViewer.enjarifyVersion)) {
					setTitle("Bytecode Viewer Boot Screen - Removing Outdated " + f.getName() + "...");
					System.out.println("Removing oudated " + f.getName());
					try {
						FileUtils.deleteDirectory(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			BytecodeViewer.enjarifyWorkingDirectory = BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion + BytecodeViewer.fs + "enjarify-master";		
			File enjarifyDirectory = new File(BytecodeViewer.getBCVDirectory() + BytecodeViewer.fs + "enjarify_" + BytecodeViewer.enjarifyVersion);
			if(!enjarifyDirectory.exists()) {
				try {
					setTitle("Bytecode Viewer Boot Screen - Updating to "+enjarifyDirectory.getName()+"...");
					ZipUtils.unzipFilesToPath(enjarifyZip.getAbsolutePath(), enjarifyDirectory.getAbsolutePath());
					System.out.println("Updated to enjarify v" + BytecodeViewer.enjarifyVersion);
				} catch(Exception e) {
					BytecodeViewer.showMessage("ERROR: There was an issue unzipping enjarify (possibly corrupt). Restart BCV."+BytecodeViewer.nl+
							"If the error persists contact @Konloch.");
					new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e);
					enjarifyZip.delete();
				}
			}
			completedCheck++;
			progressBar.setValue(completedCheck);

			setTitle("Bytecode Viewer Boot Screen - Booting!");

		} catch(Exception e) {
			Settings.saveGUI();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			e.printStackTrace();
			new the.bytecode.club.bytecodeviewer.api.ExceptionUI("Bytecode Viewer ran into an error while booting, trying to force it anyways."+ BytecodeViewer.nl+ BytecodeViewer.nl+
					"Please ensure you have an active internet connection and restart BCV. If this presists please visit http://github.com/Konloch/Bytecode-Viewer or http://bytecodeviewer.com"+ BytecodeViewer.nl + BytecodeViewer.nl + sw.toString());
		}

		setTitle("Bytecode Viewer Boot Screen - Finished");

		if(CLI == 1)
			BytecodeViewer.BOOT(args, false);
		else {
			BytecodeViewer.BOOT(args, true);
			CommandLineInput.executeCommandLine(args);
		}

		this.setVisible(false);
	}
}