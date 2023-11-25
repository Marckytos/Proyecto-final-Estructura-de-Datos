package laberinto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

public class GeneradorLaberintoGUI extends JFrame {

    private GeneradorLaberinto generadorLaberinto;
    private PanelLaberinto panelLaberinto;
    private Timer temporizadorResolver;
    private JLabel etiquetaEstado;
    private int currentX;
    private int currentY;

    // Constructor de la clase GUI
    public GeneradorLaberintoGUI(int ancho, int alto) {
        // Inicialización de componentes
        generadorLaberinto = new GeneradorLaberinto(ancho, alto);
        setTitle("Laberinto Generado");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración del panel para mostrar el laberinto
        panelLaberinto = new PanelLaberinto();
        add(panelLaberinto, BorderLayout.CENTER);

        // Configuración de botones y etiquetas
        JButton botonGenerar = new JButton("Generar Laberinto");
        botonGenerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarLaberinto();
            }
        });

        JButton botonResolver = new JButton("Resolver Laberinto");
        botonResolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resolverLaberintoPasoAPaso();
            }
        });

        etiquetaEstado = new JLabel("Listo para generar un nuevo laberinto");
        etiquetaEstado.setHorizontalAlignment(JLabel.CENTER);
        etiquetaEstado.setForeground(Color.BLUE);

        // Configuración del diseño de la interfaz
        JPanel panelBotones = new JPanel();
        panelBotones.add(botonGenerar);
        panelBotones.add(botonResolver);
        add(panelBotones, BorderLayout.SOUTH);

        add(etiquetaEstado, BorderLayout.NORTH);

        // Configuración del temporizador para la resolución paso a paso del laberinto
        temporizadorResolver = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resolverLaberintoPaso();
            }
        });

        currentX = 1;
        currentY = 1;

        // Configuración del listener para el teclado
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                moverManualmenteLaberinto(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        setFocusable(true);

        setVisible(true);
    }

    // Método para generar un nuevo laberinto
    private void generarLaberinto() {
        if (temporizadorResolver.isRunning()) {
            // Si el temporizador está en ejecución, detenerlo
            temporizadorResolver.stop();
            mostrarMensaje("Resolución automática detenida");
        }

        // Crear una nueva instancia del generador de laberinto y reiniciar la interfaz
        generadorLaberinto = new GeneradorLaberinto(generadorLaberinto.getAncho(), generadorLaberinto.getAlto());
        panelLaberinto.setSolucion(new int[generadorLaberinto.getAlto()][generadorLaberinto.getAncho()]);

        // Restablecer las coordenadas del punto azul al inicio del laberinto
        currentX = 1;
        currentY = 1;

        // Reiniciar variables relacionadas con la resolución automática
        panelLaberinto.getPila().clear();

        // Eliminar el KeyListener existente y agregar uno nuevo
        removeKeyListener(getKeyListeners()[0]);
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                moverManualmenteLaberinto(e);
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // Establecer el enfoque nuevamente
        setFocusable(true);
        requestFocusInWindow();

        panelLaberinto.repaint();
        etiquetaEstado.setText("Listo para generar un nuevo laberinto");
    }

    // Método para resolver el laberinto paso a paso (iniciar temporizador)
    private void resolverLaberintoPasoAPaso() {
        temporizadorResolver.start();
    }

    // Método para resolver el laberinto paso a paso en cada iteración del temporizador
    private void resolverLaberintoPaso() {
        int[][] solucion = busquedaProfundidadPaso();
        panelLaberinto.setSolucion(solucion);
        panelLaberinto.repaint();

        if (esLaberintoResuelto(solucion)) {
            temporizadorResolver.stop();
            mostrarMensaje("¡Laberinto resuelto automáticamente!");
        }
    }

    // Método para realizar un paso en la búsqueda en profundidad para resolver el laberinto
    private int[][] busquedaProfundidadPaso() {
        int[][] laberinto = generadorLaberinto.getLaberinto();
        int[][] solucion = panelLaberinto.getSolucion();

        Stack<Point> pila = panelLaberinto.getPila();

        if (pila.isEmpty()) {
            pila.push(new Point(1, 1));
        }

        Point actual = pila.pop();
        int x = actual.x;
        int y = actual.y;

        if (x == laberinto.length - 2 && y == laberinto[0].length - 2) {
            while (!pila.isEmpty()) {
                Point p = pila.pop();
                solucion[p.x][p.y] = 1;
            }
            return solucion;
        }

        if (estaEnLimites(x, y) && laberinto[x][y] == 0 && solucion[x][y] == 0) {
            solucion[x][y] = 1;
            pila.push(new Point(x, y));  // Agregar la posición actual a la pila

            if (estaEnLimites(x + 1, y) && laberinto[x + 1][y] == 0 && solucion[x + 1][y] == 0) {
                pila.push(new Point(x + 1, y));
            }
            if (estaEnLimites(x - 1, y) && laberinto[x - 1][y] == 0 && solucion[x - 1][y] == 0) {
                pila.push(new Point(x - 1, y));
            }
            if (estaEnLimites(x, y - 1) && laberinto[x][y - 1] == 0 && solucion[x][y - 1] == 0) {
                pila.push(new Point(x, y - 1));
            }
            if (estaEnLimites(x, y + 1) && laberinto[x][y + 1] == 0 && solucion[x][y + 1] == 0) {
                pila.push(new Point(x, y + 1));
            }
        }

        return solucion;
    }

    // Método para verificar si una celda está dentro de los límites del laberinto
    private boolean estaEnLimites(int x, int y) {
        return x >= 0 && x < generadorLaberinto.getAncho() && y >= 0 && y < generadorLaberinto.getAlto();
    }

    // Método para verificar si el laberinto está completamente resuelto
    private boolean esLaberintoResuelto(int[][] solucion) {
        for (int i = 0; i < solucion.length; i++) {
            for (int j = 0; j < solucion[0].length; j++) {
                if (solucion[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // Método para mover manualmente el punto azul en el laberinto
    private void moverManualmenteLaberinto(KeyEvent e) {
        int[][] laberinto = generadorLaberinto.getLaberinto();
        int[][] solucion = panelLaberinto.getSolucion();
        Stack<Point> pila = panelLaberinto.getPila();

        int x = currentX;
        int y = currentY;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (estaEnLimites(x, y - 1) && laberinto[x][y - 1] == 0 && solucion[x][y - 1] == 0) {
                    currentY--;
                    pila.push(new Point(x, y - 1));

                    if (x == generadorLaberinto.getAncho() - 2 && y - 1 == generadorLaberinto.getAlto() - 2) {
                        mostrarMensaje("¡Llegaste a la meta manualmente!");
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                if (estaEnLimites(x, y + 1) && laberinto[x][y + 1] == 0 && solucion[x][y + 1] == 0) {
                    currentY++;
                    pila.push(new Point(x, y + 1));

                    if (x == generadorLaberinto.getAncho() - 2 && y + 1 == generadorLaberinto.getAlto() - 2) {
                        mostrarMensaje("¡Llegaste a la meta manualmente!");
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
                if (estaEnLimites(x - 1, y) && laberinto[x - 1][y] == 0 && solucion[x - 1][y] == 0) {
                    currentX--;
                    pila.push(new Point(x - 1, y));

                    if (x - 1 == generadorLaberinto.getAncho() - 2 && y == generadorLaberinto.getAlto() - 2) {
                        mostrarMensaje("¡Llegaste a la meta manualmente!");
                    }
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (estaEnLimites(x + 1, y) && laberinto[x + 1][y] == 0 && solucion[x + 1][y] == 0) {
                    currentX++;
                    pila.push(new Point(x + 1, y));

                    if (x + 1 == generadorLaberinto.getAncho() - 2 && y == generadorLaberinto.getAlto() - 2) {
                        mostrarMensaje("¡Llegaste a la meta manualmente!");
                    }
                }
                break;
        }

        panelLaberinto.repaint();
    }

    // Método para mostrar un mensaje en una ventana emergente
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "¡Meta alcanzada!", JOptionPane.INFORMATION_MESSAGE);
    }

    // Clase interna para el panel que muestra el laberinto
    class PanelLaberinto extends JPanel {
        private int[][] solucion;
        private Stack<Point> pila;

        // Constructor del panel
        public PanelLaberinto() {
            solucion = new int[generadorLaberinto.getAlto()][generadorLaberinto.getAncho()];
            pila = new Stack<>();
        }

        // Configuración de la solución actual
        public void setSolucion(int[][] solucion) {
            this.solucion = solucion;
        }

        // Obtención de la solución actual
        public int[][] getSolucion() {
            return solucion;
        }

        // Obtención de la pila de puntos
        public Stack<Point> getPila() {
            return pila;
        }

        // Método de dibujo del laberinto
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarLaberinto(g);
            dibujarSolucion(g);
            dibujarPosicionActual(g);
        }

        // Método para dibujar las celdas del laberinto
        private void dibujarLaberinto(Graphics g) {
            int tamanoCelda = 20;

            for (int i = 0; i < generadorLaberinto.getAlto(); i++) {
                for (int j = 0; j < generadorLaberinto.getAncho(); j++) {
                    if (generadorLaberinto.getLaberinto()[j][i] == 1) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }

                    int x = j * tamanoCelda;
                    int y = i * tamanoCelda;

                    g.fillRect(x, y, tamanoCelda, tamanoCelda);
                }
            }
        }

        // Método para dibujar la solución actual en verde
        private void dibujarSolucion(Graphics g) {
            g.setColor(Color.GREEN);
            int tamanoCelda = 20;

            for (int i = 0; i < solucion.length; i++) {
                for (int j = 0; j < solucion[0].length; j++) {
                    if (solucion[i][j] == 1) {
                        int x = i * tamanoCelda;
                        int y = j * tamanoCelda;
                        g.fillRect(x, y, tamanoCelda, tamanoCelda);
                    }
                }
            }
        }

        // Método para dibujar la posición actual del punto azul
        private void dibujarPosicionActual(Graphics g) {
            g.setColor(Color.BLUE);
            int tamanoCelda = 20;

            int x = currentX * tamanoCelda;
            int y = currentY * tamanoCelda;

            g.fillOval(x, y, tamanoCelda, tamanoCelda);
        }
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GeneradorLaberintoGUI(33, 33);
        });
    }
}

