package statisticplugin.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bastiao
 */
public class QueryParserStat 
{

    public static List<String> getRangeDate(String queryStr)
    {
        List<String> result = new ArrayList<String>();
        String d1 = queryStr.substring(11, 19);
        String d2 = queryStr.substring(23,31 );
        result.add(d1);
        result.add(d2);
        
        return result;
    }
    
    
}
