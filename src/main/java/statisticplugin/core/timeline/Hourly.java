
package statisticplugin.core.timeline;

import java.util.Date;
import java.util.List;

/**
 *
 * @author bastiao
 */
public class Hourly  {

    private List<String> slots = null;
    public String increment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public static String getSlotS(String s)
    {
        if (s==null)
            return "";
        String r = s.substring(0, 2);
        if (r.charAt(0)=='0')
        {
            return ""+r.charAt(1);
        }
        return r;
    }
    
    
    public String getCurrentDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public void setInitDate(Date date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setEndDate(Date date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public List<String> getSlots() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public String getSlot(String date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public int avgDaysIncrement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
