//package com.yumu.hexie.common.config;
//
//import java.net.URLEncoder;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.springframework.web.servlet.view.document.AbstractExcelView;
//
//public class ViewExcel<T> extends AbstractExcelView {
//
//	private String fileName;
//	private String sheetName;
//	private List<String> titles;
//	private List<T> rows;
//	private RowProcessor<T> processor;
//	
//	public ViewExcel(String fileName,String sheetName,List<String> titles, List<T> rows,RowProcessor<T> processor){
//		this.fileName = fileName;
//		this.sheetName = sheetName;
//		this.titles = titles;
//		this.rows = rows;
//		this.processor = processor;
//	}
//	
//	
//	@Override
//	protected void buildExcelDocument(Map<String, Object> model,
//			HSSFWorkbook workbook, HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//		String excelName = fileName;//"用户信息.xls";  
//        // 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开  
//        response.setContentType("APPLICATION/OCTET-STREAM");  
//        response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode(excelName, "UTF-8"));    
//        // 产生Excel表头  
//        HSSFSheet sheet = workbook.createSheet(sheetName);  
//        HSSFRow header = sheet.createRow(0); // 第0行  
//        // 产生标题列  
//        int i = 0;
//        for(String title:titles) {
//            header.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(title);  
//        }
//        processor.process(rows, sheet);
//
//	}
//
//	public interface RowProcessor<T>{
//		public void process(List<T> t,HSSFSheet sheet);
//	}
//}
