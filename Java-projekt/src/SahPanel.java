import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public class SahPanel extends JPanel {
	private Figures[][] figures = new Figures[8][8];
	private Figures selectedFigure = null;
	private Map<String, BufferedImage> images = new HashMap<>();
	private List<Point> legalMoves = new ArrayList<>();
	
    public SahPanel() {
        super();
        setBackground(Color.WHITE);
        
        initFigures();
        loadImages();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }
    
    private void handleClick(int mouseX, int mouseY) {

        int velPolje = Math.min(getWidth(), getHeight()) / 8;
        int velikostSahovnice = 8 * velPolje;

        int odmikX = (getWidth() - velikostSahovnice) / 2;
        int odmikY = (getHeight() - velikostSahovnice) / 2;

        int col = (mouseX - odmikX) / velPolje;
        int row = (mouseY - odmikY) / velPolje;

        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return;
        }

        Figures clicked = figures[row][col];

        // ======================
        // 1. NI izbrane figure
        // ======================
        if (selectedFigure == null) {

            if (clicked != null) {
                selectedFigure = clicked;

                legalMoves = getLegalMoves(clicked);
            }

        }
        // ======================
        // 2. JE izbrana figura
        // ======================
        else {

            if (selectedFigure.isValidMove(figures, row, col)) {

                figures[selectedFigure.getRow()][selectedFigure.getCol()] = null;

                selectedFigure.setPosition(row, col);
                figures[row][col] = selectedFigure;
            }

            selectedFigure = null;
            legalMoves.clear();
        }

        repaint();
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
        if (selectedFigure != null) {
            graphics.setColor(new Color(0, 255, 0, 100));

            int x = odmikX + selectedFigure.getCol() * velPolje;
            int y = odmikY + selectedFigure.getRow() * velPolje;

            graphics.fillRect(x, y, velPolje, velPolje);
        }
        drawFigures(graphics, velPolje, odmikX, odmikY);
        
        if (selectedFigure != null) {

            graphics.setColor(new Color(0, 255, 0, 80));

            int x = odmikX + selectedFigure.getCol() * velPolje;
            int y = odmikY + selectedFigure.getRow() * velPolje;

            graphics.fillRect(x, y, velPolje, velPolje);
        }
        
        graphics.setColor(new Color(0, 0, 0, 80));

        for (Point p : legalMoves) {

            int x = odmikX + p.y * velPolje;
            int y = odmikY + p.x * velPolje;

            int dotSize = velPolje / 4;

            graphics.fillOval(
                x + velPolje / 2 - dotSize / 2,
                y + velPolje / 2 - dotSize / 2,
                dotSize,
                dotSize
            );
        }
    }
    
    private void initFigures() {

        // ======================
        // PAWNS (kmetje)
        // ======================
        for (int c = 0; c < 8; c++) {
            figures[1][c] = new Figures(Figures.Type.PAWN, false, 1, c); // črni
            figures[6][c] = new Figures(Figures.Type.PAWN, true, 6, c);  // beli
        }

        // ======================
        // ČRNI (zgornja vrsta)
        // ======================
        figures[0][0] = new Figures(Figures.Type.ROOK, false, 0, 0);
        figures[0][1] = new Figures(Figures.Type.KNIGHT, false, 0, 1);
        figures[0][2] = new Figures(Figures.Type.BISHOP, false, 0, 2);
        figures[0][3] = new Figures(Figures.Type.QUEEN, false, 0, 3);
        figures[0][4] = new Figures(Figures.Type.KING, false, 0, 4);
        figures[0][5] = new Figures(Figures.Type.BISHOP, false, 0, 5);
        figures[0][6] = new Figures(Figures.Type.KNIGHT, false, 0, 6);
        figures[0][7] = new Figures(Figures.Type.ROOK, false, 0, 7);

        // ======================
        // BELI (spodnja vrsta)
        // ======================
        figures[7][0] = new Figures(Figures.Type.ROOK, true, 7, 0);
        figures[7][1] = new Figures(Figures.Type.KNIGHT, true, 7, 1);
        figures[7][2] = new Figures(Figures.Type.BISHOP, true, 7, 2);
        figures[7][3] = new Figures(Figures.Type.QUEEN, true, 7, 3);
        figures[7][4] = new Figures(Figures.Type.KING, true, 7, 4);
        figures[7][5] = new Figures(Figures.Type.BISHOP, true, 7, 5);
        figures[7][6] = new Figures(Figures.Type.KNIGHT, true, 7, 6);
        figures[7][7] = new Figures(Figures.Type.ROOK, true, 7, 7);
    }
    
    private List<Point> getLegalMoves(Figures f) {

        List<Point> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                if (f.isValidMove(figures, r, c)) {
                    moves.add(new Point(r, c));
                }
            }
        }

        return moves;
    }
    
    private void loadImages() {

        try {
            images.put("white_king", ImageIO.read(getClass().getResource("/w-king.png")));
            images.put("black_king", ImageIO.read(getClass().getResource("/b-king.png")));

            images.put("white_queen", ImageIO.read(getClass().getResource("/w-queen.png")));
            images.put("black_queen", ImageIO.read(getClass().getResource("/b-queen.png")));

            images.put("white_rook", ImageIO.read(getClass().getResource("/w-rook.png")));
            images.put("black_rook", ImageIO.read(getClass().getResource("/b-rook.png")));

            images.put("white_bishop", ImageIO.read(getClass().getResource("/w-bishop.png")));
            images.put("black_bishop", ImageIO.read(getClass().getResource("/b-bishop.png")));

            images.put("white_knight", ImageIO.read(getClass().getResource("/w-knight.png")));
            images.put("black_knight", ImageIO.read(getClass().getResource("/b-knight.png")));

            images.put("white_pawn", ImageIO.read(getClass().getResource("/w-pawn.png")));
            images.put("black_pawn", ImageIO.read(getClass().getResource("/b-pawn.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void drawFigures(Graphics2D g, int size, int offsetX, int offsetY) {

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                Figures f = figures[r][c];

                if (f != null) {

                    int x = offsetX + c * size + size / 2;
                    int y = offsetY + r * size + size / 2;

                    BufferedImage img = null;

                    String key = (f.isWhite() ? "white_" : "black_") +
                                 f.getType().name().toLowerCase();

                    img = images.get(key);

                    if (img != null) {
                        g.drawImage(
                            img,
                            x - size / 2,
                            y - size / 2,
                            size,
                            size,
                            null
                        );
                    }
                }
            }
        }
    }

}