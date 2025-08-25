package mg.ituprom16.utils;

import java.util.HashMap;

public class ModelAndView {
    String url;
    HashMap<String,Object> data;

   public String getUrl(){ return this.url; }
   public HashMap getData() { return this.data; }

   public void setData(HashMap data){this.data = data; }
   public void setUrl(String url) { this.url = url; }

   public ModelAndView(String url)
   {
      this.setUrl(url);
      this.data = new HashMap<String,Object>();
   }
   public ModelAndView(String url,HashMap<String,Object> myMap)
   {
      this.setUrl(url);
      this.data = myMap;
   }
   public void addObject(String key,Object valueObject)
   {
        HashMap map = this.getData();
        map.put(key, valueObject);
   }
   public Object getObject(String keyString)
   {
    HashMap map = this.getData();
    return map.get(keyString);
   }
    
}
