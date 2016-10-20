import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by Dimuch on 18.10.2016.
 */
public class Main {

    //массив строк с "деталями"
    private static String[] data;
    //просто константа для выделения памяти под массив
    private static int temp = 15;
    //лист листов с деталями
    private static List<List<Detail>> alDetails;
    //лист разновидностей деталей
    private static List<String> listTotalDetails;
    private static int[][] matrixExistence;
    private static int[][] matrixMatch;

    public static void main(String[] args) {

        //возвращает заполненый data[]
        getData();

        String stroka = "";
        for (String s : data)
            stroka += s + "\n";
        System.out.println(stroka);

        //выводим в консоль data[]
//        for (String str : data)
//            System.out.println(str);

        //Analysis - класс для работы с data[]
        Analysis analysis = new Analysis(data);
        if (analysis.checkInput()) {
            alDetails = analysis.feelData();
            listTotalDetails = analysis.checkDetails();

            //Формирование 1 матрицы (наличия)
            constructionMatrixExistence();

            //Формирование 2 матрицы (совпадения)
            constructionMatrixMatch();

            //объединения в группы
            grouping();
        }
    }

    private static void constructionMatrixExistence() {
        //выделение памяти под 1 матрицу (наличия)
        matrixExistence = new int[data.length][];
        for (int i = 0; i < data.length; i++)
            matrixExistence[i] = new int[listTotalDetails.size()];

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < listTotalDetails.size(); j++) {
                String[] parts = data[i].split(" ");
                for (String str : parts)
                    if (listTotalDetails.get(j).equals(str)) {
                        matrixExistence[i][j] = 1;
                        break;
                    } else
                        matrixExistence[i][j] = 0;
            }
        showMatrixExistence();
    }

    private static void showMatrixExistence() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < listTotalDetails.size(); j++)
                System.out.print(matrixExistence[i][j]);
            System.out.println();
        }
    }

    private static void constructionMatrixMatch() {

        matrixMatch = new int[data.length][];
        for (int i = 0; i < data.length; i++)
            matrixMatch[i] = new int[data.length];

        int count = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                for (int k = 0; k < listTotalDetails.size(); k++) {
                    if (matrixExistence[i][k] == matrixExistence[j][k])
                        count++;
                }
                if ( i!=j ) matrixMatch[i][j] = count;
                count = 0;
            }
        }
        showMatrixMatch();
    }

    private static void showMatrixMatch() {
        for (int i = 0; i < data.length; i++) {
            System.out.println();
            for (int j = 0; j < data.length; j++)
                System.out.print(matrixMatch[i][j]);
        }
    }

    private static void grouping() {
        int countGroup = 0;
        int countRow = 0;
        int[] arrayDetail = new int[data.length];
        for (int i = 0; i < data.length; i++)
            arrayDetail[i] = i+1;

        System.out.println();

        while (countGroup < data.length && countRow < data.length) {
            int maxI = 0;
            int maxJ = 0;

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data.length; j++) {
                    if (matrixMatch[i][j] > matrixMatch[maxI][maxJ]) {
                        maxI = i;
                        maxJ = j;
                    }
                }

            arrayDetail[maxI] = 0;
            arrayDetail[maxJ] = 0;
            countGroup++;
            countRow += 2;
            if (matrixMatch[maxI][maxJ] != 0) {
                System.out.print("\nGroup " + countGroup + ": " + (maxI + 1) + ", " + (maxJ + 1));

                for (int i = 0; i < data.length; i++) {
                    if (matrixMatch[i][maxJ] == matrixMatch[maxI][maxJ] && i != maxI) {
                        System.out.print(", " + (i + 1));
                        countRow++;
                        arrayDetail[i] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[i][k] = 0;
                            matrixMatch[k][i] = 0;
                        }
                    }
                }
                for (int j = 0; j < data.length; j++) {
                    if (matrixMatch[maxI][j] == matrixMatch[maxI][maxJ] && j != maxJ) {
                        System.out.print(", " + (j + 1));
                        countRow++;
                        arrayDetail[j] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[k][j] = 0;
                            matrixMatch[j][k] = 0;
                        }
                    }
                }
                for (int i = 0; i < data.length; i++) {
                    matrixMatch[i][maxJ] = 0;
                    matrixMatch[maxJ][i] = 0;
                }
                for (int j = 0; j < data.length; j++) {
                    matrixMatch[maxI][j] = 0;
                    matrixMatch[j][maxI] = 0;
                }
            } else {
                System.out.print("\nGroup " + countGroup + ": ");
                for (int i = 0; i < data.length; i++)
                    if (arrayDetail[i] != 0) {
                        System.out.print((i + 1) + ". ");
                        arrayDetail[i] = 0;
                    }
            }
//            showMatrixMatch();
        }
    }

    private static String[] getData() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "C:\\Data_\\_Workspace\\GKS\\src\\dataFile_Catherine.txt"));

            String str;
            String[] tempData = new String[temp];
            int numberOfLines = 0;

            for (int i = 0; (str = in.readLine()) != null; i++) {
//                System.out.println(str);
                tempData[i] = str;
                numberOfLines = i + 1;
            }
            data = new String[numberOfLines];

            for (int i = 0; i < numberOfLines; i++)
                data[i] = tempData[i];

            in.close();
        } catch (IOException e) {
        }
        return data;
    }
}
