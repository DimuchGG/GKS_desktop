import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
 */
public class Main {

    public static final int RESTRICTION = 5;
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
    private static List<List<List<Element>>> alGroupDetails;
    //лист групп уникальных (неповторяющихся) деталей
    private static List<List<Element>> alGroupUniqueElements;
    //лист упрощенных групп деталей
    private static List<List<List<Element>>> alSimpleGroupDetails;
    //лист модулей
    private static List<List<Element>> alModules;

    public static void main(String[] args) {

        //возвращает заполненый data[]
        data = getData();

        //Выводим в консоль содержимое файла с деталями
        String stroka = "";
        for (String s : data)
            stroka += s + "\n";
        System.out.print(stroka);

        //Analysis - класс для работы с data[]
        Analysis analysis = new Analysis(data);
        if (analysis.checkInput()) {
            //Получаем список деталей
            alDetails = analysis.getAlDetails();
            //Получаем список всех элементов
            listTotalElements = analysis.getTotalElements();
            System.out.println("\n" + listTotalElements.toString());

            //Формирование 1 матрицы (наличия)
            constructionMatrixExistence();
            showMatrixExistence();

            //Формирование 2 матрицы (совпадения)
            constructionMatrixMatch();
            showMatrixMatch();

            //объединение в группы
            unionOfDetailsInGroups();
            showGroupDetails(alGroupDetails);

            //создание групп с уникальными деталями
            alGroupUniqueElements = createGroupUniqueDetails(alGroupDetails);
//            showGroupUniqueDetails("Group", alGroupUniqueElements);

            //сортировка листов групп по колличеству элементов в листе уникальных элементов
            sortListDetailsAndListUniqueDetails();
//            showGroupDetails(alGroupDetails);
//            showGroupUniqueDetails("Group", alGroupUniqueElements);

            //упрощение групп
            alSimpleGroupDetails = simplifyGroups();
            showGroupDetails(alSimpleGroupDetails);
//            showGroupDetailsOnTheElements(alSimpleGroupDetails);

            //создание модулей
            createModule();
            showGroupUniqueDetails("Module", alModules);
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
                for (String str : parts) {
//                    System.out.println(String.valueOf(listTotalElements.get(j).getName() + "   *   " + str + " = " + listTotalElements.get(j).getName().equals(str)));
                    if (listTotalElements.get(j).getName().equals(str)) {
                        matrixExistence[i][j] = 1;
                        break;
                    } else
                        matrixExistence[i][j] = 0;
                }
            }
    }

    private static void showMatrixExistence() {
        System.out.println();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < listTotalElements.size(); j++)
                System.out.print(matrixExistence[i][j] + "  ");
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
        System.out.println();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++)
                System.out.print(matrixMatch[i][j] + "  ");
            System.out.println();
        }
    }

    private static void unionOfDetailsInGroups() {
        alGroupDetails = new ArrayList<>();

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
                alGroupDetails.add(alDetailsInGroup);
            } else {
                List<List<Element>> alDetailsInGroup = new ArrayList<>();
//                System.out.print("\nGroup " + countGroup + ": ");
                for (int i = 0; i < data.length; i++)
                    if (arrayDetail[i] != 0) {
                        alDetailsInGroup.add(alDetails.get(i));
//                        System.out.print((i + 1) + ". ");
                        arrayDetail[i] = 0;
                    }
                alGroupDetails.add(alDetailsInGroup);
            }
        }
    }

    private static void showGroupDetails(List<List<List<Element>>> alGroupDetails) {
        System.out.println("");
        for (int i = 0; i < alGroupDetails.size(); i++) {
            System.out.print("Group " + (i + 1) + ":");
            for (int j = 0; j < alGroupDetails.get(i).size(); j++) {
//                System.out.print("  " + alGroupDetails.get(i).get(j).toString());
                System.out.print("  " + String.valueOf(alDetails.indexOf(alGroupDetails.get(i).get(j)) + 1));
            }
            System.out.println(".");
        }
    }

    private static void showGroupDetailsOnTheElements(List<List<List<Element>>> alGroupDetails) {
        System.out.println("");
        for (int i = 0; i < alGroupDetails.size(); i++) {
            System.out.print("Group " + (i + 1) + ":");
            for (int j = 0; j < alGroupDetails.get(i).size(); j++) {
                System.out.print("  " + alGroupDetails.get(i).get(j).toString());
//                System.out.print("  " + String.valueOf(alDetails.indexOf(alGroupDetails.get(i).get(j)) + 1));
            }
            System.out.println(".");
        }
    }

    private static List<List<Element>> createGroupUniqueDetails(List<List<List<Element>>> alGroupDetails) {
        List<List<Element>> alGroupUniqueElements = new ArrayList<>();

        for (int i = 0; i < alGroupDetails.size(); i++) {
            List<Element> alElementsInGroup = new ArrayList<>();
            for (int j = 0; j < alGroupDetails.get(i).size(); j++)
                for (int k = 0; k < alGroupDetails.get(i).get(j).size(); k++) {
                    if (alElementsInGroup.isEmpty()) {
                        alElementsInGroup.add(alGroupDetails.get(i).get(j).get(0));
                        k++;
                    }
                    for (int l = 0; l < alElementsInGroup.size(); l++)
                        if (alGroupDetails.get(i).get(j).get(k).equals(alElementsInGroup.get(l))) {
                            break;
                        } else if (l == alElementsInGroup.size() - 1) {
                            alElementsInGroup.add(alGroupDetails.get(i).get(j).get(k));
                            break;
                        }
                }
            alGroupUniqueElements.add(alElementsInGroup);
        }
        return alGroupUniqueElements;
    }

    private static void showGroupUniqueDetails(String message, List<List<Element>> alGroupUniqueElements) {
        System.out.println("");
        for (int i = 0; i < alGroupUniqueElements.size(); i++)
            System.out.println(message + " " + (i + 1) + ":" + "  " + alGroupUniqueElements.get(i).toString() + ".");
    }

    private static void sortListDetailsAndListUniqueDetails() {
        for (int i = 0; i < alGroupUniqueElements.size(); i++) {
            for (int j = i; j < alGroupUniqueElements.size(); j++) {
                if (alGroupUniqueElements.get(j).size() > alGroupUniqueElements.get(i).size()) {
                    List<List<Element>> lleTemp = alGroupDetails.get(j);
                    alGroupDetails.set(j, alGroupDetails.get(i));
                    alGroupDetails.set(i, lleTemp);

                    List<Element> leTemp = alGroupUniqueElements.get(j);
                    alGroupUniqueElements.set(j, alGroupUniqueElements.get(i));
                    alGroupUniqueElements.set(i, leTemp);
                }
            }
        }
    }

    private static List<List<List<Element>>> simplifyGroups() {
        List<List<List<Element>>> alSimpleGroupDetails = new ArrayList<>();

        for (int i = 0; i < alGroupDetails.size(); i++) {
            if (alGroupDetails.get(i).size() == 0)
                break;
            else
                alSimpleGroupDetails.add(alGroupDetails.get(i));
            for (int j = alGroupDetails.size() - 1; j > i; j--) {
                for (int k = alGroupDetails.get(j).size() - 1; k >= 0; k--) {
                    if (isIncludeListElements(alGroupUniqueElements.get(i), alGroupDetails.get(j).get(k))) {
                        alSimpleGroupDetails.get(i).add(alGroupDetails.get(j).get(k));
                        alGroupDetails.get(j).remove(k);
                    }
                }
            }
            alGroupUniqueElements = createGroupUniqueDetails(alGroupDetails);
            sortListDetailsAndListUniqueDetails();
        }
        return alSimpleGroupDetails;
    }

    private static void createModule() {
        List<List<Element>> alGroupUniqueElements = createGroupUniqueDetails(alSimpleGroupDetails);
//        showGroupUniqueDetails("Group", alGroupUniqueElements);

        for (int i = 0; i < 1; i++) {
//        for (int i = 0; i < alGroupUniqueElements.size(); i++) {
            List<List<Element>> alLinksBetweenDetails = creatingLinksBetweenDetails(alSimpleGroupDetails.get(i));
            List<List<Boolean>> matrixForGraph = createMatrixLinks(alLinksBetweenDetails,
                    alGroupUniqueElements.get(i));

            alModules = new ArrayList<>();

            combineFirstRule(matrixForGraph, alGroupUniqueElements.get(i));
//            System.out.println(alModules);
//            combineSecondRule(matrixForGraph);
//            combineThirdRule(matrixForGraph);
        }

    }

    private static boolean combineThirdRule(List<List<Boolean>> matrixForGraph) {
        boolean isCombine = false;

        return isCombine;
    }

    private static boolean combineSecondRule(List<List<Boolean>> matrixForGraph) {
        boolean isCombine = false;

        return isCombine;
    }

    private static boolean combineFirstRule(List<List<Boolean>> matrixForGraph, List<Element> alGroupUniqueElements) {
        boolean isCombine = false;
        for (int i = 0; i < matrixForGraph.size(); i++) {
            for (int j = 0; j < matrixForGraph.size(); j++) {
                if (matrixForGraph.get(i).get(j) && matrixForGraph.get(j).get(i)) {
//                    showMatrix_0_1(matrixForGraph);
//                    System.out.println(" i = " + i + " j = " + j +
//                            " Element[i] " + alGroupUniqueElements.get(i) +
//                            " Element[j] " + alGroupUniqueElements.get(j));

//                    System.out.println("****************************");
//                    System.out.println();
//
//
//                    System.out.println();
//                    System.out.println("****************************");

                    List<Element> alCombineElements = getALCombineElements(alGroupUniqueElements, new int[]{i, j});
                    List<Element> rowModule = belongsToModules(alCombineElements);
                    List<Element> alNewElement = newElementForModule(rowModule, alCombineElements);
                    if (rowModule.isEmpty()) {
                        for (int k = 0; k < alCombineElements.size(); k++) {
                            rowModule.add(alCombineElements.get(k));
                        }
                        alModules.add(rowModule);
//                        System.out.println(false);
                    } else if (!rowModule.isEmpty() && (rowModule.size() + alNewElement.size() <= RESTRICTION)) {
//                        System.out.println(alNewElement);
                        for (int k = 0; k < alNewElement.size(); k++) {
                            rowModule.add(alNewElement.get(k));
                        }
//                        System.out.println(true);
                    } else if (!rowModule.isEmpty() && (rowModule.size() + alNewElement.size() > RESTRICTION)) {
                        for (int k = 0; k < alNewElement.size(); k++) {
                            List<Element> elementOfALNewElement = new ArrayList<>();
                            elementOfALNewElement.add(alNewElement.get(k));
                            alModules.add(elementOfALNewElement);
                        }
                        for (int k = 0; k < matrixForGraph.size(); k++) {
                            matrixForGraph.get(k).remove(i);
                        }
                        matrixForGraph.remove(i);
                        alGroupUniqueElements.remove(i);
                    }

//                    System.out.println(alModules);

                    combineRows(matrixForGraph, alGroupUniqueElements, new int[]{i, j});
                    combineFirstRule(matrixForGraph, alGroupUniqueElements);
                    isCombine = true;
                    break;
//                    System.out.println(alModules);
//                    showMatrix_0_1(matrixForGraph);
//                    isCombine = true;
//                    System.out.println(true);
                }
            }
        }
        if (!isCombine) {
            for (int k = 0; k < alGroupUniqueElements.size(); k++) {
                List<Element> elementThatRemained = new ArrayList<>();
                elementThatRemained.add(alGroupUniqueElements.get(k));
                alModules.add(elementThatRemained);
            }
        }
        return isCombine;
    }

    private static List<Element> getALCombineElements(List<Element> alGroupUniqueElements, int[] ints) {
        List<Element> alCombineElements = new ArrayList<>();
        for (int i = 0; i < ints.length; i++) {
            alCombineElements.add(alGroupUniqueElements.get(ints[i]));
        }
        return alCombineElements;
    }

    private static List<Element> newElementForModule(List<Element> rowModule, List<Element> alCombineElements) {
        List<Element> newElement = new ArrayList<>();
        for (int i = 0; i < alCombineElements.size(); i++) {
            boolean isBelongs = false;
            for (int j = 0; j < rowModule.size(); j++) {
//                System.out.println("rowModule = " + rowModule.get(j));
//                System.out.println("alCombineElements = " + alCombineElements.get(i));
                if (rowModule.get(j).equals(alCombineElements.get(i))) {
                    isBelongs = true;
                    break;
                }
            }
            if (!isBelongs) newElement.add(alCombineElements.get(i));
        }
        return newElement;
    }

    private static List<Element> belongsToModules(List<Element> alCombineElements) {
        for (int i = 0; i < alModules.size(); i++) {
            for (int j = 0; j < alModules.get(i).size(); j++) {
                for (int k = 0; k < alCombineElements.size(); k++) {
                    if (alModules.get(i).get(j).equals(alCombineElements.get(k)))
                        return alModules.get(i);
                }
            }
        }
        return new ArrayList<>();
    }

    private static void combineRows(List<List<Boolean>> matrixForGraph, List<Element> alGroupUniqueElements, int[] rows) {


//        System.out.println();
//        System.out.print("rows:");
//        for (int i : rows) System.out.print(" " + i);
//        System.out.print(" Elements " + alGroupUniqueElements);
//        showMatrix_0_1(matrixForGraph);

        for (int i = 1; i < rows.length; i++) {
            for (int j = 0; j < matrixForGraph.size(); j++) {
                if (matrixForGraph.get(rows[i]).get(j) && rows[0] != j) {
                    matrixForGraph.get(rows[0]).set(j, true);
//                    System.out.println(" rows[" + i + "] = " + rows[i] + " i = " + j);
//                    System.out.println(" rows[0] = " + rows[0] + " i = " + j);
                }
                if (matrixForGraph.get(j).get(rows[i]) && rows[0] != j) {
                    matrixForGraph.get(j).set(rows[0], true);
//                    System.out.println(" i = " + j + " rows[" + i + "] = " + rows[i]);
//                    System.out.println(" i = " + j + " rows[0] = " + rows[0]);
                }
            }
        }
//        showMatrix_0_1(matrixForGraph);
        for (int i = 1; i < rows.length; i++) {
            for (int j = 0; j < matrixForGraph.size(); j++) {
                matrixForGraph.get(j).remove(rows[i] - (i - 1));
            }
            matrixForGraph.remove(rows[i] - (i - 1));
            alGroupUniqueElements.remove(rows[i] - (i - 1));
        }

//        showMatrix_0_1(matrixForGraph);
//        alModules.add(rowModule);
    }

    private static void showMatrix_0_1(List<List<Boolean>> matrixForGraph) {
        System.out.println("");
        for (int i = 0; i < matrixForGraph.size(); i++) {
            for (int j = 0; j < matrixForGraph.size(); j++)
                if (matrixForGraph.get(i).get(j))
                    System.out.print(1 + " ");
                else
                    System.out.print(0 + " ");
            System.out.println();
        }
    }

    private static List<List<Element>> creatingLinksBetweenDetails(List<List<Element>> lists) {
        List<List<Element>> alLinks = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size() - 1; j++) {
                List<Element> rowLinks = new ArrayList<>();
                rowLinks.add(lists.get(i).get(j));
                rowLinks.add(lists.get(i).get(j + 1));
                alLinks.add(rowLinks);
            }
        }
        return alLinks;
    }

    private static List<List<Boolean>> createMatrixLinks(List<List<Element>> alLinksBetweenDetails, List<Element> elements) {
        List<List<Boolean>> matrixForGraph = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            List<Boolean> rowMatrixForGraph = new ArrayList<>();
            for (int j = 0; j < elements.size(); j++) {
                if (isBetweenTheElementsOfLink(alLinksBetweenDetails, elements.get(i), elements.get(j)))
                    rowMatrixForGraph.add(true);
                else rowMatrixForGraph.add(false);
            }
            matrixForGraph.add(rowMatrixForGraph);
        }
        return matrixForGraph;
    }

    private static boolean isBetweenTheElementsOfLink(List<List<Element>> alLinksBetweenDetails, Element el1, Element el2) {
        for (int i = 0; i < alLinksBetweenDetails.size(); i++) {
            if (alLinksBetweenDetails.get(i).get(0).equals(el1) &&
                    alLinksBetweenDetails.get(i).get(1).equals(el2))
                return true;
        }
        return false;
    }

    private static boolean isIncludeListElements(List<Element> alUniqueElements, List<Element> alElementsInDetail) {
        int count = 0;
        for (int i = 0; i < alElementsInDetail.size(); i++)
            for (int j = 0; j < alUniqueElements.size(); j++)
                if (alElementsInDetail.get(i).equals(alUniqueElements.get(j))) {
                    count++;
                    break;
                }
        if (alElementsInDetail.size() == count)
            return true;
        else
            return false;
    }

    private static String[] getData() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "D:\\JavaProject\\GKS_desktop\\src\\dataFile_Dmitry.txt"));

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
