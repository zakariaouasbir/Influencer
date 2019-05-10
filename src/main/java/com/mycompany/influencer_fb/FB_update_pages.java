
        package com.mycompany.influencer_fb;

        import com.mongodb.BasicDBObject;
        import com.mongodb.MongoClient;
        import com.mongodb.client.FindIterable;
        import com.mongodb.client.MongoCollection;
        import com.mongodb.client.MongoDatabase;
        import com.restfb.DefaultFacebookClient;
        import com.restfb.FacebookClient;
        import com.restfb.Parameter;
        import com.restfb.exception.FacebookException;
        import com.restfb.exception.FacebookGraphException;
        import com.restfb.exception.FacebookNetworkException;
        import com.restfb.types.Page;

        import java.util.ArrayList;
        import java.util.Date;
        import java.util.Iterator;
        import org.apache.commons.lang3.StringUtils;
        import org.bson.Document;
        import org.bson.types.ObjectId;

        /**
        *
        * @author root
        */
        public class FB_update_pages {
        public static int semaphore=1;
        public static boolean valid=false;
        public static  void  main(String args[])
        {Util u=new Util();
        ArrayList<String> tokens=u.getaccesokenfb(u.get_atoken("Initial_Data", "192.168.1.108", "Access_Token"));
        System.out.println("--main"+tokens.size());
        MongoClient mongoClient= new MongoClient("localhost" ,27017 );
        MongoDatabase db = mongoClient.getDatabase("Initial_Data");
        MongoCollection<org.bson.Document> collfb=db.getCollection("Facebook_Pages");
        ArrayList<Document> list=getpages(db,collfb);
        FacebookClient facebookClient=null;
        int i=0;
           System.out.println("tokens:"+tokens.size());
        for(i=800;i<list.size();i++){
            facebookClient= new DefaultFacebookClient(tokens.get(semaphore-1));
             System.out.println("-->"+i+"page : "+list.get(i).get("facebook_page_title"));
        updatepage(tokens,list.get(i),collfb,facebookClient);
        }

        }

        public static void updatepage( ArrayList<String> tokens, Document list, MongoCollection<org.bson.Document> collfb,FacebookClient facebookClient) throws FacebookException ,FacebookNetworkException
        {
        try {
            System.out.println("seema"+semaphore);
        Page page1 = facebookClient.fetchObject(list.get("facebook_page_id").toString(), Page.class,
        Parameter.with("fields", "fan_count,verification_status,release_date,picture"));
        Document dd=   list;
        BasicDBObject updateQuery = new BasicDBObject();
        BasicDBObject updateQuery1 = new BasicDBObject();
        BasicDBObject updateQuerydate= new BasicDBObject();
        BasicDBObject updateQueryrelease= new BasicDBObject();
        BasicDBObject a=new BasicDBObject("facebook_page_fans",page1.getFanCount());
        a.append("date_update", new Date());
        BasicDBObject b=new BasicDBObject("verified",page1.getVerificationStatus());
        BasicDBObject c=new BasicDBObject("facebook_page_img",page1.getPicture().getUrl());
        BasicDBObject d=new BasicDBObject("released_date",page1.getReleaseDate());
            System.out.println("---> date "+page1.getReleaseDate());
        updateQuery.append("$push",a);
        updateQuery1.append("$set",b);
        updateQuerydate.append("$set",c);
        updateQueryrelease.append("$set",d);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append("facebook_page_title", dd.get("facebook_page_title"));
        System.out.println(""+updateQueryrelease.toJson());
        collfb.updateMany(searchQuery, updateQuery);
        collfb.updateMany(searchQuery, updateQuery1);
        collfb.updateMany(searchQuery, updateQuerydate);
         collfb.updateMany(searchQuery, updateQueryrelease);
        } catch (FacebookGraphException e) {
        System.out.println("--->"+e.getErrorMessage());
         if(e.getErrorMessage().contains("<!DOCTYPE html><html"))
          System.out.println("SOMETHING WENT WRONG");
               
        if(e.getErrorMessage().contains("request limit reached"))
        {  
        if(semaphore<tokens.size()){
        semaphore++;System.out.println("semaphore incremented ++ "+semaphore  );}
        else System.out.println("done here");;
        }
        if(e.getErrorMessage().contains(" migrated to page ID "))
        migratepage(list.get("facebook_page_id").toString(),  removespace(StringUtils.substringBetween(e.getErrorMessage(),"migrated to page ID","." )),"Initial_Data","Facebook_Pages", "192.168.1.108",27017);
        if(e.getErrorMessage().contains("does not exist, cannot be loaded"))
        disableepage(removespace(StringUtils.substringBetween(e.getErrorMessage(),"Object with ID '","' does not exist" )),"Initial_Data","Facebook_Pages", "localhost",27017);

        }}



        public static void migratepage(String page, String newpage,String dbs,String colls, String server,int port){
        MongoClient mongoClient= new MongoClient(server ,port );
        MongoDatabase db = mongoClient.getDatabase(dbs);
        MongoCollection<org.bson.Document> coll = db.getCollection(colls);

        coll.updateOne( new BasicDBObject("facebook_page_id", page),  new BasicDBObject("$set", new BasicDBObject("facebook_page_id", newpage)));
        System.out.println(page+"---------->"+newpage);

        }
        public static void disableepage(String page,String dbs,String colls, String server,int port){
        MongoClient mongoClient= new MongoClient(server ,port );
        MongoDatabase db = mongoClient.getDatabase(dbs);
        MongoCollection<org.bson.Document> coll = db.getCollection(colls);

        coll.updateOne( new BasicDBObject("facebook_page_id", page),  new BasicDBObject("$set", new BasicDBObject("disabled", true)));


        }
        public static String removespace(String a){
        return a.replaceAll("\\s+","");
        }
        public static ArrayList<Document> getpages( MongoDatabase db,  MongoCollection<org.bson.Document> collfb ){
        ArrayList<Document> list =new ArrayList();
        FindIterable<Document> all =  collfb.find(Document.parse("{$and :[{disabled:false}]}"));
        Iterator it = all.iterator(); 
        while (it.hasNext()) 
        list.add((Document) it.next());
        System.out.println("-->"+list.size());
        return list;
        } 

        }


