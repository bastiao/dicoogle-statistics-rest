package statisticplugin.core;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

import statisticplugin.core.timeline.DateManager;
import statisticplugin.core.timeline.DateSpace;

/**
 *
 * @author bastiao
 */
public class Duration {

    private Directory index;

    public Duration() {

           try {
            index = FSDirectory.open(new File("index/" + "indexed"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public HashMap<String, Set> getStudyDurationFull(String queryStr, int minumum) {


        System.out.println("getStudyDurationFull" + queryStr);
        // Doing the processing by Day

        List<String> tmp2 = QueryParserStat.getRangeDate(queryStr);
        String d1 = tmp2.get(0);
        String d2 = tmp2.get(1);

        System.out.println(d1);
        System.out.println(d2);


        String tmpDate = DicomDate.incrementDate(d1);
        statisticplugin.core.timeline.Duration space = new statisticplugin.core.timeline.Duration();
        HashMap<String, Set> tree = new HashMap<String, Set>();


        for (String s : space.getSlots()) {
            tree.put(s, new HashSet<String>());
        }
        while (!tmpDate.equals(d2)) {

            String queryTmp = "StudyDate:[" + tmpDate + " TO " + tmpDate + "]";
            System.out.println(queryTmp);
            HashMap<String, Set> tmp = getStudyDuration(queryTmp, minumum);


            for (String s : tmp.keySet()) {
                int d = duration(tmp, s);

                //System.out.println("Duration: " + d);

                String slot = space.getSlot(Integer.toString(d));
                //System.out.println(slot);
                if (!slot.equals("UN")) {
                    tree.get(slot).add(s);
                }
            }
            tmpDate = DicomDate.incrementDate(tmpDate);
        }


        return tree;
    }

    public int duration(HashMap<String, Set> tree, String uid) {


        //System.out.println("Start Duration");
        long diffMinutes = 0;

        String minTime = getMinTime(tree);
        double minTimeD = Double.parseDouble(minTime);
        String closestPointStr = getMaxTime(tree);;
        double closestPoint = Double.parseDouble(closestPointStr);
        System.out.println("minTime" + minTime);
        System.out.println("closestPoint" + closestPoint);
        for (String k : tree.keySet()) {
            if (!k.equals(uid)) {
                String timeCurrentStr = getMinTime(tree.get(k));
                double timeTmp = Double.parseDouble(timeCurrentStr);
                /*
                 * System.out.println("timeCurrentStr" + timeCurrentStr);
                 * System.out.println("minTimeD" + minTimeD);
                 * System.out.println("timeTmp" + timeTmp);
                 * System.out.println("closestPoint" + closestPoint);
                System.out.println(timeCurrentStr);
                 */
                if (timeTmp > minTimeD && timeTmp < closestPoint) {
                    closestPoint = timeTmp;
                    closestPointStr = timeCurrentStr;
                }
            }
        }

        System.out.println("D1: " + minTime);
        System.out.println("D2: " + closestPointStr);


        Date d1 = DateManager.toDate(minTime);
        Date d2 = DateManager.toDate(closestPointStr);


        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(d1);
        calendar2.setTime(d2);
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        diffMinutes = diff / (60 * 1000);
        //System.out.println("End Duration");

        return (int) diffMinutes;
    }

    
    
    

    public String getMinTime(Set<String> times) {


            Iterator it = times.iterator();

                String minStr = (String) it.next();
                double min = Double.parseDouble(minStr);
            

            while (it.hasNext()) {
                String time = (String) it.next();

                double current = Double.parseDouble(time);
                if (current < min) {
                    min = current;
                    minStr = time;
                }


            }

        return minStr;

    }

    public String getMaxTime(Set<String> times) {
        

        
        

        Iterator it = times.iterator();

                String maxStr = (String) it.next();
                double max = Double.parseDouble(maxStr);

        
        
        
        
        while (it.hasNext()) {
            String time = (String) it.next();

            double current = Double.parseDouble(time);
            if (current > max) {
                max = current;
                maxStr = time;
            }

        }
        

        return maxStr;

    }
    
    
    public String getMinTime(HashMap<String, Set> tree) {


        String minStr = null;
        double min = 0;


        for (String k : tree.keySet()) {

            Set<String> times = tree.get(k);

            System.out.println(times);

            Iterator it = times.iterator();
            if (minStr==null)
            {
                minStr = (String) it.next();
                min = Double.parseDouble(minStr);
            }

            while (it.hasNext()) {
                String time = (String) it.next();

                double current = Double.parseDouble(time);
                if (current < min) {
                    min = current;
                    minStr = time;
                }
            }
        }

        return minStr;

    }

    public String getMaxTime(HashMap<String, Set> tree) {
        

        String maxStr = null;
        double max = 0;
        
        
        
        for (String k : tree.keySet()) 
        {
            
        Set<String> times = tree.get(k);
        Iterator it = times.iterator();
        if (maxStr==null)
        {    
            maxStr = (String) it.next();
            max = Double.parseDouble(maxStr);
        }
        
        while (it.hasNext()) {
            String time = (String) it.next();

            double current = Double.parseDouble(time);
            if (current > max) {
                max = current;
                maxStr = time;
            }

        }
        }

        return maxStr;

    }

    public int duration(Set<String> times) {

        long diffMinutes = 0;
        try {

            int d = 0;

            Iterator it = times.iterator();
            String minStr = (String) it.next();
            double min = Double.parseDouble(minStr);
            double max = min;

            String maxStr = minStr;
            while (it.hasNext()) {
                String contentTime = (String) it.next();
                double current = Double.parseDouble(contentTime);
                if (current < min) {
                    min = current;
                    minStr = contentTime;
                }
                if (current > max) {
                    max = current;
                    maxStr = contentTime;
                }
            }


            Date d1 = DateManager.toDate(minStr);
            Date d2 = DateManager.toDate(maxStr);


            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            calendar1.setTime(d1);
            calendar2.setTime(d2);
            long milliseconds1 = calendar1.getTimeInMillis();
            long milliseconds2 = calendar2.getTimeInMillis();
            long diff = milliseconds2 - milliseconds1;
            long diffSeconds = diff / 1000;
            diffMinutes = diff / (60 * 1000);
        } catch (Exception e) {
        }
        return (int) diffMinutes;
    }

    public HashMap<String, Set> getStudyDuration(String queryStr, int minumum) {

        if (queryStr == "") 
        {
            return null;
        }
        HashMap<String, Set> tree = new HashMap<String, Set>();


        List<String> tmp = QueryParserStat.getRangeDate(queryStr);
        String d1 = tmp.get(0);
        String d2 = tmp.get(1);

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


        System.out.println(queryStr);
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

        List<ScoreDoc> hitsList = collector.getHits();
        int hitcount = collector.getHits().size();

        for (int i = 0; i < hitcount; i++) {
            Document doc = null;
            try {
                doc = searcher.doc(hitsList.get(i).doc);
            } catch (CorruptIndexException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (doc != null) {
                Field _n = doc.getField(GlobalSettings.getInstance().getTag(GlobalSettings.getInstance().getLevel()));
                Field _nD = doc.getField(GlobalSettings.getInstance().getTagTime());
                if (_n != null && _nD != null) {
                    String n = TopTerms.asciiMode(_n.stringValue());
                    if (n != null) {
                        //System.out.println("SerieInstanceUID:" +_nD.stringValue());

                        //System.out.println("Count: " + _nD);
                        //System.out.println("SeriesInstanceUID:" + n);
                        if (tree.get(n) == null) {
                            tree.put(n, new HashSet<String>());
                        }
                        tree.get(n).add(TopTerms.asciiMode(_nD.stringValue()));
                    }

                }
            }

        }
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tree;

    }
}
