
package statisticplugin.core.timeline;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bastiao
 */
public class Duration extends DateSpace
{

    static String [] RANGES_TIMES = { "0-10", "10-20", "20-30", "30-40", "40-50", "50-60", "60-70", "70-220"};
    
    private List<String> slots = null;
    
    @Override
    public String getQuery(String date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String nextDate(String date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String increment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getSlots() {
        
        if (slots==null)
        {
            slots = new ArrayList<String>();
            for (int i = 0 ; i<RANGES_TIMES.length;i++)
            {
                slots.add(RANGES_TIMES[i]);
            }
        }
        return slots;
        
    }

    @Override
    public String getSlot(String date) 
    {
        
        int current = Integer.parseInt(date);
        for (int i = 0 ; i<RANGES_TIMES.length;i++)
        {
            String [] twoValues = RANGES_TIMES[i].split("-");
            int n1 = Integer.parseInt(twoValues[0]);
            int n2 = Integer.parseInt(twoValues[1]);
            
            if (current>=n1 && current<=n2)
            {
                return RANGES_TIMES[i];
            }
        }
        return "UN";
        
    }

    @Override
    public int avgDaysIncrement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
