
import javafx.application.Application;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Searcher {

    private String directory;
    private ArrayList<File> txtFilesList = new ArrayList<>();
    private String resultPath;


    public Searcher(String directory) {
    this.directory = directory;
    this.resultPath = directory + "\\result.txt";
    }

    public static void main(String[] args) {

        String directory  = consoleInput();

        Searcher searcher = new Searcher(directory);

        File file = new File(searcher.directory);
        searcher.search(file);

        searcher.sort();

        searcher.read_write();

        System.out.println(String.format("Всего найдено текстовых файлов : %d, данные из которых были записаны " +
                                         "в результирующий файл: %s", searcher.txtFilesList.size(),searcher.resultPath));
    }


    public static String consoleInput(){
        String directory = null;
        try {
            BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Пожалуйста введите директорию для поиска!");
            directory = bufferedInputStream.readLine().trim();
            if(directory.equals("exit"))  System.exit(0);

            File file = new File(directory);
            if(!file.exists()) throw new FileNotFoundException();
            //bufferedInputStream.close();


        }catch (Exception ex){

          if(ex.getClass().getSimpleName().equals("FileNotFoundException")) {
              System.out.println("Вы ввели некорректную директорию, пожалуйста " +
                      "проверьте данные. Для завершения работы программы введите: exit");
              return consoleInput();
          }
            else ex.printStackTrace();
        }

        return directory;
    } //ввод директории с консоли с проверкой на валидность

    public void search(File directory){
        if(directory.isDirectory()){
            for(File file : directory.listFiles()){
                if(file.isFile() && (file.getName().endsWith(".txt") ||  file.getName().endsWith(".TXT"))){
                    txtFilesList.add(file);
                } else {
                    search(file);
                }
            }
        }
        if(txtFilesList.size() == 0){
            System.out.println("В указанной вами директории файлов формата \".txt\" не найдено, " +
                               "файл результата не изменён.");
            System.exit(0);
        }

    } //рекурсивный метод поиска файлов в категории, заполняет txtFilesList

    public void sort(){

        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        Collections.sort(this.txtFilesList, comparator);

    } //метод сортировки файлов по названию

    public void read_write() {
        try{

        BufferedWriter bw = new BufferedWriter(new FileWriter(this.resultPath));
        for ( File file : this.txtFilesList ) {
            try(BufferedReader br = new BufferedReader(new FileReader(file.toString())))
            {
                while (br.ready()){
                    bw.write(br.readLine());
                    bw.newLine();
                }
            }
        }
        bw.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    } //метод записи содержимого файлов в результирующий файл

}
