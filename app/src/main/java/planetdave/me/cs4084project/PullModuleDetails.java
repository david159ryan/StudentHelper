package planetdave.me.cs4084project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Dave on 16/04/2017.
 * Pull Module details from file and save them as CVS
 */

public class PullModuleDetails {

    private static String filename = "G:\\Documents\\BookOfModules.txt";
    private static String output = "G:\\Documents\\BookOfModules.csv";

    private enum ModulePatterns {

        MODULE_PATTERN("[A-Z]{2}[1-9]{1}[0-9]{3}"),
        TITLE_PATTERN( "(?<=([A-Z]{2}[1-9]{1}[0-9]{3} - ))(.*)(?=( Year Last Offered:))"),
        SYLLABUS_PATTERN("(?<=(Syllabus: )).*(?=( Learning Outcomes:))"),
        SEMESTER_PATTERN("(?<=(Semester - Year to be First Offered: ))(Spring|Autumn)"),
        LECTURER_PATTERN("(?<=(Module Leader: ))([A-Za_z]{1}[a-z]+)\\.([A-Za-z]{1}[a-z]+)" +
                "@(staffmail\\.)?(ul\\.ie)");


        private String pattern;

        ModulePatterns(String pattern){
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }


    public static void main(String args[]){
        StringBuilder moduleData;
        ArrayList<String> csv;
        ArrayList<String> splitModules;
        Pattern compiledPatterns[] = compilePatterns();
        File file = new File(filename);
        if(file.exists()) {
            moduleData = readFile(file);
            splitModules = splitModules(moduleData);
            csv = parseModuleData(splitModules, compiledPatterns);
            saveFile(csv);
        }else{
            System.out.println("cannot find file");
        }

    }

    private static void saveFile(ArrayList<String> csv) {
        try {
            PrintWriter pr = new PrintWriter(new File(output));
            for(String s : csv){
                pr.println(s);
            }
            pr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> splitModules(StringBuilder moduleData){
        String pattern = "(?<=(Module Code - Title:))(.+?)(?=(Module Code - Title:))";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(moduleData);
        ArrayList<String> result = new ArrayList<>();
       // System.out.println("IN split modules");
        while(m.find()){
            String s = m.group();
            //System.out.println(s);
            result.add(s);
        }
        return result;
    }

    private static ArrayList<String> parseModuleData(ArrayList<String> splitModules,
                                                     Pattern[] compiledPatterns){
        ArrayList<String> result = new ArrayList<>();
        Matcher matchers[] = new Matcher[compiledPatterns.length];
        //for()
        for(int i = 0; i < compiledPatterns.length; i++){
            matchers[i] = compiledPatterns[i].matcher("");
        }

        for(String current : splitModules){
            String currentResult = "";
            for(int i = 0; i < compiledPatterns.length; i++){
                matchers[i].reset(current);
                if(matchers[i].find()){
                    currentResult += matchers[i].group().trim();
                }
                currentResult += "#";
            }
            result.add(currentResult.substring(0, currentResult.length() - 1));
        }

        return result;
    }

    private static Pattern[] compilePatterns() {
        Pattern result[] = new Pattern[ModulePatterns.values().length];
        ModulePatterns[] values = ModulePatterns.values();
        for(int i = 0; i < result.length; i++){
            result[i] = Pattern.compile(values[i].getPattern());
        }

        return  result;
    }

    private static StringBuilder readFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            Scanner in = new Scanner(file);
            while(in.hasNext()){
                result.append(in.nextLine()).append(" ");
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
