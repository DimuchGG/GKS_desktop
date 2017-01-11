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
    //лист модулей в группе
    private static List<List<Element>> alModules;
    //лист модулей
    private static List<List<List<Element>>> alGroupModules;
    private static int counter = 0;

    public static void main(String[] args) {

        //возвращает заполненый data[]
        data = getData();

        //Выводим в консоль содержимое файла с деталями
        String stroka = "";
        for (String s : data)
            stroka += s + "\n";
//        System.out.print(stroka);

        //Analysis - класс для работы с data[]
        Analysis analysis = new Analysis(data);
        if (analysis.checkInput()) {
            //Получаем список деталей
            alDetails = analysis.getAlDetails();
            //Получаем список всех элементов
            listTotalElements = analysis.getTotalElements();
//            System.out.println("\n" + listTotalElements.toString());

            //Формирование 1 матрицы (наличия)
            constructionMatrixExistence();
            showMatrixExistence();

            //Формирование 2 матрицы (совпадения)
            constructionMatrixMatch();
            showMatrixMatch();

            //объединение в группы
            unionOfDetailsInGroups();
            showGroupDetails("Group", alGroupDetails);

            //создание групп с уникальными деталями
            alGroupUniqueElements = createGroupUniqueDetails(alGroupDetails);
//            showGroupUniqueDetails("Group", alGroupUniqueElements);

            //сортировка листов групп по колличеству элементов в листе уникальных элементов
            sortListDetailsAndListUniqueDetails();
//            showGroupDetails("Group", alGroupDetails);
//            showGroupUniqueDetails("Group", alGroupUniqueElements);

            //упрощение групп
            alSimpleGroupDetails = simplifyGroups();
            showGroupDetails("Group", alSimpleGroupDetails);
//            showGroupDetailsOnTheElements(alSimpleGroupDetails);

            //создание модулей
            createModule();
            showGroupModules("Group", alGroupModules);
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

    private static void showGroupDetails(String message, List<List<List<Element>>> alGroupDetails) {
        System.out.println("");
        for (int i = 0; i < alGroupDetails.size(); i++) {
            System.out.print(message + " " + (i + 1) + ":");
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

        alGroupModules = new ArrayList<>();

//        for (int i = 0; i < 1; i++) {
        for (int i = 0; i < alGroupUniqueElements.size(); i++) {
            List<List<Element>> alLinksBetweenDetails = creatingLinksBetweenDetails(alSimpleGroupDetails.get(i));
//            List<List<Element>> alLinksBetweenDetails = creatingLinksBetweenDetails();

            List<List<Boolean>> matrixForGraph = createMatrixLinks(alLinksBetweenDetails,
                    alGroupUniqueElements.get(i));

            alModules = new ArrayList<>();

            List<List<Element>> alElementaryGraphChain;

            alElementaryGraphChain = createElementaryGraphChain(matrixForGraph, alGroupUniqueElements.get(i));
            sortALElementaryGraphChain(alElementaryGraphChain);
            removeExcessInChain(alElementaryGraphChain);
//            show3List(alElementaryGraphChain);
//            showMatrix_0_1(matrixForGraph);

            combineThirdRule(matrixForGraph, alGroupUniqueElements.get(i),
                    alLinksBetweenDetails, alElementaryGraphChain);

            alElementaryGraphChain = createElementaryGraphChain(matrixForGraph, alGroupUniqueElements.get(i));
            sortALElementaryGraphChain(alElementaryGraphChain);
            removeExcessInChain(alElementaryGraphChain);

            combineFirstRule(matrixForGraph, alGroupUniqueElements.get(i),
                    alLinksBetweenDetails, alElementaryGraphChain);

            alGroupModules.add(alModules);
        }
    }

    private static void showGroupModules(String message, List<List<List<Element>>> alGroupDetails) {
        System.out.println("");
        for (int i = 0; i < alGroupDetails.size(); i++) {
            System.out.print(message + " " + (i + 1) + ":");
            for (int j = 0; j < alGroupDetails.get(i).size(); j++) {
                System.out.print("\n\t\tModule " + (j + 1) + ": " + alGroupDetails.get(i).get(j).toString());
//                System.out.print("  " + String.valueOf(alDetails.indexOf(alGroupDetails.get(i).get(j)) + 1));
            }
            System.out.println(".");
        }
    }

    private static List<List<Element>> creatingLinksBetweenDetails() {
        List<List<Element>> alLinksBetweenDetails = new ArrayList<>();

        List<Element> temp = new ArrayList<>();
        temp.add(new SpecificElement("T1"));
        temp.add(new SpecificElement("T2"));
        alLinksBetweenDetails.add(temp);

        temp = new ArrayList<>();
        temp.add(new SpecificElement("T2"));
        temp.add(new SpecificElement("T1"));
        alLinksBetweenDetails.add(temp);
        return alLinksBetweenDetails;
    }

    private static void sortALElementaryGraphChain(List<List<Element>> alElementaryGraphChain) {
        Collections.sort(alElementaryGraphChain,
                (o1, o2) -> ((o1.size() < o2.size()) ? 1 : (o1.size() == o2.size() ? 0 : -1)));
    }

    private static void removeExcessInChain(List<List<Element>> alElementaryGraphChain) {
        for (int i = 0; i < alElementaryGraphChain.size(); i++) {
            if (alElementaryGraphChain.get(i).size() > RESTRICTION) {
                for (int j = 5; j < alElementaryGraphChain.get(i).size(); j++) {
                    alElementaryGraphChain.get(i).remove(j);
                    j--;
                }
            }
            if (i > 0 && alElementaryGraphChain.get(i).equals(alElementaryGraphChain.get(i - 1))) {
                alElementaryGraphChain.remove(i);
                i--;
            }
        }
    }

    private static void show3List(List<List<Element>> list) {
        System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    private static List<List<Element>> createElementaryGraphChain(List<List<Boolean>> matrixForGraph,
                                                                  List<Element> alUniqueElements) {
        List<List<Element>> alElementaryGraphChain = new ArrayList<>();
        for (int i = 0; i < matrixForGraph.size(); i++) {
            for (int j = 0; j < matrixForGraph.size(); j++) {
                if (matrixForGraph.get(i).get(j)) {
                    List<Element> elementaryGraphChain = new ArrayList<>();
                    elementaryGraphChain.add(alUniqueElements.get(i));
                    addVertex(matrixForGraph, alElementaryGraphChain, elementaryGraphChain, j, alUniqueElements);
                }
            }
        }
        return alElementaryGraphChain;
    }

    private static void addVertex(List<List<Boolean>> matrixForGraph, List<List<Element>> alElementaryGraphChain,
                                  List<Element> elementaryGraphChain, int firstVertex, List<Element> alUniqueElements) {
        elementaryGraphChain.add(alUniqueElements.get(firstVertex));
        List<Element> tempElementaryGraphChain = new ArrayList<>();
        tempElementaryGraphChain.addAll(elementaryGraphChain);
        alElementaryGraphChain.add(tempElementaryGraphChain);
        for (int i = 0; i < matrixForGraph.size(); i++) {
            if (matrixForGraph.get(firstVertex).get(i) && elementaryGraphChain.contains(alUniqueElements.get(i))) {
                continue;
            } else if (matrixForGraph.get(firstVertex).get(i)) {
                List<Element> saveElementaryGraphChain = new ArrayList<>();
                saveElementaryGraphChain.addAll(elementaryGraphChain);
                addVertex(matrixForGraph, alElementaryGraphChain, elementaryGraphChain, i, alUniqueElements);
                elementaryGraphChain = saveElementaryGraphChain;
            }
        }
    }

    private static boolean combineThirdRule(List<List<Boolean>> matrixForGraph, List<Element> alUniqueElements,
                                            List<List<Element>> alLinksBetweenDetails,
                                            List<List<Element>> alElementaryGraphChain) {
        boolean isCombine = false;
        for (int i = 0; i < alElementaryGraphChain.size(); i++) {
            if (isBetweenTheElementsOfLink(alLinksBetweenDetails, alElementaryGraphChain.get(i).get(0),
                    alElementaryGraphChain.get(i).get(alElementaryGraphChain.get(i).size() - 1))
                    && alElementaryGraphChain.get(i).size() > 2) {
                //мы нашли цепь подходящую под правило (3)

                //нужно создать модули из элементов цепи
                List<Integer> rowsForCombine = new ArrayList<>();
                for (int j = 0; j < alElementaryGraphChain.get(i).size(); j++) {
                    rowsForCombine.add(alUniqueElements.indexOf(alElementaryGraphChain.get(i).get(j)));
                }
//                System.out.println(rowsForCombine);

                List<Element> alCombineElements = getALCombineElements(alUniqueElements, rowsForCombine);
//                System.out.println(alCombineElements);
                List<Element> rowModule = belongsToModules(alCombineElements);
                List<Integer> alNewRowsForCombine = new ArrayList<>();
                List<Element> alNewElement = newElementForModule(rowModule, alCombineElements,
                        alNewRowsForCombine, alUniqueElements);
//                System.out.println(alNewRowsForCombine);

                if (rowModule.isEmpty()) {
                    for (int k = 0; k < alCombineElements.size(); k++) {
                        rowModule.add(alCombineElements.get(k));
                    }
                    alModules.add(rowModule);

                    //удалить все элементы цепи из матрицы (matrixForGraph), (alUniqueElements)
                    sortALNewRowsForCombine(rowsForCombine);
                    combineRowsInMatrixForGraph(matrixForGraph, alUniqueElements, rowsForCombine, 1);
                    if (rowModule.size() == RESTRICTION) {
                        for (int j = 0; j < matrixForGraph.size(); j++) {
                            matrixForGraph.get(j).remove(alUniqueElements.indexOf(rowModule.get(0)));
                        }
                        matrixForGraph.remove(alUniqueElements.indexOf(rowModule.get(0)));
                        alUniqueElements.remove(alUniqueElements.indexOf(rowModule.get(0)));
                    }
                    //найти все элементарные цепи в новой матрице
                    alElementaryGraphChain = createElementaryGraphChain(matrixForGraph, alUniqueElements);
                    sortALElementaryGraphChain(alElementaryGraphChain);
                    removeExcessInChain(alElementaryGraphChain);

                    isCombine = true;
                } else if (rowModule.size() + alNewElement.size() <= RESTRICTION) {
//                        System.out.println(alNewElement);
                    for (int k = 0; k < alNewElement.size(); k++) {
                        rowModule.add(alNewElement.get(k));
                    }
                    //удалить все элементы цепи из матрицы (matrixForGraph), (alUniqueElements)
                    combineRowsInMatrixForGraph(matrixForGraph, alUniqueElements, alNewRowsForCombine, 0);
                    if (rowModule.size() == RESTRICTION) {
                        for (int j = 0; j < matrixForGraph.size(); j++) {
                            matrixForGraph.get(j).remove(alUniqueElements.indexOf(rowModule.get(0)));
                        }
                        matrixForGraph.remove(alUniqueElements.indexOf(rowModule.get(0)));
                        alUniqueElements.remove(alUniqueElements.indexOf(rowModule.get(0)));
                    }
                    //найти все элементарные цепи в новой матрице
                    alElementaryGraphChain = createElementaryGraphChain(matrixForGraph, alUniqueElements);
                    sortALElementaryGraphChain(alElementaryGraphChain);
                    removeExcessInChain(alElementaryGraphChain);

                    isCombine = true;
                } else if (rowModule.size() + alNewElement.size() > RESTRICTION) {
                    continue;
                }
                if (isCombine) {
                    combineThirdRule(matrixForGraph, alUniqueElements,
                            alLinksBetweenDetails, alElementaryGraphChain);
                    break;
                }
            }
        }
        return isCombine;
    }

    private static void sortALNewRowsForCombine(List<Integer> alNewRowsForCombine) {
        List<Integer> temp = new ArrayList<>();
        temp.addAll(alNewRowsForCombine);
        temp.remove(0);
        Collections.sort(temp, ((o1, o2) -> (o1 > o2 ? 1 : -1)));
        for (int j = 0; j < temp.size(); j++)
            alNewRowsForCombine.set(j + 1, temp.get(j));
    }

    private static boolean combineFirstRule(List<List<Boolean>> matrixForGraph, List<Element> alUniqueElements,
                                            List<List<Element>> alLinksBetweenDetails,
                                            List<List<Element>> alElementaryGraphChain) {
        boolean isCombine = false;
        for (int i = 0; i < alElementaryGraphChain.size(); i++) {
            if (isBetweenTheElementsOfLink(alLinksBetweenDetails,
                    alElementaryGraphChain.get(i).get(alElementaryGraphChain.get(i).size() - 1),
                    alElementaryGraphChain.get(i).get(0))) {
                //мы нашли цепь подходящую под правило (1)

                //нужно создать модули из элементов цепи
                List<Integer> rowsForCombine = new ArrayList<>();
                for (int j = 0; j < alElementaryGraphChain.get(i).size(); j++) {
                    rowsForCombine.add(alUniqueElements.indexOf(alElementaryGraphChain.get(i).get(j)));
                }
//                System.out.println(rowsForCombine);

                List<Element> alCombineElements = getALCombineElements(alUniqueElements, rowsForCombine);
//                System.out.println(alCombineElements);
                List<Element> rowModule = belongsToModules(alCombineElements);
                List<Integer> alNewRowsForCombine = new ArrayList<>();
                List<Element> alNewElement = newElementForModule(rowModule, alCombineElements,
                        alNewRowsForCombine, alUniqueElements);
//                System.out.println(alNewRowsForCombine);

                if (rowModule.isEmpty()) {
                    for (int k = 0; k < alCombineElements.size(); k++) {
                        rowModule.add(alCombineElements.get(k));
                    }
                    alModules.add(rowModule);

                    //удалить все элементы цепи из матрицы (matrixForGraph), (alUniqueElements)
                    sortALNewRowsForCombine(rowsForCombine);
                    combineRowsInMatrixForGraph(matrixForGraph, alUniqueElements, rowsForCombine, 1);
                    if (rowModule.size() == RESTRICTION) {
                        for (int j = 0; j < matrixForGraph.size(); j++) {
                            matrixForGraph.get(j).remove(alUniqueElements.indexOf(rowModule.get(0)));
                        }
                        matrixForGraph.remove(alUniqueElements.indexOf(rowModule.get(0)));
                        alUniqueElements.remove(alUniqueElements.indexOf(rowModule.get(0)));
                    }
                    //найти все элементарные цепи в новой матрице
                    alElementaryGraphChain = createElementaryGraphChain(matrixForGraph, alUniqueElements);
                    sortALElementaryGraphChain(alElementaryGraphChain);
                    removeExcessInChain(alElementaryGraphChain);

                    isCombine = true;
                } else if (rowModule.size() + alNewElement.size() <= RESTRICTION) {
//                        System.out.println(alNewElement);
                    for (int k = 0; k < alNewElement.size(); k++) {
                        rowModule.add(alNewElement.get(k));
                    }
                    //удалить все элементы цепи из матрицы (matrixForGraph), (alUniqueElements)
                    combineRowsInMatrixForGraph(matrixForGraph, alUniqueElements, alNewRowsForCombine, 0);
                    if (rowModule.size() == RESTRICTION) {
                        for (int j = 0; j < matrixForGraph.size(); j++) {
                            matrixForGraph.get(j).remove(alUniqueElements.indexOf(rowModule.get(0)));
                        }
                        matrixForGraph.remove(alUniqueElements.indexOf(rowModule.get(0)));
                        alUniqueElements.remove(alUniqueElements.indexOf(rowModule.get(0)));
                    }
                    //найти все элементарные цепи в новой матрице
                    alElementaryGraphChain = createElementaryGraphChain(matrixForGraph, alUniqueElements);
                    sortALElementaryGraphChain(alElementaryGraphChain);
                    removeExcessInChain(alElementaryGraphChain);

                    isCombine = true;
                } else if (rowModule.size() + alNewElement.size() > RESTRICTION) {
                    continue;
                }
                if (isCombine) {
                    combineFirstRule(matrixForGraph, alUniqueElements,
                            alLinksBetweenDetails, alElementaryGraphChain);
                    break;
                }
            }
        }
        if (!isCombine) {
            for (int i = 0; i < alUniqueElements.size(); i++) {
                boolean isCheck = false;
                for (int j = 0; j < alModules.size(); j++) {
                    if (alModules.get(j).get(0).equals(alUniqueElements.get(i)))
                        isCheck = true;
                }
                if (!isCheck) {
                    List<Element> elementThatRemained = new ArrayList<>();
                    elementThatRemained.add(alUniqueElements.get(i));
                    alModules.add(elementThatRemained);
                }
            }
        }
        return isCombine;
    }

    private static List<Element> getALCombineElements(List<Element> alGroupUniqueElements, List<Integer> ints) {
        List<Element> alCombineElements = new ArrayList<>();
        for (int i = 0; i < ints.size(); i++) {
            alCombineElements.add(alGroupUniqueElements.get(ints.get(i)));
        }
        return alCombineElements;
    }

    private static List<Element> newElementForModule(List<Element> rowModule, List<Element> alCombineElements,
                                                     List<Integer> alNewRowsForCombine, List<Element> alUniqueElements) {
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
            if (!isBelongs) {
                newElement.add(alCombineElements.get(i));
                alNewRowsForCombine.add(alUniqueElements.indexOf(alCombineElements.get(i)));
            }
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

    private static void combineRowsInMatrixForGraph(List<List<Boolean>> matrixForGraph,
                                                    List<Element> alGroupUniqueElements, List<Integer> rows, int xz) {
//        System.out.println();
//        System.out.print("rows:");
//        for (int i : rows) System.out.print(" " + i);
//        System.out.print(" Elements " + alGroupUniqueElements);
//        showMatrix_0_1(matrixForGraph);

        for (int i = 1; i < rows.size(); i++) {
            for (int j = 0; j < matrixForGraph.size(); j++) {
                if (matrixForGraph.get(rows.get(i)).get(j) && rows.get(0) != j) {
                    matrixForGraph.get(rows.get(0)).set(j, true);
//                    System.out.println(" rows[" + i + "] = " + rows[i] + " i = " + j);
//                    System.out.println(" rows[0] = " + rows[0] + " i = " + j);
                }
                if (matrixForGraph.get(j).get(rows.get(i)) && rows.get(0) != j) {
                    matrixForGraph.get(j).set(rows.get(0), true);
//                    System.out.println(" i = " + j + " rows[" + i + "] = " + rows[i]);
//                    System.out.println(" i = " + j + " rows[0] = " + rows[0]);
                }
            }
        }
//        showMatrix_0_1(matrixForGraph);
        for (int i = rows.size() - 1; i > xz - 1; i--) {
            for (int j = 0; j < matrixForGraph.size(); j++) {
                matrixForGraph.get(j).remove((int) rows.get(i));
            }
            matrixForGraph.remove((int) rows.get(i));
            alGroupUniqueElements.remove((int) rows.get(i));
        }

//        showMatrix_0_1(matrixForGraph);
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

    private static List<List<Boolean>> createMatrixLinks(List<List<Element>> alLinksBetweenDetails,
                                                         List<Element> alUniqueElements) {
        List<List<Boolean>> matrixForGraph = new ArrayList<>();
        for (int i = 0; i < alUniqueElements.size(); i++) {
            List<Boolean> rowMatrixForGraph = new ArrayList<>();
            for (int j = 0; j < alUniqueElements.size(); j++) {
                if (isBetweenTheElementsOfLink(alLinksBetweenDetails, alUniqueElements.get(i), alUniqueElements.get(j)))
                    rowMatrixForGraph.add(true);
                else rowMatrixForGraph.add(false);
            }
            matrixForGraph.add(rowMatrixForGraph);
        }
        return matrixForGraph;
    }

    private static boolean isBetweenTheElementsOfLink(List<List<Element>> alLinksBetweenDetails,
                                                      Element el1, Element el2) {
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
                    "D:\\JavaProject\\GKS_desktop\\src\\dataFile_Catherine.txt"));

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
