import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import java.security.MessageDigest;
import java.io.InputStream;
import java.math.BigInteger;


class Flash extends JFrame implements ActionListener, WindowListener{
	private static final int  BUFFER_SIZE = 4096;
	private static JTextArea textArea;
    private static JProgressBar progressBar;
    private JPanel contentPane;
    String OS = System.getProperty("os.name").toLowerCase();
    boolean flashing;
    JCheckBox chckbxClearCache;
    JCheckBox chckbxClearUserData;
    String outputZip = "OuyaBuild.zip";
    Update update;
    public long flashStartTime;


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

        
        ImageIcon image = new ImageIcon(".images/Ouya.png");
        JLabel label;
        File imageLocation = new File(".images/Ouya.png");
        if (imageLocation.exists()){
            label = new JLabel("", image, JLabel.CENTER);
        }
        else {
            label = new JLabel("",JLabel.CENTER);
            JOptionPane.showMessageDialog(null, "The jar file is not in the appropriate folder \n" +
                "Please place the jar file back in the Flash folder or redownload the Flash tool", "alert", JOptionPane.ERROR_MESSAGE); 
        }
        chckbxClearCache = new JCheckBox("Clear Cache");
        chckbxClearCache.setSelected(true);

        chckbxClearUserData = new JCheckBox("Clear User Data");
        chckbxClearUserData.setSelected(true);

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
                      .addGap(30)
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
            if (Math.abs(System.currentTimeMillis()-flashStartTime) >=300000) {
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
                flashStartTime=0;
                long startTime = System.currentTimeMillis();
                publish(new Log("Preparing to Flash. Process may take several minutes.\nWARNING:" + 
                    " Please do not turn off the console or stop the flashing process"));
                checkBuild();
                addDevice();
                publish(new Log("Flashing Device. Please do not interupt!"));
                progressBar.setValue(50);
				flashDevice();

				progressBar.setValue(100);
				publish(new Log("Flashing Complete! You may now disconnect your console"));
                long finishTime = System.currentTimeMillis();
                publish(new Log("[TOTAL TIME: " + (finishTime-startTime)/1000.0 + "s]"));
                JOptionPane.showMessageDialog(null, "Flashing complete! You may now disconnect console.", "Complete", JOptionPane.INFORMATION_MESSAGE);
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

        public void runProcess(String s){
            runProcess(s,true);
        }

        public void runProcess(String s, Boolean print) {
            try {
                Thread.sleep(500);
                Process p = Runtime.getRuntime().exec(s);
                while(print){
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

                    }

                    stdInput.close();
                    stdError.close();
                }
                p.waitFor();
            } catch (Exception e){}

        }

        public boolean isWindows() {
            return (OS.indexOf("win") >= 0);
        }

        public boolean isMac() {
            return (OS.indexOf("mac") >= 0);
        }

        public void checkBuild() {
            try {
                publish(new Log("Searching for local build..."));
                File localBuild = new File("./release-user");
                File localZip = new File("./OuyaBuild.zip");
                if (!localBuild.exists()){
                    publish(new Log("Local build not found"));
                    getZip();
                }
                else if (localBuild.exists() && needsNewMD5()) {
                    publish(new Log("Local build is outdated"));
                    delete(localBuild);
                    delete(localZip);
                    getZip();
                }
                else {
                    publish(new Log("Local build found"));
                }
                progressBar.setValue(40);
                
            } catch(Exception e){}
        }

        public void delete(File file){

            // Check if file is directory/folder
            if(file.isDirectory()) {
                // Get all files in the folder
                File[] files=file.listFiles();
                for(int i=0;i<files.length;i++) {
                    // Delete each file in the folder
                    delete(files[i]);
                }
                // Delete the folder
                file.delete();
            }
            else {
                // Delete the file if it is not a folder
                file.delete();
            }
        }

        public boolean needsNewMD5(){
            try {
                String s = readUrl("http://rabid.ouya.tv/api/v1/retail_firmware");
                JSONObject json = new JSONObject(s);
                JSONArray jArr = json.getJSONArray("result");
                JSONObject jObj = jArr.getJSONObject(0);
                String webMD5 = jObj.getString("md5sum");

                if (!webMD5.equals(localMD5())){
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (Exception e){
                return false;
            }
        }

        public String localMD5(){
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                File f = new File("./OuyaBuild.zip");
                InputStream is = new FileInputStream(f);                
                byte[] buffer = new byte[8192];
                int read = 0;
                try {
                    while( (read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }       
                    byte[] md5sum = digest.digest();
                    BigInteger bigInt = new BigInteger(1, md5sum);
                    String output = bigInt.toString(16);
                    return output;
                }
                catch(IOException e) {
                    throw new RuntimeException("Unable to process file for MD5", e);
                }
                finally {
                    try {
                        is.close();
                    }
                    catch(IOException e) {
                        throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
                    }
                } 
            } catch (Exception ex) {}
            return "";

        }

        private String readUrl(String urlString) throws Exception {
            BufferedReader reader = null;
            try {
                URL url = new URL(urlString);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer buffer = new StringBuffer();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1) {
                    buffer.append(chars, 0, read); 
                }
                    
                return buffer.toString();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }

        /* Method is used to add the device to 
        *  the computer
        */
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
					String installDrivers;
                    if (System.getProperty("sun.arch.data.model").equals("32")){
						 installDrivers = path + "\\.NewDrivers\\dpinst32.exe";
					}
					else {
						installDrivers = path + "\\.NewDrivers\\dpinst.exe";

					}
                    publish(new Log("Installing Windows drivers..."));
                    
					Runtime load = Runtime.getRuntime();
					try {
						load.exec(installDrivers);
                    } catch (Exception ex){
						ex.printStackTrace();
						publish(new Log("Failed to install drivers"));
					}
                    Process p = Runtime.getRuntime().exec(installDrivers);
					Thread.sleep(3000);
                    editINIFile();

                    runProcess(start,false);

                }catch (Exception e){}
            }
			publish(new Log("Device added"));
        }

        /* This method edits the .ini file to append the device ID so it can
        *  be found by android
        */
        public void editINIFile() {
            try {
                String data = "0x2836";
                boolean deviceNumberFound =false;
                String userHome = System.getProperty("user.home");

                String dotAndroid = userHome + "/.android";
                File android = new File(dotAndroid);
                android.mkdirs();
                String usb = android + "/adb_usb.ini";

                //creates the .ini file if it doesn't exist
                File file = new File(usb);
                if (!file.exists()) {
                    file.createNewFile();
                }

                Scanner readFile = new Scanner(file);
                readFile.useDelimiter("\\s+");

                // Searches for the device idea to see if its in document
                while(readFile.hasNext()) {
                    String fileWord= readFile.next();
                    if(data.equals(fileWord))
                    {
                        deviceNumberFound=true;
                    }
                }

                if (!deviceNumberFound){
                    FileWriter fileWriter = new FileWriter(file,true);
                    BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
                    bufferWritter.write(data);
                    bufferWritter.close();
                }
            } catch (Exception e) {}
        }

        public void getZip() throws IOException {
            // Open file streams and get channels for them.
            
            try {
                String s = readUrl("http://rabid.ouya.tv/api/v1/retail_firmware");
                JSONObject json = new JSONObject(s);
                JSONArray jArr = json.getJSONArray("result");
                JSONObject jObj = jArr.getJSONObject(0);


                String web = jObj.getString("url");

                publish(new Log("Getting files from web server"));
                URL website = new URL(web);
                //URL website = new URL("http://10.0.0.11:8080/RC-OUYA-1.0.264-r1_user.zip");
                ReadableByteChannel in = Channels.newChannel(website.openStream());
                WritableByteChannel out;

                out = new FileOutputStream(outputZip).getChannel();

                copy(in, out);

                File zip = new File(outputZip);
                File output = new File(".");
                publish(new Log("Extracting zip file"));
                extract(zip,output);

            } catch (Exception e) { 
                e.printStackTrace();
            }
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
                flashStartTime = System.currentTimeMillis();
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

                //list of bash commands to be executed
				String[] waitForDevice = new String[]{path,slash, ".boot",slash,"adb", extension, " wait-for-device"};
                String[] devices = new String[]{ path, slash, ".boot",slash,"adb",extension, " devices"};
                String[] rebootBootloader = new String[]{ path,slash,".boot", slash, "adb",extension, " reboot", " bootloader"};
                String[] flashBoot = new String[]{ path, slash,".boot",slash ,"fastboot",extension," flash", " boot ", buildPath + slash + "boot.img"};
                String[] flashSystem = new String[]{ path, slash,".boot",slash ,"fastboot",extension," flash", " system ", buildPath + slash + "system.img"};
                String[] flashBootloader = new String[]{ path, slash,".boot",slash ,"fastboot",extension," flash", " bootloader ", buildPath + slash + "bootloader.bin"};
                String[] fastBootRebootBootLoader = new String[]{ path, slash,".boot",slash,"fastboot",extension, " reboot-bootloader"};

                String[] sleep = new String[]{"echo"," taking a nap"};

                String[] flashRecovery = new String[]{ path, slash,".boot",slash ,"fastboot",extension," flash", " recovery ", buildPath + slash + "recovery.img"};
                String[] formatCache = new String[]{ path,slash,".boot",slash, "fastboot",extension, " format", " cache"};
                String[] formatUserData = new String[]{ path,slash,".boot",slash, "fastboot",extension," format", " userdata"};
                String[] fastBootReboot = new String[]{ path,slash,".boot",slash, "fastboot",extension, " reboot"};
                
                String[][] commands;

                if (chckbxClearCache.isSelected() && chckbxClearUserData.isSelected()){
                    commands = new String[][]{waitForDevice,rebootBootloader,flashBoot,flashSystem,flashBootloader,
                        fastBootRebootBootLoader,sleep,flashRecovery,formatCache,formatUserData,fastBootReboot};
                } else if (chckbxClearCache.isSelected() && !chckbxClearUserData.isSelected()){
                    commands = new String[][]{waitForDevice,rebootBootloader,flashBoot,flashSystem,flashBootloader,
                        fastBootRebootBootLoader,sleep,flashRecovery,formatCache,fastBootReboot};
                } else if (chckbxClearUserData.isSelected() && !chckbxClearCache.isSelected()) {
                    commands = new String[][]{waitForDevice,rebootBootloader,flashBoot,flashSystem,flashBootloader,
                        fastBootRebootBootLoader,sleep,flashRecovery,formatUserData,fastBootReboot};
                } else {
                    commands = new String[][]{waitForDevice,rebootBootloader,flashBoot,flashSystem,flashBootloader,
                        fastBootRebootBootLoader,sleep,flashRecovery,fastBootReboot};
                }
				//String[][] commands = new String[][]{waitForDevice,rebootBootloader,fastBootReboot};
                for (int i = 0; i < commands.length; i++){
                    String currentCommand ="";
                    for (int j = 0;j<commands[i].length ;j++) {
                        currentCommand+=commands[i][j];
                    }
					if (commands[i]==sleep){
						Thread.sleep(500);
                        progressBar.setValue(75);
					}
                    if (commands[i]==formatCache){
                        progressBar.setValue(85);
                    }
                    if (commands[i]==formatUserData){
                        progressBar.setValue(90);
                    }

                    //System.out.println(currentCommand);
                    runProcess(currentCommand);
                }
            }catch (Exception e){}
        }
    }
}
