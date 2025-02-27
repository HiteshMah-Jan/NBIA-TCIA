/*L
 *  Copyright SAIC, Ellumen and RSNA (CTP)
 *
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/national-biomedical-image-archive/LICENSE.txt for details.
 */

package gov.nih.nci.nbia.dao;

import gov.nih.nci.nbia.dto.QcSearchResultDTO;
import gov.nih.nci.nbia.dto.QcStatusHistoryDTO;
import gov.nih.nci.nbia.dto.AdvancedCriteriaDTO;
import gov.nih.nci.ncia.criteria.QCSearchCriteria;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;


/**
 * 
 */
public interface QcStatusDAO {

	public List<QcSearchResultDTO> findSeries(String[] qcStatus,
			                                  List<String> collectionSites, String[] additionalQcFlagList, 
			                                  String[] patients) throws DataAccessException;
		
	public List<QcSearchResultDTO> findSeries(Map<String, QCSearchCriteria>criteria, Map<String, AdvancedCriteriaDTO> criteriaMap, List<String> cretieriaList, int maxRows);
	public List<QcSearchResultDTO> findSeries(String[] qcStatus,
            List<String> collectionSites, String[] additionalQcFlagList, 
            String[] patients, Date fromDate, Date toDate, int maxRows) throws DataAccessException;

	public List<QcStatusHistoryDTO> findQcStatusHistoryInfo(List<String> seriesList)throws DataAccessException;


	public void updateQcStatus(List<String> seriesList,
			                   List<String> statusList, 
			                   String newStatus, String[] additionalQcFlagList, String[] newAdditionalQcFlagList, 
			                   String userName, 
			                   String comment) throws DataAccessException;
	public void updateQcStatus(List<String> seriesList,
            String newStatus, 
            String batch,
            String submissionType,
            String releasedStatus,
            String userName, 
            String comment) throws DataAccessException;
	
	public List<Map<String,String>> findExistingStatus(String project, String site, List<String> seriesUids) throws DataAccessException;
	
}
