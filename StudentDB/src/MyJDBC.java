
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;


public class MyJDBC{

    private static Connection connection = null;
    private static String URL = "jdbc:sqlserver://localhost;databaseName=Students;";
    private Statement statement = null;
    private ResultSet result = null;
    private static BufferedReader bufferedReader = null;

    private String value = "";
    private String SQL = "";

    public static void main(String[] args) throws Exception {

        System.out.println("Добро пожаловать в программу для работы с базой данных по студентам! \n" +
                "\nДля работы с базой данных доступны следующие команды: \n" +
                "show - для отображения всех записей БД; \n" +
                "insert - для внесения новой записи в БД; \n" +
                "remove - для удаления записи из БД; \n" +
                "back - для возврата к выбору команд; \n" +
                "exit - для выхода из программы.");

        MyJDBC myJDBC = new MyJDBC();
        myJDBC.setConnection();

        while(true){
            System.out.println("\nПожалуйста введите команду: ");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String comand = bufferedReader.readLine();

            switch (comand){
                case "show" : myJDBC.printAllStudents(); break;
                case "insert" : myJDBC.insertNewStudent(); break;
                case "remove" : myJDBC.removeStudent(); break;
                case "exit" : closeConnection(); break;
                case "back" : break;
                default: System.out.println("Введена некорректная команда!");break;
            }
        }
    }


    public void setConnection(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            System.out.println("Устанавливаем соединение с базой");
            this.connection = DriverManager.getConnection(URL,"user","1");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    } //Устанавливаем соединение с базой данных

    public static void closeConnection(){
        try{
            connection.close();
            if(bufferedReader != null) bufferedReader.close();

            System.out.println("Соединение закрыто");
            System.exit(0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void printAllStudents(){
        try {
            SQL = "SELECT * FROM Students";
            statement = this.connection.createStatement();
            result = statement.executeQuery(SQL);

            System.out.println("Выводим данные о студентах:");
            while (result.next()) {
                value = String.format("Уникальный номер - %d; Фамилия - %s; Имя - %s; Отчество - %s; Дата рождения - %td:%tm:%tY; Группа - %s",
                        result.getInt("id"),result.getString("Фамилия"),result.getString("Имя"),
                        result.getString("Отчество"),result.getDate("Дата рождения"),result.getDate("Дата рождения"),
                        result.getDate("Дата рождения"),result.getString("Группа"));
                System.out.println(value);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    } //Вывод списка всех студентов

    public void insertNewStudent() {

        String name = "";
        String surname = "";
        String midlename = "";
        java.util.Date birthday = null;
        String group = "";


        int column = 1;
        while (column < 6) {
            switch (column) {
                case 1:
                    System.out.print("Введите имя студента: ");
                    try {
                        name = readConsole();
                    }catch (BackException back){
                        return;
                    }

                    if (name.trim().isEmpty()) System.out.println("Пустое имя недопустимо!");
                    else column++;
                    break;

                case 2:
                    System.out.print("Введите фамилию студента: ");
                    try{
                        surname = readConsole();
                    }catch (BackException back){
                        return;
                    }
                    if (surname.trim().isEmpty()) System.out.println("Пустая фамилия недопустима!");
                    else column++;
                    break;

                case 3:
                    System.out.print("Введите отчество студента: ");
                    try{
                        midlename = readConsole();
                    }catch (BackException back){
                        return;
                    }
                    if (midlename.trim().isEmpty()) midlename = null;
                    column++;
                    break;

                case 4:
                    System.out.print("Введите день рождения студента в числовом формате День:Месяц:Год ");
                    String birthdayString = "";
                    try{
                        birthdayString = readConsole();
                    }catch (BackException back){
                        return;
                    }
                    boolean isDate = true;
                    SimpleDateFormat parser = new SimpleDateFormat("dd:MM:yyyy");
                    try {
                        birthday = parser.parse(birthdayString);
                    } catch (Exception ex) {
                        isDate = false;
                    }
                    if (!isDate) System.out.println("Некорректная дата!");
                    else column++;
                    break;

                case 5:
                    System.out.print("Введите группу студента: ");
                    try{
                        group = readConsole();
                    }catch (BackException back){
                        return;
                    }
                    if (group.trim().isEmpty()) System.out.println("Пустая группа недопустима!");
                    else column++;
                    break;

                default:
                    break;
            }
        }

        //Create new Student in base
        try {
            SQL = String.format("INSERT INTO [Students].[dbo].[Students] ([Имя]," +
                            "[Фамилия],[Отчество],[Дата рождения],[Группа]) VALUES " +
                            "('%s','%s','%s','%tY%tm%td','%s')",
                    name, surname, midlename, birthday,
                    birthday, birthday, group);

            statement = this.connection.createStatement();
            statement.executeUpdate(SQL);

            System.out.println("В базу данных добалена новая запись!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    } //добавляем нового студента

    public static String readConsole() throws BackException{
        String input = "";
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            input = bufferedReader.readLine();

            if(input.equals("exit"))closeConnection();

            if(input.equals("back")) throw new BackException();

        } catch (Exception ex){
            if(ex instanceof BackException) throw new BackException();
            ex.printStackTrace();
        }
        return input;
    } //Читаем с консоли и проверяем на ключевые слова

    public void removeStudent(){
        System.out.print("Введите уникальный номер студента, которого необходимо удалить: ");
        try {
            int id = Integer.parseInt(readConsole());
            SQL = String.format("DELETE FROM Students WHERE id=%d",id);
            statement = this.connection.createStatement();
            statement.executeUpdate(SQL);
            System.out.println("Запись успешно удалена.");
        }catch (Exception ex){
            if(ex instanceof BackException) return;
            ex.printStackTrace();
        }

    }

    private static class BackException extends Exception{

    }


}
