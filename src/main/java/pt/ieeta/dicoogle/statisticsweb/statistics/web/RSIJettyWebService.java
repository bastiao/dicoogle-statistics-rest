

package pt.ieeta.dicoogle.statisticsweb.statistics.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Luís A. Bastião Silva - <bastiao@ua.pt>
 */
public class RSIJettyWebService  extends HttpServlet {
        
    
    private static RSIJettyPlugin plugin;


    public RSIJettyWebService() {
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response)
                    throws ServletException, IOException {

        String SOPInstanceUID = req.getParameter("uid");
        if (SOPInstanceUID == null) {
                response.sendError(402, "No UID");
                return;
        }

        response.setContentType("text/json;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.print("{\"action\":\"test\"}");
    }


    public static void setPlugin(RSIJettyPlugin plugin) {
            RSIJettyWebService.plugin = plugin;
    }

}
