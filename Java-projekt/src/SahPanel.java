import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Stroke;
import javax.swing.JButton;
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
	private static final int STATUS_HEIGHT = 30;
	private Figures[][] figures = new Figures[8][8];
	private Figures selectedFigure = null;
	private Map<String, BufferedImage> images = new HashMap<>();
	private List<Point> legalMoves = new ArrayList<>();
	private JButton restartButton = new JButton("Nova igra");
	private boolean whiteTurn = true;
	private boolean gameOver = false;
	private String message = "Beli je na potezi";
	
    public SahPanel() {
        super();
        setBackground(Color.WHITE);
        setLayout(null);
        
        initFigures();
        loadImages();
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> resetGame());
        add(restartButton);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }
    
    private void handleClick(int mouseX, int mouseY) {
    	if (gameOver) {
    		return;
    	}

        int velPolje = Math.min(getWidth(), getHeight() - STATUS_HEIGHT) / 8;
        int velikostSahovnice = 8 * velPolje;

        int odmikX = (getWidth() - velikostSahovnice) / 2;
        int odmikY = (getHeight() - velikostSahovnice) / 2;

        if (mouseX < odmikX || mouseX >= odmikX + velikostSahovnice ||
            mouseY < odmikY || mouseY >= odmikY + velikostSahovnice) {
        	return;
        }

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

            if (clicked != null && clicked.isWhite() == whiteTurn) {
                selectedFigure = clicked;

                legalMoves = getLegalMoves(clicked);
            }

        }
        // ======================
        // 2. JE izbrana figura
        // ======================
        else {

        	if (clicked != null && clicked.isWhite() == selectedFigure.isWhite()) {
        		selectedFigure = clicked;
        		legalMoves = getLegalMoves(clicked);
        		repaint();
        		return;
        	}

            if (canMoveTo(selectedFigure, row, col)) {

                figures[selectedFigure.getRow()][selectedFigure.getCol()] = null;

                selectedFigure.setPosition(row, col);
                figures[row][col] = selectedFigure;

                whiteTurn = !whiteTurn;

                if (isKingInCheck(whiteTurn) && !hasLegalMove(whiteTurn)) {
                	gameOver = true;
                	message = (whiteTurn ? "Beli" : "Črni") + " je v šahmatu, " +
                	          (whiteTurn ? "črni" : "beli") + " je zmagal.";
                } else if (isKingInCheck(whiteTurn)) {
                	message = (whiteTurn ? "Beli" : "Crni") + " je v šahu";
                } else if (!hasLegalMove(whiteTurn)) {
                	gameOver = true;
                	message = "Remi - pat";
                } else {
                	message = whiteTurn ? "Beli je na potezi" : "Crni je na potezi";
                }
            }

            selectedFigure = null;
            legalMoves.clear();
        }

        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics = (Graphics2D) g;

        int velPolje = Math.min(getWidth(), getHeight() - STATUS_HEIGHT) / 8;
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

        drawCheckBorder(graphics, velPolje, odmikX, odmikY, true);
        drawCheckBorder(graphics, velPolje, odmikX, odmikY, false);

        graphics.setColor(Color.BLACK);
        graphics.drawString(message, odmikX, odmikY + velikostSahovnice + 20);

        if (gameOver) {
        	drawGameOver(graphics, odmikX, odmikY, velikostSahovnice);
        } else {
        	restartButton.setVisible(false);
        }
    }
    
    private void initFigures() {
    	for (int r = 0; r < 8; r++) {
    		for (int c = 0; c < 8; c++) {
    			figures[r][c] = null;
    		}
    	}

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

    private void resetGame() {
    	initFigures();
    	selectedFigure = null;
    	legalMoves.clear();
    	whiteTurn = true;
    	gameOver = false;
    	message = "Beli je na potezi";
    	restartButton.setVisible(false);
    	repaint();
    }
    
    private List<Point> getLegalMoves(Figures f) {

        List<Point> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                if (canMoveTo(f, r, c)) {
                    moves.add(new Point(r, c));
                }
            }
        }

        return moves;
    }

    private boolean canMoveTo(Figures f, int row, int col) {
    	if (f == null) {
    		return false;
    	}

    	Figures target = figures[row][col];

    	if (target != null && target.isWhite() == f.isWhite()) {
    		return false;
    	}

    	if (target != null && target.getType() == Figures.Type.KING) {
    		return false;
    	}

    	if (!f.isValidMove(figures, row, col)) {
    		return false;
    	}

    	return !wouldLeaveKingInCheck(f, row, col);
    }

    private boolean wouldLeaveKingInCheck(Figures f, int newRow, int newCol) {
    	int oldRow = f.getRow();
    	int oldCol = f.getCol();
    	Figures target = figures[newRow][newCol];

    	figures[oldRow][oldCol] = null;
    	figures[newRow][newCol] = f;
    	f.setPosition(newRow, newCol);

    	boolean inCheck = isKingInCheck(f.isWhite());

    	figures[oldRow][oldCol] = f;
    	figures[newRow][newCol] = target;
    	f.setPosition(oldRow, oldCol);

    	return inCheck;
    }

    private boolean isKingInCheck(boolean whiteKing) {
    	Point king = findKing(whiteKing);

    	if (king == null) {
    		return false;
    	}

    	for (int r = 0; r < 8; r++) {
    		for (int c = 0; c < 8; c++) {
    			Figures f = figures[r][c];

    			if (f != null && f.isWhite() != whiteKing &&
    				f.isValidMove(figures, king.x, king.y)) {
    				return true;
    			}
    		}
    	}

    	return false;
    }

    private Point findKing(boolean whiteKing) {
    	for (int r = 0; r < 8; r++) {
    		for (int c = 0; c < 8; c++) {
    			Figures f = figures[r][c];

    			if (f != null && f.isWhite() == whiteKing &&
    				f.getType() == Figures.Type.KING) {
    				return new Point(r, c);
    			}
    		}
    	}

    	return null;
    }

    private boolean hasLegalMove(boolean whitePlayer) {
    	for (int r = 0; r < 8; r++) {
    		for (int c = 0; c < 8; c++) {
    			Figures f = figures[r][c];

    			if (f != null && f.isWhite() == whitePlayer) {
    				for (int newRow = 0; newRow < 8; newRow++) {
    					for (int newCol = 0; newCol < 8; newCol++) {
    						if (canMoveTo(f, newRow, newCol)) {
    							return true;
    						}
    					}
    				}
    			}
    		}
    	}

    	return false;
    }

    private void drawCheckBorder(Graphics2D g, int size, int offsetX, int offsetY, boolean whiteKing) {
    	if (!isKingInCheck(whiteKing)) {
    		return;
    	}

    	Point king = findKing(whiteKing);

    	if (king == null) {
    		return;
    	}

    	int x = offsetX + king.y * size;
    	int y = offsetY + king.x * size;

    	Stroke oldStroke = g.getStroke();
    	g.setColor(Color.RED);
    	g.setStroke(new BasicStroke(4.0f));
    	g.drawRect(x + 2, y + 2, size - 4, size - 4);
    	g.setStroke(oldStroke);
    }

    private void drawGameOver(Graphics2D g, int offsetX, int offsetY, int boardSize) {
    	g.setColor(new Color(0, 0, 0, 150));
    	g.fillRect(offsetX, offsetY, boardSize, boardSize);

    	g.setColor(Color.WHITE);
    	g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
    	g.drawString("GAME OVER", offsetX + boardSize / 2 - 135, offsetY + boardSize / 2 - 20);

    	g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    	g.drawString(message, offsetX + boardSize / 2 - 120, offsetY + boardSize / 2 + 15);

    	int buttonWidth = 130;
    	int buttonHeight = 35;
    	int buttonX = offsetX + boardSize / 2 - buttonWidth / 2;
    	int buttonY = offsetY + boardSize / 2 + 40;

    	restartButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
    	restartButton.setVisible(true);
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
