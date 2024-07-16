package processimagebalckwhite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;

public class ProcessImageBalckWhite {

    public static int[][] lerPixels(String caminho) {

        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(caminho));
            int largura = bufferedImage.getWidth(null);
            int altura = bufferedImage.getHeight(null);

            int[][] pixels = new int[largura][altura];
            for (int i = 0; i < largura; i++) {
                for (int j = 0; j < altura; j++) {
                    // normalizando de forma simplificada para imagem escala de cinza (é esperado
                    // ocorrer "clareamento")
                    float vermelho = new Color(bufferedImage.getRGB(i, j)).getRed();
                    float verde = new Color(bufferedImage.getRGB(i, j)).getGreen();
                    float azul = new Color(bufferedImage.getRGB(i, j)).getBlue();
                    int escalaCinza = (int) (vermelho + verde + azul) / 3;

                    pixels[i][j] = escalaCinza;
                }
            }

            return pixels;
        } catch (IOException ex) {
            System.err.println("Erro no caminho indicado pela imagem");
        }

        return null;
    }

    public static void gravarPixels(String caminhoGravar, int pixels[][]) {

        caminhoGravar = caminhoGravar
                .replace(".png", "_modificado.png")
                .replace(".jpg", "_modificado.jpg");

        int largura = pixels.length;
        int altura = pixels[0].length;

        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_GRAY);

        // transformando a mat. em um vetor de bytes
        byte bytesPixels[] = new byte[largura * altura];
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                bytesPixels[y * (largura) + x] = (byte) pixels[x][y];
            }
        }

        // copaindo todos os bytes para a nova imagem
        imagem.getRaster().setDataElements(0, 0, largura, altura, bytesPixels);

        // criamos o arquivo e gravamos os bytes da imagem nele
        File ImageFile = new File(caminhoGravar);
        try {
            ImageIO.write(imagem, "png", ImageFile);
            System.out.println("Nova Imagem dispon. em: " + caminhoGravar);
        } catch (IOException e) {
            System.err.println("Erro no caminho indicado pela imagem");
        }
    }

    public static int[][] corrigirImagem(int imgMat[][]) throws InterruptedException {
        int largura = imgMat.length;
        int altura = imgMat[0].length;
        int numThreads = Runtime.getRuntime().availableProcessors();
    
        Trabalhador[] trabs = new Trabalhador[numThreads];
        int chunkSize = largura / numThreads;
    
        for (int i = 0; i < numThreads; i++) {
            int startX = i * chunkSize;
            int endX = (i == numThreads - 1) ? largura : startX + chunkSize;
    
            int[][] block = new int[endX - startX][altura];
            for (int x = startX; x < endX; x++) {
                System.arraycopy(imgMat[x], 0, block[x - startX], 0, altura);
            }
    
            Trabalhador.addWork(new Block(startX, block));
            System.out.println("Bloco adicionado para thread, começando em " + startX);
        }
    
        for (int i = 0; i < numThreads; i++) {
            trabs[i] = new Trabalhador();
            trabs[i].start();
        }
    
        for (Trabalhador t : trabs) {
            t.join();
        }
    
        int[][] novaImgMat = new int[largura][altura];
        BlockingQueue<Block> resultQueue = Trabalhador.getResultQueue();
    
        while (!resultQueue.isEmpty()) {
            Block partialResult = resultQueue.poll();
            if (partialResult != null) {
                for (int x = 0; x < partialResult.data.length; x++) {
                    System.arraycopy(partialResult.data[x], 0, novaImgMat[partialResult.startX + x], 0, partialResult.data[x].length);
                }
            }
        }
    
        return novaImgMat;
    }
    
    public static void main(String[] args) {

        File directory = new File(
                "C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas");
        File imagesFile[] = directory.listFiles();

        // iamgens que precisam ser corrigidas
        for (File img : imagesFile) {
            int imgMat[][] = lerPixels(img.getAbsolutePath());

            // fica a seu critério modificar essa invocação
            try {
                imgMat = corrigirImagem(imgMat);

                if (imgMat != null) {
                    gravarPixels(img.getAbsolutePath(), imgMat);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(imgMat[125][742]);
            System.out.println(imgMat[126][742]);
            System.out.println(imgMat[127][742]);
            System.out.println(imgMat[125][743]);
            System.out.println(imgMat[126][743]);
            System.out.println(imgMat[127][743]);
            System.out.println(imgMat[125][744]);
            System.out.println(imgMat[126][744]);
            System.out.println(imgMat[127][744]);

            break;

        }
    }
}
