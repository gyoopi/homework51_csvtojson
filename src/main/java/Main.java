import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.*;
import com.opencsv.CSVReader;
import org.json.simple.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // парсинг csv файла
        String[] columnMapping = {"id","firstName","lastName","country","age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping,fileName);
        String json = listToJson(list);
        writeString(json);
    }

    // parseCSV - реализация чтения csv файла
    public static List<Employee> parseCSV(String[] columnMapping, String fileName){
        try(CSVReader reader = new CSVReader(new FileReader(fileName))){
            List<Employee> listEmployee;
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).
                    withMappingStrategy(strategy).
                    build();
            return listEmployee = csv.parse();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    // listToJson - реализация метода записи в cтроку для json формат
    public static String listToJson(List<Employee> list){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    // writeString - запись в json файл
    public static void writeString(String json){
        JSONObject obj = new JSONObject();
        try(FileWriter file = new FileWriter("data.json")){
            file.write(json);
            file.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
