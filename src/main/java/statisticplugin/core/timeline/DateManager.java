/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statisticplugin.core.timeline;

import java.util.Date;
import org.dcm4che2.data.VR;
import org.dcm4che2.util.DateUtils;
import org.dcm4che2.util.StringUtils;

/**
 *
 * @author bastiao
 */
public class DateManager 
{
    
    
    
    
    
        public static Date toDate(String date)
        {
            
            return DateUtils.parseTM(
                    StringUtils.trim(date), false);
        }
        
        public static void main(String [] args)
        {
        
            String date = "114920.94";
            System.out.println(toDate(date));
            
        }
        
        
        
        
        
}
