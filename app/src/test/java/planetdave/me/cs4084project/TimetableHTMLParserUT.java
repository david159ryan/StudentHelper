package planetdave.me.cs4084project;

import org.junit.Test;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TimetableHTMLParserUT {

    private static String testHTML = "<td align=\"center\"> <b> <font color=\"#000080\" size=\"2\">" +
            "Monday</font> </b> </td>, <td align=\"center\"> <b> <font color=\"#000080\" size=\"2\">" +
            "Tuesday</font> </b> </td>, <td align=\"center\"> <b> <font color=\"#000080\" size=\"2\">" +
            "Wednesday</font> </b> </td>, <td align=\"center\"> <b> <font color=\"#000080\" size=\"2\">" +
            "Thursday</font> </b> </td>, <td align=\"center\"> <b> <font color=\"#000080\" size=\"2\">" +
            "Friday</font> </b> </td>, <td align=\"center\"> <b> <font color=\"#000080\" size=\"2\">" +
            "Saturday</font> </b> </td>, <td align=\"justify\" valign=\"top\"> " +
            "<p><font size=\"1\" color=\"#800000\"><b>09:00 <font> - </font> 10:00 " +
            "<br> CS4416 <font> - </font>LAB <font>- </font> 2A <br> CS2044 <br> Wks:1-11,13 </b>" +
            "</font> </p>";

    private static String testRoomString = "<br> CS2004 <br> ";

    @Test
    public void parseTimeCompiles() throws Exception {
        try{
            TimetableHTMLParser.parseTime(testHTML);
            assertTrue(true);
        }catch(PatternSyntaxException e){
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void parseModuleCompiles() throws Exception {
        try{
            TimetableHTMLParser.parseModule(testHTML);
            assertTrue(true);
        }catch(PatternSyntaxException e){
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void parseRoomCompiles() throws Exception {
        try{
            TimetableHTMLParser.parseRoom(testHTML);
            assertTrue(true);
        }catch(PatternSyntaxException e){
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void parseTypeCompiles() throws Exception {
        try{
            TimetableHTMLParser.parseType(testHTML);
            assertTrue(true);
        }catch(PatternSyntaxException e){
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void parseGroupCompiles() throws Exception {
        try{
            TimetableHTMLParser.parseGroup(testHTML);
            assertTrue(true);
        }catch(PatternSyntaxException e){
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    public void parseTimeReturnsValue()  {
        List<String> result = TimetableHTMLParser.parseTime(testHTML);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void parseModuleReturnsValue()  {
        List<String> result = TimetableHTMLParser.parseModule(testHTML);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void parseRoomReturnsValue() {
        List<String> result = TimetableHTMLParser.parseRoom(testHTML);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void parseRoomValidString()  {
        List<String> result = TimetableHTMLParser.parseRoom(testRoomString);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void parseTypeReturnsValue()  {
        List<String> result = TimetableHTMLParser.parseType(testHTML);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void parseGroupReturnsValue()  {
        List<String> result = TimetableHTMLParser.parseGroup(testHTML);
        assertTrue(!result.isEmpty());
    }

    @Test
    public void testIsValidTimetableHTML()  {
        assertTrue(TimetableHTMLParser.isValidTimetableHTML(testHTML));
    }

    @Test
    public void testParseTimeTableEntry(){
        String result = TimetableHTMLParser.parseTimetableEntry(testHTML);
        System.out.println(result);
        assertTrue(result.equals("09:00,10:00,CS4416,LAB,2A,CS2044"));
    }
}