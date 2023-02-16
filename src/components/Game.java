package components;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static javax.swing.JOptionPane.showMessageDialog;


public class Game extends JFrame{
    Controller controller;
    static private Integer CURR_FPS;
    static private Integer FPS_MIN, FPS_MAX, FPS_INIT; 
    static private Double RADIUS_MIN, RADIUS_MAX;
    static private Double MASS_MIN, MASS_MAX;
    static private Integer MAPSIZE_X_MAX, MAPSIZE_Y_MAX;
    static private boolean isExit, isPaused = true;
    static String BACKGROUND_FILEPATH, SAVES_FILEPATH;
    private Object[] defaultPlanets, backgroundOptions, specialities;
    JPanel topPanel, bottomPanel, mainPanel;
    HashMap<String, PlanetPlacer> defaultPlanetPlacer;
    HashMap<String, BackgroundChanger> backgroundChanger;
    JComboBox<Object> cboxPlanets, cboxBackground, cboxSpec;
    JTextField radiusField;
    JTextField massField;
    BufferedImage backgroundBuffImage;

    static{
        FPS_MIN = 1;
        FPS_INIT = 300;
        FPS_MAX = 1100;
        CURR_FPS = FPS_INIT;
        RADIUS_MIN = 1.0;
        RADIUS_MAX = 50.0;
        MASS_MIN = 1.0;
        MASS_MAX = 10e18;
        MAPSIZE_X_MAX = 1400;
        MAPSIZE_Y_MAX = 700;
        BACKGROUND_FILEPATH = "src/components/Background_High_Quality.jpg";                                                                 
        SAVES_FILEPATH = "src/savefiles.dat";
    }

    public Game(){
        controller = new Controller(MAPSIZE_X_MAX, MAPSIZE_Y_MAX);
        defaultPlanets = new String[]{"Earth", "Mars", "Moon", "Sun", "Neptun", "Black Hole","Custom"};
        backgroundOptions = new String[]{"Universe", "White", "Black"};
        specialities = new String[]{"Orbit", "Static"};
        if(BACKGROUND_FILEPATH != null && BACKGROUND_FILEPATH != ""){
            try {
                FileInputStream is = new FileInputStream(BACKGROUND_FILEPATH);
                backgroundBuffImage = ImageIO.read(is);
                is.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        fillBackgroundChanger();
        fillDefaultPlanetPlacer();
        setUpGUI();
    }

    /**
    * Feltolti a defualtPlanetPlacer HashMapet úgy, hogy hozzárendeli a bolygók neveihez a 
    * hozzájuk tartozó PlanetPlacer interface álltal megvalósított bolygó lerakókat
    * <p>
    */
    private void fillDefaultPlanetPlacer(){
        defaultPlanetPlacer = new HashMap<>();
        defaultPlanetPlacer.put("Earth", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                String currSpec = (String)cboxSpec.getSelectedItem();
                controller.placePlanet(14.371, 4.57e4, new Vector(x, y), new Color(40, 122, 184), currSpec);
            }
        });
        defaultPlanetPlacer.put("Mars", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                String currSpec = (String)cboxSpec.getSelectedItem();
                controller.placePlanet(10.389, 3.39e3, new Vector(x, y), new Color(0xc1440e), currSpec);
            }
        });
        defaultPlanetPlacer.put("Moon", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                String currSpec = (String)cboxSpec.getSelectedItem();
                controller.placePlanet(5.37, 7.34e2, new Vector(x, y), new Color(102, 102, 102 ), currSpec);
            }
        });
        defaultPlanetPlacer.put("Neptun", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                //controller.placePlanet(17.37, 5.97e6, new Vector(x, y), new Color(0x3f54ba));
                showMessageDialog(null, "Neptun is not available at this time!");
            }
        });
        defaultPlanetPlacer.put("Sun", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                String currSpec = (String)cboxSpec.getSelectedItem();
                controller.placePlanet(30.024, 5.97e18, new Vector(x, y), new Color( 253, 184, 19 ), currSpec);
            }
        });
        defaultPlanetPlacer.put("Black Hole", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                String currSpec = (String)cboxSpec.getSelectedItem();
                controller.placePlanet(3.024, 8.97e20, new Vector(x, y), new Color( 0, 0, 0), currSpec);
            }
        });
        defaultPlanetPlacer.put("Custom", new PlanetPlacer() {
            @Override
            public void placePlanet(Integer x, Integer y) {
                String radiusFieldText = radiusField.getText();
                String massFieldText = massField.getText();
                Double radiusFieldValue, massFieldValue;
                try{
                    radiusFieldValue = Double.parseDouble(radiusFieldText);
                    massFieldValue = Double.parseDouble(massFieldText);
                    if(massFieldValue.compareTo(MASS_MAX) > 0 || massFieldValue.compareTo(MASS_MIN) < 0){
                        showMessageDialog(null, "Given mass is out of range (min:"+MASS_MIN+"), (max:"+MASS_MAX+")!");
                        return;
                    }
                    if(radiusFieldValue.compareTo(RADIUS_MAX) > 0 || radiusFieldValue.compareTo(RADIUS_MIN) < 0){
                        showMessageDialog(null, "Given radius is out of range (min:"+RADIUS_MIN+"), (max:"+RADIUS_MAX+")!");
                        return;
                    }
                } catch(NumberFormatException exc){
                    showMessageDialog(null, "Given radius or mass field value is not valid!");
                    return;
                }
                Vector mousePosition = new Vector(x, y);
                Random r = new Random();
                Color color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
                String currSpec = (String)cboxSpec.getSelectedItem();
                controller.placePlanet(radiusFieldValue, massFieldValue, mousePosition, color, currSpec);
            }
        });
    }

    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    private void fillBackgroundChanger(){
        backgroundChanger = new HashMap<>();
        backgroundChanger.put("Universe", new BackgroundChanger() {
            @Override
            public void paintBackground(Graphics g, Component comp) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(backgroundBuffImage, 0, 0, comp);
            }
        });
        backgroundChanger.put("Black", new BackgroundChanger() {
            @Override
            public void paintBackground(Graphics g, Component comp) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(Color.black);
                g2d.fillRect(0, 0, MAPSIZE_X_MAX, MAPSIZE_Y_MAX);            
            }
        });
        backgroundChanger.put("White", new BackgroundChanger() {
            @Override
            public void paintBackground(Graphics g, Component comp) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(Color.white);
                g2d.fillRect(0, 0, MAPSIZE_X_MAX, MAPSIZE_Y_MAX);   
            }
        });
    }

    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    private void setUpGUI(){
        this.setPreferredSize(new Dimension(MAPSIZE_X_MAX, MAPSIZE_Y_MAX));
        this.setSize(getPreferredSize());
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        setUpMainPanel();
        setUpTopPanel();
        setUpBottomPanel();


        // On Resize
        JFrame thisFrame = this;
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                MAPSIZE_X_MAX = thisFrame.getWidth();
                MAPSIZE_Y_MAX = thisFrame.getHeight();
                controller.setMaxMapsize(MAPSIZE_X_MAX, MAPSIZE_Y_MAX-124);
            }
        });

        // Mouse
        mainPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    int x = e.getX();
                    int y = e.getY();
                               
                    String currDefaultPlanet = (String)cboxPlanets.getSelectedItem();
                    if(defaultPlanetPlacer.containsKey(currDefaultPlanet)){
                        defaultPlanetPlacer.get(currDefaultPlanet).placePlanet(x, y);
                    }          
                }
                if (e.getButton() == MouseEvent.BUTTON3){
                    int x = e.getX();
                    int y = e.getY();
                    controller.removePlanet(x, y);
                }
                mainPanel.repaint();
                return;
            }
            @Override
            public void mouseEntered(MouseEvent e) {

                
            }
            @Override
            public void mouseExited(MouseEvent e) {
                
                
            }
            @Override
            public void mousePressed(MouseEvent e) {

                
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                
            }
        }
        );

        this.pack();
        this.setVisible(true);
    }

    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    private void setUpBottomPanel(){
        // bottomPanel
        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(0x003b59));

        // ComboBox for Planet Speciality
        cboxSpec = new JComboBox<>(specialities);
        cboxSpec.setEditable(false);
        cboxSpec.setSelectedIndex(0);
        cboxSpec.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        bottomPanel.add(cboxSpec);

        radiusField = new JTextField("", 20);
        massField = new JTextField("", 6);
        radiusField.setEditable(true);
        massField.setEditable(true);
        JLabel radiusLabel = new JLabel("Radius: ");
        radiusLabel.setForeground(Color.BLACK);
        bottomPanel.add(radiusLabel);
        bottomPanel.add(radiusField);
        JLabel massLabel = new JLabel("Mass: ");
        massLabel.setForeground(Color.BLACK);
        bottomPanel.add(massLabel);
        bottomPanel.add(massField);

        // ComboBox for Default Planets
        cboxPlanets = new JComboBox<>(defaultPlanets);
        cboxPlanets.setEditable(false);
        cboxPlanets.setSelectedIndex(0);
        cboxPlanets.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String currDefaultPlanet = (String)cboxPlanets.getSelectedItem();
                if(!currDefaultPlanet.equals("Custom")){
                    radiusField.setEditable(false);
                    massField.setEditable(false);
                }else{
                    radiusField.setEditable(true);
                    massField.setEditable(true);
                }       
            }
        });
        bottomPanel.add(cboxPlanets);

        // ComboBox to change Background
        cboxBackground = new JComboBox<>(backgroundOptions);
        cboxBackground.setEditable(false);
        cboxBackground.setSelectedIndex(0);
        cboxBackground.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String currBackground = (String)cboxBackground.getSelectedItem();
                if(currBackground.equals("Universe") && backgroundBuffImage == null){
                    showMessageDialog(null, "Could not load Universe background! Background was set to White.");
                    cboxBackground.setSelectedItem("White");
                }
                //mainPanel
                mainPanel.repaint();
            }
        });
        bottomPanel.add(cboxBackground);

        this.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    private void setUpTopPanel(){
        // topPanel
        topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(0x003b59));

        // Button to Save
        JButton buttonSave = new JButton("SAVE");
        buttonSave.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    controller.saveSimulationToFile(SAVES_FILEPATH);
                }catch(IOException err){
                    err.printStackTrace();
                }
                
            }
        });
        topPanel.add(buttonSave);


        // Button to Load
        JButton buttonLoad = new JButton("LOAD");
        buttonLoad.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    controller.loadSimulationFile(SAVES_FILEPATH);
                    mainPanel.repaint();
                }catch(IOException err){
                    err.printStackTrace();
                }
            }
        });
        topPanel.add(buttonLoad);

        // Button to Pause
        String buttonTitleInit = isPaused ? "START" : "PAUSE";
        JButton buttonPause = new JButton(buttonTitleInit);
        buttonPause.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //isPaused = !isPaused;
                if(isPaused = !isPaused){
                    buttonPause.setText("RESUME");
                } else{
                    buttonPause.setText("PAUSE");
                }
                mainPanel.repaint();
            }
        });
        topPanel.add(buttonPause);

        // Slider
        JSlider slider = new JSlider(JSlider.HORIZONTAL, FPS_MIN, FPS_MAX, FPS_INIT);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int fps = (int)source.getValue();
                    CURR_FPS = fps;
                }
            }
        });
        slider.setMajorTickSpacing(500);
        slider.setMinorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        topPanel.add(slider);

        // Button to Reset
        JButton buttonReset = new JButton("RESET");
        buttonReset.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.resetGame();
                mainPanel.repaint();
            }
        });
        topPanel.add(buttonReset);


        // Button to Exit
        JButton buttonExit = new JButton("EXIT");
        buttonExit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                isExit = true;
            }
        });
        topPanel.add(buttonExit);
        
        this.add(topPanel, BorderLayout.NORTH);
    }

    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    private void setUpMainPanel(){
        // mainPanel
        PerformanceCounter c = new PerformanceCounter("mainPanel.paintComponent()");
        mainPanel = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                c.countStart();
                Graphics2D g2d = (Graphics2D) g;
        
                //g2d.clearRect(0, 0, MAPSIZE_X_MAX, MAPSIZE_Y_MAX); 

                String currBackground = (String)cboxBackground.getSelectedItem();
                backgroundChanger.get(currBackground).paintBackground(g2d, this);
                

                controller.drawPlanets(g2d);
                c.countStop();
            }
        };
        mainPanel.setBackground(new Color(0,0,0,0));
        
        this.add(mainPanel, BorderLayout.CENTER);
    }
    
    
    // returns sleepTime in milliseconds
    private long getSleepTime(){
        Double sleepTimeDouble = (1/CURR_FPS.doubleValue()*1000);
        return sleepTimeDouble.longValue();
    }

    public void runSimulation(){
        try{
            PerformanceCounter cntr = new PerformanceCounter("runSimulation():while(!isExit)");

            while(!isExit){
                Long start = System.currentTimeMillis();
                cntr.countStart();
                if(!isPaused){
                    controller.calculateNewPlanetPositions();
                    mainPanel.repaint();
                    Long elapsed = System.currentTimeMillis() - start;
                    Long sleepTime = getSleepTime();
                    if(sleepTime-elapsed>0){
                        Thread.sleep(getSleepTime()-elapsed); 
                    }
                }
                cntr.countStop();
            }
            try{
                File file = new File("src/results.txt");
                //file.delete();
                file.createNewFile();
                FileWriter writer = new FileWriter("src/results.txt");
                PerformanceCounter.writeResults(writer);
                writer.close();
            } catch(IOException e){
                e.printStackTrace();
            }


            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
