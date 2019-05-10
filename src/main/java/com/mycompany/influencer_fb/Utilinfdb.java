            /*
            * To change this license header, choose License Headers in Project Properties.
            * To change this template file, choose Tools | Templates
            * and open the template in the editor.
            */
            package com.mycompany.influencer_fb;

            import com.mongodb.BasicDBObject;
            import com.mongodb.MongoClient;
            import com.mongodb.client.FindIterable;
            import com.mongodb.client.MongoCollection;
            import com.mongodb.client.MongoDatabase;
            import static com.mongodb.client.model.Projections.fields;
            import static com.mongodb.client.model.Projections.include;
            import java.sql.DriverManager;
            import java.sql.ResultSet;
            import java.sql.SQLException;
            import java.sql.Statement;
            import java.text.ParseException;
            import java.util.ArrayList;
            import java.util.Date;
            import java.util.HashMap;
            import java.util.Iterator;
            import java.util.Map;
            import org.bson.Document;
            import org.bson.types.ObjectId;

            /**
            *
            * @author root
            */
            public class Utilinfdb {
                
            public  Util util=new Util();
            public static ArrayList<Categorie> getcategories(String reqq,String field1,String field2,String server,int port,String db,String user,String pass){
            String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
            String DB_URL = "jdbc:mysql://"+server+":"+port+"/"+db+"?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC";
            ArrayList<Categorie> pages = new ArrayList<>();
            //  Database credentials
            String USER = user;
            String PASS = pass;
            java.sql.Connection conn = null;
            Statement stmt = null;
            
            try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql = reqq;
            ResultSet rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            while(rs.next()){
            //field to fetch
            String a = rs.getString(field1);
            int b= rs.getInt(field2);

            pages.add(new Categorie(a,Integer.toString(b)));


            //Display values


            }
            rs.close();
            System.out.println("size="+pages.size());
            }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
            }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
            }finally{
            //finally block used to close resources
            try{
            if(stmt!=null)
            conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
            if(conn!=null)
            conn.close();
            }catch(SQLException se){
            se.printStackTrace();

            }//end finally try
            }//end try

            System.out.println("****"+pages.size());
            return pages;
            }
            public static ArrayList<Document> get_pages(String categorie){
            ArrayList<Document> list =new ArrayList();
            MongoClient mongoClient= new MongoClient("localhost" ,27017 );
            MongoDatabase db = mongoClient.getDatabase("Initial_Data");
            MongoCollection<org.bson.Document> coll = db.getCollection("Facebook_Pages");
            Document query=new Document("disabled",false);
            query.put("facebook_page_categorie", categorie);
            FindIterable<Document> all =  coll.find(query).projection(fields(include("_id","facebook_page_title")));
            Iterator it = all.iterator(); 
            while (it.hasNext()) 
            list.add((Document) it.next());

            if(list.size()>0)
            return list;
            else return null;
            }

            //get max value by categorie
            public static HashMap<String,Long> get_list_values(ArrayList<Document> page,String server,int port , String dbstring ,String collString)throws ParseException
            {   HashMap<String,Long> map=new HashMap<>();
            MongoClient mongoClient= new MongoClient(server , port );
            MongoDatabase db = mongoClient.getDatabase(dbstring);
            MongoCollection<org.bson.Document> coll = db.getCollection(collString);
            for(int i=0;i<page.size();i++)
            map.put(page.get(i).get("_id").toString(),coll.countDocuments( new Document("id_page",page.get(i).get("_id").toString())));

            return map;

            }
            //get max value by categorie
            public static HashMap<String,Long> get_list_engagement(ArrayList<Document> page,String server,int port , String dbstring ,String collString)throws ParseException
            {  
            HashMap<String,Long> map=new HashMap<>();
            MongoClient mongoClient= new MongoClient(server , port );
            MongoDatabase db = mongoClient.getDatabase(dbstring);
            MongoCollection<org.bson.Document> coll = db.getCollection(collString);
            for(int i=0;i<page.size();i++){
            FindIterable<Document> all = coll.find(new Document("_id",new ObjectId(page.get(i).get("_id").toString()))).projection(fields(include("facebook_page_fans")));
            Iterator it = all.iterator(); 
            while (it.hasNext()) {
            Document d=( Document) it.next();
            System.out.println(""+d);
            ArrayList<Long> c=(ArrayList) d.get("facebook_page_fans"); 
            Long a=c.get(c.size()-1);
            map.put(page.get(i).get("_id").toString(),a);

            }

            }

            return map;

            }

            public static HashMap<String,Double> get_list_Actions(ArrayList<Document> page,String server,int port , String dbstring ,String collString)throws ParseException
            {   HashMap<String,Double> map=new HashMap<>();
            ArrayList<Double> values=new ArrayList<>();
            MongoClient mongoClient= new MongoClient(server , port );
            MongoDatabase db = mongoClient.getDatabase(dbstring);
            MongoCollection<org.bson.Document> coll = db.getCollection(collString);
            for(int i=0;i<page.size();i++){
            FindIterable<Document> all = coll.find(new Document("id_page",page.get(i).get("_id").toString()));
       // new Document("_id",new ObjectId(page.get(i).get("_id").toString()))).projection(fields(include("nbr_partages","likes","comments")));

            Iterator it = all.iterator(); 
            while (it.hasNext()) {
            Document d=(Document) it.next();
           System.out.println("nbr_partages:"+d.getInteger("nbr_partages")+"likes"+d.getDouble("likes"));
            ArrayList<Long> a=(ArrayList) d.get("comments"); 
            values.add(new Double(d.getInteger("nbr_partages")*0.5)+new Double(d.getDouble("likes")*0.2)+new Double(a.size()*0.3));
                System.out.println("array:"+a.size());
            }
            
             if(values.size()>0)
            map.put(page.get(i).get("_id").toString(),getaverage(values));
            else 
            map.put(page.get(i).get("_id").toString(),0.0);

            } 

            return map;
            }
        
             //get max value by categorie
            public static HashMap<String,Long> get_list_ancienity(ArrayList<Document> page,String server,int port , String dbstring ,String collString)throws ParseException
            { 
             Date now=new Date();
            HashMap<String,Long> map=new HashMap<>();
            MongoClient mongoClient= new MongoClient(server , port );
            MongoDatabase db = mongoClient.getDatabase(dbstring);
            MongoCollection<org.bson.Document> coll = db.getCollection(collString);
            for(int i=0;i<page.size();i++){
            FindIterable<Document> all = coll.find(new Document("_id",new ObjectId(page.get(i).get("_id").toString()))).projection(fields(include("release_date")));
            Iterator it = all.iterator(); 
            while (it.hasNext()) {
            Document d=( Document) it.next();
            System.out.println(""+d);
            Date c=(Date) d.get("release_date"); 
            Long l= Util.weeksBetween(Util.getdate(c.toString()),now);
            map.put(page.get(i).get("_id").toString(),l);
             }

            }

            return map;

            }
            

            public static  Map.Entry<String, Long> getmax(HashMap<String,Long> map){
            Map.Entry<String, Long> maxEntry=null;

            for (Map.Entry<String, Long> entry : map.entrySet())
            {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
            maxEntry = entry;
            }
            } 
            return maxEntry;

            }
            public static  Map.Entry<String, Double> getmaxdouble(HashMap<String,Double> map){
            Map.Entry<String,Double> maxEntry=null;

            for (Map.Entry<String, Double> entry : map.entrySet())
            {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
            maxEntry = entry;
            }
            } 
            return maxEntry;

            }
            public static void update_page_active(double active,String page,  MongoCollection<org.bson.Document> collfb){
             BasicDBObject searchQuery= new BasicDBObject();
            searchQuery.append("_id", new ObjectId(page));
            BasicDBObject updateQuery= new BasicDBObject();
            BasicDBObject activedoc=new BasicDBObject("active",active);
            updateQuery.append("$push",activedoc);


            collfb.updateMany(searchQuery, updateQuery);

            }
            public static void update_page_engagement(double engagement,String page,  MongoCollection<org.bson.Document> collfb){
            BasicDBObject searchQuery= new BasicDBObject();
            searchQuery.append("_id", new ObjectId(page));
            BasicDBObject updateQuery= new BasicDBObject();
            BasicDBObject activedoc=new BasicDBObject("engagement",engagement);
            updateQuery.append("$push",activedoc);


            collfb.updateMany(searchQuery, updateQuery);

            }
            public static void update_page_actions(double actions,String page,  MongoCollection<org.bson.Document> collfb){
            System.out.println("-->actions on update:"+actions);
            BasicDBObject searchQuery= new BasicDBObject();
            searchQuery.append("_id", new ObjectId(page));
            BasicDBObject updateQuery= new BasicDBObject();
            BasicDBObject activedoc=new BasicDBObject("actions",actions);
            updateQuery.append("$push",activedoc);


            collfb.updateMany(searchQuery, updateQuery);

            }
              public static void update_page_ancienity(double anc,String page,  MongoCollection<org.bson.Document> collfb){
            System.out.println("-->ancienityon update:"+anc);
            BasicDBObject searchQuery= new BasicDBObject();
            searchQuery.append("_id", new ObjectId(page));
            BasicDBObject updateQuery= new BasicDBObject();
            BasicDBObject activedoc=new BasicDBObject("actions",anc);
            updateQuery.append("$set",activedoc);


            collfb.updateMany(searchQuery, updateQuery);

            }

            public static Double getaverage(ArrayList<Double> a){
                Double total=0.0;
                for(int i = 0; i<a.size(); i++){
                total = total+a.get(i);
                }
                return total / a.size();
                
            }

            }
