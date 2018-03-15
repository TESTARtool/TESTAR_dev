package nl.ou.testar.utils.report;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nl.ou.testar.utils.consumer.Item;

/**
 * ReportItem class.
 *
 */
public class ReportItem implements Item {
	
	private String fileName;
	private String data;
	private boolean append;
	
	/**
	 * Constructor.
	 * @param fileName name of report file
	 * @param data to be reported data
	 * @param append indicator whether data should be appended to the file
	 */
	public ReportItem(String fileName, String data, boolean append) {
		this.fileName = fileName;
		this.data = data;
		this.append = append;
	}

	@Override
	public void process() {
		PrintWriter pWriter = null;
        try {
			FileWriter fWriter = new FileWriter(fileName, append);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			pWriter = new PrintWriter(bWriter);
            pWriter.write(data);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (pWriter != null) {
                pWriter.close();
            }
        }
	}

}
