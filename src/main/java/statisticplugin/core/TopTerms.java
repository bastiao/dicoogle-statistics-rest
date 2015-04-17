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

import java.io.File;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import statisticplugin.core.timeline.DateManager;
import statisticplugin.core.timeline.DateSpace;
import statisticplugin.core.timeline.Duration;

/**
 *
 * @author bastiao
 */
public class TopTerms implements ITopTerms {

    private Directory index;

    public TopTerms() {


        try {
            index = FSDirectory.open(new File( "index/" + "indexed"));
            System.out.println("homePath");

            
        } catch (IOException ex) {
            ex.printStackTrace();
        }




    }

    @Override
    public List<String> getHighestFrequencyTerms(String field) {
        TermInfo[] terms = null;
        List<String> termStr = new ArrayList<String>();

        try {
            terms = HighFreqTerms.getHighFreqTerms(IndexReader.open(index), null, new String[]{field});
        } catch (Exception ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < terms.length; i++) {

            termStr.add(terms[i].term.text());

        }

        return termStr;

    }

    public Set<String> getHighestFrequencySentenses(String field, String q) {

        Set<String> termStr = new HashSet<String>();
        Map<String, Integer> mapPercentage = new HashMap<String, Integer>();
        IndexSearcher searcher = null;
        try {
            //standard search... nearly textbook example
            QueryParser parser;

            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            }

            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(q);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();

            /**
             * Check for each result, if there is a field that matches with the
             * extrafields parameter.
             */
            
            for (int i = 0; i < hitcount; i++) {
                Document doc = searcher.doc(hitsList.get(i).doc);
                String fieldStr = doc.get(field);
                if (fieldStr != null) {
                    //System.out.println(fieldStr);
                    termStr.add(fieldStr);
                    Integer count = 0;
                    if (mapPercentage.get(fieldStr)!=null)
                    {
                        count = mapPercentage.get(fieldStr);
                        
                    }
                    count++;
                    
                    
                    mapPercentage.put(fieldStr, count);
                    
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                searcher.close();
            } catch (IOException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int totalTerms = 0;
        for (String s : mapPercentage.keySet())
        {
            totalTerms += mapPercentage.get(s);
        }
        Set<String> termStrReturn = new HashSet<String>();
        
        for (String s:termStr)
        {
            termStrReturn.add(s  + ", " + new DecimalFormat("#.##").format(((double)mapPercentage.get(s)/totalTerms )* 100) + "%");
            System.out.println(mapPercentage.get(s));
            System.out.println(totalTerms);
        }

        return termStrReturn;

    }

    public Collection<String> getFields() throws CorruptIndexException {
        try {
            return IndexReader.open(index).getFieldNames(IndexReader.FieldOption.ALL);
        } catch (Exception ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public Map<String, Integer> getTotalBlanks() {
        return total;

    }

    public int totalIndexedFiles(String queryStr) {

        try {
            //standard search... nearly textbook example
            QueryParser parser;
            IndexSearcher searcher = null;
            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return 0;
            }
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(queryStr);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();


            searcher.close();


            return hitcount;
        } catch (Exception e) {
        }




        return 0;

    }

    /**
     * Volume of indexed data
     */
    public long getTotalAmount(String queryStr) {

        long r = 0;
        Map<String, Integer> blanks = new HashMap<String, Integer>();
        total = new HashMap<String, Integer>();
        ArrayList list = new ArrayList();
        IndexSearcher searcher = null;
        try {
            //standard search... nearly textbook example
            QueryParser parser;

            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return r;
            }
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(queryStr);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();

            /**
             * Check for each result, if there is a field that matches with the
             * extrafields parameter.
             */
            for (int i = 0; i < hitcount; i++) {
                Document doc = searcher.doc(hitsList.get(i).doc);
                String fileSize = doc.get("FileSize");
                if (fileSize != null) {
                    long v = Long.parseLong(fileSize);
                    r += v;
                }
            }

        } catch (Exception e) {
        }

        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;


    }

    public Map<String, Double> getTotalAmountOfFramesRange(String queryStr, DateSpace space) {

        HashMap<String, Double> tree = new HashMap<String, Double>();
        String currentDate = convertDateToString(space.getInit());
        for (String s : space.getSlots()) {

            System.out.println(s);
            String queryAppendix = space.getQuery(currentDate);
            String queryTmp = "";
            if (!queryStr.equals("")) {
                queryTmp = queryAppendix + "" + queryStr;
            } else {
                queryTmp = queryAppendix;
            }
            System.out.println(queryTmp);
            // MB
            Double amount = getTotalAmountFrames(queryTmp);


            tree.put(s, amount);
            currentDate = space.nextDate(currentDate);
        }
        return tree;


    }

    public Map<String, Double> getTotalAmountOfFramesRangeDynamic(String queryStr, DateSpace space) {

        HashMap<String, Double> tree = new HashMap<String, Double>();
        String currentDate = convertDateToString(space.getInit());
        for (String s : space.getSlots()) {

            System.out.println(s);
            String queryAppendix = space.getQuery(currentDate);
            String queryTmp = "";
            if (!queryStr.equals("")) {
                queryTmp = queryAppendix + "" + queryStr;
            } else {
                queryTmp = queryAppendix;
            }
            System.out.println(queryTmp);
            // MB
            Double amount = getTotalAmountFramesDynamic(queryTmp);


            tree.put(s, amount);
            currentDate = space.nextDate(currentDate);
        }
        return tree;


    }

    public Map<String, Double> getTotalAmountOfFramesRangeStatic(String queryStr, DateSpace space) {

        HashMap<String, Double> tree = new HashMap<String, Double>();
        String currentDate = convertDateToString(space.getInit());
        for (String s : space.getSlots()) {

            System.out.println(s);
            String queryAppendix = space.getQuery(currentDate);
            String queryTmp = "";
            if (!queryStr.equals("")) {
                queryTmp = queryAppendix + "" + queryStr;
            } else {
                queryTmp = queryAppendix;
            }
            System.out.println(queryTmp);
            // MB
            Double amount = getTotalAmountFramesStatic(queryTmp);


            tree.put(s, amount);
            currentDate = space.nextDate(currentDate);
        }
        return tree;


    }

    public Double getTotalAmountFrames(String queryStr) {

        double r = 0;
        int count = 0;
        Map<String, Integer> blanks = new HashMap<String, Integer>();
        total = new HashMap<String, Integer>();
        ArrayList list = new ArrayList();
        IndexSearcher searcher = null;
        try {
            //standard search... nearly textbook example
            QueryParser parser;

            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return null;
            }
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(queryStr);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();

            /**
             * Check for each result, if there is a field that matches with the
             * extrafields parameter.
             */
            for (int i = 0; i < hitcount; i++) {
                Document doc = searcher.doc(hitsList.get(i).doc);
                String fSTr = doc.get("NumberOfFrames");

                if (fSTr != null) {
                    double v = Double.parseDouble(fSTr);
                    r += v;
                    System.out.println("Frame:  )" + v);
                } else {
                    r += 1;
                }
                count++;

            }

        } catch (Exception e) {
        }

        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r / count;


    }

    public Double getTotalAmountFramesDynamic(String queryStr) {

        double r = 0;
        int count = 0;
        Set<String> series = new HashSet<String>();
        Map<String, Integer> blanks = new HashMap<String, Integer>();
        total = new HashMap<String, Integer>();
        ArrayList list = new ArrayList();
        IndexSearcher searcher = null;
        try {
            //standard search... nearly textbook example
            QueryParser parser;

            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return null;
            }
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(queryStr);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();

            /**
             * Check for each result, if there is a field that matches with the
             * extrafields parameter.
             */
            for (int i = 0; i < hitcount; i++) {
                Document doc = searcher.doc(hitsList.get(i).doc);
                String fSTr = doc.get("NumberOfFrames");
                String fSTr2 = doc.get("SeriesInstanceUID");

                if (fSTr != null) {
                    double v = Double.parseDouble(fSTr);
                    r += 1;
                    series.add(fSTr2);

                }

            }

        } catch (Exception e) {
        }

        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r / series.size();


    }

    public Double getTotalAmountFramesStatic(String queryStr) {

        double r = 0;
        int count = 0;
        Map<String, Integer> blanks = new HashMap<String, Integer>();
        total = new HashMap<String, Integer>();
        Set<String> series = new HashSet<String>();
        ArrayList list = new ArrayList();
        IndexSearcher searcher = null;
        try {
            //standard search... nearly textbook example
            QueryParser parser;

            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return null;
            }
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(queryStr);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();

            /**
             * Check for each result, if there is a field that matches with the
             * extrafields parameter.
             */
            for (int i = 0; i < hitcount; i++) {
                Document doc = searcher.doc(hitsList.get(i).doc);
                String fSTr = doc.get("NumberOfFrames");
                String fSTr2 = doc.get("SeriesInstanceUID");
                if (fSTr == null) {

                    r += 1;
                    series.add(fSTr2);
                }


            }

        } catch (Exception e) {
        }

        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r / series.size();


    }
    private Map<String, Integer> total = new HashMap<String, Integer>();

    public Map<String, Integer> getBlanks(String queryStr, List<String> fields) {

        Map<String, Integer> blanks = new HashMap<String, Integer>();
        total = new HashMap<String, Integer>();
        ArrayList list = new ArrayList();
        IndexSearcher searcher = null;
        try {
            //standard search... nearly textbook example
            QueryParser parser;

            if (IndexReader.indexExists(index)) {
                searcher = new IndexSearcher(index, true);
            } else {
                return blanks;
            }
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            parser = new QueryParser(Version.LUCENE_30, "FileName", analyzer);

            Query query = parser.parse(queryStr);

            AllDocCollector collector = new AllDocCollector();
            searcher.search(query, collector);

            int hitcount = collector.getHits().size();
            List<ScoreDoc> hitsList = collector.getHits();

            /**
             * Check for each result, if there is a field that matches with the
             * extrafields parameter.
             */
            for (int i = 0; i < hitcount; i++) {
                Document doc = searcher.doc(hitsList.get(i).doc);
                for (String s : fields) {

                    Field f = doc.getField(s);
                    if (f != null && !f.isBinary()) {
                        String name = f.name();

                        String value = f.stringValue();

                        //if (value != null) {
                            Integer oldValue = blanks.get(name);
                            Integer oldValueTotal = total.get(name);
                            if (oldValue == null) {
                                oldValue = 0;
                            }
                            if (oldValueTotal == null) {
                                oldValueTotal = 0;
                            }

                            if (value==null||value.equals("")||value.length()==0) {
                                
                                oldValue++;
                            }
                            if (value != null && !value.equals("")) {
                                oldValueTotal++;
                            }

                            blanks.put(name, oldValue);
                            total.put(name, oldValueTotal);
                        //}


                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(blanks);
        System.out.println(total);
        
        return blanks;

    }

    /**
     * Convert a String indexed (given in UTF) to ASCII normal mode
     *
     * @param utfString utf string (2chars per bit) with terminal char
     * @return n ascii string (1 char per bit) without terminal char
     */
    public static String asciiMode(String utfString) {
        String n = null;
        try {
            n = new String(utfString.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }

        return n.trim();

    }

    public String convertDateToString(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);

    }

    public Map<String, Integer> getSizeDistribution(String queryStr, DateSpace space) {

        HashMap<String, Integer> tree = new HashMap<String, Integer>();
        String currentDate = convertDateToString(space.getInit());
        for (String s : space.getSlots()) {

            System.out.println(s);
            String queryAppendix = space.getQuery(currentDate);
            String queryTmp = "";
            if (!queryStr.equals("")) {
                queryTmp = queryAppendix + "" + queryStr;
            } else {
                queryTmp = queryAppendix;
            }
            System.out.println(queryTmp);
            // MB
            int amount = (int) (getTotalAmount(queryTmp) / 1024) / 1024;


            tree.put(s, amount);
            currentDate = space.nextDate(currentDate);
        }

        return tree;


    }

    // tag: PatientName or StudyInstanceUID
    public HashMap<String, Set> getDistributionTime(String queryStr, DateSpace space, String tag) {

        HashMap<String, Set> tree = new HashMap<String, Set>();
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



        for (String s : space.getSlots()) {
            tree.put(s, new HashSet<String>());
        }



        List<ScoreDoc> hitsList = collector.getHits();
        System.out.println("Hit count" + hitcount);
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
                Field _n = doc.getField(tag);
                Field _nD = doc.getField("StudyDate");

                if (_n != null && _nD != null) {
                    String n = asciiMode(_n.stringValue());
                    if (n != null && !n.equals("")) {
                        System.out.println(n);
                        System.out.println(_nD.stringValue());
                        tree.get(space.getSlot(asciiMode(_nD.stringValue()))).add(n);
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

    public List<ScoreDoc> searchGeneric(String queryStr) {

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



        List<ScoreDoc> hitsList = collector.getHits();
        try {
            searcher.close();
        } catch (IOException ex) {
            Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hitsList;

    }

    public int countResults(String queryStr) {
        List<ScoreDoc> hitsList = searchGeneric(queryStr);
        if (hitsList == null) {
            return 0;
        }
        return hitsList.size();
    }

//
    public HashMap<String, Set> detectInvalidSerie(String queryStr, DateSpace space, int minumum) {

        if (queryStr == "") {
            return null;
        }



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



        List<ScoreDoc> hitsList = collector.getHits();
        int hitcount = collector.getHits().size();
        Map<String, Integer> result = new HashMap<String, Integer>();
        HashMap<String, Set> tree = new HashMap<String, Set>();
        for (String s : space.getSlots()) {
            tree.put(s, new HashSet<String>());
        }

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
                Field _n = doc.getField("SeriesInstanceUID");
                Field _nD = doc.getField("StudyDate");
                if (_n != null && _nD != null) {
                    String n = asciiMode(_n.stringValue());
                    if (n != null) {
                        //System.out.println("SerieInstanceUID:" +_nD.stringValue());
                        if (!result.containsKey(n)) {
                            int count = countResults("SeriesInstanceUID" + ":" + n);
                            System.out.println("Count: " + count);
                            System.out.println("SeriesInstanceUID:" + n);
                            result.put(n, count);
                            if (count < minumum) 
                            {
                                tree.get(space.getSlot(asciiMode(_nD.stringValue()))).add(n);
                            }
                        }
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

    // SerieInstanceUID?
    /**
     *
     * @param queryStr
     * @param tag: Tag and number of occurences
     * @return
     */
    public Map<String, Integer> countImagesPerTag(String queryStr, String tag) {
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



        List<ScoreDoc> hitsList = collector.getHits();
        int hitcount = collector.getHits().size();
        Map<String, Integer> result = new HashMap<String, Integer>();

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
                Field _n = doc.getField(tag);

                if (_n != null) {
                    String n = asciiMode(_n.stringValue());
                    if (n != null) {
                        if (!result.containsKey(n)) {
                            int count = countResults(tag + ":" + n);
                            result.put(n, count);
                        }

                    }
                }

            }
            try {
                searcher.close();
            } catch (IOException ex) {
                Logger.getLogger(TopTerms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    public List<Integer> minAndMax(Set<String> times) {



        return null;
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

    public HashMap<String, Set> getStudyDurationFull(String queryStr, int minumum) {

        HashMap<String, Set> tmp = getStudyDuration(queryStr, minumum);

        HashMap<String, Set> tree = new HashMap<String, Set>();

        Duration space = new Duration();


        for (String s : space.getSlots()) {
            tree.put(s, new HashSet<String>());
        }

        for (String s : tmp.keySet()) {
            int d = duration(tmp.get(s));
            String slot = space.getSlot(Integer.toString(d));
            System.out.println(slot);
            if (!slot.equals("UN")) {
                tree.get(slot).add(s);
            }

        }
        return tree;

    }
    
    
    public HashMap<String, Set> getStudyDurationFullApprox(String queryStr, int minumum) {

        HashMap<String, Set> tmp = getStudyDurationApprox(queryStr, minumum);

        HashMap<String, Set> tree = new HashMap<String, Set>();

        Duration space = new Duration();

        for (String s : space.getSlots()) 
        {
            tree.put(s, new HashSet<String>());
        }

        for (String s : tmp.keySet()) {
            int d = duration(tmp.get(s));
            String slot = space.getSlot(Integer.toString(d));
            System.out.println(slot);
            if (!slot.equals("UN")) {
                tree.get(slot).add(s);
            }

        }
        return tree;

    }

    
    

    public HashMap<String, Set> getStudyDuration(String queryStr, int minumum) {

        
        if (queryStr == "") {
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
                Field _n = doc.getField("SeriesInstanceUID");
                Field _nD = doc.getField("ContentTime");
                if (_n != null && _nD != null) {
                    String n = asciiMode(_n.stringValue());
                    if (n != null) {
                        //System.out.println("SerieInstanceUID:" +_nD.stringValue());


                        System.out.println("Count: " + _nD);
                        System.out.println("SeriesInstanceUID:" + n);
                        if (tree.get(n) == null) {
                            tree.put(n, new HashSet<String>());
                        }
                        tree.get(n).add(asciiMode(_nD.stringValue()));

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
    
    public HashMap<String, Set> getStudyDurationApprox(String queryStr, int minumum) {

        if (queryStr == "") {
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
                    String n = asciiMode(_n.stringValue());
                    if (n != null) {
                        //System.out.println("SerieInstanceUID:" +_nD.stringValue());

                        System.out.println("Count: " + _nD);
                        System.out.println("SeriesInstanceUID:" + n);
                        if (tree.get(n) == null) {
                            tree.put(n, new HashSet<String>());
                        }
                        tree.get(n).add(asciiMode(_nD.stringValue()));
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
