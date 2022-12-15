package prodcons.v1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class TestProdCons {
	public final static int TAILLE = 100;
	public static int nProd, nCons;
	public static long prodTime, consTime, prodMax, prodMin, mCons;
	
	public static void main(String[] args) throws
				InterruptedException, 
				InvalidPropertiesFormatException, 
				IOException {
		loadData();
		
		Producer[] producteurs   = new Producer[nProd];
		Consumer[] consommateurs = new Consumer[nCons];
		
		for (int k = 0 ; k < nProd ; k++)
			producteurs[k] = new Producer(prodMin, prodMax, prodTime);
		for (int k = 0 ; k < nCons ; k++)
			consommateurs[k] = new Consumer(mCons, consTime);
		
		for (Producer p : producteurs)
			p.join();
		for (Consumer c : consommateurs)
			c.join();
	}
	
	
	static void loadData() throws InvalidPropertiesFormatException, IOException {
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream("options.xml"));
		
		nProd = Integer.parseInt(properties.getProperty("nProd"));
		nCons = Integer.parseInt(properties.getProperty("nCons"));
		
		prodTime = Long.parseLong(properties.getProperty("prodTime"));
		consTime = Long.parseLong(properties.getProperty("consTime"));
		
		prodMax = Long.parseLong(properties.getProperty("prodMax"));
		prodMin = Long.parseLong(properties.getProperty("prodMin"));
		mCons = Long.parseLong(properties.getProperty("mCons"));
	}
}
