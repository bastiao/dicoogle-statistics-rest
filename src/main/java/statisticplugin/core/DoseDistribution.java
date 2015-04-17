package statisticplugin.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import statisticplugin.core.models.MinMaxAvg;
import statisticplugin.core.models.MinMaxAvgSingle;
import statisticplugin.core.timeline.DateSpace;

/**
 *
 * @author bastiao
 */
public class DoseDistribution 
{
    private Directory index;

    public DoseDistribution()
    {
        try {
            index = FSDirectory.open(new File("index/" + "indexed"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    
    public MinMaxAvg<Double, Double, Double, Double> calculateDoseDistribution(String tag, String queryStr, DateSpace space)
    {
        
        HashMap<String, List> tree = new HashMap<String, List>();
        
        //standard search... nearly textbook example
        QueryParser parser;
        
        IndexSearcher searcher = null;
        
        try {
        if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

        parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

        Query query = null;
        try {
            query = parser.parse(queryStr);
        } catch (ParseException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        AllDocCollector collector = new AllDocCollector();
        try {
            searcher.search(query, collector);
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        int hitcount = collector.getHits().size();

        for (String s : space.getSlots()) 
        {
            tree.put(s, new ArrayList<Double>());
        }

        List<ScoreDoc> hitsList = collector.getHits();
        
        for (int i = 0; i < hitcount; i++) {
            Document doc = null;
            try 
            {
                doc = searcher.doc(hitsList.get(i).doc);
            } catch (CorruptIndexException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (doc != null) {
                Field _n = doc.getField(tag);
                Field _nD = doc.getField("StudyDate");

                if (_n != null && _nD != null) {
                    String n = TopTerms.asciiMode(_n.stringValue());
                    if (n != null && !n.equals("")) {
                        System.out.println(n);
                        System.out.println(_nD.stringValue());
                        tree.get(space.getSlot(TopTerms.asciiMode(_nD.stringValue()))).add(Double.parseDouble(n));
                    }
                    else
                    {
                        
                    }

                }
            }
            else
            {
                
            }

        }
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        // Processing data
        MinMaxAvg<Double, Double, Double, Double> result = new MinMaxAvg<Double, Double, Double, Double>();
        
        HashMap<String, Double> minTree = new HashMap<String, Double>();
        HashMap<String, Double> maxTree = new HashMap<String, Double>();
        HashMap<String, Double> avgTree = new HashMap<String, Double>();
        HashMap<String, Double> medianTree = new HashMap<String, Double>();
        HashMap<String, Double> firstQuartilTree = new HashMap<String, Double>();
        HashMap<String, Double> thQuartilTree = new HashMap<String, Double>();
        HashMap<String, Double> stdDevTree = new HashMap<String, Double>();
        
        for (String s : space.getSlots()) 
        {
            List<Double> l = tree.get(s);
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (Double d:l)
            {
                stats.addValue(d);
            }
            minTree.put(s, stats.getMin());
            maxTree.put(s, stats.getMax());
            avgTree.put(s, stats.getMean());
            medianTree.put(s, stats.getPercentile(50));
            firstQuartilTree.put(s, stats.getPercentile(25));
            thQuartilTree.put(s, stats.getPercentile(75));
            stdDevTree.put(s, stats.getStandardDeviation());
            
        }
        
        result.setAvg(avgTree);
        result.setMax(maxTree);
        result.setMedian(medianTree);
        result.setMin(minTree);
        result.setFirstQuart(firstQuartilTree);
        result.setStddev(stdDevTree);
        result.setSecondQuart(thQuartilTree);
        
        result.setTitle("Dose Distribution (S-Value)");
        
        return result;
    }
            
            
    public MinMaxAvgSingle<Double> getParameters(String tag, String queryStr, DateSpace space)
    {
    
        
        //standard search... nearly textbook example
        QueryParser parser;
        
        IndexSearcher searcher = null;
        
        try {
        if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

        parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

        Query query = null;
        try {
            query = parser.parse(queryStr);
        } catch (ParseException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        AllDocCollector collector = new AllDocCollector();
        try {
            searcher.search(query, collector);
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        int hitcount = collector.getHits().size();

        List<ScoreDoc> hitsList = collector.getHits();
        List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < hitcount; i++) 
        {
            Document doc = null;
            try 
            {
                doc = searcher.doc(hitsList.get(i).doc);
            } catch (CorruptIndexException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (doc != null) {
                Field _n = doc.getField(tag);
                Field _nD = doc.getField("StudyDate");

                if (_n != null && _nD != null) {
                    String n = TopTerms.asciiMode(_n.stringValue());
                    if (n != null && !n.equals("")) {
                        System.out.println(n);
                        System.out.println(_nD.stringValue());
                        list.add(Double.parseDouble(n));
                    }
                    else
                    {
                        
                    }

                }
            }
            else
            {
                
            }

        }
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        // Processing data
        MinMaxAvgSingle<Double> result = new MinMaxAvgSingle<Double>();
        
        
        
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Double d:list)
        {
            stats.addValue(d);
        }
        
        result.setMin( stats.getMin());
        result.setMax( stats.getMax());
        result.setAvg( stats.getMean());
        result.setMedian( stats.getPercentile(50));
        result.setFirstQuart(stats.getPercentile(25));
        result.setSecondQuart(stats.getPercentile(75));
        result.setStdDev(stats.getPercentile(75));
        result.setTitle(tag);
        
        return result;
        
        
    }
    
    
    
    
}
