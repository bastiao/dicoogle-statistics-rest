package statisticplugin.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import statisticplugin.core.timeline.DateSpace;
import statisticplugin.core.timeline.Hourly;

/**
 *
 * @author bastiao
 */
public class Timeline 
{
    
    private Directory index;

    public Timeline() {

        try {
            index = FSDirectory.open(new File("index/" + "indexed"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    
    // tag: PatientName or StudyInstanceUID
    
    public TimelineResults getDistributionTime(String queryStr, DateSpace space, String tag) {

        System.out.println("getDistributionTime: " + queryStr);
        
        HashMap<String, Set> tree = new HashMap<String, Set>();
        
        int numberOfConsideredImages = 0;
        int numberOfFailImages = 0;
        
        //standard search... nearly textbook example
        QueryParser parser;
        int counterPatiends = 0;
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
            tree.put(s, new HashSet<String>());
        }

        List<ScoreDoc> hitsList = collector.getHits();
        System.out.println("Hit count" + hitcount);
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
                        System.out.println(TopTerms.asciiMode(_nD.stringValue()));
                        System.out.println(space.getSlot(TopTerms.asciiMode(_nD.stringValue())));
                        try
                        {
                            tree.get(space.getSlot(TopTerms.asciiMode(_nD.stringValue()))).add(n);
                            numberOfConsideredImages++;
                        }
                        catch (Exception e)
                        {
                            numberOfFailImages++;
                        }
                        
                    }
                    else
                    {
                        numberOfFailImages++;
                    }

                }
            }
            else
            {
                numberOfFailImages++;
            }

        }
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        TimelineResults time = new TimelineResults();
        time.setResults(tree);
        time.setNumberOfMissedStudies(numberOfFailImages);
        time.setNumberOfConsideredImages(numberOfConsideredImages);
        return time;

    }

    
    
    
    
    public TimelineResults getDistributionTimeByHourly(String queryStr, 
            DateSpace space, String tag) {

        System.out.println("getDistributionTime: " + queryStr);
        
        HashMap<String, List> tree = new HashMap<String, List>();
        
        int numberOfConsideredImages = 0;
        int numberOfFailImages = 0;
        
        //standard search... nearly textbook example
        QueryParser parser;
        int counterPatiends = 0;
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

        for (int i = 0 ; i<25; i++)

        {
            tree.put(""+i, new ArrayList<String>());
        }

        List<ScoreDoc> hitsList = collector.getHits();
        System.out.println("Hit count" + hitcount);
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
                Field _nD = doc.getField("StudyTime");

                if (_n != null && _nD != null) {
                    String n = TopTerms.asciiMode(_n.stringValue());
                    if (n != null && !n.equals("")) {
                        System.out.println(n);
                        System.out.println(_nD.stringValue());
                        System.out.println(Hourly.getSlotS(TopTerms.asciiMode(_nD.stringValue())));
                        tree.get(Hourly.getSlotS(TopTerms.asciiMode(_nD.stringValue()))).add(n);
                        numberOfConsideredImages++;
                        
                    }
                    else
                    {
                        numberOfFailImages++;
                    }

                }
            }
            else
            {
                numberOfFailImages++;
            }

        }
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        HashMap<String, Long> r = new HashMap<String, Long>();
        for (int i = 0 ; i<25; i++)

        {
            
            String time = i+"";
            List<String> l = tree.get(time);
            for (String s : l)
            {
                if (r.get(time)==null)
                {
                    r.put(time, Long.parseLong(s));
                }
                else
                {
                    r.put(time,r.get(time)+Long.parseLong(s));
                }
            }
        }
        for (int i = 0 ; i<25; i++)

        {
            if (r.get(i+"")!=null)
                System.out.println("Time="+i + " , " +r.get(i+"")/1024/1024);
        }
        
        
        
        
        TimelineResults time = new TimelineResults();
        /*
        time.setResults(tree);
        time.setNumberOfMissedStudies(numberOfFailImages);
        time.setNumberOfConsideredImages(numberOfConsideredImages);*/
        return time;

    }
    
    
    
    
}
