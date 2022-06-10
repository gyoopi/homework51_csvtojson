import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.*;
import com.opencsv.CSVReader;
import org.json.simple.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.io.File;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // парсинг csv файла
        String[] columnMapping = {"id","firstName","lastName","country","age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping,fileName);
        String csv_json = listToJson(list);
        writeString(csv_json, "data1.json");
        List<Employee> xmlList = parseXML("data.xml");
        String xml_json = listToJson(xmlList);
        writeString(xml_json, "data2.json");
    }

    // parseCSV - реализация чтения csv файла
    public static List<Employee> parseCSV(String[] columnMapping, String fileName){
        try(CSVReader reader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).
                    withMappingStrategy(strategy).
                    build();
            return csv.parse();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(new File(fileName));
        Node root = doc.getDocumentElement();
        return read(root);
    }

    private static List <Employee> read(Node root) {
        List<Employee> staffList = new ArrayList<>();

        NodeList staff = root.getChildNodes();
        for (int i = 0; i < staff.getLength(); i++) {

            if (Node.ELEMENT_NODE != staff.item(i).getNodeType()){
                continue;
            }

            if(!staff.item(i).getNodeName().equals("employee")){
                continue;
            }

            long id = 0;
            String firstName = "";
            String lastName = "";
            String country = "";
            int age = 0;



            NodeList employee = staff.item(i).getChildNodes();
                for (int j = 0; j < employee.getLength(); j++) {

                    if (Node.ELEMENT_NODE != employee.item(j).getNodeType()){
                        continue;
                    }

                    switch (employee.item(j).getNodeName()){
                        case "id": {
                            id = Integer.parseInt(employee.item(j).getTextContent());
                            break;
                        }
                        case "firstName":{
                            firstName = employee.item(j).getTextContent();
                            break;
                        }
                        case "lastName":{
                            lastName = employee.item(j).getTextContent();
                            break;
                        }
                        case "country":{
                            country = employee.item(j).getTextContent();
                            break;
                        }
                        case "age":{
                            age = Integer.parseInt(employee.item(j).getTextContent());
                            break;
                        }
                    }
                }
               staffList.add(new Employee(id, firstName, lastName, country, age));
            }
        return staffList;
    }

    // listToJson - реализация метода записи в cтроку для json формат
    public static String listToJson(List<Employee> list){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    // writeString - запись в json файл
    public static void writeString(String json, String fileName){
        JSONObject obj = new JSONObject();
        try(FileWriter file = new FileWriter(fileName)){
            file.write(json);
            file.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
