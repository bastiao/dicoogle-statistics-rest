/*  Copyright   2011 - IEETA
 *
 *  This file is part of Dicoogle.
 *
 *  Author: Luís A. Bastião Silva <bastiao@ua.pt>
 *
 *  Dicoogle is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Dicoogle is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Dicoogle.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package statisticplugin.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class DicomDate 
{

    
    
    public static void main(String [] args)
    {
        
        System.out.println(incrementDate("19990131"));
        System.out.println(incrementDate("19991231"));
        
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
    
}
