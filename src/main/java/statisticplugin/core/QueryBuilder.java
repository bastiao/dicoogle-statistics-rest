
package statisticplugin.core;

import java.util.Iterator;

/**
 * This class collects the query string and collects the information in the Global
 * Settings and generate a new query to send to Indexer
 * @author bastiao
 */
public class QueryBuilder 
{

    private static QueryBuilder instance = new QueryBuilder();
    
    private String queryStr = "";
    
    private QueryBuilder()
    {
    
    }
    
    public static QueryBuilder getInstance()
    {
        if (instance == null)
        {
            instance = new QueryBuilder();
        }
        return instance;
    }
    
    
    public String getQueryDates()
    {
        String d1 = GlobalSettings.getInstance().getInitialDate();
        String d2 = GlobalSettings.getInstance().getFinalDate();
        String q = "";
        if (d1!=null && !d1.equals(""))
        {
            q = "StudyDate:["+ d1 + " TO ";
        }
        if (!q.equals("") && d2!=null && !d2.equals(""))
        {
            q += d2 + "]";
        }
        else if (!q.equals("") )
        {
            q += d1 + "]";
        }
        return q;
        
    }
    
    
    public String getQueryModalities()
    {
        String q = "";
        Iterator<String> it = GlobalSettings.getInstance().getModalities().iterator();
        for (int i = 0 ; i<GlobalSettings.getInstance().getModalities().size(); i++)
        {
            String value = it.next();
            if (!q.equals(""))
            {
                q += " AND Modality:"+ value;
            }
            else
            {
                q += "Modality:"+ value;
            }
        }
        
        return q;
    }
    
    public String getQuery()
    {
        String queryFinal = "";
        
        String queryDate = getQueryDates();
        String queryModality = getQueryModalities();
        
        
        System.out.println("QueryDate: " + queryDate );
        System.out.println("queryModality: " + queryModality );
        System.out.println("QueryStr: " + queryStr );
        
        boolean hasValues = false;
        boolean hasValues2 = false;

        
        if (!queryDate.equals(""))
        {
                queryFinal += queryDate ;
                hasValues = true;
        }
        
        if (!queryStr.equals(""))
        {
            if (hasValues)
            {
                queryFinal += " AND " + getQueryStr();
            }
            else
            {
                queryFinal +=  getQueryStr();
            }
            hasValues2 = true;
        }
        
        if (!queryModality.equals(""))
        {
            if (hasValues2 || hasValues)
            {
                queryFinal += " AND " + queryModality;
            }
            else
            {
                queryFinal += queryModality;
            }
        }
        
        
        return queryFinal;
    }

    /**
     * @return the queryStr
     */
    public String getQueryStr() {
        return queryStr;
    }

    /**
     * @param queryStr the queryStr to set
     */
    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }
    
    
    
}
