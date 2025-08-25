package authentification ; 

public class AuthLevel {
    String nameAuth ; 
    int level ; 


    public AuthLevel(String nameAuth, int level ) { 
        this.nameAuth = nameAuth ; 
        this.level = level ; 
    } 

    public AuthLevel() {} 
    
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getNameAuth() {
        return nameAuth;
    }
    public void setNameAuth(String nameAuth) {
        this.nameAuth = nameAuth;
    }
}
