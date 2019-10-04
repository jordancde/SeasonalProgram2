package Components;

import Components.Exceptions.InvalidInputException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManualInputParser {

    public class SecuritySeries {
        List<Calendar> date = new ArrayList();
        List<Double> open = new ArrayList();;
        List<Double> high = new ArrayList();;
        List<Double> low = new ArrayList();;
        List<Double> close = new ArrayList();;
        List<Double> volume = new ArrayList();;
    }

    public enum Type {
        DATE,
        OPEN,
        HIGH,
        LOW,
        CLOSE,
        VOLUME
    }

    String inputFilePath = "input.csv";

    private static ManualInputParser single_instance = null;
    public Map<String, SecuritySeries> entries;

    public void refresh() throws InvalidInputException {
        entries = new HashMap();

        List<List<String>> csv = readList();

        int columnHeadersIndex = -1;
        for(int i = 0;i<csv.size();i++){
            if(csv.get(i).contains("Date") || csv.get(i).contains("date")){
                columnHeadersIndex = i;
                break;
            }
        }

        if(columnHeadersIndex < 1) throw new InvalidInputException("Manual input file format invalid.");;

        List<String> columnHeaders = csv.get(columnHeadersIndex);
        List<String> symbolHeaders = csv.get(columnHeadersIndex - 1);

        String currentSymbol = "";

        for(int col = 0; col < columnHeaders.size(); col++){

            if(col < symbolHeaders.size() && !symbolHeaders.get(col).isEmpty()) currentSymbol = symbolHeaders.get(col);
            if(currentSymbol.isEmpty()) continue;

            if(!entries.containsKey(currentSymbol))
                entries.put(currentSymbol, new SecuritySeries());

            List series = new ArrayList();
            switch(columnHeaders.get(col).trim().toLowerCase()){
                case "date":
                    series = entries.get(currentSymbol).date;
                    break;
                case "open":
                    series = entries.get(currentSymbol).open;
                    break;
                case "high":
                    series = entries.get(currentSymbol).high;
                    break;
                case "low":
                    series = entries.get(currentSymbol).low;
                    break;
                case "close":
                    series = entries.get(currentSymbol).close;
                    break;
                case "volume":
                    series = entries.get(currentSymbol).volume;
                    break;
            }


            if(columnHeaders.get(col).trim().toLowerCase().equals("date")){
                // if we're in the date column
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

                for(int row = columnHeadersIndex + 1; row < csv.size(); row++) {
                    String cell = csv.get(row).get(col);
                    if(cell.isEmpty()) break;

                    try {
                        Calendar c = new GregorianCalendar();
                        c.setTime(sdf.parse(cell));

                        // values are in descending order
                        series.add(0,c);
                    }catch(ParseException e){
                        throw new InvalidInputException("Date in manual input file invalid.");
                    }
                }

            }else{
                // if we're not

                for(int row = columnHeadersIndex + 1; row < csv.size(); row++) {
                    String cell = csv.get(row).get(col);
                    if(cell.isEmpty()) break;

                    // values are in descending order
                    series.add(0,Double.valueOf(cell));
                }
            }
        }
    }

    private ManualInputParser() throws InvalidInputException {
        refresh();
    }

    List<List<String>> readList(){
        try {
            String row;
            File csvFile = new File(inputFilePath);

            List<List<String>> csv = new ArrayList<>();

            if (csvFile.isFile()) {
                BufferedReader csvReader = new BufferedReader(new FileReader(inputFilePath));

                while ((row = csvReader.readLine()) != null) {
                    List<String> rowList = new ArrayList<>();
                    String[] data = row.split(",");

                    for(String s: data) rowList.add(s);

                    csv.add(rowList);
                }
                csvReader.close();
            }
            return csv;
        }catch(IOException e){
            return new ArrayList<>();
        }
    }

    public Boolean hasSymbol(String symbol){
        return entries.containsKey(symbol);
    }

    public List getSeries(String symbol, Type type){
        switch(type){
            case DATE:
                return entries.get(symbol).date;
            case OPEN:
                return entries.get(symbol).open;
            case HIGH:
                return entries.get(symbol).high;
            case LOW:
                return entries.get(symbol).low;
            case CLOSE:
                return entries.get(symbol).close;
            case VOLUME:
                return entries.get(symbol).volume;
        }
        return null;
    }

    public static ManualInputParser getInstance() throws InvalidInputException {
        if (single_instance == null)
            single_instance = new ManualInputParser();
        return single_instance;
    }
}