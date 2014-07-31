import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ParamManager {

	public static Color cBackground = Color.white;
	public static Color cLineagesIn = Color.gray;
	public static Color cLineagesOut = Color.lightGray;
	public static Color cOrigination = Color.white;
	public static Color cDuplication = Color.pink;
	public static Color cSpeciation = Color.orange;
	public static Color cTransfer = Color.red;
	
	public static boolean showSpecBranchSource = true, showSpecBranchLin = true, showSpecBranchReceiving = false;
	public static boolean showTransferSource = true, showTransferLin = true, showTransferReceiving = false;
	public static boolean showSpecNode = true, showDuplications = true, showLosses = true;
	
	public static void loadParam() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("param"));
			String line;
			while((line = reader.readLine()) != null) {
				int sep = line.indexOf(':');
				if(sep !=-1) {
					String field = line.substring(0, sep);
					String[] params = line.substring(sep+1).split("-");
					Color color = new Color(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]));
					if(field.equals("BACKGROUND")) {
						cBackground = color;
					}
					if(field.equals("LINEAGES_IN")) {
						cLineagesIn = color;
					}
					if(field.equals("LINEAGES_OUT")) {
						cLineagesOut = color;
					}
					if(field.equals("ORIGINATION")) {
						cOrigination = color;
					}
					if(field.equals("DUPLICATION")) {
						cDuplication = color;
					}
					if(field.equals("SPECIATION")) {
						cSpeciation = color;
					}
					if(field.equals("TRANSFER")) {
						cTransfer = color;
					}
					
					if(field.equals("SHOWSPECBRANCH")) {
						showSpecBranchSource = (Integer.parseInt(params[0])==1);
						showSpecBranchLin = (Integer.parseInt(params[1])==1);
						showSpecBranchReceiving = (Integer.parseInt(params[2])==1);
					}
					if(field.equals("SHOWTRANSFER")) {
						showTransferSource = (Integer.parseInt(params[0])==1);
						showTransferLin = (Integer.parseInt(params[1])==1);
						showTransferReceiving = (Integer.parseInt(params[2])==1);
					}
					if(field.equals("SHOWOTHER")) {
						showSpecNode = (Integer.parseInt(params[0])==1);
						showDuplications = (Integer.parseInt(params[1])==1);
						showLosses = (Integer.parseInt(params[2])==1);
					}
				}
				
			}
			
			reader.close();
		}
		catch(IOException e) {
			System.out.println("Warning : impossible to open the param file. Default parameters will be used.");
		}
	}
	
	
	
	public static void saveParam() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("param", false));
			
			writer.write("BACKGROUND:" + cBackground.getRed() + '-' + cBackground.getGreen() + '-' + cBackground.getBlue() + '\n');
			writer.write("LINEAGES_IN:" + cLineagesIn.getRed() + '-' + cLineagesIn.getGreen() + '-' + cLineagesIn.getBlue() + '\n');
			writer.write("LINEAGES_OUT:" + cLineagesOut.getRed() + '-' + cLineagesOut.getGreen() + '-' + cLineagesOut.getBlue() + '\n');
			writer.write("ORIGINATION:" + cOrigination.getRed() + '-' + cOrigination.getGreen() + '-' + cOrigination.getBlue() + '\n');
			writer.write("DUPLICATION:" + cDuplication.getRed() + '-' + cDuplication.getGreen() + '-' + cDuplication.getBlue() + '\n');
			writer.write("SPECIATION:" + cSpeciation.getRed() + '-' + cSpeciation.getGreen() + '-' + cSpeciation.getBlue() + '\n');
			writer.write("TRANSFER:" + cTransfer.getRed() + '-' + cTransfer.getGreen() + '-' + cTransfer.getBlue() + '\n');
			writer.write("\n\n");
			writer.write("SHOWSPECBRANCH:" + (showSpecBranchSource ? 1 : 0) + "-" + (showSpecBranchLin ? 1 : 0) + "-" + (showSpecBranchReceiving ? 1 : 0) + '\n');
			writer.write("SHOWTRANSFER:" + (showTransferSource ? 1 : 0) + "-" + (showTransferLin ? 1 : 0) + "-" + (showTransferReceiving ? 1 : 0) + '\n');
			writer.write("SHOWOTHER:" + (showSpecNode ? 1 : 0) + "-" + (showDuplications ? 1 : 0) + "-" + (showLosses ? 1 : 0) + '\n');

			writer.close();
		}
		catch(IOException e) {
			System.out.println("Warning : impossible to open the param file. Default parameters will be used.");
		}
	}
	
	
	
	
}
