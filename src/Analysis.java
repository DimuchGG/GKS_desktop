import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimuch on 18.10.2016.
 */
public class Analysis {

    private String[] data;
    //всего разновидностей деталей
    private int total;
    //максимальное кол-во деталей в одной строке
    private int max;
    //лист деталей
    private List<Detail> alDetailsRow;
    //лист листов с деталями
    private List<List<Detail>> alDetails;

    public Analysis(String[] data) {
        this.data = data;
    }

    //проверяет корректность ввода String
    public boolean checkInput() {

        if (true) {

            max = checkMaxDetailsInColumn();
//            System.out.println("max = " + max + "   total = " + total);
            return true;
        } else
            return false;
    }

    //возвращает list разновидностей деталей
    public List<String> checkDetails() {
        List<String> listTotalDetails = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            String[] parts = data[i].split(" ");
            for (int j = 0; j < parts.length; j++) {
                if (listTotalDetails.size() == 0)
                    listTotalDetails.add(parts[0]);
                for (int k = 0; k < listTotalDetails.size(); k++) {
//                    System.out.println(parts[j] + " * " + listTotalDetails.get(k) + listTotalDetails.toString());
                    if (parts[j].equals(listTotalDetails.get(k))) {
//                    if (parts[j].charAt(0) == listTotalDetails.get(k).charAt(0) &&
//                            parts[j].charAt(1) == listTotalDetails.get(k).charAt(1)) {
//                        System.out.println("true");
                        break;
                    } else {
//                        System.out.println("false");
                        if (k == listTotalDetails.size() - 1) {
//                            System.out.println("false-true");
                            listTotalDetails.add(parts[j]);
                            break;
                        }
                    }
                }
            }
        }
//        System.out.println(listTotalDetails.toString());
        return listTotalDetails;
    }

    //возвращает максимальное кол-во деталей в строках
    private int checkMaxDetailsInColumn() {
        int maxDetails = 0;

        for (int i = 0; i < data.length; i++) {
            String[] parts = data[i].split(" ");
            if (maxDetails < parts.length)
                maxDetails = parts.length;
        }
        return maxDetails;
    }

    //возвращает лист с массивом деталей
    public List<List<Detail>> feelData() {

//        Detail[][] detail = new SpecificDetail[data.length][];
//        for (int i = 0; i < data.length; i++)
//            detail[i] = new SpecificDetail[max];

        alDetails = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            alDetailsRow = new ArrayList<>();
            for (int j = 0; j < max; j++)
                alDetailsRow.add(new SpecificDetail());
            alDetails.add(alDetailsRow);
        }

        for (int i = 0; i < data.length; i++) {
            String[] parts = data[i].split(" ");
            for (int j = 0; j < parts.length; j++) {
//                System.out.println(parts[j].charAt(0) + "-" + parts[j].charAt(1));
                alDetails.get(i).get(j).setSymbolKey(String.valueOf(parts[j].charAt(0)));
                alDetails.get(i).get(j).setNumKey(String.valueOf(parts[j].charAt(1)));
            }
        }

//        for (int i = 0; i < data.length; i++)
//            for (int j = 0; j < max; j++) {
//                System.out.println(alDetails.get(i).get(j).getName());
//            }

        return alDetails;
    }
}
