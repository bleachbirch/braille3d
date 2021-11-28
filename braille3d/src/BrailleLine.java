import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlusslus on 25.05.2016.
 */
public class BrailleLine {

    private int index;
    private List<BrailleToken> brailleTokens;

    public int getIndex() {
        return index;
    }

    public BrailleLine() {
        index = 0;
        this.brailleTokens = new ArrayList<BrailleToken>();
    }

    public void add(BrailleToken brailleToken) {
        index++;
        this.brailleTokens.add(brailleToken);
    }

    public List<BrailleToken> getBrailleTokens() {
        return this.brailleTokens;
    }
}


