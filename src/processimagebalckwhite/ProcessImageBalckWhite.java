package processimagebalckwhite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
					//normalizando de forma simplificada para imagem escala de cinza (é esperado ocorrer "clareamento")
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

        //transformando a mat. em um vetor de bytes
        byte bytesPixels[] = new byte[largura * altura];
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                bytesPixels[y * (largura) + x] = (byte) pixels[x][y];
            }
        }

        //copaindo todos os bytes para a nova imagem
        imagem.getRaster().setDataElements(0, 0, largura, altura, bytesPixels);

        //criamos o arquivo e gravamos os bytes da imagem nele
        File ImageFile = new File(caminhoGravar);
        try {
            ImageIO.write(imagem, "png", ImageFile);
            System.out.println("Nova Imagem dispon. em: " + caminhoGravar);
        } catch (IOException e) {
            System.err.println("Erro no caminho indicado pela imagem");
        }
    }

    
    public static int[][] corrigirImagem(int imgMat[][]) {
        int largura = imgMat.length;
        int altura = imgMat[0].length;
    
        int[][] novaImgMat = new int[largura][altura];
    
        // Copiando os valores originais para a nova matriz
        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                novaImgMat[i][j] = imgMat[i][j];
            }
        }
    
        // Percorrendo a matriz para encontrar pixels com valor 0 ou 255
        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                if (imgMat[i][j] == 0 || imgMat[i][j] == 255) {
                    int soma = 0;
                    int contador = 0;
                    int contadorPretos = 0;
    
                    // Somando os pixels vizinhos na matriz 3x3 e contando os pixels pretos
                    for (int i1 = -1; i1 <= 1; i1++) {
                        for (int j1 = -1; j1 <= 1; j1++) {
                            int x = i + i1;
                            int y = j + j1;
    
                            // Verificando se o pixel vizinho está dentro dos limites da imagem
                            if (x >= 0 && x < largura && y >= 0 && y < altura) {
                                // Ignorando o pixel central
                                if (!(i1 == 0 && j1 == 0)) {
                                    int valorPixel = imgMat[x][y];
                                    soma += valorPixel;
                                    contador++;
                                    if (valorPixel == 0) {
                                        contadorPretos++;
                                    }
                                }
                            }
                        }
                    }
    
                    // Se a maioria dos pixels vizinhos for preta, o pixel central vira 0
                    if (contadorPretos > contador / 2) {
                        novaImgMat[i][j] = 0;
                    } else {
                        // Caso contrário, calcula a média dos pixels vizinhos
                        if (contador > 0) {
                            novaImgMat[i][j] = soma / contador;
                        }
                    }
                }
            }
        }
    
        return novaImgMat;
    }
    
    
    

    public static void main(String[] args) {

        File directory = new File("C:\\Users\\Kaiky Pires\\Downloads\\Trabalho-Saulo\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas");
        File imagesFile[] = directory.listFiles();
        
        //iamgens que precisam ser corrigidas
        for(File img : imagesFile){
            int imgMat[][] = lerPixels(img.getAbsolutePath());
            
            //fica a seu critério modificar essa invocação
           imgMat = corrigirImagem(imgMat);
            
            
           //grava nova imagem com as correções
           if(imgMat != null){
             gravarPixels(img.getAbsolutePath(), imgMat);
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
