        /*
        * To change this license header, choose License Headers in Project Properties.
        * To change this template file, choose Tools | Templates
        * and open the template in the editor.
        */
        package com.mycompany.influencer_fb;

        import com.google.gson.Gson;
        import com.mongodb.MongoClient;
        import com.mongodb.client.MongoCollection;
        import com.mongodb.client.MongoDatabase;
import static java.lang.System.currentTimeMillis;
        import java.text.ParseException;
        import java.util.ArrayList;
import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
        import org.bson.Document;

        /**
        *
        * @author root
        */
        public class Influencer {   
        public static void main(String[] args) throws ParseException {
        Date datefin =new Date(currentTimeMillis()); 
        Date datedebut =new Date(currentTimeMillis() - 90*60*60*1000); 
        Gson g=new Gson();
        Utilinfdb udb=new  Utilinfdb();
        MongoClient mongoClient= new MongoClient("localhost" ,27017 );
        MongoDatabase db = mongoClient.getDatabase("Initial_Data");
        MongoCollection<org.bson.Document> collfb=db.getCollection("Facebook_Pages");

        ArrayList<Categorie>  cats=udb.getcategories("SELECT titre_cat,id_cat FROM Category", "titre_cat", "id_cat","localhost", 3306, "yeep", "root", "root");
        Map.Entry<String, Long> maxposts=null;
        Map.Entry<String, Long> maxfans=null;
         Map.Entry<String, Double> maxaction=null;
         Map.Entry<String, Long> maxadate=null;
        for(int i=0;i<cats.size();i++){
                Long res;
        System.out.println("--> "+cats.get(i).getCat_name()+"<-----");
        ArrayList<Document> pages =udb.get_pages(cats.get(i).getCat_id());
          HashMap<String,Long> fans=udb.get_list_engagement(pages, "localhost",27017,"Initial_Data", "Facebook_Pages");
         if(pages!=null){
       
    

        HashMap<String,Long> pages_cat=udb.get_list_values(pages, "localhost",27017,"Initial_Data", "Facebook_Data");
                     maxposts=udb.getmax(pages_cat);
        for(Map.Entry<String, Long> entry : pages_cat.entrySet()){
        if(entry!=null){
     
        udb.update_page_active(setactive(entry.getValue(),maxposts.getValue()),entry.getKey(), collfb);
        }
        else System.out.println("map null");
        }
        ////
           
//        HashMap<String,Long> pages_fans=udb.get_list_engagement(pages, "localhost",27017,"Initial_Data", "Facebook_Pages");
//        maxfans=udb.getmax(pages_fans);
//        
//        for(Map.Entry<String, Long> entry : pages_fans.entrySet()){
//        if(entry!=null){
//       
//        udb.update_page_engagement(setengagement(entry.getValue(),maxfans.getValue()),entry.getKey(), collfb);
//        }
//        else System.out.println("map null");
//        }
        
        ///
           
//        HashMap<String,Double> pages_moyen=udb.get_list_Actions(pages, "localhost",27017,"Initial_Data", "Facebook_Data");
//        maxaction=udb.getmaxdouble(pages_moyen);
//          System.out.println("max actions : "+maxaction.getValue());
//       for(Map.Entry<String, Double> entry : pages_moyen.entrySet()){
//        if(entry!=null){
//        System.out.println("id : "+maxaction.getKey()+" max : "+maxaction.getValue());
//        udb.update_page_actions(setactions(entry.getValue(),maxaction.getValue()),entry.getKey(), collfb);
//        }
//        else System.out.println("map null");
//        }
       
        ///senioriy
           
        HashMap<String,Long> pages_ancienity=udb.get_list_ancienity(pages, "localhost",27017,"Initial_Data", "Facebook_Pages");
        maxadate=udb.getmax(pages_ancienity);
          System.out.println("max actions : "+maxadate.getValue());
       for(Map.Entry<String, Long> entry : pages_ancienity.entrySet()){
        if(entry!=null){
        System.out.println("id : "+maxadate.getKey()+" max : "+maxaction.getValue());
        udb.update_page_ancienity(setancienity(entry.getValue(),maxadate.getValue()),entry.getKey(), collfb);
        }
        else System.out.println("map null");
        }
         
         }
         
         else
                System.out.println("categories vides");
         
        }
        }
         

        public static double setactive(Long nbr_post,Long maxpost){
          if(nbr_post !=null && maxpost !=null && maxpost !=0 )
        return ((nbr_post*100)/(double)maxpost);
       else
        return 0;

        }
        public static double setengagement(Long nbr_folow,Long max_follow){
           
            if(nbr_folow !=null && max_follow !=null && max_follow !=0 )
        return ((nbr_folow*100)/(double)max_follow);
       else
        return 0;

        }
           public static double setactions(Double nbr_folow,Double max_follow){
              System.out.println("-----actions--- ("+nbr_folow+"*100)"+max_follow);

            if(nbr_folow !=null && max_follow !=null && max_follow !=0 )
        return ((nbr_folow*100)/(double)max_follow);
       else
        return 0;

        }
            public static double setancienity(Long nbr_semaine,Long max_semaine){
              System.out.println("-----ancienity--- ("+nbr_semaine+"*100)"+max_semaine);

            if(nbr_semaine !=null && max_semaine !=null && max_semaine !=0 )
        return ((nbr_semaine*100)/(double)max_semaine);
        else
        return 0;

        }

        }


