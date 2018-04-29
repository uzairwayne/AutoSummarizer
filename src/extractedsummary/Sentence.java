package extractedsummary;

final class Sentence implements Comparable<Sentence>{
    int serial_no;
    String text;
    int wordCount;
    int stopWordCount;
    Double importanceFactor;
    
    @Override
    public int compareTo(Sentence s){
        return importanceFactor.compareTo(s.importanceFactor);
    }
}
