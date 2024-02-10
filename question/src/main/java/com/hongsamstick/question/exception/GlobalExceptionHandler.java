package com.hongsamstick.question.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

  // 접근 거부 예외 처리
  @ExceptionHandler(AccessDeniedException.class)
  public String handleAccessDeniedException(
    AccessDeniedException ex,
    WebRequest request,
    RedirectAttributes redirectAttributes
  ) {
    redirectAttributes.addFlashAttribute("error", ex.getMessage());
    String previousPage = request.getHeader("Referer");
    if (previousPage == null) {
      previousPage = "/"; // 기본 리다이렉트 URL 설정
    }
    return "redirect:" + previousPage;
  }

  // 엔티티를 찾을 수 없는 예외 처리
  @ExceptionHandler(EntityNotFoundException.class)
  public String handleEntityNotFoundException(
    EntityNotFoundException ex,
    WebRequest request,
    RedirectAttributes redirectAttributes
  ) {
    redirectAttributes.addFlashAttribute("error", ex.getMessage());
    return "redirect:/errorPage";
  }

  // 메소드 인자 타입 불일치 예외 처리
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public String handleMethodArgumentTypeMismatchException(
    MethodArgumentTypeMismatchException ex,
    WebRequest request,
    RedirectAttributes redirectAttributes
  ) {
    Class<?> requiredType = ex.getRequiredType();
    String typeName = requiredType != null
      ? requiredType.getSimpleName()
      : "알 수 없음";
    String error =
      "입력값이 잘못되었습니다. " +
      ex.getName() +
      "은(는) " +
      typeName +
      " 타입이어야 합니다.";
    redirectAttributes.addFlashAttribute("error", error);
    return "redirect:/errorPage";
  }

  // 유효성 검사 실패 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public String handleMethodArgumentNotValidException(
    MethodArgumentNotValidException ex,
    WebRequest request,
    RedirectAttributes redirectAttributes
  ) {
    // 현재 요청의 메소드와 URL 경로를 확인
    String method = request.getParameter("_method"); // PUT 요청인 경우 hidden input에서 _method 파라미터를 참조
    String requestUrl = request.getDescription(false).replace("uri=", ""); // 요청 URL 추출

    // 리다이렉션 URL 설정
    String redirectUrl;
    if ("PUT".equalsIgnoreCase(method)) { 
      // PUT 요청 실패 시 (게시글 수정)
      String code = requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
      redirectUrl = "redirect:/post/" + code + "/edit";
    } else {
      // POST 요청 실패 시 (게시글 생성)
      redirectUrl = "redirect:/post/create";
    }

    // 유효성 검사 실패 메시지와 폼 데이터를 RedirectAttributes에 추가
    ex
      .getBindingResult()
      .getFieldErrors()
      .forEach(fieldError -> {
        redirectAttributes.addFlashAttribute(
          fieldError.getField() + "Error",
          fieldError.getDefaultMessage()
        );
      });
    redirectAttributes.addFlashAttribute(
      "postDto",
      ex.getBindingResult().getTarget()
    );

    return redirectUrl; // 해당 폼으로 리다이렉션
  }
}
