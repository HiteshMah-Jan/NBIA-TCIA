//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=json
//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=html
//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=xml
//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=csv


package gov.nih.nci.nbia.restAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.nih.nci.nbia.lookup.*;
import gov.nih.nci.nbia.util.NCIAConfig;
import gov.nih.nci.nbia.util.SpringApplicationContext;
import gov.nih.nci.nbia.util.StringEncrypter;
import gov.nih.nci.nbia.security.*;
import gov.nih.nci.nbia.util.SiteData;
import gov.nih.nci.nbia.restUtil.AuthorizationUtil;
import gov.nih.nci.nbia.restUtil.ManifestMaker;
import gov.nih.nci.nbia.dto.CustomSeriesListDTO;
import gov.nih.nci.nbia.dto.SeriesDTO;
import gov.nih.nci.nbia.dao.CustomSeriesListDAO;
import gov.nih.nci.nbia.dao.GeneralSeriesDAO;
import gov.nih.nci.nbia.searchresult.SeriesSearchResult;
import gov.nih.nci.nbia.util.SeriesDTOConverter;

@Path("/getManifestFromPatientStudySeries")
public class GetManifestFromPatientStudySeries extends getData {
	public final static String TEXT_CSV = "text/csv";

	@Context
	private HttpServletRequest httpRequest;
	private HashSet<String> collectionSet;

	/**
	 * This method get a set of all collection names
	 *
	 * @return String - set of collection names
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)

	public Response constructResponse(@FormParam("patientIDs") String patients, 
			@FormParam("studyUIDs") String studies, 
			@FormParam("seriesUIDs") String series, 
			@FormParam("anyOrAll") String anyOrAll, 
			@FormParam("includeAnnotation") String includeAnnotation) {

		try {
            if (includeAnnotation==null||includeAnnotation.length()<1) {
            	includeAnnotation="true";
            }
			long currentTimeMillis = System.currentTimeMillis();
			String manifestFileName = "manifest-" + currentTimeMillis + ".tcia";
			List<String> patientList=null;
			List<String> studyList=null;
			List<String> seriesList=null;
			if (patients!=null&&patients.length()>1) {
				patientList = (List<String>)Arrays.asList(patients.split("\\s*,\\s*"));
			}
			if (studies!=null&&studies.length()>1) {
				studyList = (List<String>)Arrays.asList(studies.split("\\s*,\\s*"));
			}
			if (series!=null&&series.length()>1) {
				seriesList = (List<String>)Arrays.asList(series.split("\\s*,\\s*"));
			}
			GeneralSeriesDAO generalSeriesDAO = (GeneralSeriesDAO)SpringApplicationContext.getBean("generalSeriesDAO");
			List<String> seriesListOut = generalSeriesDAO.getSeriesFromPatientStudySeriesUIDs(patientList, studyList, seriesList);
			String manifest=ManifestMaker.getManifextFromSeriesIds(seriesListOut, includeAnnotation, manifestFileName);
			return Response.ok(manifest).type("application/x-nbia-manifest-file").build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(500).entity("Server was not able to process your request").build();
	}

}