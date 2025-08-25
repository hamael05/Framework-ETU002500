package mg.ituprom16.utils;
import jakarta.servlet.http.HttpSession;
public class MySession {
    public MySession(HttpSession session) {
        this.session = session;
    }

    public MySession() {
    }

    HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
    public Object get(String key)
    {
        return this.session.getAttribute(key);
    }
    public void add(String key, Object o)
    {
        this.session.setAttribute(key, o);
    }
    public void delete(String key)
    {
        this.session.removeAttribute(key);
    }
}
ne