import javax.swing.*;
import java.awt.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class Beatbox {
    JFrame frame;
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    String[] instrumentNames={"Base Drum", "Closed Hi-Hat", "open Hi-Hit",
    "Acoustic Snare", "Crash Cymbal" , "Hand Clap", "High Tom", "Hi Bongo",
    "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap", "Low-mid Tom",
    "High Agogo", "Open Hi Conga"};
    int [] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    Sequencer player;
    Sequence se;
    Track track;


    public static void main(String[] args)
    {
        new Beatbox().buildGui();
    }

    void buildGui(){
        frame = new JFrame("BeatBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout bdrLayout = new BorderLayout();
        JPanel bgPanel = new JPanel(bdrLayout); //set border layout because flowlayout is default in panel
        //another way is : bgPanel.setLayout(new BorderLayout())
        bgPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        checkboxList = new ArrayList<JCheckBox>();
        Box buttonbox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new startListn());
        buttonbox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new stopListn());
        buttonbox.add(stop);

        JButton upTempo = new JButton("UpTempo");
        upTempo.addActionListener(new upTempoListn());
        buttonbox.add(upTempo);

        JButton downTempo = new JButton("DownTempo");
        downTempo.addActionListener(new downTempoListn());
        buttonbox.add(downTempo);


        Box namebox = new Box(BoxLayout.Y_AXIS);
        for(int i=0; i<16;i++){
            namebox.add(new Label(instrumentNames[i]));
        }

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);

        for(int i=0;i<256;i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        bgPanel.add(BorderLayout.EAST, buttonbox);
        bgPanel.add(BorderLayout.CENTER,mainPanel);
        bgPanel.add(BorderLayout.WEST,namebox);

        frame.getContentPane().add(bgPanel);
        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }//close  build gui


    void setUpMidi(){
        try{
            player = MidiSystem.getSequencer();
            player.open();
            se = new Sequence(Sequence.PPQ, 4);
            track = se.createTrack();
            player.setTempoInBPM(120);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    } // close setupmidi
    void buildTrackAndStart(){
        int[] trackList = null;

        se.deleteTrack(track);
        track = se.createTrack();
        for(int i =0; i <16; i++) {  //chackbox 16 ha
            trackList = new int[16];
            int key = instruments[i];

            for(int j=0;j<16;j++){
                JCheckBox jc = (JCheckBox) checkboxList.get(j+(16*i));
                if (jc.isSelected()){
                    trackList[j] = key;
                }else {trackList[j]=0;}
            } //close for inner loop

            makeTracks(trackList);
            track.add(makeEvent(176, 1, 127, 0, 16));
        }//close outter loop

        track.add(makeEvent(192, 9, 1, 0, 15));

        try{
            player.setSequence(se);
            player.setLoopCount(player.LOOP_CONTINUOUSLY);
            player.start();
            player.setTempoInBPM(120);

        }catch(Exception e){e.printStackTrace();}


    }

    public class startListn implements ActionListener{
        public void actionPerformed(ActionEvent event){
            buildTrackAndStart();
        }
    }

    public class stopListn implements ActionListener{
        public void actionPerformed(ActionEvent event){
            player.stop();
        }
    }

    public class upTempoListn implements ActionListener{
        public void actionPerformed(ActionEvent event){
            float tempoFactor = player.getTempoFactor();
            player.setTempoFactor( (float) (tempoFactor * 1.03) );
        }
    }
    public class downTempoListn implements ActionListener{
        public void actionPerformed(ActionEvent event){
            float tempoFactor = player.getTempoFactor();
            player.setTempoFactor( (float) (tempoFactor * .97) );
        }
    }



    public void makeTracks(int [] list){
        for(int i=0;i<16;i++){
            int key = list[i];
            if(key!=0){
                track.add(makeEvent(144, 9, key, 100, i));
                track.add(makeEvent(128, 9, key, 100, i+1));
            }
        }
    }
    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage msg = new ShortMessage();
            msg.setMessage(comd, chan, one, two);
            event = new MidiEvent(msg, tick);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return event;
    }

}// close beatbox
