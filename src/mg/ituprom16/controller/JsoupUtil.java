package mg.ituprom16.controller;
import java.util.Map;

import javax.print.Doc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

public class JsoupUtil {
    public static String getChamp(String exceptionString)
    {
        return exceptionString.trim().split("!")[1];
    }
    public static String getMessage(String exceptionString)
    {
        return exceptionString.trim().split("!")[0];
    }
    public static String getDefaultPage(String urlPage,String action,String method,String exceptionString,Map<String,String> mapValue) {
        try {
            // Exemple de document HTML avec des formulaires
            // Fetch the document with the Referer header
            Document document = Jsoup.connect(urlPage)
                .header("Referer", urlPage)  // Setting the Referer header
                .get();
            Element form = document.select("form[action=/"+action+"][method="+method+"]").first();
            if (form!=null) {
                Elements inputsInForm = form.select("input,textarea,select");
                for(Element input : inputsInForm)
                {
                    if (input != null && input.attr("name").compareTo(getChamp(exceptionString))==0) {
                        Element error = new Element("h1");
                        error.text(getMessage(exceptionString));
                        input.after(error);
                    }
                }
            }
            Elements elements = document.select("input,textarea,select");
            for(Element input : elements)
            {
                if (mapValue.containsKey(input.attr("name"))) {
                    input.attr("value",mapValue.get(input.attr("name")));
                }
            }
            return document.html();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR PAGE NOT FOUND FOR : "+urlPage;
    }
}
