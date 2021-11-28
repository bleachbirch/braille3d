import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import java.io.*;
import java.util.zip.DataFormatException;

/**
 * Created by vlus on 22.05.2016.
 */
public class Translator {

    private ConfigReader config;
    private String inputText;
    private String svg;

    private int linesCount;
    private int symbolsCount;
    private double circleRadio;
    private double circlesRatio;
    private double widthRatio;
    private double heigthRatio;




    public Translator(File inputFile, String brailleparamsPath) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "CP1251"));
        String text = "";
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            text += line;
        }
        bufferedReader.close();
        this.inputText = text;
        if(text.length()>500) {
            throw new UnsupportedOperationException("Text's length is too long");
        }
        this.config = new ConfigReader(brailleparamsPath);

        this.linesCount = (int)config.getParamByName("lines_count");
        this.symbolsCount = (int)config.getParamByName("tokens_count");
        this.circleRadio = config.getParamByName("circle_radio");
        this.circlesRatio = config.getParamByName("circles_ratio");
        this.widthRatio = config.getParamByName("width_ratio");
        this.heigthRatio = config.getParamByName("heigth_ratio");
    }

    public void convertToSVG(String outparamsPath) throws IOException{

        File paramsFile = new File(outparamsPath);

        //Считываем данные из outparams
        /*FileReader fr = new FileReader(paramsFile);
        char[] outParams = new char[(int)paramsFile.length()];
        fr.read(outParams);
        fr.close();*/

        //Заполнение свободного пространства пробелами
        BrailleTable brailleTable = new BrailleTable();
        int rest = symbolsCount*linesCount - this.inputText.length()%(symbolsCount* linesCount);
        for(int restIndex=0; restIndex<rest; restIndex++) {
            this.inputText += "~";
        }

        for (int tableIndex=0; tableIndex<this.inputText.length(); tableIndex+=symbolsCount) {
            BrailleLine brailleLine = new BrailleLine();
            for(int lineIndex=0; lineIndex<symbolsCount; lineIndex++) {
                int position = tableIndex + lineIndex;
                String token = this.inputText.substring(position, position + 1);
                brailleLine.add(new BrailleToken(token));
            }
            brailleTable.add(brailleLine);
        }
        //brailleTable.print();

        double currentVerticalPos = 10;
        double currentHorizontalPos = 15;
        int tokenIndex = 0;
        int lineIndex = 0;

        //открыли outparams для записи координат
        String brailleTableParams = new String();
        FileOutputStream fw = new FileOutputStream(paramsFile, true);

        for(BrailleLine line: brailleTable.getBrailleLines()) {
            tokenIndex = 0;
            for(BrailleToken token:line.getBrailleTokens()) {
                /*this.svg += "<rect " +
                        "x=\"" + (int)getRX(tokenIndex) + "\" " +
                        "y=\"" + (int)getRY(lineIndex) + "\" " +
                        "width=\"" + (int)(this.widthRatio + 1 * this.circleRadio + this.circlesRatio) + "\" " +
                        "height=\"" + (int)(this.heigthRatio + 2 * this.circleRadio + 2 * this.circlesRatio) + "\" " +
                        "style=\"fill:none;stroke:black;stroke-width:5\" />";*/
                for(int circleIndex=0; circleIndex < token.getCirclesPositions().size(); circleIndex++) {
                    int cx = (int)currentHorizontalPos + (int)getCX(tokenIndex, token.getCirclesPositions().get(circleIndex).getXPosition());
                    int cy = (int)currentVerticalPos + (int)getCY(lineIndex, token.getCirclesPositions().get(circleIndex).getYPosition());
                    brailleTableParams = (cx + " " + cy + System.getProperty("line.separator"));
                    fw.write(brailleTableParams.getBytes(), 0, brailleTableParams.length());
                    /*this.svg += "<circle " +
                            "cx=\"" + cx + "\" " +
                            "cy=\"" + cy + "\" " +
                            "r=\"" +  (int)circleRadio + "\" " +
                            "stroke=\"black\" " +
                            "stroke-width=\"1\" " +
                            "fill=\"black\" /> ";
                    this.svg += "<rect " +
                            "x=\"" + (int)getRX(tokenIndex) + "\" " +
                            "y=\"" + (int)getRY(lineIndex) + "\" " +
                            "width=\"" + (int)(this.widthRatio + 1 * this.circleRadio + this.circlesRatio) + "\" " +
                            "height=\"" + (int)(this.heigthRatio + 2 * this.circleRadio + 2 * this.circlesRatio) + "\" " +
                            "style=\"fill:none;stroke:black;stroke-width:5\" />";*/
                }
                currentHorizontalPos += widthRatio;
                tokenIndex++;
            }
            lineIndex++;
            currentVerticalPos += heigthRatio;
            currentHorizontalPos = 15;
        }

        fw.close();
        /*File paramsFile = new File("C:\\Users\\vlusslus\\IdeaProjects\\Braille3D\\outparams.txt");
        if(paramsFile.exists()) {
            try{
                FileWriter fw = new FileWriter(paramsFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(brailleTableParams);
                bw.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        File svgFile = new File(svgPath);
        if(svgFile.exists()) {
            try{
                FileWriter fw2 = new FileWriter(svgFile);
                BufferedWriter bw2 = new BufferedWriter(fw2);
                bw2.write(this.svg);
                bw2.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }*/
    }

    private double getCX(int tokenIndex, int circleX) {

        return /*this.widthRatio/2 + */tokenIndex * (this.widthRatio + 2 * this.circleRadio + this.circlesRatio) + (circleX - 1) * (this.circlesRatio + this.circleRadio);
    }

    private double getCY(int lineIndex, int circleY) {

        return /*this.heigthRatio/2 + */lineIndex * (this.heigthRatio + 3 * this.circleRadio + 2 * this.circlesRatio) + (circleY - 1) * (this.circlesRatio + this.circleRadio);
    }

    private double getRX(int tokenIndex) {
        return tokenIndex * (this.widthRatio + 2 * this.circleRadio + this.circlesRatio);
    }

    private double getRY(int lineIndex) {
        return lineIndex * (this.heigthRatio + 3 * this.circleRadio + 2 * this.circlesRatio);
    }




}
