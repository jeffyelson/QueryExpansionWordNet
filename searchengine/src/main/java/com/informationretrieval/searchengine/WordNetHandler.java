package com.informationretrieval.searchengine;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

public class WordNetHandler {

	private static Dictionary dict;

	public static boolean initialize() {
		try {
			JWNL.initialize(new FileInputStream(ProjectConstants.FILE_PROPERTY));
			dict = Dictionary.getInstance();
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	public static List<String> getSynonyms(List<String> stringList) {

		List<String> returnList = new ArrayList<String>();
		
		for(String s: stringList) {
			returnList.addAll(getSynonyms(s));
		}
		
		return returnList;
	}

	private static List<String> getSynonyms(String word) {

		Object[] posList = getWordPos(word).toArray();
		
		List<String> synonyms = new ArrayList<String>();
		
		for(Object obj: posList) {
			POS pos = (POS) obj;
			Synset synset = getCommonSynset(word, pos);
			if(synset != null) {
				synonyms.addAll(Arrays.asList(getLemmas(synset)));
			}
		}
		
		synonyms.removeIf(s -> s.contains(word));

		return synonyms;
	}

	private static Set<POS> getWordPos(String word) {

		if (dict == null)
			return null;
		IndexWordSet indexWordSet = null;
		try {
			indexWordSet = dict.lookupAllIndexWords(word);
		} catch (JWNLException e) {
		}
		return indexWordSet.getValidPOSSet();
	}

	private static Synset getCommonSynset(String word, POS pos) {
		if (dict == null)
			return null;

		Synset synset = null;
		try {
			IndexWord indexWord = dict.lookupIndexWord(pos, word);
			if (indexWord == null)
				return null;
			synset = indexWord.getSense(1);
		} catch (JWNLException e) {
		}

		return synset;
	}

	private static String[] getLemmas(Synset[] synsets) {
		HashSet<String> lemmaSet = new HashSet<String>();

		for (Synset synset : synsets) {
			String[] lemmas = getLemmas(synset);
			for (String lemma : lemmas)
				lemmaSet.add(lemma);
		}

		return lemmaSet.toArray(new String[lemmaSet.size()]);
	}

	private static String[] getLemmas(Synset synset) {
		Word[] words = synset.getWords();
		String[] lemmas = new String[words.length];

		for (int i = 0; i < words.length; i++) {
			lemmas[i] = words[i].getLemma();
			lemmas[i] = lemmas[i].replace("_", " ");
		}

		return lemmas;
	}

}
