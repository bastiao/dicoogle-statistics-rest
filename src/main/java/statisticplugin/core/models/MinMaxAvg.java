package statisticplugin.core.models;

import java.util.Map;
import java.util.Random;

/**
 *
 * @author bastiao
 */
public class MinMaxAvg <T, E, K, F>
{

    private Map<String, T> min;
    private Map<String, E> max;
    private Map<String, K> avg;
    private Map<String, F> median;
    private Map<String, F> stddev;
    private Map<String, F> firstQuart;
    private Map<String, F> secondQuart;
    
    public Map<String, F> getStddev() {
        return stddev;
    }

    public void setStddev(Map<String, F> stddev) {
        this.stddev = stddev;
    }

    public Map<String, F> getFirstQuart() {
        return firstQuart;
    }

    public void setFirstQuart(Map<String, F> firstQuart) {
        this.firstQuart = firstQuart;
    }

    public Map<String, F> getSecondQuart() {
        return secondQuart;
    }

    public void setSecondQuart(Map<String, F> secondQuart) {
        this.secondQuart = secondQuart;
    }

    
    
    private String title;

    public MinMaxAvg()
    {
    }
    
    /**
     * @return the min
     */
    public Map<String, T> getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(Map<String, T> min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public Map<String, E> getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(Map<String, E> max) {
        this.max = max;
    }

    /**
     * @return the avg
     */
    public Map<String, K> getAvg() {
        return avg;
    }

    /**
     * @param avg the avg to set
     */
    public void setAvg(Map<String, K> avg) {
        this.avg = avg;
    }

    /**
     * @return the median
     */
    public Map<String, F> getMedian() {
        return median;
    }

    /**
     * @param median the median to set
     */
    public void setMedian(Map<String, F> median) {
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

   
}
