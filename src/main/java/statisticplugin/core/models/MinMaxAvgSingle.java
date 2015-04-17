package statisticplugin.core.models;

import java.util.Map;

/**
 *
 * @author bastiao
 */
public class MinMaxAvgSingle <T>
{

    private T min;
    private T max;
    private Double avg;
    private Double stdDev;
    private T median;
    
    
    private T firstQuart;
    private T secondQuart;
    
    
    private String title;

    /**
     * @return the min
     */
    public T getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(T min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public T getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(T max) {
        this.max = max;
    }

    /**
     * @return the avg
     */
    public Double getAvg() {
        return avg;
    }

    /**
     * @param avg the avg to set
     */
    public void setAvg(Double avg) {
        this.avg = avg;
    }

    /**
     * @return the stdDev
     */
    public Double getStdDev() {
        return stdDev;
    }

    /**
     * @param stdDev the stdDev to set
     */
    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }

    /**
     * @return the median
     */
    public T getMedian() {
        return median;
    }

    /**
     * @param median the median to set
     */
    public void setMedian(T median) {
        this.median = median;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * @return the firstQuart
     */
    public T getFirstQuart() {
        return firstQuart;
    }

    /**
     * @param firstQuart the firstQuart to set
     */
    public void setFirstQuart(T firstQuart) {
        this.firstQuart = firstQuart;
    }

    /**
     * @return the secondQuart
     */
    public T getSecondQuart() {
        return secondQuart;
    }

    /**
     * @param secondQuart the secondQuart to set
     */
    public void setSecondQuart(T secondQuart) {
        this.secondQuart = secondQuart;
    }

    
    
}
