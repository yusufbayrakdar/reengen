package com.reengen.utils.auditreporter;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class JunitTest {
	
	private void prepareResource() throws IOException {
		// Create report directory and report.csv file
        File directory = new File("report");
        directory.mkdir();
        File users = new File("./resources/users.csv");
        FileOutputStream fos = new FileOutputStream(users);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        String writeLine = "USER_ID,USER_NAME\n" + 
        		"1,jpublic\n" + 
        		"2,atester";
        bw.write(writeLine);
        bw.close();
        
        File files = new File("./resources/files.csv");
        fos = new FileOutputStream(files);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
        writeLine = "FILE_ID,SIZE,FILE_NAME,OWNER_USER_ID\n" + 
        		"5974448e-9afd-4c9a-ac5a-9b1e84227820,5372274,pic.jpg,2\n" + 
        		"fab16fa4-8251-4394-a673-c961a65eb1d2,1638232,audit.xlsx,1\n" + 
        		"b4f3eecf-95aa-42a7-bffc-83a5441b7d2e,734003200,movie.avi,1\n" + 
        		"675672f6-a3ff-4872-baa9-955feead534d,570110,holiday.docx,2\n" + 
        		"73cadd04-c810-4b7d-9516-7b65a22a8cff,150680,marketing.txt,1";
        bw.write(writeLine);
        bw.close();
        
	}
	
	@Test
	public void testmain() throws IOException {
		prepareResource();
		Runner r = new Runner();
		String[] args = {"resources/users.csv", "resources/files.csv", "-c", "--top", "3"};
		r.main(args);
		List<List<String>> report;
		String line;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("report/report.csv"));
            report = new ArrayList<List<String>>();

            while ((line = reader.readLine()) != null) {
            	report.add(Arrays.asList(line.split(",")));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        
        assertEquals(3, report.size());
        
        assertEquals("movie.avi", report.get(0).get(0));
        assertEquals("jpublic", report.get(0).get(1));
        assertEquals("734003200", report.get(0).get(2));
        
        assertEquals("pic.jpg", report.get(1).get(0));
        assertEquals("atester", report.get(1).get(1));
        assertEquals("5372274", report.get(1).get(2));
        
        assertEquals("audit.xlsx", report.get(2).get(0));
        assertEquals("jpublic", report.get(2).get(1));
        assertEquals("1638232", report.get(2).get(2));
	}
	
	@Test
	public void sortTest() throws IOException {
		Runner r = new Runner();
		List<List<String>> unorderedList = new ArrayList<List<String>>();
		unorderedList.add(Arrays.asList("yusuf,3697,yusuf.jpeg".split(",")));
		unorderedList.add(Arrays.asList("sabri,527,sabri.jpeg".split(",")));
		unorderedList.add(Arrays.asList("bayrakdar,2016400378,bayrakdar.jpeg".split(",")));
		
		r.topOption = true;
		r.topCount = 2;
		r.reportCSV = false;
		
		List<List<String>> orderedList = r.sortFiles(unorderedList);
		assertEquals(2, orderedList.size());
		
		assertEquals("bayrakdar", orderedList.get(0).get(0));
		assertEquals("2016400378", orderedList.get(0).get(1));
		assertEquals("bayrakdar.jpeg", orderedList.get(0).get(2));
		
		assertEquals("yusuf", orderedList.get(1).get(0));
		assertEquals("3697", orderedList.get(1).get(1));
		assertEquals("yusuf.jpeg", orderedList.get(1).get(2));
		
		r.topCount = 4;
		
		orderedList = r.sortFiles(unorderedList);
		assertEquals(3, orderedList.size());
		
	}
	

	
}
