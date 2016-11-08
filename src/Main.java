import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimuch on 18.10.2016.
 * Строка в файле - это деталь.
 * Деталь состоит из элементов.
 * Элементы визуально представляют собой запись формата "[буква][цифра]".
 * С помощью класса Analysis, мы получаем список деталей и список всех элементов (50-53).
 * В матрице наличия (55-57) показано какие элементы (из общего списка) есть в каждой детале
 * В матрице совпадения(59-61) показано сколько элементов совпадает у каждой детали с каждой
 * (сама с собой 0 совпадений - так нужно! :D )
 * В (63-65) мы группируем детали в группы, это тяжелый процесс и описать его сложно =(
 *
 */
public class Main {

    //массив строк "деталей"
    private static String[] data;
    //просто константа для выделения памяти под массив
    private static int TEMP = 15;
    //лист листов с деталями
    private static List<List<Element>> alDetails;
    //лист разновидностей деталей
    private static List<Element> listTotalElements;
    //Матрица (наличия)
    private static int[][] matrixExistence;
    //Матрица (совпадения)
    private static int[][] matrixMatch;
    //лист групп деталей
    private static List<List<List<Element>>> alGroups;

    public static void main(String[] args) {

        //возвращает заполненый data[]
        data = getData();

        //Выводим в консоль содержимое файла с деталями
//        String stroka = "";
//        for (String s : data)
//            stroka += s + "\n";
//        System.out.println(stroka);

        //Analysis - класс для работы с data[]
        Analysis analysis = new Analysis(data);
        if (analysis.checkInput()) {
            //Получаем список деталей
            alDetails = analysis.getAlDetails();
            //Получаем список всех элементов
            listTotalElements = analysis.getTotalElements();

            //Формирование 1 матрицы (наличия)
            constructionMatrixExistence();
            showMatrixExistence();

            //Формирование 2 матрицы (совпадения)
            constructionMatrixMatch();
            showMatrixMatch();

            //объединения в группы
            grouping();
            showGroups();
        }
    }

    private static void constructionMatrixExistence() {
        //выделение памяти под 1 матрицу (наличия)
        matrixExistence = new int[data.length][];
        for (int i = 0; i < data.length; i++)
            matrixExistence[i] = new int[listTotalElements.size()];

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < listTotalElements.size(); j++) {
                String[] parts = data[i].split(" ");
                for (String str : parts)
                    if (listTotalElements.get(j).getName().equals(str)) {
                        matrixExistence[i][j] = 1;
                        break;
                    } else
                        matrixExistence[i][j] = 0;
            }
    }

    private static void showMatrixExistence() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < listTotalElements.size(); j++)
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
                for (int k = 0; k < listTotalElements.size(); k++) {
                    if (matrixExistence[i][k] == matrixExistence[j][k])
                        count++;
                }
                if (i != j) matrixMatch[i][j] = count;
                count = 0;
            }
        }
    }

    private static void showMatrixMatch() {
        for (int i = 0; i < data.length; i++) {
            System.out.println();
            for (int j = 0; j < data.length; j++)
                System.out.print(matrixMatch[i][j]);
        }
    }

    private static void grouping() {
        alGroups = new ArrayList<>();

        int[] arrayDetail = new int[data.length];
        for (int i = 0; i < data.length; i++)
            arrayDetail[i] = i + 1;

        while (arrayDetailIsExist(arrayDetail)) {
//            System.out.println(String.valueOf(arrayDetailIsExist(arrayDetail)));
            int maxI = 0;
            int maxJ = 0;

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data.length; j++)
                    if (matrixMatch[i][j] > matrixMatch[maxI][maxJ]) {
                        maxI = i;
                        maxJ = j;
                    }

            arrayDetail[maxI] = 0;
            arrayDetail[maxJ] = 0;
//            countGroup++;
//            countRow += 2;
            if (matrixMatch[maxI][maxJ] != 0) {
                List<List<Element>> alDetailsInGroup = new ArrayList<>();
                alDetailsInGroup.add(alDetails.get(maxI));
                alDetailsInGroup.add(alDetails.get(maxJ));
//                System.out.print("\nGroup " + countGroup + ": " + (maxI + 1) + ", " + (maxJ + 1));

                for (int i = 0; i < data.length; i++)
                    if (matrixMatch[i][maxJ] == matrixMatch[maxI][maxJ] && i != maxI) {
                        alDetailsInGroup.add(alDetails.get(i));
//                        System.out.print(", " + (i + 1));
//                        countRow++;
                        arrayDetail[i] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[i][k] = 0;
                            matrixMatch[k][i] = 0;
                        }
                    }
                for (int j = 0; j < data.length; j++)
                    if (matrixMatch[maxI][j] == matrixMatch[maxI][maxJ] && j != maxJ) {
                        alDetailsInGroup.add(alDetails.get(j));
//                        System.out.print(", " + (j + 1));
//                        countRow++;
                        arrayDetail[j] = 0;
                        for (int k = 0; k < data.length; k++) {
                            matrixMatch[k][j] = 0;
                            matrixMatch[j][k] = 0;
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
                alGroups.add(alDetailsInGroup);
            } else {
                List<List<Element>> alDetailsInGroup = new ArrayList<>();
//                System.out.print("\nGroup " + countGroup + ": ");
                for (int i = 0; i < data.length; i++)
                    if (arrayDetail[i] != 0) {
                        alDetailsInGroup.add(alDetails.get(i));
//                        System.out.print((i + 1) + ". ");
                        arrayDetail[i] = 0;
                    }
                alGroups.add(alDetailsInGroup);
            }
        }
    }

    private static void showGroups() {
        System.out.println("\n");
        for (int i = 0; i < alGroups.size(); i++) {
            System.out.print("Group " + (i + 1) + ":");
            for (int j = 0; j < alGroups.get(i).size(); j++) {
//                System.out.print(alGroups.get(i).get(j).toString() + "  ");
                System.out.print("  " + String.valueOf(alDetails.indexOf(alGroups.get(i).get(j)) + 1));
            }
            System.out.println(".");
        }
    }

    private static String[] getData() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "C:\\Data_\\_Workspace\\GKS\\src\\dataFile_f.txt"));

            String str;
            String[] tempData = new String[TEMP];
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

    private static boolean arrayDetailIsExist(int[] arrayDetail) {
        for (int i = 0; i < data.length; i++)
            if (arrayDetail[i] != 0)
                return true;
        return false;
    }
}
