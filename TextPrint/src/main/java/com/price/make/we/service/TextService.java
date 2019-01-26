package com.price.make.we.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.price.make.we.dto.ParameterDto;
import com.price.make.we.dto.ResultDto;

@Service
public class TextService {
	
	public static String HTML_REG = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
	public static String NUMBER_ALPHABET_REG = "[^a-zA-Z0-9]";
	public static String NUMBER_REG = "^[0-9]*$";
	public static String ALPHABET_REG = "^[a-zA-Z]*$";

	public ResultDto getTextBind(ParameterDto param) throws Exception {
		String src = getHtmlSrcFromUrl(param.getUrl());
		String text = null;
		
		switch(param.getType()) {
		case HTML:
			text = src.replaceAll(HTML_REG, "")
					  .replaceAll(NUMBER_ALPHABET_REG, "");
			break;
			
		case TEXT:
			text = src.replaceAll(NUMBER_ALPHABET_REG, "");;
			break;
		}
		
//		String regText = getReplacedTextByRegex(text, NUMBER_ALPHABET_REG);
		List<String> alphabets = getSortedListByRegex(text, ALPHABET_REG);
		List<String> numbers = getSortedListByRegex(text, NUMBER_REG);
		String numerator = getCrossedTextFromArrays(alphabets, numbers);
		
		return getResultDto(numerator, param.getBind());
	}
	
	/**
	 * 주어진 URL의 웹페이지 소스 가져오기
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String getHtmlSrcFromUrl(String path) throws Exception {
		
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		BufferedReader buff = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		StringBuilder strBuilder = new StringBuilder();
		String contents = "";
		while((contents = buff.readLine()) != null) {
			strBuilder.append(contents);
			strBuilder.append("\r\n");
		}
		
		buff.close();
		conn.disconnect();
		
		return strBuilder.toString();
	}
	
	/**
	 * 정규 표현식에 따라 특정 패턴의 정렬된 문자열 얻기
	 * 
	 * @param src
	 * @param regex
	 * @return
	 */
	public List<String> getSortedListByRegex(String src, String regex) {
		List<String> result = new ArrayList<>(Arrays.asList(src.split("")))
				.parallelStream()
				.filter(Pattern.compile(regex).asPredicate())
				.collect(Collectors.toList());
		
		result.sort(new Comparator<String>() {

					@Override
					public int compare(String o1, String o2) {
						int compareTo = o1.toLowerCase().compareTo(o2.toLowerCase());
						if(compareTo == 0) {
							compareTo = o1.compareTo(o2);
						}
						return compareTo;
					}
				});
		
		return result;
	}
	
	/**
	 * 두 문자열을 순서대로 한글자씩 합쳐 하나의 String 객체 얻기
	 * 
	 * @param sa1
	 * @param sa2
	 * @return
	 */
	public String getCrossedTextFromArrays(List<String> sa1, List<String> sa2) {
		StringBuilder result = new StringBuilder();
		
		int sa1Size = sa1.size();
		int sa2Size = sa2.size();
		int bigSize = sa1Size > sa2Size ? sa1Size : sa2Size;
		
		for(int i = 0; i < bigSize; i++) {

			if(i < sa1Size) {
				result.append(sa1.get(i));
			}
			
			if(i < sa2Size) {
				result.append(sa2.get(i));
			}
		}
		
		return result.toString();
	}
	
	/**
	 * 주어진 String 객체를 bind단위로 묶어 Dto 객체로 전달한다.
	 * 
	 * @param numerator
	 * @param bind
	 * @return
	 */
	public ResultDto getResultDto(String numerator, int bind) {
		int length = numerator.length();
		int quotient = length / bind;
		int remainder = length % bind;
		int dividedLength = bind * quotient;

		List<String> quotStrs = new ArrayList<>();
		int strIndex = 0;
		for(int i = 0; i < quotient; i++) {
			strIndex = i * bind;
			quotStrs.add(numerator.substring(strIndex, (strIndex + bind)));
		}
		
		String remainStr = remainder > 0 ? numerator.substring(dividedLength) : "";
		
		ResultDto result = new ResultDto();
		result.setQuotient(quotStrs);
		result.setRemainder(remainStr);
		
		return result;
	}
}
