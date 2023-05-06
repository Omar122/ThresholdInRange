package com.omar122.thresholdinrange;

import java.awt.BorderLayout;
import java.awt.Button;

import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.PointerScope;
import static org.bytedeco.opencv.global.opencv_core.inRange;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_java;
import org.opencv.highgui.HighGui;

public class ThresholdInRange {

    private static int MAX_VALUE = 255;
    private static int MAX_VALUE_H = 360 / 2;
    private static final String WINDOW_NAME = "Thresholding Operations using inRange demo";
    private static final String LOW_H_NAME = "Low H";
    private static final String LOW_S_NAME = "Low S";
    private static final String LOW_V_NAME = "Low V";
    private static final String HIGH_H_NAME = "High H";
    private static final String HIGH_S_NAME = "High S";
    private static final String HIGH_V_NAME = "High V";
    private JSlider sliderLowH;
    private JSlider sliderHighH;
    private JSlider sliderLowS;
    private JSlider sliderHighS;
    private JSlider sliderLowV;
    private JSlider sliderHighV;

    private final JFrame jFrame;
    private JLabel imgCaptureLabel;
    private JLabel imgDetectionLabel;
    Button cancelButton;
    JTextArea textA;
    CaptureTask captureTask;
    Button excuteButton;
    File filename;

    public ThresholdInRange() {
        Mat matFrame;
        Loader.load(opencv_java.class);
        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null, "Please Select the File");
        jfc.setVisible(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.jpg, *.jpeg", "jpg", "jpeg", "jp2", "png", "tiff", "tif", "tiff, tif");
        jfc.setFileFilter(filter);

        // Create and set up the window.
        jFrame = new JFrame(WINDOW_NAME);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        try (PointerScope scope = new PointerScope()) {
            captureTask = new CaptureTask();
          
        }
        int result = jfc.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            filename = jfc.getSelectedFile();
            matFrame = imread(filename.getPath());
            Image img = HighGui.toBufferedImage(new org.opencv.core.Mat(matFrame.address())).getScaledInstance(matFrame.rows() / 6, matFrame.cols() / 6, Image.SCALE_SMOOTH);
            addComponentsToPane(jFrame.getContentPane(), img);
            jFrame.pack();
            jFrame.setVisible(true);
        } else {
            System.exit(0);
        }

    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        cancelButton = new Button("Cance!");
        excuteButton = new Button("Excute!");
        cancelButton.addActionListener((ActionEvent e) -> {
          
            captureTask.cancel(false);

        });

        excuteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureTask = new CaptureTask();
               
                captureTask.execute();
            }
        });
        JPanel sliderPanel = new JPanel();
        sliderPanel.add(cancelButton);
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel(LOW_H_NAME));
        sliderLowH = new JSlider(0, MAX_VALUE_H, 0);
        sliderLowH.setMajorTickSpacing(50);
        sliderLowH.setMinorTickSpacing(10);
        sliderLowH.setPaintTicks(true);
        sliderLowH.setPaintLabels(true);
        sliderPanel.add(sliderLowH);
        sliderPanel.add(new JLabel(HIGH_H_NAME));
        sliderHighH = new JSlider(0, MAX_VALUE_H, MAX_VALUE_H);
        sliderHighH.setMajorTickSpacing(50);
        sliderHighH.setMinorTickSpacing(10);
        sliderHighH.setPaintTicks(true);
        sliderHighH.setPaintLabels(true);
        sliderPanel.add(sliderHighH);
        sliderPanel.add(new JLabel(LOW_S_NAME));
        sliderLowS = new JSlider(0, MAX_VALUE, 0);
        sliderLowS.setMajorTickSpacing(50);
        sliderLowS.setMinorTickSpacing(10);
        sliderLowS.setPaintTicks(true);
        sliderLowS.setPaintLabels(true);
        sliderPanel.add(sliderLowS);
        sliderPanel.add(new JLabel(HIGH_S_NAME));
        sliderHighS = new JSlider(0, MAX_VALUE, MAX_VALUE);
        sliderHighS.setMajorTickSpacing(50);
        sliderHighS.setMinorTickSpacing(10);
        sliderHighS.setPaintTicks(true);
        sliderHighS.setPaintLabels(true);
        sliderPanel.add(sliderHighS);
        sliderPanel.add(new JLabel(LOW_V_NAME));
        sliderLowV = new JSlider(0, MAX_VALUE, 0);
        sliderLowV.setMajorTickSpacing(50);
        sliderLowV.setMinorTickSpacing(10);
        sliderLowV.setPaintTicks(true);
        sliderLowV.setPaintLabels(true);
        sliderPanel.add(sliderLowV);
        sliderPanel.add(new JLabel(HIGH_V_NAME));
        sliderHighV = new JSlider(0, MAX_VALUE, MAX_VALUE);
        sliderHighV.setMajorTickSpacing(50);
        sliderHighV.setMinorTickSpacing(10);
        sliderHighV.setPaintTicks(true);
        sliderHighV.setPaintLabels(true);
        sliderPanel.add(sliderHighV);
        sliderLowH.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valH = Math.min(sliderHighH.getValue() - 1, source.getValue());
                sliderLowH.setValue(valH);
            }
        });
        sliderHighH.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valH = Math.max(source.getValue(), sliderLowH.getValue() + 1);
                sliderHighH.setValue(valH);
            }
        });
        sliderLowS.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valS = Math.min(sliderHighS.getValue() - 1, source.getValue());
                sliderLowS.setValue(valS);
            }
        });
        sliderHighS.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valS = Math.max(source.getValue(), sliderLowS.getValue() + 1);
                sliderHighS.setValue(valS);
            }
        });
        sliderLowV.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valV = Math.min(sliderHighV.getValue() - 1, source.getValue());
                sliderLowV.setValue(valV);
            }
        });
        sliderHighV.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int valV = Math.max(source.getValue(), sliderLowV.getValue() + 1);
                sliderHighV.setValue(valV);
            }
        });
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgCaptureLabel);
        imgDetectionLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgDetectionLabel);

        pane.add(framePanel, BorderLayout.CENTER);
        textA = new JTextArea();
         framePanel.add(textA, BorderLayout.SOUTH);
        framePanel.add(cancelButton,BorderLayout.CENTER);
        framePanel.add(excuteButton,BorderLayout.WEST);
       
    }

    private class CaptureTask extends SwingWorker<Mat, Integer> {

        Mat matFrame;
        Mat thresh;

        public CaptureTask() {

        }

        @Override
        protected Mat doInBackground() throws Exception {
            thresh = new Mat();

            matFrame = imread(filename.getPath());
            Mat frameHSV = new Mat();

            opencv_imgproc.cvtColor(matFrame, frameHSV, opencv_imgproc.COLOR_BGR2HSV);

            inRange(frameHSV, new Mat((double) sliderLowH.getValue(), (double) sliderLowS.getValue(), (double) sliderLowV.getValue()),
                    new Mat((double) sliderHighH.getValue(), (double) sliderHighS.getValue(), (double) sliderHighV.getValue()), thresh);
            StringBuilder string = new StringBuilder();
            string.append("Lower:(").append(sliderLowH.getValue()).append(",").append(sliderLowS.getValue()).append(",").append(sliderLowV.getValue()).append(")");
            string.append(" Upper:(").append(sliderHighH.getValue()).append(",").append(sliderHighS.getValue()).append(",").append(sliderHighV.getValue()).append(")");
            //opencv_imgproc.putText(thresh, string.toString(), new Point(1, thresh.rows() / 4), opencv_imgproc.FONT_HERSHEY_DUPLEX, 3.0, new Scalar(118, 185, 0, 0));
            textA.setText(string.toString());
            return thresh;

        }

        @Override
        protected void done() {

            try {
                thresh = new Mat();

                thresh = this.get();

                if (!matFrame.empty() || !matFrame.isNull()) {

                    org.opencv.core.Mat OrgMatFrame = new org.opencv.core.Mat(matFrame.address());

                    Image imgCapture = HighGui.toBufferedImage(OrgMatFrame).getScaledInstance(OrgMatFrame.rows() / 6, OrgMatFrame.cols() / 6, Image.SCALE_DEFAULT);
                    OrgMatFrame.release();
                    //matFrame.close();
                    imgCaptureLabel.setIcon(new ImageIcon(imgCapture));
                }

                if (!thresh.empty() || !thresh.isNull()) {

                    opencv_imgcodecs.imwrite("thresh.jpg", thresh);

                    Image imgThresh = HighGui.toBufferedImage(new org.opencv.core.Mat(thresh.address())).getScaledInstance(thresh.rows() / 6, thresh.cols() / 6, Image.SCALE_DEFAULT);
                    //thresh.close();
                    imgDetectionLabel.setIcon(new ImageIcon(imgThresh));
                }
                jFrame.pack();
                jFrame.repaint();

            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(ThresholdInRange.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected void process(List<Integer> chunks) {

        }

    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
       

        System.setProperty("org.bytedeco.javacpp.logger.debug", "false");
        //Issue with threads without it. 
        System.setProperty("org.bytedeco.javacpp.nopointergc", "true");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ThresholdInRange thresholdInRange = new ThresholdInRange();
            }
        });

    }
}
