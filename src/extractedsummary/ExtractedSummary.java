package extractedsummary;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.regex.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.scene.input.KeyCode;

/**
 *
 * @author HP
 */
public class ExtractedSummary extends Application {
    TextArea OT = new TextArea();
    TextArea summaryArea = new TextArea();
    @Override
    public void start(Stage primaryStage) {
        
        GridPane root=new GridPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(0, 10, 0,10));
        
        Label labelOT=new Label("Pre-Summarized Text:");
        root.add(labelOT,0,3);
        
        
        OT.setPromptText("Enter text here...");
        OT.setFocusTraversable(false);
        OT.setWrapText(true);
        root.add(OT, 0, 4,4,9);
        
        Label labelsummaryArea=new Label("Summarized Text:");
        root.add(labelsummaryArea,5,3);
        
        summaryArea.setPromptText("Summary...");
        summaryArea.setEditable(false);
        summaryArea.setFocusTraversable(false);
        summaryArea.setWrapText(true);
        root.add(summaryArea, 5, 4,9,9);
        
        Button btnClear = new Button();
        btnClear.setText("Clear");
        btnClear.setFocusTraversable(false);
        root.add(btnClear, 0,13);
        btnClear.setOnAction(new EventHandler<ActionEvent>() {
           @Override
            public void handle(ActionEvent event) {
                OT.clear();
            }
        });
        
        Button btnSummarize = new Button();
        btnSummarize.setText("Summarize");
        btnSummarize.setFocusTraversable(false);
        root.add(btnSummarize, 0,14);
        
        btnSummarize.setOnAction(new EventHandler<ActionEvent>() {
           @Override
            public void handle(ActionEvent event) {
                summaryArea.clear();
                summarize(OT.getText());
            }
        });
        
        Scene scene = new Scene(root, 1000, 500);
        
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setTitle("Auto Summarizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void summarize(String text){
        String[] temp=text.split("\\.+");
        int i=0;
        ArrayList<Sentence> sentences = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher;
        while(i<temp.length){
            Sentence sentence = new Sentence();
            sentence.wordCount=0;
            sentence.serial_no=i+1;
            sentence.text="";
            sentence.importanceFactor=0.0;
            matcher= pattern.matcher(temp[i]);
              while (matcher.find()) {
                  sentence.wordCount++;
                  sentence.text+=matcher.group()+" ";
              }
              sentence.stopWordCount=countStopWords(temp[i]);
//              System.out.println(sentence.stopWordCount);
              sentence.importanceFactor=(double)(sentence.wordCount-sentence.stopWordCount)/sentence.wordCount;
              if(!"".equals(sentence.text)){
                sentences.add(sentence);
              }
              else{
                  sentence=null;
                  System.gc();
              }
            i++;
        }
//        i=0;
//        while(i<sentences.size()){
//            summaryArea.appendText(sentences.get(i).text+" "+sentences.get(i).serial_no+" "+sentences.get(i).wordCount+" "+sentences.get(i).stopWordCount+" "+sentences.get(i).importanceFactor+"\n");
//            i++;    
//        }
        Collections.sort(sentences,Collections.reverseOrder()); //sort sentences in reverse order of importanceFactor
        
        i=(sentences.size()-1);
        int j=sentences.size();
        while(i>=(j/3)){ //summary is one third of original text
            sentences.remove(i);
            i--;
        }
        
        Collections.sort(sentences,new Comparator<Sentence>(){ // sort sentences again in ascending order of sentence serial_no.
            @Override public int compare(Sentence s1,Sentence s2){
                return s1.serial_no-s2.serial_no;
            }
        });
          
        sentences.forEach((s) -> { //output summarized text
//            summaryArea.appendText(s.text+" "+s.serial_no+" "+s.wordCount+" "+s.stopWordCount+" "+s.importanceFactor+"\n");
              summaryArea.appendText(s.text+'.');
        });
    }

    public int countStopWords(String sentence){
//        System.out.println(sentence);
        int i=0,stopWordCount=0;
        String stopWord;
        String[] word=sentence.replaceAll("\\s+", " ").trim().split("\\s|,");
        BufferedReader br;
        try {
                FileInputStream FIn=new FileInputStream("stopwords.txt");
                br = new BufferedReader(new InputStreamReader(FIn));
                while(i<word.length){
                    FIn.getChannel().position(0);
                    br = new BufferedReader(new InputStreamReader(FIn));
                    while((stopWord=br.readLine())!=null){
//                        System.out.println(stopWord);
//                        System.out.println(word[i]);
                        if(stopWord.equalsIgnoreCase(word[i])){
//                            System.out.println(stopWord+"         --------MATCHED-------");
                            stopWordCount++;
                            break;
                        }
                    }
                    //br.close();
                    i++;
                }   
        br.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return stopWordCount;
    }
    
//    public ArrayList<Sentence> sort(ArrayList<Sentence> sentences){
//        ArrayList<Sentence> sortedSentences;
//        Collections.sort(sentences, new Comparator(){
//            
//        });
//    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
