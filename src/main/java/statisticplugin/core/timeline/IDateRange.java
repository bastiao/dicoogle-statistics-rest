/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statisticplugin.core.timeline;

import java.util.Date;
import java.util.List;
import java.util.List;

/**
 *
 * @author bastiao
 */
public interface IDateRange 
{

    
    public String increment();
    public String getCurrentDate();
    
    public void setInitDate(Date date);
    public void setEndDate(Date date);
    
    public List<String> getSlots();
    public String getSlot(String date);
    
    
    public int avgDaysIncrement();
    
}
