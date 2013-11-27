package sample.extract;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Excel {
	private String[][] morpheme;
	private double[][][] weight;
	private HSSFWorkbook wb;
	private Sheet sheet;
	private FileOutputStream out;
	
	
	Excel(String[][] morpheme, double[][][] weight){
		this.morpheme = morpheme;
		this.weight = weight;
	}
	
	public void createXLS(){
		wb = new HSSFWorkbook();
		String[] sheetName = {"日付重み","時間重み","住所重み"};
		for(int i=0; i<weight.length; i++){
			sheet = wb.createSheet(sheetName[i]);
			for(int j=0; j<morpheme.length; j++){
				inputCell(i,j);
			}
		}
		
		try {
			out = new FileOutputStream("test.xls");
			wb.write(out);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch(IOException ie){
			ie.printStackTrace();
		}
	}
	
	private void inputCell(int i, int j){
		Row[] row = new Row[morpheme.length];
		row[j] = sheet.createRow(j);
		Cell[] cell = new Cell[morpheme[j].length*2];
		for(int k=0; k<cell.length; k=k+2){
			//形態素の出力。またはセルの設定
			cell[k] = row[j].createCell(k);
			cell[k].setCellValue(morpheme[j][k/2]);
			CellStyle morphemeStyle = wb.createCellStyle();
			morphemeStyle.setAlignment(CellStyle.ALIGN_CENTER);
			morphemeStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    morphemeStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		    cell[k].setCellStyle(morphemeStyle);
		    
		    //各形態素の重みを出力。またはセルの設定
			cell[k+1] = row[j].createCell(k+1);
			cell[k+1].setCellValue(weight[i][j][k/2]);
		    CellStyle weightStyle = wb.createCellStyle();
		    weightStyle.setAlignment(CellStyle.ALIGN_CENTER);
			weightStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    weightStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
		    weightStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
		    cell[k+1].setCellStyle(weightStyle);
		}
	}
}