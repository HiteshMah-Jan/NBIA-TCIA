//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=json
//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=html
//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=xml
//To Test: http://localhost:8080/nbia-api/api/v1/getCollectionValues?format=csv


package gov.nih.nci.nbia.restAPI;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.nih.nci.nbia.dto.LicenseDTO;
import gov.nih.nci.nbia.dao.LicenseDAO;
import gov.nih.nci.nbia.restUtil.RoleCache;
import gov.nih.nci.nbia.security.NCIASecurityManager;
import gov.nih.nci.nbia.util.SpringApplicationContext;
@Path("/submitLicense")
public class SubmitLicense extends getData{
	private static final String column="Collection";
	public final static String TEXT_CSV = "text/csv";

	@POST
	@Produces(MediaType.APPLICATION_JSON)

	public Response constructResponse(@FormParam("longName") String longName,
			@FormParam("id") String id,
			@FormParam("shortName") String shortName,
			@FormParam("licenseURL") String licenseURL,
			@FormParam("licenseText") String licenseText,
			@FormParam("commercialUse") String commercialUse) {

		try {	
			   Authentication authentication = SecurityContextHolder.getContext()
						.getAuthentication();
				String user = (String) authentication.getPrincipal();
				List<String> roles=RoleCache.getRoles(user);
                if (roles==null) {
                	roles=new ArrayList<String>();
                	System.out.println("geting roles for user");
				    NCIASecurityManager sm = (NCIASecurityManager)SpringApplicationContext.getBean("nciaSecurityManager");
				    roles.addAll(sm.getRoles(user));
				    RoleCache.setRoles(user, roles);
                }
                boolean hasRole=false;
                for (String role:roles) {
                	if(role.equalsIgnoreCase("NCIA.MANAGE_COLLECTION_DESCRIPTION")) {
                		hasRole=true;
                	}
                }
                if (!hasRole) {
					return Response.status(401)
							.entity("Insufficiant Privileges").build();
                }
                LicenseDAO licenseDAO = (LicenseDAO)SpringApplicationContext.getBean("licenseDAO");
                LicenseDTO licenseDTO=new LicenseDTO();
                licenseDTO.setCommercialUse(commercialUse);
                if (id!=null&&id.length()>0) {
                    licenseDTO.setId(Integer.parseInt(id));
                }
                licenseDTO.setLicenseText(licenseText);
                licenseDTO.setShortName(shortName);
                licenseDTO.setLongName(longName);
                licenseDTO.setLicenseURL(licenseURL);
                licenseDAO.save(licenseDTO);
        
		return Response.ok().type("text/plain")
				.entity("License updated")
				.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(500)
				.entity("Server was not able to process your request").build();
	}

}
