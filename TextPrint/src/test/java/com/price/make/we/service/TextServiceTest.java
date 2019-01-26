package com.price.make.we.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.price.make.we.dto.ResultDto;


public class TextServiceTest {

	private static TextService textService;
	
	@BeforeClass
	public static void setUp() {
		textService = new TextService();
	}
	
	@Test
	public void getHtmlSrcFromUrlTest() throws Exception {
		
		String url = "http://www.wemakeprice.com";
		String result = textService.getHtmlSrcFromUrl(url);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getTextWithoutHtmlTest() {
		
		String src = "<HTML><HEAD>Test</HEAD><BODY>Text Without HTML TAG.</BODY></HTML>";
		String result = src.replaceAll(TextService.HTML_REG, "");
		
		Pattern pattern = Pattern.compile(".*" + TextService.HTML_REG + ".*");
		Matcher matcher = pattern.matcher(result);
		Assert.assertFalse(matcher.matches());
	}
	
	@Test
	public void getOnlyNumberAndAlphabetTest() {
		
		String text = "    		/n %d On@%@ly 1 t>#$o 9 and a~!@t o Z\\s";
		String result = text.replaceAll(TextService.NUMBER_ALPHABET_REG, "");
		
		Assert.assertTrue(Pattern.matches("^[a-zA-Z0-9]*$", result));
	}
	
	@Test
	public void getListOnlyNumberTest() {
		
		String text = "    		/n %d On@%@ly 1 t>#$o 9 and a~!@t o Z\\s";
		List<String> result = textService.getSortedListByRegex(text, TextService.NUMBER_REG);
		
		Pattern pattern = Pattern.compile(TextService.NUMBER_REG);
		for(String s : result) {
			Assert.assertTrue(pattern.matcher(s).matches());
		}
	}
	
	@Test
	public void getArrayOnlyAlphabetTest() {
		
		String text = "    		/n %d On@%@ly 1 t>#$o 9 And a~!@t o Z\\s";
		List<String> result = textService.getSortedListByRegex(text, TextService.ALPHABET_REG);
		
		Pattern pattern = Pattern.compile(TextService.ALPHABET_REG);
		for(String s : result) {
			Assert.assertTrue(pattern.matcher(s).matches());
		}
	}
	
	@Test
	public void getCrossedTextFromArraysTest() {
		
		List<String> alphabets = Arrays.asList("A", "a", "B", "b", "C", "c", "D", "d", "e", "F", "G", "g", "H", "I", "j");
		List<String> numbers = Arrays.asList("0", "1", "2", "3", "5", "6", "7", "8", "9");
		
		String result = textService.getCrossedTextFromArrays(alphabets, numbers);
		Assert.assertEquals(result.length(), (alphabets.size() + numbers.size()));
		Assert.assertEquals(String.valueOf(result.charAt(0)), alphabets.get(0));
		Assert.assertEquals(String.valueOf(result.charAt(3)), numbers.get(1));
	}
	
	@Test
	public void getResultDto() {
		
		String numerator = "A1c2D4E6g7H9i0stz";
		int bind = 7;
		
		ResultDto result = textService.getResultDto(numerator, bind);
		List<String> quots = result.getQuotient();
		for(String quot : quots) {
			Assert.assertEquals(quot.length(), bind);
		}
		Assert.assertEquals(result.getRemainder().length(), numerator.length() % bind);
	}
}
