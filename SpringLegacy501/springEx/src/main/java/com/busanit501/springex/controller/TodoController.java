package com.busanit501.springex.controller;

import com.busanit501.springex.dto.PageRequestDTO;
import com.busanit501.springex.dto.PageResponseDTO;
import com.busanit501.springex.dto.TodoDTO;
import com.busanit501.springex.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

// 데이터만 전달. API 서버, API REST 서버,
//@RestController
// 화면 + 데이터 전달.
@Controller
// 화면상에서 접근하는 URL 주소를 맵핑 해주는 역할.
// 설정은 , 클래스 앞에도 가능하고, 메서드 앞에도 가능함.
@RequestMapping("/todo")
@Log4j2
@RequiredArgsConstructor
public class TodoController {

  final TodoService todoService;

  @GetMapping("/list")
  public  void listTest(Model model, PageRequestDTO pageRequestDTO) {
        log.info("todo list 조회 화면 테스트 콘솔");
        // 수정하기, 반환 타입을 PageResponseDTO 타입으로 변경하기.
    // 10개 나눠진 것만 확인용.
//        List<TodoDTO> dtoList = todoService.listAll(pageRequestDTO);
        // 서버 -> 화면, 모델
    // pageResponseDTO 의 내용물
    // 1) PageRequestDTO(page,size,getSkip)  2) List<TodoDTO> dtoList 3) total 전체갯수

    // 검색 조건 : 1) finished = 1 2) 작성자 w, 3) 제목 t, 4) keyword : 오늘
    // page = 1, size = 10
    // 기한 은 빼기.
    // 검색 후 페이징 번호 처리 안됨 1차조사
    // 검색 후 결과의 갯수 2차 조사,
    // 검색 결과 2,
    /// 페이지 표시: 1 만 나오기.
    log.info("pageRequestDTO의 finished 조회 : " + pageRequestDTO);

       PageResponseDTO<TodoDTO> pageResponseDTO = todoService.listAll(pageRequestDTO);;
    model.addAttribute("pageResponseDTO", pageResponseDTO);

  }

  @GetMapping({"/read", "/update"})
  public  void readTest(Long tno,PageRequestDTO pageRequestDTO, Model model) {
    log.info("todo list 조회 화면 테스트 콘솔");
    // C -> S -> Mapper -> DB
    // C <- S <- Mapper <- DB
    TodoDTO todoDTO = todoService.getOne(tno);
    // 서버 -> 화면, 모델
    // 파라미터에 정의된. PageRequestDTO pageRequestDTO , 화면에서 바로 사용가능.
    //
    model.addAttribute("todoDTO", todoDTO);

  }
  // 수정 관련 로직 처리  .
  @PostMapping("/update")
  public String updateTest(@Valid TodoDTO todoDTO, BindingResult bindingResult, PageRequestDTO pageRequestDTO, RedirectAttributes redirectAttributes){
    log.info("수정시 tno 확인 : " + todoDTO);

    int page = pageRequestDTO.getPage();
    int size = pageRequestDTO.getSize();

    // 유효성 검사 실패시에만 동작을함.
    if(bindingResult.hasErrors()) {
      log.info("현재: 수정중 오류 확인. bindingResult.hasErrors() 실행됨. ");
      redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
      log.info("page : " + page + " size : " + size + "todoDTO.getTno() : " +todoDTO.getTno());
      // 서버 -> 화면으로 , 데이터 전달, 방식 :쿼리스트링 하는 방식.
      redirectAttributes.addAttribute("page",page );
      redirectAttributes.addAttribute("size",size );
      redirectAttributes.addAttribute("tno", todoDTO.getTno());
      return "redirect:/todo/update";
    }

      redirectAttributes.addAttribute("page",page );
      redirectAttributes.addAttribute("size",size );
      redirectAttributes.addAttribute("tno", todoDTO.getTno());
    todoService.update(todoDTO);
    return "redirect:/todo/list";

  }


  @PostMapping("/delete")
  public String deleteTest(Long tno, PageRequestDTO pageRequestDTO,RedirectAttributes redirectAttributes){
    log.info("삭제시 tno 확인 : " + tno);
    log.info("화면에서 전달된 검색 재료 getLink 확인 : " + pageRequestDTO.getLink());
    int page = pageRequestDTO.getPage();
    int size = pageRequestDTO.getSize();

    // 서버 -> 화면으로 , 데이터 전달, 방식 :쿼리스트링 하는 방식.
    // 이미 쿼리 스트링으로 전달하는 방법이 있어서 중첩 되므로, 주석.
//    redirectAttributes.addAttribute("page",page );
//    redirectAttributes.addAttribute("size",size );
    todoService.delete(tno);
//    return "redirect:/todo/list?page="+page+"&size="+size;
    return "redirect:/todo/list?"+pageRequestDTO.getLink();

  }


//  @RequestMapping(value = "/register", method = RequestMethod.GET)
  @GetMapping("/register")
  public void registerGetTest() {
    log.info("todo register 등록 화면 Get  테스트 콘솔");
  }

  @PostMapping("/register")
  //@Valid : 유효성 체크 하겠다 의미.
  // BindingResult : 유효성 검사 실패시, 실패 관련 내용이 자동으로 담기는 도구
  // RedirectAttributes , 서버 -> 화면으로 , 쿼리 스트링으로 내용 전달.
  public String registerPostTest(@Valid TodoDTO todoDTO, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
//    log.info("todo register 등록 화면 Post 테스트 콘솔");
//    log.info(" TodoDTO todoDTO 타입 원래 register 확인.  : " + todoDTO );
    log.info("post 작업 중. ");

    // 유효성 검사 실패시에만 동작을함.
    if(bindingResult.hasErrors()) {
      log.info("bindingResult.hasErrors() 실행됨. ");
      redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
      return "redirect:/todo/register";
    }
    todoService.insert(todoDTO);
    return "redirect:/todo/list";

  }

  // 데이터 수집 방법들, 여러 예제 확인 해보기.
  // 파라미터 수집,
  // 기본, 데이터 포맷팅, 모델.
  // 결론, 스프링에서, 데이터 수집도 자동화를 이용하고,
  // 서버 측에서, 유효성 검사도 조금 더 쉽게 작업할 예정. 도구 사용해서.

  // 경로 확인. /todo/ex1
  // URL 쿼리 스트링온다.
  // 예) /todo/ex1?name="lsy"&age=30
  @GetMapping("/ex1")
  public void ex1Test(String name , int age ) {
    log.info("name : " + name + ", age : " + age);
  }

  // 기본값을 설정해보기.
  // @RequestParam, 클라이언트 보내는 정보에 대해서 수집관련 도구.
  @GetMapping("/ex2")
  public void ex2Test(@RequestParam(name = "name", defaultValue = "default lsy" ) String name , int age ) {
    log.info(" 기본값 name(이름 파라미터 안보내기) : " + name + ", age : " + age);
  }

  // 문자열, 숫자 크게 문제 안되는데, 날짜를 보내게 되면, 문제가 됨.
  // 필터 ,
  // 문제점 제시, 날짜 관련 포맷으로 쿼리 스트링 보냈을 경우, 자동 맵핑 안되는 문제점.

  // 경로 확인. /todo/ex3?dueDate=2024-05-27
  //클라이언트에서 전송하는 데이터 타입이 문자열이고,
  // 받는 타입은 LocalDate
  @GetMapping("/ex3")
  public void ex3Test(LocalDate dueDate) {
    log.info("ex3 test...");
    log.info(" LocalDate 타입 1차 확인.  : " + dueDate );
  }

  @PostMapping("/ex4")
  public void ex4Test(TodoDTO todoDTO) {
    log.info("ex4 test...");
    log.info(" TodoDTO todoDTO 타입 1차 확인.  : " + todoDTO );
  }

  // 서버 -> 화면 으로 특정 데이터 전달하기.
  // 화면 : 받은 데이터 -> ${dto}
  // Model 타입을 이용해서 전달할 예정.
  @GetMapping("/ex5")
  public void ex5Test(Model model) {
    log.info("ex5 test...");
    TodoDTO todoDTO = TodoDTO.builder()
        .tno(100L)
        .title("메뉴1")
        .writer("이상용")
        .dueDate(LocalDate.now())
        .finished(true)
        .build();
    // 서버 -> 화면 , 데이터 전달.
    model.addAttribute("menu","잡채밥");
    model.addAttribute("todoDTO",todoDTO);
  }

  // @ModelAttribute("dto"): 화면에서 사용하는 이름:todoDTO 이렇게 사용했지만,
  // 이름:todoDTO  -> 이름:dto
  @PostMapping("/ex6")
  public void ex6Test(@ModelAttribute("dto") TodoDTO todoDTO, Model model) {
    log.info("ex6 test...");
//    TodoDTO todoDTO = TodoDTO.builder()
//        .tno(100L)
//        .title("메뉴1")
//        .writer("이상용")
//        .dueDate(LocalDate.now())
//        .finished(true)
//        .build();
    model.addAttribute("menu","잡채밥");
    model.addAttribute("todoDTO",todoDTO);
  }

  // 리다이렉션 :
  // 1) 페이지 전환,
  // 2) 값을 전달시, 일회용으로 전달하는 기법,
  @GetMapping("/ex7")
  public String ex7Test(RedirectAttributes redirectAttributes) {
    log.info("ex6 test...");

    // 페이지 이동도 하면서, 데이터 전달. 전달시, 1회용으로 도 전달.
    // 데이터 전달 , 쿼리 스트링, 파라미터에 값을 전달.
    // 결과 확인, URL 주소를 잘 확인 하기.
    // /ex8?menu=한글 깨짐
    // /ex8?menu2=tomorrow lunch menu lamen
    // 데이터 유지가 됨.
    redirectAttributes.addAttribute("menu","내일 점심 칼국수");
    redirectAttributes.addAttribute("menu2","tomorrow lunch menu lamen");
    // 일회용 데이터 사용법.
    redirectAttributes.addFlashAttribute("result", "라면");
        // 페이지 전환,
    return "redirect:/todo/ex8";
      }

  @GetMapping("/ex8")
  public void ex8Test() {
    log.info("ex8 test...");
  }

  @GetMapping("/ex9")
  public void ex9Test(String name, int age) {
    // 강제로 예외 발생시키키 시나리오, 숫자 타입에 문자열 타입을 전달해서, 예외 발생시킴.
    log.info("ex9 test...");
    log.info("name: " + name);
    log.info("age: " + age);
  }

}







