package planetdave.me.cs4084project;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dave on 13/04/2017.
 * Uses Patterns and Matcher to extract different parts of timetable data from
 * HTML Strings and returns the data in CSV for.
 */

class TimetableHTMLParser {

    private static String timeRegex =
            "[0-2]{1}[0-9]{1}(?=(:00))";
    private static String moduleRegex =
            "(?<=(<br>\\s))[A-Z]{2}[1-9]{1}[0-9]{3}(?=(\\s<font>))";
    private static String roomRegex =
            "(?<=(<br>\\s))" +
            "(([A-Z]{1}[G0M123]{1}[0-9]{1,3})|" +
            "([A-Z]{2}[G0M123]{1}[0-9]{1,3})|" +
            "([A-Z]{3}[G0M123]{1}[0-9]{1,3}))" +
            "[A-Z]?(?=(\\s<br>))";
    private static String groupRegex =
            "[1-9]{1}[A-Z]{1}";
    private static String typeRegex =
            "(LEC|TUT|LAB)";

    private static String timeTableRegex =
                    ".*" + timeRegex + ".*" + moduleRegex + ".*" + typeRegex +
                    ".*(" + groupRegex + ")?" + ".*" + roomRegex + ".*";


    static List<String> parseTime(String html){
        return parse(html, timeRegex);
    }

    static List<String> parseModule(String html){
        return parse(html, moduleRegex);
    }

    static List<String> parseRoom(String html){
        return parse(html, roomRegex);
    }

    static List<String> parseType(String html){
        return parse(html, typeRegex);
    }

    static List<String> parseGroup(String html){
        List<String> result = parse(html, groupRegex);
        if(result.isEmpty()){
            result.add("NA");
        }
        return result;
    }

    static List<String> parse(String html, String pattern){
        List<String> results = new ArrayList<>();

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(html);

        while(m.find()){
            results.add(m.group());
        }
        return results;
    }

    static boolean isValidTimetableHTML(String html){
        return html.matches(timeTableRegex);
    }

    static String parseTimetableEntry(String html){
        String result;
        List<String> times = parseTime(html);
        result = times.get(0) + "," + times.get(1) + "," +
                parseModule(html).get(0) + "," + parseType(html).get(0) + "," +
                parseGroup(html).get(0) + "," + parseRoom(html).get(0);

        return result;
    }
}
