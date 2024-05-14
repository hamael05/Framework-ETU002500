package mg.ituProm16;

import jakarta.servlet.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class FrontController extends HttpServlet {
    String packageName;
    List<Class> listController;

    public void init() throws ServletException {
        try{
            packageName = getInitParameter("package-sources");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Class> getListController() {
        List<Class> list = new ArrayList();
        ServletContext context = getServletContext();
        String path = context.getResource(packageName).getPath();
        File file = new File(path);
        for (File f : file.listFiles()) {
            if(f.isFile() && file.getName().endsWith(".class")) {
                String className = f.getName().substring(0, file.getName().length() - 6);
                Class class = Thread.currentThread().getContextClassLoader().loadClass(className);
                if (class.isAnnotationPresent(Controller.class)) {
                    list.add(class);
                }
            }
        }
        return list;
    }
    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        ServletOutputStream out = response.getOutputStream();
        String result="";
        if (listController!=null){
            for (int i = 0; i< listController.size(); i++) {
                result += listController.get(i).getName() + "\n";
            }
        } else {
            getListController();
            for (int i = 0; i< listController.size(); i++) {
                result += listController.get(i).getName() + "\n";
            }
        }
        out.whrite((result).getBytes());
        out.close();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.processRequest(req, resp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            this.processRequest(req, resp);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
