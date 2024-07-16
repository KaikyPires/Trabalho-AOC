package processimagebalckwhite;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Trabalhador extends Thread {
    private static BlockingQueue<Block> workQueue = new LinkedBlockingQueue<>();
    private static BlockingQueue<Block> resultQueue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        while (!workQueue.isEmpty()) {
            try {
                Block block = workQueue.take();
                System.out.println("Thread " + Thread.currentThread().getId() + " processando bloco começando em " + block.startX);
                int[][] result = processBlock(block.data);
                resultQueue.put(new Block(block.startX, result));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private int[][] processBlock(int[][] block) {
        int largura = block.length;
        int altura = block[0].length;
        int[][] resultMat = new int[largura][altura];

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                resultMat[i][j] = block[i][j];

                if (block[i][j] == 0 || block[i][j] == 255) {
                    int soma = 0;
                    int contador = 0;
                    int contadorPretos = 0;

                    for (int i1 = -1; i1 <= 1; i1++) {
                        for (int j1 = -1; j1 <= 1; j1++) {
                            int x = i + i1;
                            int y = j + j1;

                            if (x >= 0 && x < largura && y >= 0 && y < altura) {
                                if (!(i1 == 0 && j1 == 0)) {
                                    int valorPixel = block[x][y];
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
                        resultMat[i][j] = 0;
                    } else {
                        if (contador > 0) {
                            resultMat[i][j] = soma / contador;
                        }
                    }
                }
            }
        }
        return resultMat;
    }

    public static void addWork(Block block) {
        workQueue.add(block);
    }

    public static BlockingQueue<Block> getResultQueue() {
        return resultQueue;
    }
}

class Block {
    int startX;
    int[][] data;

    Block(int startX, int[][] data) {
        this.startX = startX;
        this.data = data;
    }
}
