/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.ugandaemrreports.reports;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ugandaemrreports.UgandaEMRReportUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.PatientToEncounterDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.EncounterToObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.ObsDataDefinition;
import org.openmrs.module.reporting.data.obs.definition.PatientToObsDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.dataset.definition.EncounterDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.ObsDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.BaseReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.renderer.XlsReportRenderer;
import org.openmrs.module.reporting.report.util.ReportUtil;

/**
 * Base implementation of ReportManager that provides some common method implementations
 */
public abstract class UgandaEMRReportManager extends BaseReportManager {

	protected final Log log = LogFactory.getLog(getClass());

	protected void addColumn(PatientDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		dsd.addColumn(columnName, pdd, Mapped.straightThroughMappings(pdd));
	}

	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		addColumn(dsd, columnName, new PatientToEncounterDataDefinition(pdd));
	}

	protected void addColumn(EncounterDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
		dsd.addColumn(columnName, edd, ObjectUtil.toString(Mapped.straightThroughMappings(edd), "=", ","));
	}

	protected void addColumn(ObsDataSetDefinition dsd, String columnName, PatientDataDefinition pdd) {
		addColumn(dsd, columnName, new PatientToObsDataDefinition(pdd));
	}

	protected void addColumn(ObsDataSetDefinition dsd, String columnName, EncounterDataDefinition edd) {
		addColumn(dsd, columnName, new EncounterToObsDataDefinition(edd));
	}

	protected void addColumn(ObsDataSetDefinition dsd, String columnName, ObsDataDefinition odd) {
		dsd.addColumn(columnName, odd, ObjectUtil.toString(Mapped.straightThroughMappings(odd), "=", ","));
	}

	protected ReportDesign createExcelTemplateDesign(String reportDesignUuid, ReportDefinition reportDefinition,
	                                                 String templatePath) {
		log.debug("Template path for report design " + reportDesignUuid + " with report definition " + reportDefinition + " is " + templatePath + " with report package as path " + ReportUtil.getPackageAsPath(getClass()));
		// TODO: Update this function below to use the class from the Report definition
		String resourcePath = ReportUtil.getPackageAsPath(getClass()) + "/" + templatePath;
		log.debug("Resource path for " + templatePath + " is " + resourcePath);
		return ReportManagerUtil.createExcelTemplateDesign(reportDesignUuid, reportDefinition, resourcePath);
	}

	protected ReportDesign createJSONTemplateDesign(String reportDesignUuid, ReportDefinition reportDefinition,
													String templatePath) {
		log.debug("Template path for JSON report design " + reportDesignUuid + " with report definition " + reportDefinition + " is " + templatePath + " with report package as path " + ReportUtil.getPackageAsPath(getClass()));
		// TODO: Update this function below to use the class from the Report definition
		String resourcePath = ReportUtil.getPackageAsPath(getClass()) + "/" + templatePath;
		log.debug("Resource path for " + templatePath + " is " + resourcePath);
		return ReportManagerUtil.createJSONTemplateDesign(reportDesignUuid, reportDefinition, resourcePath);
	}

	protected ReportDesign createExcelDesign(String reportDesignUuid, ReportDefinition reportDefinition) {
		return UgandaEMRReportUtil.createExcelDesign(reportDesignUuid, reportDefinition);
	}

	protected ReportDesign createCSVDesign(String reportDesignUuid, ReportDefinition reportDefinition) {
		return UgandaEMRReportUtil.createCSVDesign(reportDesignUuid, reportDefinition);
	}

	protected ReportRequest createMonthlyScheduledReportRequest(String requestUuid, String reportDesignUuid,
	                                                            Map<String, Object> parameters,
	                                                            ReportDefinition reportDefinition) {
		try {
			ReportRequest rr = new ReportRequest();
			rr.setUuid(requestUuid);
			rr.setReportDefinition(new Mapped<ReportDefinition>(reportDefinition, parameters));
			rr.setPriority(ReportRequest.Priority.NORMAL);
			rr.setProcessAutomatically(true);
			rr.setRenderingMode(
					new RenderingMode(XlsReportRenderer.class.newInstance(), "Excel", reportDesignUuid, Integer.MAX_VALUE));
			rr.setSchedule("0 0 4 1 * ?"); // Run monthly on the first of the month at 4:00am
			rr.setMinimumDaysToPreserve(45);
			return rr;
		}
		catch (Exception e) {
			throw new IllegalStateException("Error constructing scheduled report", e);
		}
	}

	public <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new NullPointerException("Programming error: missing parameterizable");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
}
