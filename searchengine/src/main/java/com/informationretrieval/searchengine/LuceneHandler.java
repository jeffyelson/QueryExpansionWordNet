package com.informationretrieval.searchengine;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneHandler {

	public static void IndexCreater(List<DocumentDetails> docoumentList) throws IOException {

		CustomAnalyzer customAnalyzer = CustomAnalyzer.builder().withTokenizer("standard").addTokenFilter("lowercase")
				.addTokenFilter("stop").addTokenFilter("porterStem").build();

		Directory index = FSDirectory.open(Paths.get(ProjectConstants.INDEX_PATH));
		IndexWriterConfig config = new IndexWriterConfig(customAnalyzer);
		IndexWriter writer = new IndexWriter(index, config);
		writer.deleteAll();

		for (DocumentDetails document : docoumentList) {
			Document doc = new Document();
			FieldType fieldType = new FieldType();
			fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			fieldType.setStored(true);
			fieldType.setStoreTermVectors(true);
			fieldType.setStoreTermVectorPositions(true);
			doc.add(new Field(ProjectConstants.TOPIC_NAME_FIELD, document.getTopicName(), fieldType));
			doc.add(new Field(ProjectConstants.TOPIC_CONTENT_FIELD, document.getTopicDocument(), fieldType));
			writer.addDocument(doc);
		}

		writer.close();
		index.close();
	}

	public static List<String> Tokenize(String str) throws IOException {

		List<String> result = new ArrayList<>();

		CustomAnalyzer analyzer = CustomAnalyzer.builder().withTokenizer("standard").addTokenFilter("lowercase")
				.addTokenFilter("stop").build();

		TokenStream stream = analyzer.tokenStream(null, new StringReader(str));
		stream.reset();

		CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);

		while (stream.incrementToken()) {
			result.add(termAttribute.toString());
		}

		return result;
	}

	public static List<DocumentDetails> QuerySearch(List<String> queryList, List<String> synonymList)
			throws ParseException, IOException, InvalidTokenOffsetsException {

		CustomAnalyzer customAnalyzer = CustomAnalyzer.builder().withTokenizer("standard").addTokenFilter("lowercase")
				.addTokenFilter("stop").addTokenFilter("porterStem").build();

		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

		MultiFieldQueryParser multiFieldQueryParser1 = new MultiFieldQueryParser(
				new String[] { ProjectConstants.TOPIC_NAME_FIELD, ProjectConstants.TOPIC_CONTENT_FIELD },
				customAnalyzer);

		for (String s : queryList) {
			queryBuilder.add(multiFieldQueryParser1.parse(s), Occur.SHOULD);
		}

		HashMap<String, Float> boosts = new HashMap<String, Float>();
		boosts.put(ProjectConstants.TOPIC_NAME_FIELD, 0.1f);
		boosts.put(ProjectConstants.TOPIC_CONTENT_FIELD, 0.1f);

		MultiFieldQueryParser multiFieldQueryParser2 = new MultiFieldQueryParser(
				new String[] { ProjectConstants.TOPIC_NAME_FIELD, ProjectConstants.TOPIC_CONTENT_FIELD },
				customAnalyzer, boosts);

		for (String s : synonymList) {
			queryBuilder.add(multiFieldQueryParser2.parse(s), Occur.SHOULD);
		}

		Query query = queryBuilder.build();

		Directory index = FSDirectory.open(Paths.get(ProjectConstants.INDEX_PATH));

		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);

		TopScoreDocCollector collector = TopScoreDocCollector.create(ProjectConstants.HITS_PER_PAGE, Integer.MAX_VALUE);

		searcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		List<Document> documentList = new ArrayList<Document>();

		List<DocumentDetails> docList = new ArrayList<DocumentDetails>();

		for (int i = 0; i < hits.length; i++) {
			documentList.add(searcher.doc(hits[i].doc));
		}

		Formatter formatter = new SimpleHTMLFormatter();
		QueryScorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, scorer);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 300);
		highlighter.setTextFragmenter(fragmenter);

		int id = 0;
		for (Document doc : documentList) {
			String highlightedSnippet = highlighter.getBestFragment(customAnalyzer,
					ProjectConstants.TOPIC_CONTENT_FIELD, doc.get(ProjectConstants.TOPIC_CONTENT_FIELD));
			docList.add(new DocumentDetails(id, doc.get(ProjectConstants.TOPIC_NAME_FIELD), highlightedSnippet, doc.get(ProjectConstants.TOPIC_CONTENT_FIELD)));
			id++;
		}

		reader.close();
		index.close();

		return docList;

	}

}
