package statisticplugin.core.timeline;

import java.util.Date;

/**
 *
 * @author bastiao
 */
public abstract class DateSpace implements IDateRange
{
    
    private Date init;
    private Date end;
    
    
    private String currentDate;
    
    
    public static int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
    
    public abstract String getQuery(String date);
    public abstract String nextDate(String date);
    
    @Override
    public void setInitDate(Date date) {
        this.setInit(date);
    }

    @Override
    public void setEndDate(Date date) {
        this.setEnd(date);
    }

    /**
     * @return the init
     */
    public Date getInit() {
        return init;
    }

    /**
     * @param init the init to set
     */
    public void setInit(Date init) {
        this.init = init;
    }

    /**
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * @return the currentDate
     */
    @Override
    public String getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
    
    
}
