package unity.younggamExperimental.graphs;

//GraphCommon 그래프계의 타입 느낌? Consume?
public class Graph{
    public boolean isMultiConnector;
    public int[] accept;

    public void setAccept(int... newAccept){
        accept = newAccept;
    }

    void drawPlace(int x, int y, int size, int rotation, boolean valid){}
}
