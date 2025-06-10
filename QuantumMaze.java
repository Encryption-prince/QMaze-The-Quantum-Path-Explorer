import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuantumMaze extends JPanel {
    static int[][] maze = {
            { 1, 0, 1, 1, 0, 1, 1, 1, 1, 1 },
            { 1, 1, 0, 1, 0, 1, 0, 0, 0, 1 },
            { 0, 1, 1, 1, 1, 1, 1, 0, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0 },
            { 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 0, 1, 1, 1, 1, 0, 1 },
            { 0, 0, 1, 1, 1, 0, 0, 1, 1, 1 },
            { 1, 1, 1, 0, 0, 1, 1, 1, 1, 0 },
            { 0, 0, 1, 0, 1, 1, 0, 0, 1, 1 }
    };

    static int n = 10;
    static int cellSize = 50;
    List<Qubit> qubits = new ArrayList<>();
    String foundPath = null;
    int step = 0;
    int qubitLifetime = 6;
    Timer timer;

    static class Qubit {
        int x, y;
        String path;
        int createdAt;

        Qubit(int x, int y, String path, int createdAt) {
            this.x = x;
            this.y = y;
            this.path = path;
            this.createdAt = createdAt;
        }
    }

    public QuantumMaze() {
        qubits.add(new Qubit(0, 0, "", 0));

        timer = new Timer(300, e -> {
            step++;
            List<Qubit> nextQubits = new ArrayList<>();

            if (foundPath != null) {
                repaint();
                timer.stop();
                // Delay before exit to show final path
                new Timer(3000, ev -> System.exit(0)).start();
                return;
            }

            for (Qubit q : qubits) {
                if (q.x == n - 1 && q.y == n - 1) {
                    foundPath = q.path;
                    System.out.println("✨ Path found: " + foundPath);
                    break;
                }

                if (step - q.createdAt > qubitLifetime) {
                    continue;
                }

                int[][] moves = { { 1, 0, 'D' }, { -1, 0, 'U' }, { 0, 1, 'R' }, { 0, -1, 'L' } };

                for (int[] move : moves) {
                    int newX = q.x + move[0];
                    int newY = q.y + move[1];
                    char dir = (char) move[2];

                    if (isSafe(newX, newY)) {
                        nextQubits.add(new Qubit(newX, newY, q.path + dir, step));
                    }
                }
            }

            if (foundPath == null) {
                qubits = nextQubits;
                if (qubits.isEmpty()) {
                    System.out.println("❌ No path found.");
                    timer.stop();
                    new Timer(2000, ev -> System.exit(0)).start();
                }
            }

            repaint();
        });

        timer.start();
    }

    boolean isSafe(int x, int y) {
        return (x >= 0 && y >= 0 && x < n && y < n && maze[x][y] == 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw maze grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (maze[i][j] == 0) {
                    g.setColor(Color.DARK_GRAY);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        // Draw start and end points
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, cellSize, cellSize);
        g.setColor(Color.RED);
        g.fillRect((n - 1) * cellSize, (n - 1) * cellSize, cellSize, cellSize);

        // Draw active qubits
        g.setColor(Color.CYAN);
        for (Qubit q : qubits) {
            g.fillOval(q.y * cellSize + cellSize / 4, q.x * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
        }

        // Draw found path if any
        if (foundPath != null) {
            int x = 0, y = 0;
            g.setColor(new Color(128, 0, 128)); // Purple
            for (char c : foundPath.toCharArray()) {
                switch (c) {
                    case 'U':
                        x -= 1;
                        break;
                    case 'D':
                        x += 1;
                        break;
                    case 'L':
                        y -= 1;
                        break;
                    case 'R':
                        y += 1;
                        break;
                }
                g.fillRect(y * cellSize, x * cellSize, cellSize, cellSize);
            }

            // Redraw start and end markers
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, cellSize, cellSize);
            g.setColor(Color.RED);
            g.fillRect((n - 1) * cellSize, (n - 1) * cellSize, cellSize, cellSize);
        }

        // Draw stats
        g.setColor(Color.BLACK);
        g.drawString("Step: " + step + " | Active Qubits: " + qubits.size(), 10, n * cellSize + 20);
        if (foundPath != null) {
            g.drawString("Path found: " + foundPath, 10, n * cellSize + 40);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Quantum Maze Simulator");
        QuantumMaze panel = new QuantumMaze();
        frame.add(panel);
        frame.setSize(n * cellSize + 20, n * cellSize + 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}