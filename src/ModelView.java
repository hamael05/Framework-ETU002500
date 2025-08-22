package modelview ;

import java.util.HashMap;

public class ModelView{
    String url ; 
    HashMap<String , Object > data  ; 
    public ModelView()
    {
        this.data = new HashMap<>() ;  // Initialisation de la HashMap de données 
    } 
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    } 
    public void addObject(String key , Object value) {
        this.data.put( key , value ) ; 
    }
}