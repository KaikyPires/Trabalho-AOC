package processimagebalckwhite;

public class Trabalhador extends Thread {
    private int startX, endX;
    private int[][] image;
    private int[][] result;

    // Construtor para definir a parte da imagem que a thread irá processar
    public Trabalhador(int startX, int endX, int[][] image, int[][] result) {
        this.startX = startX;
        this.endX = endX;
        this.image = image;
        this.result = result;
    }

    @Override
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getId() + " processando de " + startX + " até " + endX);
        for (int i = startX; i < endX; i++) {
            for (int j = 0; j < image[0].length; j++) {
                result[i][j] = image[i][j];

                if (image[i][j] == 0 || image[i][j] == 255) {
                    int soma = 0;
                    int contador = 0;
                    int contadorPretos = 0;

                    for (int i1 = -1; i1 <= 1; i1++) {
                        for (int j1 = -1; j1 <= 1; j1++) {
                            int x = i + i1;
                            int y = j + j1;

                            if (x >= 0 && x < image.length && y >= 0 && y < image[0].length) {
                                if (!(i1 == 0 && j1 == 0)) {
                                    int valorPixel = image[x][y];
                                    soma += valorPixel;
                                    contador++;
                                    if (valorPixel == 0) {
                                        contadorPretos++;
                                    }
                                }
                            }
                        }
                    }

                    if (contadorPretos > contador / 2) {
                        result[i][j] = 0;
                    } else {
                        if (contador > 0) {
                            result[i][j] = soma / contador;
                        }
                    }
                }
            }
        }
    }
}
