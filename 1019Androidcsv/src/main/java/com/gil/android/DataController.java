package com.gil.android;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//html 대신에 csv나 json을 리턴하는 Controller 만들어주는 어노테이션
@RestController
public class DataController {

	//csv 파일이 다운로드 된다.
	@RequestMapping(value ="data.csv",method = RequestMethod.GET)
	public String csv (){
		return "james,curry,rondo,bryant,rose";
	}
	
}
