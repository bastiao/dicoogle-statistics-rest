package statisticplugin.core;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author bastiao
 */
public class GlobalSettings 
{

    public enum DICOM_LEVEL {PATIENT, STUDY, SERIE, IMAGE};
    
    
    
    private DICOM_LEVEL level = DICOM_LEVEL.IMAGE;
    
    private Set<String> modalities = new HashSet<String>();
    private Set<String> stats = new HashSet<String>();

    public Set<String> getStats() {
        return stats;
    }

    public void setStats(Set<String> stats) {
        this.stats = stats;
    }
    
    private String initialDate;
    private String finalDate;
    
    static GlobalSettings instance = null;
    
    private GlobalSettings()
    {

    }
   
    public boolean isAverage()
    {
        return this.stats.contains("Average");
    }
    
    public boolean isMedian()
    {
        return this.stats.contains("Median");
    }
    public boolean isStdDev()
    {
        return this.stats.contains("StdDev");
    }
    
    public boolean is1stQuartile()
    {
        return this.stats.contains("1st Quart");
    }
    public boolean is3thQuartile()
    {
        return this.stats.contains("3th Quart");
    }
    
    
    public boolean isMax()
    {
        return this.stats.contains("Max");
    }
    
    public boolean isMin()
    {
        return this.stats.contains("Min");
    }
    
    public String getName(DICOM_LEVEL level)
    {
        
        String tag = " Images";
        
        if (level == DICOM_LEVEL.PATIENT)
        {
            tag = "Patients";
        }
        else if (level == DICOM_LEVEL.STUDY)
        {
            tag = "Studies";
        }
        else if (level == DICOM_LEVEL.SERIE)
        {
            tag = "Series";
        }   
        else if (level == DICOM_LEVEL.IMAGE)
        {
            tag = "Images";
        }   
        return tag;    
    }
    
    
    public String getTag(DICOM_LEVEL level)
    {
        
        String tag = "SOPInstanceUID";
        
        if (level == DICOM_LEVEL.PATIENT)
        {
            tag = "PatientID";
        }
        else if (level == DICOM_LEVEL.STUDY)
        {
            tag = "StudyInstanceUID";
        }
        else if (level == DICOM_LEVEL.SERIE)
        {
            tag = "SerieInstanceUID";
        }   
        else if (level == DICOM_LEVEL.IMAGE)
        {
            tag = "SOPInstanceUID";
        }   
        return tag;    
    }
    
    
    public String getTagTime()
    {
        
        String tag = "SOPInstanceUID";
        
        if (level == DICOM_LEVEL.PATIENT)
        {
            tag = "StudyTime";
        }
        else if (level == DICOM_LEVEL.STUDY)
        {
            tag = "StudyTime";
        }
        else if (level == DICOM_LEVEL.SERIE)
        {
            tag = "SerieTime";
        }   
        else if (level == DICOM_LEVEL.IMAGE)
        {
            tag = "SerieTime";
        }   
        return tag;    
    }
    
    
    
    
    
    public static GlobalSettings getInstance()
    {
        if (instance==null)
        {
            instance = new GlobalSettings();
        }
        return instance;
    }
    
    
    /**
     * @return the modalities
     */
    public Set<String> getModalities()
    {
        return modalities;
    }

    /**
     * @param modalities the modalities to set
     */
    public void setModalities(Set<String> modalities) {
        this.modalities = modalities;
    }

    public void addModality(String modality)
    {
        this.modalities.add(modality);
    }

    public void removeModality(String modality)
    {
        this.modalities.remove(modality);
    }
    
    /**
     * @return the level
     */
    public DICOM_LEVEL getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(DICOM_LEVEL level) {
        this.level = level;
    }

    /**
     * @return the initialDate
     */
    public String getInitialDate() {
        return initialDate;
    }

    /**
     * @param initialDate the initialDate to set
     */
    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    /**
     * @return the finalDate
     */
    public String getFinalDate() {
        return finalDate;
    }

    /**
     * @param finalDate the finalDate to set
     */
    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }
    
    
    
   
}
