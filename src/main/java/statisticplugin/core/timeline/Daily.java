/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statisticplugin.core.timeline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bastiao
 */
public class Daily extends DateSpace
{

    @Override
    public String increment() 
    {
        
        setCurrentDate(incrementDate(getCurrentDate()));
        return getCurrentDate();
    }


    @Override
    public int avgDaysIncrement() {
        return 1;
    }

    public static String incrementDate(String date)
    {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException ex) {
            Logger.getLogger(DicomDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        c.add(Calendar.DATE, 1);  // number of days to add
        return sdf.format(c.getTime());  // dt is now the new date
    }

    @Override
    public List<String> getSlots() {
        
        List<String> set = new ArrayList<String>();
        
        Date d1 = getInit();
        Date d2 = getEnd();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s1 = sdf.format(d1);
        String s2 = sdf.format(d2);
        String currentDate = s1;
        int i = 0 ; 
        while (true)
        {
            currentDate = incrementDate(currentDate);
            set.add(getSlot(currentDate));    
            if (currentDate.equals(s2))
            {
                break;
            }
            
            i++;
            if (i>365)
                break;
        }
        
        return set;
        
        
    }

    @Override
    public String getSlot(String date) {
        return date.substring(6, 8);
    }

    @Override
    public String getQuery(String date) 
    {   
        HashSet<String> set = new HashSet<String>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s1 = date;
        
        String currentDate = s1;
        currentDate = incrementDate(currentDate);
        return "StudyDate:["+ currentDate + " TO " + currentDate +"]";

    }
    
    
    @Override
    public String nextDate(String date) {
        
        return incrementDate(date);
        
    }
    

}
