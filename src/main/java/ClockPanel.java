import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.Timer;

class ClockPanel extends JPanel implements ActionListener{
    private int radius;
    private int xh, yh, xm, ym, xs, ys;
    private int minutes, hours, seconds;
    private final float HOURS_WIDTH = 3.0F, MINUTES_WIDTH = 2.0F,
            SECONDS_WIDTH = 1.2F, CIRCLE_WIDTH = 1.5F;
    private Color SECONDS_COLOR = new Color(180, 140, 100),
            MINUTES_COLOR = new Color(100, 100, 100),
            HOURS_COLOR = new Color(90, 60, 100),
            HOURS_POINTS_COLOR = new Color(100, 100, 100),
            CIRCLE_COLOR = new Color(230, 230, 130),
            NUMBERS_COLOR = new Color(0, 0, 255);
    private Point hoursPoint;
    private int minutesLength, secondsLength, hoursLength;
    private javax.swing.Timer timer;
    private String today;
    private Calendar calendar;
    private SimpleDateFormat formatter;
    private Formatter fmt;
    private Date currentDate;
    public ClockPanel(int radius){
        hoursLength = (int) ((double) radius * 0.6);
        minutesLength = (int) ((double) radius * 0.8);
        secondsLength = (int) ((double) radius * 0.9);
        hoursPoint = new Point();
        this.radius = radius;
    }
    public void startClock(){
        formatter = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy", Locale.getDefault());
        currentDate = new Date();

        formatter.applyPattern("s");
        try{
            seconds = Integer.parseInt(formatter.format(currentDate));
        } catch (NumberFormatException n){
            seconds = 25;
        }
        formatter.applyPattern("m");
        try{
            minutes = Integer.parseInt(formatter.format(currentDate));
        } catch (NumberFormatException n){
            minutes = 10;
        }
        formatter.applyPattern("h");
        try{
            hours = Integer.parseInt(formatter.format(currentDate));
        } catch (NumberFormatException n){
            hours = 21;
        }

        timer = new Timer(1000, this);
        timer.start();
    }
    public void stopClock(){
        timer.stop();
    }
    public void continueClock(){
        startClock();
    }
    public String getFullDate(){
        fmt = new Formatter();
        calendar = Calendar.getInstance();
        return fmt.format("%1$tA %1$td %1$tB %1$tY",calendar).toString();
    }
    public String twelveHourFormat(){
        fmt = new Formatter();
        calendar = Calendar.getInstance();
        today = fmt.format("%tr",calendar).toString();
        return today;
    }
    public String twentyFourthHourFormat(){
        fmt = new Formatter();
        calendar = Calendar.getInstance();
        today = fmt.format("%tT",calendar).toString();
        return today;
    }
    public void actionPerformed(ActionEvent e){
        repaint();
        seconds++;
        if(seconds == 60){
            seconds = 0;
            minutes++;
            if(minutes == 60){
                minutes = 0;
                hours++;
            }
            if(hours == 13){
                hours = 1;
            }
        }
    }

    public void paintComponent(Graphics g){
        Graphics2D g2D = (Graphics2D) g;
        // use antialialising at visualisation for better quality
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set position of the ends of the hands
        xs = (int) (Math.cos(seconds * Math.PI / 30 - Math.PI / 2) * secondsLength + radius);
        ys = (int) (Math.sin(seconds * Math.PI / 30 - Math.PI / 2) * secondsLength + radius);
        xm = (int) (Math.cos(minutes * Math.PI / 30 - Math.PI / 2) * minutesLength + radius);
        ym = (int) (Math.sin(minutes * Math.PI / 30 - Math.PI / 2) * minutesLength + radius);
        xh = (int) (Math.cos((hours*30 + minutes / 2) * Math.PI / 180 - Math.PI / 2) * hoursLength + radius);
        yh = (int) (Math.sin((hours*30 + minutes / 2) * Math.PI / 180 - Math.PI / 2) * hoursLength + radius);

        // draw circle of the clock
        g.setColor(CIRCLE_COLOR);
        g.fillOval(0, 0, radius * 2, radius * 2);
        g.setColor(NUMBERS_COLOR);
        g2D.setStroke(new BasicStroke(CIRCLE_WIDTH));
        g.drawOval(0, 0, radius * 2, radius * 2);

        // draw hands of clock
        g.setColor(SECONDS_COLOR);
        g2D.setStroke(new BasicStroke(SECONDS_WIDTH));
        g.drawLine(radius, radius, xs, ys);
        g.setColor(MINUTES_COLOR);
        g2D.setStroke(new BasicStroke(MINUTES_WIDTH));
        g.drawLine(radius, radius, xm, ym);
        g.setColor(HOURS_COLOR);
        g2D.setStroke(new BasicStroke(HOURS_WIDTH));
        g.drawLine(radius, radius, xh, yh);

        // draw numbers of circle
        g.setColor(NUMBERS_COLOR);
        g.drawString("12", radius-5, radius-65);
        g.drawString("1", 2 * radius - 45, radius / 3);
        g.drawString("2", 2 * radius - 20, radius * 2 / 3);
        g.drawString("3", 2 * radius - 10, radius + 5);
        g.drawString("4", 2 * radius - 20, radius + 42);
        g.drawString("5", 2 * radius - 45, radius + 65);
        g.drawString("6", radius - 3, 2 * radius - 5);
        g.drawString("7", radius / 2, radius + 65);
        g.drawString("8", radius / 3 - 10, radius + 42);
        g.drawString("9", radius - 75, radius + 5);
        g.drawString("11", radius / 2, radius / 3);
        g.drawString("10", radius / 3 - 12, radius * 2 / 3);

        // draw points of circle
        g.setColor(Color.red);
        for (int i = 0; i < 12; i++){
            hoursPoint.x = radius-1+Math.round((float) (radius * Math.sin(Math.toRadians(30 * i))));
            hoursPoint.y = radius-1-Math.round((float) (radius * Math.cos(Math.toRadians(30 * i))));
            g2D.fill3DRect(hoursPoint.x, hoursPoint.y, 5, 5, true);
        }
        for (int i = 0; i <= 60; i++){
            hoursPoint.x = radius-1+Math.round((float) (77 * Math.sin(Math.toRadians(6 * i))));
            hoursPoint.y = radius-1-Math.round((float) (77 * Math.cos(Math.toRadians(6 * i))));
            g2D.fill3DRect(hoursPoint.x, hoursPoint.y, 2, 2, true);
        }
    }
}
class DigitalClock extends JPanel {
    private JLabel labelDigitTime;
    private JRadioButton rbTwelveHourFormat;
    private JRadioButton rbTwentyFourthHourFormat;
    private ButtonGroup choiceFormatRadioGroup;
    private String time;
    private ClockPanel panelClock;
    public DigitalClock(){
        setBackground(Color.cyan);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.blue, 1),
                BorderFactory.createBevelBorder(BevelBorder.RAISED)));
        labelDigitTime = new JLabel();
        choiceFormatRadioGroup = new ButtonGroup();
        rbTwelveHourFormat = new JRadioButton("12-часовой формат");
        choiceFormatRadioGroup.add(rbTwelveHourFormat);
        rbTwelveHourFormat.setBackground(Color.cyan);
        rbTwelveHourFormat.setFocusable(false);
        rbTwentyFourthHourFormat = new JRadioButton("24-часовой формат");
        rbTwentyFourthHourFormat.setSelected(true);
        choiceFormatRadioGroup.add(rbTwentyFourthHourFormat);
        rbTwentyFourthHourFormat.setBackground(Color.cyan);
        rbTwentyFourthHourFormat.setFocusable(false);

        new Thread(){
            public void run(){
                for(;;){
                    try{
                        repaint();
                        panelClock = new ClockPanel(80);
                        if(rbTwelveHourFormat.isSelected()){
                            time = panelClock.twelveHourFormat();
                        }
                        else{
                            time = panelClock.twentyFourthHourFormat();
                        }
                        labelDigitTime.setText(time);
                        Thread.sleep(500);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }.start();
        labelDigitTime.setFont(new Font("Arial", Font.BOLD, 24));
        labelDigitTime.setForeground(Color.white);
        add(labelDigitTime);
        add(rbTwelveHourFormat);
        add(rbTwentyFourthHourFormat);
    }
}
class ChoiceSignal extends JPanel{
    private Box choice;
    private JPanel left;
    private JPanel right;
    private JPanel south;
    private ButtonGroup choiceRadioGroup;
    private JRadioButton simpleSignal;
    private JRadioButton soundSignal;
    private JLabel songFileName;
    private JCheckBox activeTimer;
    private JFileChooser fileChooser;
    private String fileName;
    private JSpinnerTimerPanel spinnerTime;
    private ClockPanel panelClock;
    private Clip clip;
    public void playSong(final String fileName){
        new Thread(){
            public void run(){
                try{
                    int start = fileName.lastIndexOf("\\")+1;
                    int end = fileName.length();
                    char buf[] = new char[end-start];
                    fileName.getChars(start, end, buf, 0);
                    String song = new String(buf);

                    buf = new char[fileName.length()-song.length()];
                    fileName.getChars(0, buf.length, buf, 0);
                    String dirTemp = new String(buf);
                    String dir = dirTemp.replace("\\", "\\\\");

                    activeTimer.setEnabled(false);
                    File soundFile = new File(dir, song);
                    AudioInputStream source = AudioSystem.getAudioInputStream(soundFile);
                    DataLine.Info clipInfo = new DataLine.Info(Clip.class, source.getFormat());
                    if(AudioSystem.isLineSupported(clipInfo)){
                        clip = (Clip) AudioSystem.getLine(clipInfo);
                        clip.open(source);
                        clip.loop(0);
                        int waitTime = (int)Math.ceil(clip.getMicrosecondLength()/1000.0);
                        Thread.sleep(waitTime);
                    }
                    activeTimer.setSelected(false);
                    activeTimer.setEnabled(true);
                } catch(UnsupportedAudioFileException ex){
                    ex.printStackTrace();
                }
                catch(LineUnavailableException ex){
                    ex.printStackTrace();
                }
                catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }.start();
    }
    public ChoiceSignal(){
        setLayout(new BorderLayout());
        setBackground(Color.cyan);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.blue, 1),
                BorderFactory.createBevelBorder(BevelBorder.RAISED)));
        left = new JPanel();
        left.setBackground(Color.cyan);
        left.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.blue, 1),
                BorderFactory.createBevelBorder(BevelBorder.RAISED)));
        choice = Box.createVerticalBox();
        choiceRadioGroup = new ButtonGroup();
        simpleSignal = new JRadioButton("Звуковой сигнал");
        simpleSignal.setSelected(true);
        simpleSignal.setBackground(Color.cyan);
        simpleSignal.setFocusable(false);
        soundSignal = new JRadioButton("Воспроизвести файл");
        soundSignal.setBackground(Color.cyan);
        soundSignal.setFocusable(false);

        choiceRadioGroup.add(soundSignal);
        choiceRadioGroup.add(simpleSignal);
        choice.add(simpleSignal);
        choice.add(soundSignal);
        left.add(choice);

        south = new JPanel();
        south.setLayout(new BorderLayout());
        south.setBackground(Color.cyan);
        songFileName = new JLabel();
        songFileName.setText("");
        songFileName.setFont(new Font("Arial", Font.BOLD, 12));
        songFileName.setBackground(Color.cyan);
        south.add(songFileName);

        right = new JPanel();
        right.setLayout(null);
        right.setBackground(Color.cyan);
        right.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.blue, 1),
                BorderFactory.createBevelBorder(BevelBorder.RAISED)));
        activeTimer = new JCheckBox("Активизировать");
        activeTimer.setBackground(Color.cyan);
        activeTimer.setFocusable(false);
        activeTimer.setBounds(25,5,150,30);
        right.add(activeTimer);

        spinnerTime = new JSpinnerTimerPanel();
        panelClock = new ClockPanel(80);
        activeTimer.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new Thread(){
                    public void run(){
                        for(;;){
                            try{
                                if(activeTimer.isSelected()){
                                    String currentTime = panelClock.twentyFourthHourFormat();
                                    String signalTime = spinnerTime.getTime();
                                    if(soundSignal.isSelected()){
                                        if(signalTime.equals(currentTime)){
                                            playSong(fileName);
                                            break;
                                        }
                                    }else{
                                        if(signalTime.equals(currentTime)){
                                            activeTimer.setEnabled(false);
                                            for(int i=0;i<10;i++){
                                                Toolkit.getDefaultToolkit().beep();
                                                Thread.sleep(300);
                                            }
                                            activeTimer.setSelected(false);
                                            activeTimer.setEnabled(true);
                                            break;
                                        }
                                    }
                                }
                                Thread.sleep(500);
                            } catch(InterruptedException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        });

        fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File("."));
        soundSignal.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int r = fileChooser.showOpenDialog(null);
                if(r == JFileChooser.APPROVE_OPTION){
                    fileName = fileChooser.getSelectedFile().getPath();
                    String song = fileName.toLowerCase();
                    if( song.endsWith(".au") || song.endsWith(".aif") || song.endsWith(".wav") ){
                        fileName = fileChooser.getSelectedFile().getPath();
                        songFileName.setText(fileName);
                    }else{
                        songFileName.setText("");
                        Toolkit.getDefaultToolkit().beep();
                        simpleSignal.setSelected(true);
                        JOptionPane.showMessageDialog(null,
                                "   Выберите аудиофайл \nформата .au .aif или .wav",
                                "MessageFormatInformation", JOptionPane.INFORMATION_MESSAGE, null);
                    }
                }
                else if(r == JFileChooser.CANCEL_OPTION){
                    songFileName.setText("");
                    simpleSignal.setSelected(true);
                }
            }
        });

        simpleSignal.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                songFileName.setText("");
            }
        });

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }
}
class JSpinnerTimerPanel extends JPanel{
    private String point = "1";
    private JSpinner spinner;
    private static JTextArea text1;
    private static JTextArea text2;
    private static JTextArea text3;
    private JTextArea firstPoint;
    private JTextArea secondPoint;
    public String getTime(){
        return text1.getText()+":"+text2.getText()+":"+text3.getText();
    }
    public JSpinnerTimerPanel(){
        setLayout(null);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.blue, 1),
                BorderFactory.createBevelBorder(BevelBorder.RAISED)));
        text1 = new JTextArea();
        text1.setEditable(false);
        text1.setBackground(Color.cyan);
        text1.setForeground(Color.white);
        text1.setText("00");
        text1.setFont(new Font("Arial", Font.BOLD, 24));
        text1.setBounds(2,2,32,34);
        add(text1);

        firstPoint = new JTextArea(":");
        firstPoint.setBackground(Color.cyan);
        firstPoint.setForeground(Color.white);
        firstPoint.setFont(new Font("Arial", Font.BOLD, 24));
        firstPoint.setEditable(false);
        firstPoint.setBounds(34,2,10,34);
        add(firstPoint);

        text2 = new JTextArea();
        text2.setEditable(false);
        text2.setBackground(Color.cyan);
        text2.setForeground(Color.white);
        text2.setText("00");
        text2.setFont(new Font("Arial", Font.BOLD, 24));
        text2.setBounds(44,2,30,34);
        add(text2);

        secondPoint = new JTextArea(":");
        secondPoint.setBackground(Color.cyan);
        secondPoint.setForeground(Color.white);
        secondPoint.setFont(new Font("Arial", Font.BOLD, 24));
        secondPoint.setEditable(false);
        secondPoint.setBounds(74,2,10,34);
        add(secondPoint);

        text3 = new JTextArea();
        text3.setEditable(false);
        text3.setBackground(Color.cyan);
        text3.setForeground(Color.white);
        text3.setText("00");
        text3.setFont(new Font("Arial", Font.BOLD, 24));
        text3.setBounds(84,2,30,34);
        add(text3);

        spinner = new JSpinner();
//----------------------------------------------------------------
        final String[] hours = new String[26];
        for(int i=0,j=-1;i<hours.length;i++,j++){
            if(i == -1){
                hours[i] = ""+i;
            }
            if(j>=0 && j<10){
                hours[i] = "0"+j;
            }
            else{
                hours[i] = ""+j;
            }
        }
        final SpinnerListModel model1 = new SpinnerListModel(hours){
            public Object getNextValue(){
                if(super.getNextValue().toString().equals("24")){
                    spinner.setValue(hours[1]);
                    text1.setText(super.getValue().toString());
                    return super.getValue();
                }
                if(point.equals("1")){
                    text1.setText(super.getNextValue().toString());
                }
                return super.getNextValue();
            }
            public Object getPreviousValue(){
                if(super.getPreviousValue().toString().equals("-1")){
                    spinner.setValue(hours[24]);
                    text1.setText(super.getValue().toString());
                    return super.getValue();
                }
                if(point.equals("1")){
                    text1.setText(super.getPreviousValue().toString());
                }
                return super.getPreviousValue();
            }
        };
        spinner.setModel(model1);
        spinner.setValue(hours[1]);
        spinner.setBounds(114,1,17,35);
        add(spinner);
//----------------------------------------------------------------
        final String[] minutesAndSeconds = new String[62];
        for(int i=0,j=-1;i<minutesAndSeconds.length;i++,j++){
            if(i == -1){
                minutesAndSeconds[i] = ""+i;
            }
            if(j>=0 && j<10){
                minutesAndSeconds[i] = "0"+j;
            }
            else{
                minutesAndSeconds[i] = ""+j;
            }
        }
        final SpinnerListModel model2 = new SpinnerListModel(minutesAndSeconds){
            public Object getNextValue(){
                if(super.getNextValue().toString().equals("60")){
                    spinner.setValue(minutesAndSeconds[1]);
                    if(point.equals("2")){
                        text2.setText(super.getValue().toString());
                    }
                    else if(point.equals("3")){
                        text3.setText(super.getValue().toString());
                    }
                    return super.getValue();
                }
                if(point.equals("2")){
                    text2.setText(super.getNextValue().toString());
                }
                else if(point.equals("3")){
                    text3.setText(super.getNextValue().toString());
                }
                return super.getNextValue();
            }
            public Object getPreviousValue(){
                if(super.getPreviousValue().toString().equals("-1")){
                    spinner.setValue(minutesAndSeconds[60]);
                    if(point.equals("2")){
                        text2.setText(super.getValue().toString());
                    }
                    else if(point.equals("3")){
                        text3.setText(super.getValue().toString());
                    }
                    return super.getValue();
                }
                if(point.equals("2")){
                    text2.setText(super.getPreviousValue().toString());
                }
                else if(point.equals("3")){
                    text3.setText(super.getPreviousValue().toString());
                }
                return super.getPreviousValue();
            }
        };
//----------------------------------------------------------------
        text1.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e){
                spinner.setModel(model1);
                spinner.setValue(hours[1]);
                text1.setText("00");
                point = "1";
            }
            public void focusLost(FocusEvent e){}
        });
        text2.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e){
                spinner.setModel(model2);
                spinner.setValue(minutesAndSeconds[1]);
                text2.setText("00");
                point = "2";
            }
            public void focusLost(FocusEvent e){}
        });
        text3.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent e){
                spinner.setModel(model2);
                spinner.setValue(minutesAndSeconds[1]);
                text3.setText("00");
                point = "3";
            }
            public void focusLost(FocusEvent e){}
        });
    }
}
class JavaAlarmClock extends JFrame{
    private ClockPanel panelClock;
    private DigitalClock digit;
    private ChoiceSignal panelChoiceSignal;
    private JLabel labelClock;
    private JLabel currentTimeLabel;
    private JLabel timeSignal;
    private JSpinnerTimerPanel timerClock;
    private SystemTray systemTray = SystemTray.getSystemTray();
    private TrayIcon trayIcon;
    public void javaStart() throws IOException{
        String command = "java "+getClass().getName();
        BufferedReader buf = new BufferedReader(new StringReader(command));
        PrintWriter out = null;
        out = new PrintWriter(new BufferedWriter(new FileWriter("JavaAlarmClock.bat")));
        String str;
        while( (str = buf.readLine()) != null){
            out.println(command);
        }
        buf.close();
        out.close();
    }
    private void removeTrayIcon(){
        systemTray.remove(trayIcon);
    }
    private void addTrayIcon(){
        try{
            systemTray.add(trayIcon);
        }
        catch(AWTException ex){
            ex.printStackTrace();
        }
    }
    public JavaAlarmClock() throws IOException{
        setLayout(null);
        Container content = getContentPane();
        getContentPane().setBackground(Color.cyan);

        panelClock = new ClockPanel(80);
        panelClock.startClock();
        panelClock.setBounds(10,25,170,170);

        labelClock = new JLabel(""+panelClock.getFullDate());
        labelClock.setForeground(Color.blue);
        labelClock.setBounds(15,5,150,15);

        currentTimeLabel = new JLabel("Текущее время");
        currentTimeLabel.setBounds(235,5,100,15);
        currentTimeLabel.setForeground(Color.blue);

        panelChoiceSignal = new ChoiceSignal();
        panelChoiceSignal.setBounds(10,195,350,85);

        timeSignal = new JLabel("Время срабатывания");
        timeSignal.setBounds(220,130,130,15);
        timeSignal.setForeground(Color.blue);

        timerClock = new JSpinnerTimerPanel();
        timerClock.setBounds(215,145,133,38);

        digit = new DigitalClock();
        digit.setBounds(200,20,160,100);

        content.add(panelClock);
        content.add(labelClock);
        content.add(currentTimeLabel);
        content.add(timeSignal);
        content.add(timerClock);
        content.add(digit);
        content.add(panelChoiceSignal);

        getContentPane().add(new JColorChooser());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        trayIcon = new TrayIcon(ImageIO.read(new File("/Users/grigoriy/Desktop/sweater-SpringSecurity/Budilnik/src/main/java/ico.gif")), "JavaAlarmClock");
        trayIcon.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                setVisible(true);
                setState(JFrame.NORMAL);
                removeTrayIcon();
            }
        });
        addWindowStateListener(new WindowStateListener(){
            public void windowStateChanged(WindowEvent e){
                if(e.getNewState() == JFrame.ICONIFIED){
                    setVisible(false);
                    addTrayIcon();
                }
            }
        });
        PopupMenu popupMenu = new PopupMenu();
        MenuItem showItem = new MenuItem("Восстановить");
        MenuItem exitItem = new MenuItem("Закрыть");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
        showItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
                setState(JFrame.NORMAL);
                removeTrayIcon();
            }
        });
        popupMenu.add(exitItem);
        popupMenu.add(showItem);
        trayIcon.setPopupMenu(popupMenu);
    }
    public static void main(String[] args) throws IOException{
        JFrame.setDefaultLookAndFeelDecorated(true);
        try{
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        }
        catch ( UnsupportedLookAndFeelException e ) {
            System.exit(0);
        }
        JavaAlarmClock frame = new JavaAlarmClock();
        frame.javaStart();
        frame.setTitle("JavaAlarmClock");
        frame.setSize(375,320);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
