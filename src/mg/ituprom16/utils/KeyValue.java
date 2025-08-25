package mg.ituprom16.utils;

public class KeyValue {
    String key;
    String value;
    public void setKey(String key)
    {
        this.key = key;
    }
    public void setValue(String value)
    {
        this.value = value;
    }   
    public String getKey()
    {
        return this.key;
    }
    public String getValue()
    {
        return this.value;
    }
    public KeyValue(String key,String value)
    {
        this.setKey(key);
        this.setValue(value);
    }
}
