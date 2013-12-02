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
	private String[][][] morpheme;
	private double[][][][] weight;
	private HSSFWorkbook[] wb;
	private Sheet sheet;
	private FileOutputStream out;
	
	
	Excel(String[][][] morpheme, double[][][][] weight){
		this.morpheme = morpheme;
		this.weight = weight;
		wb = new HSSFWorkbook[morpheme.length];
	}
	
	public void createXLS(){
		String[] sheetName = {"日付重み","時間重み","住所重み"};
		for(int i=0; i<weight.length; i++){
			wb[i] = new HSSFWorkbook();
			for(int j=0; j<weight[i].length; j++){
				sheet = wb[i].createSheet(sheetName[j]);
				for(int k=0; k<morpheme[i].length; k++){
					inputCell(i,j,k);
				}
			}

			try {
				out = new FileOutputStream("excel\\kekka"+(i+1)+".xls");
				wb[i].write(out);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch(IOException ie){
				ie.printStackTrace();
			}
		}
	}
	
	/**
	 * @param i ファイルインデックス
	 * @param j 0なら日付重み、1なら時間重み、2なら住所重みの演算をしていることを指す。
	 * @param k 行インデックス
	 */
	private void inputCell(int i, int j, int k){
		Row[] row = new Row[morpheme[i].length];
		row[k] = sheet.createRow(k);
		Cell[] cell = new Cell[morpheme[i][k].length*2];
		for(int n=0; n<cell.length; n=n+2){
			//形態素の出力。またはセルの設定
			cell[n] = row[k].createCell(n);
			cell[n].setCellValue(morpheme[i][k][n/2]);
			CellStyle morphemeStyle = wb[i].createCellStyle();
			morphemeStyle.setAlignment(CellStyle.ALIGN_CENTER);
			morphemeStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    morphemeStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		    cell[n].setCellStyle(morphemeStyle);
		    
		    //各形態素の重みを出力。またはセルの設定
			cell[n+1] = row[k].createCell(n+1);
			cell[n+1].setCellValue(weight[i][j][k][n/2]);
		    CellStyle weightStyle = wb[i].createCellStyle();
		    weightStyle.setAlignment(CellStyle.ALIGN_CENTER);
			weightStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    weightStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
		    weightStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
		    cell[n+1].setCellStyle(weightStyle);
		}
	}
}