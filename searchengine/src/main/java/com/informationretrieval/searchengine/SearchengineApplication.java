package com.informationretrieval.searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SpringBootApplication
@RestController
public class SearchengineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchengineApplication.class, args);
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/search")
	public String search(@RequestParam(value = "searchString", defaultValue = "") String searchString) throws IOException, ParseException, InvalidTokenOffsetsException {
		
		WordNetHandler.initialize();
		
		searchString = searchString.replaceAll("[^a-zA-Z0-9\\s]", " ");

		List<String> stringList = LuceneHandler.Tokenize(searchString);
		List<String> synonymList = WordNetHandler.getSynonyms(stringList);

		List<DocumentDetails> result = LuceneHandler.QuerySearch(stringList, synonymList);
		
		ResultJson resultJson = new ResultJson(stringList, synonymList, result);

		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		
		String json = gson.toJson(resultJson);
		
		return json;
	}
	
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/createIndex")
	public static void createIndex() throws FileNotFoundException, IOException {

		File folder = new File(ProjectConstants.DOCUMENT_DIR);
		File[] listOfFiles = folder.listFiles();

		List<DocumentDetails> docoumentList = new ArrayList<DocumentDetails>();

		int id = 0;
		for (File file : listOfFiles) {
			String topicName = FilenameUtils.removeExtension(file.getName());

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}

				String topicData = sb.toString();
				topicData = topicData.replaceAll("[^a-zA-Z0-9\\s,.\\_\\-\\+\\=\\:\\;\'\"\\?/\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\{\\}\\[\\]]", " ");
				topicData = topicData.replace("\n", "").replace("\r", "");

				docoumentList.add(new DocumentDetails(id, topicName, "", topicData));
			}
			id++;

		}

		FileUtils.deleteDirectory(new File(ProjectConstants.INDEX_PATH));

		LuceneHandler.IndexCreater(docoumentList);

	}

}
