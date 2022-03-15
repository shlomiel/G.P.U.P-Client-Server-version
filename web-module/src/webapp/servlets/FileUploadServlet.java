
package webapp.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import logic.exceptions.*;
import logic.system.Gpup;
import logic.dataManagment.GraphManager;
import logic.utils.ObjectToDTO;
import utils.ServletUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

@WebServlet("/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        Collection<Part> parts = request.getParts();

        //out.println("Total parts : " + parts.size());

        StringBuilder fileContent = new StringBuilder();
        String createdBy = null;
        for (Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()));
            createdBy = part.getSubmittedFileName();

        }


        try {
            Gpup.getInstance().buildGraphFromXml(fileContent, createdBy);
            response.setContentType("application/json");

            GraphManager graphManager = ServletUtils.getGraphManager(getServletContext());
            graphManager.addGraph(ObjectToDTO.fromGraphToDTO(Gpup.getInstance().getSystemGraph()));
            out.print("Successfuly uploaded");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (JAXBException | SerialSettNameExistsException | ConflictException |
                UnknownTargetInDescpritionException | TargetNameExistsException | WrongFileTypeException e) {
            out.print(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            e.printStackTrace();
        }
    }

    private String createFileUpoloadResponse() {
        Gson gson = new GsonBuilder().setPrettyPrinting().
                setLongSerializationPolicy( LongSerializationPolicy.STRING ).create();

        GraphManager graphManager = ServletUtils.getGraphManager(getServletContext());
        graphManager.addGraph(ObjectToDTO.fromGraphToDTO(Gpup.getInstance().getSystemGraph()));

        String jsonInString = gson.toJson(ObjectToDTO.fromGraphToDTO(Gpup.getInstance().getSystemGraph()));
        return jsonInString;
    }

    private void printPart(Part part, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb
            .append("Parameter Name: ").append(part.getName()).append("\n")
            .append("Content Type (of the file): ").append(part.getContentType()).append("\n")
            .append("Size (of the file): ").append(part.getSize()).append("\n")
            .append("Part Headers:").append("\n");

        for (String header : part.getHeaderNames()) {
            sb.append(header).append(" : ").append(part.getHeader(header)).append("\n");
        }

        out.println(sb.toString());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private void printFileContent(String content, PrintWriter out) {
        System.out.println("File content:");
        System.out.println(content);
    }
}
