package session ;   

import jakarta.servlet.http.HttpSession; 

public class MySession {
    HttpSession session ;  


    public MySession( HttpSession session )
    {
         this.session = session; 
    }
    public Object getSession(String key)
    {
        return this.session.getAttribute( key );
    } 
    public void addSession( String key , Object value )
    { 
        this.session.setAttribute( key ,  value ); 
    } 
    public void deleteSession( String key ) 
    { 
        this.session.removeAttribute("username");
    }
}