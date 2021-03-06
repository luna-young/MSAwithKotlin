package com.microservices.kotlin.chapter3.Error

import com.fasterxml.jackson.core.JsonParseException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

/*
* 스프링은 ControllerAdvice와 ExceptionHandler 애너테이션을 사용해
* 애플리케이션 코드에서 캐치하지 않은 예외를 처리하는 매커니즘을 제공한다.
*
* ---
* 컨트롤러 어드바이스는 훌륭한 도구지만, 때로는 go-to 명령과 동등한 수준으로 사용될 수 있다.
* 흐름을 통제할 수 없게되고, 메인 로직 바깥에서 어떤 일이 일어나게 만든다.
* 컨트롤러 어드바이스는 특별한 상황을 처리해야 하는 경우에만 사용돼야 하며, 오류에 응답해야 할 때 비즈니스 로직을 추가해서 컨트롤러 어드바이스의 사용을 피할 수 있다. 
* * */

@ControllerAdvice //컨텍스트 스캔을 통해 스프링 컨텍스트에 추가된다.
class ErrorHandler {
    @ExceptionHandler(JsonParseException::class) //@ExceptionHandler 애너테이션을 추가한 메소드를 선언하고, exception 클래스를 참조해 처리할 예외 유형을 나타낸다
                //이 예제에서는 단지 JsonParseException만을 처리하고 있지만, 정말 심각한 에러가 발생할 경우 구체적인 정보를 표시하는 사용자 지정 메시지가 없을 수 있다.
                //이를 위해 컨트롤러 어드바이스에 Throwable 클래스의 generic handler를 둬서 api의 정의에 따라 에러 메시지를 제공하는 것이 좋다.
    fun JsonParserExceptionHandler(servletRquest: HttpServletRequest, exception: Exception) :
    //JsonParserException이 발생할 때 이를 처리하는 catch절이 없으며,
    //스프링이 해당 클래스에 사용 가능한 예외 핸들러를 검색해 오류를 전송한다.

    ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("JsonError", exception.message?:"invalid json"), HttpStatus.BAD_REQUEST) //-> 잘못된 json 요청 전송 시 JSON Error 메시지와 함께 400 bad request가 리턴된다.
        //오류 응답을 처리할 수 있는 ErrorResponse 데이터 클래스를 통해 에러메시지를 json형식으로 리턴한다.
    }

    @ExceptionHandler(CustomerNotFoundException::class)
    fun CustomerNotFoundExceptionHandler(servletRquest: HttpServletRequest, exception: Exception)
    : ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse("Customer not found", exception.message!!), HttpStatus.NOT_FOUND)
    } //존재하지 않는 고객을 요청할 때 리스펀스 -> {"error":"Customer not found","message":"customer '11' not found"}
}