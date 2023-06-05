import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Ivan,Petrov,RU,23".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeNext(employee1, true);
            writer.writeNext(employee2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);

        writeString(json, "data.json");

        try {
            createXML("data.xml");
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }

        List<Employee> list2;
        try {
            list2 = parseXML("data.xml");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        String json2 = listToJson(list2);

        writeString(json2, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> returnList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            returnList = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter jsonFile = new FileWriter(fileName)) {
            jsonFile.write(json);
            jsonFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createXML(String xmlName) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element staff = document.createElement("staff");
        document.appendChild(staff);

        Element employee = document.createElement("employee");
        staff.appendChild(employee);

        Element id = document.createElement("id");
        id.appendChild(document.createTextNode("1"));
        employee.appendChild(id);

        Element firstName = document.createElement("firstName");
        firstName.appendChild(document.createTextNode("John"));
        employee.appendChild(firstName);

        Element lastName = document.createElement("lastName");
        lastName.appendChild(document.createTextNode("Smith"));
        employee.appendChild(lastName);

        Element country = document.createElement("country");
        country.appendChild(document.createTextNode("USA"));
        employee.appendChild(country);

        Element age = document.createElement("age");
        age.appendChild(document.createTextNode("25"));
        employee.appendChild(age);

        Element newEmployee = document.createElement("employee");
        staff.appendChild(newEmployee);

        Element newId = document.createElement("id");
        newId.appendChild(document.createTextNode("2"));
        newEmployee.appendChild(newId);

        Element newFirstName = document.createElement("firstName");
        newFirstName.appendChild(document.createTextNode("Ivan"));
        newEmployee.appendChild(newFirstName);

        Element newLastName = document.createElement("lastName");
        newLastName.appendChild(document.createTextNode("Petrov"));
        newEmployee.appendChild(newLastName);

        Element newCountry = document.createElement("country");
        newCountry.appendChild(document.createTextNode("RU"));
        newEmployee.appendChild(newCountry);

        Element newAge = document.createElement("age");
        newAge.appendChild(document.createTextNode("23"));
        newEmployee.appendChild(newAge);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(xmlName));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }

    public static List<Employee> parseXML(String xmlName) throws ParserConfigurationException, IOException, SAXException,
            NumberFormatException {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlName);

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                employees.add(new Employee
                        (Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())));
            }
        }
        return employees;
    }
}


