package statisticplugin.core;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * This 
 * 
 * @author bastiao
 */
public class TimelineResults 
{

    private HashMap<String, Set> results;
    private int numberOfMissedStudies;
    private int numberOfConsideredImages;

    /**
     * @return the results
     */
    public HashMap<String, Set> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(HashMap<String, Set> results) {
        this.results = results;
    }

    /**
     * @return the numberOfMissedStudies
     */
    public int getNumberOfMissedStudies() {
        return numberOfMissedStudies;
    }

    /**
     * @param numberOfMissedStudies the numberOfMissedStudies to set
     */
    public void setNumberOfMissedStudies(int numberOfMissedStudies) {
        this.numberOfMissedStudies = numberOfMissedStudies;
    }

    /**
     * @return the numberOfConsideredImages
     */
    public int getNumberOfConsideredImages() {
        return numberOfConsideredImages;
    }

    /**
     * @param numberOfConsideredImages the numberOfConsideredImages to set
     */
    public void setNumberOfConsideredImages(int numberOfConsideredImages) {
        this.numberOfConsideredImages = numberOfConsideredImages;
    }
}
