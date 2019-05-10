/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.influencer_fb;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.WEEKS;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.bson.Document;

/**
 *
 * @author root
 */
public class Util {
     public static   ArrayList<String> getaccesokenfb(ArrayList<Document> d){
          System.out.println(""+d.toString());
          ArrayList<String> access=new ArrayList<>();
          for(int i=0;i<d.size();i++)
              access.add(d.get(i).get("accesstoken").toString());
        return access;
      }
             public static ArrayList<Document> get_atoken(String dbs,String server,String col){
    ArrayList<Document> list =new ArrayList();
    MongoClient mongoClient= new MongoClient(server ,27017);
    MongoDatabase db = mongoClient.getDatabase(dbs);
    MongoCollection<org.bson.Document> coll = db.getCollection(col);
     Document d=new Document();
    d.put("platform", "facebook");
    d.put("active",true);
    FindIterable<Document> all =  coll.find(d);
    Iterator it = all.iterator(); 
    while (it.hasNext()) {
    list.add((Document) it.next());}
    
                 System.out.println("+++"+list.size());
    return list;
    }
             
     public static long weeksBetween(Date date1, Date date2) {
            return WEEKS.between(date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            public static Date getdate(String string1) throws ParseException{
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date result1 = df1.parse(string1);
            return result1;
            }
//        public static  getDate(String ){
//        
//        }
//            
            
        }

