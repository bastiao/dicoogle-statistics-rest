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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bastiao
 */
public class Monthly extends DateSpace {

    
    public static int numberOfDays(String currentDate)
    {
        
        int year = Integer.parseInt(currentDate.substring(0, 4)); 
        int month = Integer.parseInt(currentDate.substring(4, 6)); 
        int day = Integer.parseInt(currentDate.substring(6, 8));  ;
        
        
        // Create a calendar object of the desired month
        Calendar cal = new GregorianCalendar(year, month, 1);

        // Get the number of days in that month
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
        return days;
        
    
    }
    
    @Override
    public String increment() {



        String currentDate = getCurrentDate();

        int days = numberOfDays(currentDate);
        setCurrentDate(incrementDate(currentDate, days));
        return getCurrentDate();


    }
    
    public static String incrementDate(String date, int numberOfDays) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException ex) {
            Logger.getLogger(DicomDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        c.add(Calendar.DATE, numberOfDays);  // number of days to add
        return sdf.format(c.getTime());  // dt is now the new date
    }

    @Override
    public int avgDaysIncrement() {
        return 30;
    }

    @Override
    public List<String> getSlots() {
        
                
        List<String> set = new ArrayList<String>();
        
        Date d1 = getInit();
        Date d2 = getEnd();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s1 = sdf.format(d1);
        String s2 = sdf.format(d1);
        int end = DateSpace.daysBetween(d1, d2)/30;
        String currentDate = s1;
        
        int i = 0 ; 
        while (true)
        {
            //System.out.println("Currentdate:"+ currentDate);
            currentDate = incrementDate(currentDate, numberOfDays(currentDate));
            set.add(getSlot(currentDate));    
            if (currentDate.equals(s2))
            {
                break;
            }
            
            i++;
            if (i+1>end)
                break;
        }
        
        return set;
        
        
    }

    @Override
    public String getSlot(String date) {
        
        return date.substring(4, 6); 
        
        
    }

    @Override
    public String getQuery(String date) {
        
        
        String year = date.substring(0, 4); 
        String month = date.substring(4, 6); 
        
        
        return "StudyDate:["+ year+month+"01 TO " + year+month+"31]";
    
    }
    
    
    @Override
    public String nextDate(String date) {
        
        return incrementDate(date, numberOfDays(date)+1);
        
    }
}
