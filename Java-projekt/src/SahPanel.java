import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;



public class SahPanel extends JPanel {

	
	
    public SahPanel() {
        super();
        setBackground(Color.WHITE);
    }

    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D graphics = (Graphics2D) g;

        int velPolje = Math.min(getWidth(), getHeight()) / 8;
        int velikostSahovnice = 8 * velPolje;

        int odmikX = (getWidth() - velikostSahovnice) / 2;
        int odmikY = (getHeight() - velikostSahovnice) / 2;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    graphics.setColor(new Color(240, 217, 181));
                } else {
                    graphics.setColor(new Color(181, 136, 99));
                }

                int x = odmikX + col * velPolje;
                int y = odmikY + row * velPolje;

                graphics.fillRect(x, y, velPolje, velPolje);
            }
        }
    }

}