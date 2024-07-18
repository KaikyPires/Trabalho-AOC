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
    public static int[][] corrigirImagem(int imgMat[][]) {
        int numCpu = Runtime.getRuntime().availableProcessors();
        Trabalhador[] trabs = new Trabalhador[numCpu];
        int largura = imgMat.length;
        int altura = imgMat[0].length;
        int[][] result = new int[largura][altura];
    
        // Dividindo a imagem em partes iguais para cada thread
        int chunkSize = largura / numCpu;
        int startX, endX;
    
        for (int i = 0; i < numCpu; i++) {
            startX = i * chunkSize;
            if (i == numCpu - 1) {
                endX = largura; // A última thread vai até o final da imagem
            } else {
                endX = startX + chunkSize;
            }
            trabs[i] = new Trabalhador(startX, endX, imgMat, result);
            trabs[i].start();
        }
    
        // Esperando todas as threads terminarem
        for (Trabalhador t : trabs) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Erro na sincronização das threads");
            }
        }
    
        return result;
    }
    
    public static void main(String[] args) {
        
        File[] imageFiles = {
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (1).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (2).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (3).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (4).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (5).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (6).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (7).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (8).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (9).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (10).jpg"),
            new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas\\img (11).jpg")
        };
        for (File imgFile : imageFiles) {
            if (imgFile.exists()) {
                int imgMat[][] = lerPixels(imgFile.getAbsolutePath());
                imgMat = corrigirImagem(imgMat);
                gravarPixels(imgFile.getAbsolutePath(), imgMat);
            } else {
                System.err.println("Arquivo de imagem não encontrado: " + imgFile.getAbsolutePath());
            }
           
            /*System.out.println(imgMat[125][742]);
            System.out.println(imgMat[126][742]);
            System.out.println(imgMat[127][742]);
            System.out.println(imgMat[125][743]);
            System.out.println(imgMat[126][743]);
            System.out.println(imgMat[127][743]);
            System.out.println(imgMat[125][744]);
            System.out.println(imgMat[126][744]);
            System.out.println(imgMat[127][744]);

            break;*/

        }
    }
}
