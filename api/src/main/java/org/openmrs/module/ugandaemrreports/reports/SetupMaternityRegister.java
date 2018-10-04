package org.openmrs.module.ugandaemrreports.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ugandaemrreports.library.DataFactory;
import org.openmrs.module.ugandaemrreports.definition.dataset.definition.MaternityDatasetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Integrated Maternity Register Report
 * */

@Component
public class SetupMaternityRegister extends UgandaEMRDataExportManager {
	
	
	@Autowired
	private DataFactory df;
	
	@Override
	public String getDescription() {
		return "Integrated Maternity Register";
	}
	
	@Override
	public String getName() {
		return "Integrated Maternity Register";
	}
	
	@Override
	public String getUuid() {
		return "640cfa10-c96b-4a37-837f-77cffe08d771";
	}
	
	@Override
	public String getVersion() {
		return "1.0";
	}
	
	/**
     * @return the uuid for the report design for exporting to Excel
     */
	@Override
	public String getExcelDesignUuid() {
		return "898f734e-f635-422d-af73-ff2298c4a682";
	}
	
	@Override
	public List<Parameter> getParameters() {
		List<Parameter> l = new ArrayList<Parameter>();
		l.add(df.getStartDateParameter());
		return l;
	}

	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		List<ReportDesign> l = new ArrayList<ReportDesign>();
		l.add(buildExcel(reportDefinition));
		l.add(buildReportDesign(reportDefinition));
		return l;
	}
	
	/**
	 * Build the report design for the specified report, this allows a user to override the report
	 * design by adding properties and other metadata to the report design
	 *
	 * @param reportDefinition
	 * @return The report design
	 */
	@Override
	public ReportDesign buildReportDesign(ReportDefinition reportDefinition) {
		ReportDesign rd = createCSVDesign(getExcelDesignUuid(), reportDefinition);
		return rd;
	}


	public ReportDesign buildExcel(ReportDefinition reportDefinition) {
		ReportDesign rd = createExcelTemplateDesign("23ad46d2-83ab-4b38-b913-489c4d880460", reportDefinition, "MaternityRegister.xls");
		Properties props = new Properties();
		props.put("repeatingSections", "sheet:1,row:8,dataset:Maternity");
		props.put("sortWeight", "5000");
		rd.setProperties(props);
		return rd;
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.setParameters(getParameters());
		
		MaternityDatasetDefinition dsd = new MaternityDatasetDefinition();
		dsd.setName(getName());
		dsd.setParameters(getParameters());
		rd.addDataSetDefinition("Maternity", Mapped.mapStraightThrough(dsd));
		return rd;
	}
}