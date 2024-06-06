package mg.ituProm16.utilitaire;

import java.util.*;


public class ModelView {
    String url;
    HashMap<String,Object> data;

    public ModelView(String url, HashMap<String,Object> data){
        this.url = url;
        this.data = data;
    }

    public ModelView(){}

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public HashMap<String, Object> getData(){
        return this.data;
    }

    public void setData(HashMap<String,Object> data){
        this.data = data;
    }

    public void add(String key, Object value){
        data.put(key, value);
    }
}