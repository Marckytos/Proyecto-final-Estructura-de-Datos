package laberinto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GeneradorLaberinto {

    // Dimensiones del laberinto (ancho y alto)
    private int ancho;
    private int alto;

    // Representación del laberinto como una matriz de enteros
    private int[][] laberinto;

    // Constructor de la clase que recibe las dimensiones del laberinto
    public GeneradorLaberinto(int ancho, int alto) {
        // Ajustar las dimensiones para asegurarse de que sean impares
        this.ancho = (ancho % 2 == 0) ? ancho + 1 : ancho;
        this.alto = (alto % 2 == 0) ? alto + 1 : alto;

        // Inicializar la matriz del laberinto con todas las celdas bloqueadas (1)
        this.laberinto = new int[this.ancho][this.alto];
        inicializarLaberinto();

        // Generar el laberinto empezando desde la celda (1, 1)
        generarLaberinto(1, 1);
    }

    // Método privado para inicializar el laberinto con todas las celdas bloqueadas
    private void inicializarLaberinto() {
        for (int i = 0; i < ancho; i++) {
            for (int j = 0; j < alto; j++) {
                laberinto[i][j] = 1; // Celda bloqueada
            }
        }
    }

    // Método privado para generar el laberinto utilizando el algoritmo de backtracking
    private void generarLaberinto(int x, int y) {
        // Marcar la celda actual como visitada (celda libre)
        laberinto[x][y] = 0;

        // Obtener direcciones posibles y mezclarlas aleatoriamente
        List<Direccion> direcciones = Arrays.asList(Direccion.values());
        Collections.shuffle(direcciones);

        // Iterar sobre las direcciones mezcladas
        for (Direccion direccion : direcciones) {
            int nx = x + direccion.dx;
            int ny = y + direccion.dy;

            // Verificar si la siguiente celda está dentro de los límites y no ha sido visitada
            if (estaEnLimites(nx, ny) && laberinto[nx][ny] == 1) {
                // Marcar la celda entre la actual y la próxima como libre
                laberinto[(x + nx) / 2][(y + ny) / 2] = 0;
                // Llamada recursiva para continuar generando el laberinto desde la nueva celda
                generarLaberinto(nx, ny);
            }
        }
    }

    // Método público para obtener el ancho del laberinto
    public int getAncho() {
        return ancho;
    }

    // Método público para obtener el alto del laberinto
    public int getAlto() {
        return alto;
    }

    // Método público para obtener la representación del laberinto como una matriz de enteros
    public int[][] getLaberinto() {
        return laberinto;
    }

    // Método privado para verificar si una celda está dentro de los límites del laberinto
    private boolean estaEnLimites(int x, int y) {
        return x >= 0 && x < ancho && y >= 0 && y < alto;
    }

    // Enumeración que representa las direcciones posibles en el laberinto
    private enum Direccion {
        ARRIBA(0, -2),
        DERECHA(2, 0),
        ABAJO(0, 2),
        IZQUIERDA(-2, 0);

        // Desplazamiento en las coordenadas (dx, dy)
        private int dx;
        private int dy;

        // Constructor de la enumeración
        Direccion(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}
