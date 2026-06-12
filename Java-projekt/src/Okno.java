import java.awt.Dimension;
import javax.swing.JFrame;



public class Okno extends JFrame {


	public Okno() {
        super("Šah");

        setSize(new Dimension(700, 700));
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SahPanel panel = new SahPanel();
        add(panel);

        setVisible(true);
    }

}