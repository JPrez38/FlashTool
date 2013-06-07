import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.GridLayout;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.util.List;
import java.awt.Container;
import java.awt.*;
import java.awt.Dimension;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JToolBar;
import javax.swing.JFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedInputStream;

class Flash extends JFrame implements ActionListener, WindowListener{
	  private static final int  BUFFER_SIZE = 4096;
	  private static JTextArea textArea;
    private static JProgressBar progressBar;
	  String outputZip = "OuyaBuild.zip";
    private JButton startButton;
    Update update;
    private JPanel contentPane;
    String operatingSystem;
    public static String OS = System.getProperty("os.name").toLowerCase();
	public boolean deviceFound;
	public boolean flashing;

    public Flash() {
        init();
    }

    public void init() {
        addWindowListener(this);
        setTitle("Ouya Flash Tool\n");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 640, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        progressBar = new JProgressBar();

        JButton btnNewButton = new JButton("Flash");
        btnNewButton.setActionCommand("Flash");
        btnNewButton.addActionListener(this);


        JButton btnKill = new JButton("kill");
        btnKill.setActionCommand("kill");
        btnKill.addActionListener(this);
        textArea = new JTextArea(20,25);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        ImageIcon image = new ImageIcon("Ouya.png");
        JLabel label = new JLabel("", image, JLabel.CENTER);

        JCheckBox chckbxClearCache = new JCheckBox("Clear Cache");

        JCheckBox chckbxClearUserData = new JCheckBox("Clear User Data");
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
          gl_contentPane.createParallelGroup(Alignment.TRAILING)
            .addGroup(gl_contentPane.createSequentialGroup()
              .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_contentPane.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 359, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                  .addComponent(btnKill))
                .addGroup(gl_contentPane.createSequentialGroup()
                  .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_contentPane.createSequentialGroup()
                      .addGap(42)
                      .addComponent(label, GroupLayout.PREFERRED_SIZE, 305, GroupLayout.PREFERRED_SIZE))
                    .addGroup(gl_contentPane.createSequentialGroup()
                      .addGap(28)
                      .addComponent(chckbxClearCache)
                      .addGap(18)
                      .addComponent(chckbxClearUserData)))
                  .addGap(35)
                  .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                    .addComponent(progressBar, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))))
              .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
          gl_contentPane.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_contentPane.createSequentialGroup()
              .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
              .addGap(47))
            .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
              .addGap(37)
              .addComponent(label, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
              .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                .addComponent(chckbxClearCache)
                .addComponent(chckbxClearUserData))
              .addGap(18)
              .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                .addComponent(btnNewButton)
                .addComponent(btnKill)))
        );
        contentPane.setLayout(gl_contentPane);

        setSize(680, 480);
        setVisible(true);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        if ("Flash" == e.getActionCommand()) {
            try {
				if (flashing == false) {
					flashing =true;
					(update = new Update()).execute();
				}
            } catch (Exception t){}
        }
        if ("kill" == e.getActionCommand()) {
            int confirmed = JOptionPane.showConfirmDialog(null,
            "Warning! Quiting during flash could endanger hardware! \n" +
            "Do you still wish to quit?", "Confirm Quit",
            JOptionPane.YES_NO_OPTION);

            if (confirmed == JOptionPane.YES_OPTION) {
                dispose();
            }
			else {}
        }
    }

    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

    public void windowClosing(WindowEvent winEvt) {
        int confirmed = JOptionPane.showConfirmDialog(null,
          "Warning! Quiting during flash could endanger hardware! \n" +
          "Do you still wish to quit?", "Confirm Quit",
        JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            dispose();
        }
		else {}
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Flash();
            }
        });
    }

    private static class Log {
        private final String message;
        Log(String s){
            message = s;
        }
    }

    private class Update extends SwingWorker<Void, Log> {
        @Override
        protected Void doInBackground() {
            try {
                checkBuild();
                publish(new Log("Flashing Device. Please do not interupt!"));
				//if (deviceFound){
				flashDevice();
				//}

				progressBar.setValue(100);
				publish(new Log("Flashing Complete, you may now disconnect console"));
				Thread.sleep(2000);
				flashing=false;
            } catch (Exception t){}
            return null;
        }

        protected void process(List<Log> logs){
            for (Log log: logs){
                textArea.append(log.message + " \n");
                System.out.print(log.message + " \n");
            }
        }

        public void checkBuild() {
            try {
                getOperatingSystem();
                System.out.println(operatingSystem);
                publish(new Log("Searching for local build"));
                File localBuild = new File("./release-user");
                if (!localBuild.exists()){
                    publish(new Log("local build not found"));
                    getZip();
                }
                else {
                    publish(new Log("local build found"));
                    progressBar.setValue(50);
                }
                addDevice();
            } catch(Exception e){}
        }

        public void editINIFile() {
            try {
                String data = "0x2836";
                boolean f =false;
                String d = System.getProperty("user.home");
                System.out.println(d);

                String x = d + "/.android";
                File android = new File(x);
                android.mkdirs();
                String usb = android + "/adb_usb.ini";


                File file = new File(usb);
                if (!file.exists()) {
                    file.createNewFile();
                }


                Scanner readFile = new Scanner(file);
                readFile.useDelimiter("\\s+");
                while(readFile.hasNext()) {
                    String z= readFile.next();
                    if(data.equals(z))
                    {
                        f=true;
                    }
                }
                if (!f){
                    FileWriter fileWriter = new FileWriter(file,true);
                    BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
                    bufferWritter.write(data);
                    bufferWritter.close();
                    System.out.println("done");
                }
            } catch (Exception e) {}
        }

        public void addDevice(){
            publish(new Log("Adding device to computer..."));
            if (isMac()) {
                try {
                    editINIFile();
                }catch (Exception e){}
            }
            if (isWindows()){
                try {
                    String d = System.getProperty("user.home");

                    String path = new File(".").getCanonicalPath();
                    String buildPath =  path + "\\release-user";

                    String kill = path + "\\adb.exe kill-server";
                    String start = path + "\\adb.exe devices";
					//System.out.println(kill + "\n" + start);
					//String kill = "adb kill-server";
                    //String start = "adb devices";
                    //String append = "echo 0x2836 >> \"" + "%USERPROFILE%" +"\\.android\\adb_usb.ini";
					String installDrivers;
                    if (System.getProperty("sun.arch.data.model").equals("32")){
						 installDrivers = path + "\\NewDrivers\\dpinst32.exe";
					}
					else {
						installDrivers = path + "\\NewDrivers\\dpinst.exe /c";
					}
					System.out.println(installDrivers);
					Runtime load = Runtime.getRuntime();
					try {
						load.exec(installDrivers);
                    } catch (Exception ex){
						ex.printStackTrace();
						System.out.println("failed");
					}
                    //runProcess(kill,false);
					Thread.sleep(3000);
                    editINIFile();
                    //runProcess(append);
                    runProcess(start,false);
                    //runProcess(installDrivers);

                }catch (Exception e){}
            }
			publish(new Log("Device added"));
        }

		public void runProcess(String s){
			runProcess(s,true);
		}

        public void runProcess(String s, Boolean print) {
            try {
                Process p = Runtime.getRuntime().exec(s);
				while(print){
					BufferedReader stdInput = new BufferedReader(new
					  InputStreamReader(p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new
						InputStreamReader(p.getErrorStream()));

					// read the output from the command
					while ((s = stdInput.readLine()) != null) {
						publish(new Log(s));
						System.out.println("hang");
					}

					// read any errors from the attempted command
					while ((s = stdError.readLine()) != null) {
						publish(new Log(s));

					}

					stdInput.close();
					stdError.close();
				}
				p.waitFor();
            } catch (Exception e){}

        }

        public void getOperatingSystem() {
            if (isWindows()) {
                operatingSystem="Windows";
            } else if (isMac()){
                operatingSystem="Mac";
            } else {
                operatingSystem="Other";
            }
        }

        public boolean isWindows() {
            return (OS.indexOf("win") >= 0);
        }

        public boolean isMac() {
            return (OS.indexOf("mac") >= 0);
        }

        public void getZip() throws IOException {
            // Open file streams and get channels for them.
            publish(new Log("Getting file from web server"));
            URL website = new URL("http://10.0.0.11:8080/RC-OUYA-1.0.264-r1_user.zip");
            ReadableByteChannel in = Channels.newChannel(website.openStream());
            WritableByteChannel out;

            out = new FileOutputStream(outputZip).getChannel();

            copy(in, out);

            File zip = new File(outputZip);
            File output = new File(".");
            publish(new Log("Extracting zip file"));
            extract(zip,output);
        }

        // Read all available bytes from one channel and copy them to the other.
        public void copy(ReadableByteChannel in, WritableByteChannel out) throws IOException {
            // buffer holds blocks of copied blocks for transfer.
            ByteBuffer buffer = ByteBuffer.allocateDirect(32 * 1024);

            // loop till all bytes read and buffer empty
            while (in.read(buffer) != -1 || buffer.position() > 0) {
                buffer.flip();
                // writes bytes to output channel
                out.write(buffer);
                buffer.compact();
            }
        }

        private void extractFile(ZipInputStream in, File outdir, String name) throws IOException {
            byte[] buffer = new byte[BUFFER_SIZE];
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir,name)));
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            out.close();
        }

        //creates new directory inside zip when needed
        private void mkdirs(File outdir,String path) {
            File d = new File(outdir, path);
            if( !d.exists() )
                d.mkdirs();
        }

        private String dirpart(String name){
            int s = name.lastIndexOf( File.separatorChar );
            return s == -1 ? null : name.substring( 0, s );
        }

      /*
       * Extract zipfile to outdir with complete directory structure
       */
        public void extract(File zipfile, File outdir) {
            try {
                ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
                ZipEntry entry;
                String name, dir;
                while ((entry = zin.getNextEntry()) != null) {
                    name = entry.getName();
                    if( entry.isDirectory()) {
                        mkdirs(outdir,name);
                        continue;
                    }
                    dir = dirpart(name);
                    if( dir != null )
                        mkdirs(outdir,dir);

                    extractFile(zin, outdir, name);
                }
                zin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void flashDevice() {
            try {
				Thread.sleep(2000);
				String[] removeZip;
                String slash;
				String extension;
                if (isWindows()) {
                    slash = "\\";
					removeZip = new String[]{"del ", outputZip};
					extension = ".exe";
                }
                else {
                    slash="/";
					removeZip = new String[]{"rm ", outputZip};
					extension = "";
                }

                String path = new File(".").getCanonicalPath();
                String buildPath =  path + slash+ "release-user";
				System.out.println(buildPath);
				String[] waitForDevice = new String[]{path,slash, "adb", extension, " wait-for-device"};
                String[] devices = new String[]{ path, slash, "adb",extension, " devices"};
                String[] rebootBootloader = new String[]{ path,slash,"adb",extension, " reboot", " bootloader"};
                String[] flashSystem = new String[]{ path, slash,"fastboot",extension," flash", " system ", buildPath + slash + "system.img"};
                String[] fastBootRebootBootLoader = new String[]{ path, slash,"fastboot",extension, " reboot-bootloader"};

                String[] sleep = new String[]{"echo"," taking a nap"};

                String[] formatCache = new String[]{ path,slash, "fastboot",extension, " format", " cache"};
                String[] formatUserData = new String[]{ path,slash, "fastboot",extension," format", " userdata"};
                String[] fastBootReboot = new String[]{ path,slash, "fastboot",extension, " reboot"};

                //String[] removeBuild = new String[]{"rm ", "-rf ", "release-user"};

                String[][] commands = new String[][]{waitForDevice,rebootBootloader,flashSystem,fastBootRebootBootLoader,sleep,
                  formatCache,formatUserData,fastBootReboot,removeZip};
				//String[][] commands = new String[][]{waitForDevice,rebootBootloader,fastBootReboot};


                for (int i = 0; i < commands.length; i++){
                    String s = null;
                    String a ="";
                    for (int j = 0;j<commands[i].length ;j++) {
                        a+=commands[i][j];
                    }
					if (commands[i]==sleep){
						Thread.sleep(500);
					}
                    //prints the command
                    //System.out.println(a);
                    /*Process p = Runtime.getRuntime().exec(a);

                    BufferedReader stdInput = new BufferedReader(new
                      InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    while ((s = stdInput.readLine()) != null) {
                        publish(new Log(s));
                    }

                    // read any errors from the attempted command
                    while ((s = stdError.readLine()) != null) {
                        publish(new Log(s));
                    }*/
                    runProcess(a);
                }
            }catch (Exception e){}
                //System.exit(0);
        }
    }
}
