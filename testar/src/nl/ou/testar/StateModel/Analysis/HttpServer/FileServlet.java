package nl.ou.testar.StateModel.Analysis.HttpServer;

import nl.ou.testar.StateModel.Analysis.AnalysisManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileServlet extends HttpServlet {

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext servletContext = getServletContext();
        // todo: this needs proper error handling
        // fetch a list of the generated models from the analysismanager
        AnalysisManager analysisManager = (AnalysisManager)servletContext.getAttribute("analysisManager");

        // prepare the response for a zip
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + request.getParameter("uid") +  ".zip\"");

        List<byte[]> screenshotBytes = analysisManager.fetchScreenShots(request.getParameter("uid"));
        // zip it
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
        final AtomicInteger counter = new AtomicInteger();
        screenshotBytes.forEach(bytes -> {
            ZipEntry zipEntry = new ZipEntry("screenshot_" + counter.incrementAndGet() + ".png");
            try {
                zipOut.putNextEntry(zipEntry);
                zipOut.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        zipOut.close();
    }

}
