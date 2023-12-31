package com.example.Board.Controller;

import com.example.Board.Dto.BoardDto;
import com.example.Board.Service.BoardService;
import com.example.Member.entity.Member;
import com.example.Member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.Board.Dto.PageRequestDTO;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

//@Tag(name = "Board API", description = "Board API")
@Controller
@Log4j2
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
//    private final MemberService memberService;

//    @Operation(summary = "Redirect to List", description = "Redirect to the list page.")
    @GetMapping("/")
    public String index() {
        return "redirect:/board/list";
    }


//    @Operation(summary = "Get Board List", description = "Get a list of all boards.")
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        model.addAttribute("result", boardService.getList(pageRequestDTO));
    }

    //게시물 등록
    @GetMapping("/register")
    public void register() {
    }

    @Transactional
    @PostMapping("/register")
    public String registerPost(BoardDto dto, RedirectAttributes redirectAttributes) {
        // 작성자 정보로 실제 Member 엔터티를 찾아옴
//        Member writer = memberService.findMemberById(dto.getWriter().getMemId());
//
//        // BoardDto에 작성자 정보 설정
//        dto.setWriter(writer);
        Long bno = boardService.register(dto);
        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";
    }

//    @GetMapping("/register")
//    public String register(Model model, Principal principal) {
//        if (principal != null) {
//            String username = principal.getName();
//            model.addAttribute("username", username);
//        }
//        return "board/register";
//    }
//
//    @Transactional
//    @PostMapping("/register")
//    public String registerPost(BoardDto dto, RedirectAttributes redirectAttributes) {
//        // 작성자 정보로 실제 Member 엔터티를 찾아옴
//        Member writer = dto.getWriter();
//
//        // BoardDto에 작성자 정보 설정
//        dto.setWriter(writer);
//        Long bno = boardservice.register(dto);
//        redirectAttributes.addFlashAttribute("msg", bno);
//
//        return "redirect:/board/list";
//    }

//    @GetMapping("/read")
//    public void read(long bno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) {
//
//        log.info("bno:" + bno);
//        BoardDto dto = service.read(bno);
//
//        model.addAttribute("dto", dto);
//    }

    @GetMapping({"/read", "/modify"})
    public void read(long bno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model) {

        BoardDto dto = boardService.read(bno);

        model.addAttribute("dto", dto);
    }

    //게시글 삭제
    @PostMapping("/remove")
    public String remove(long bno, RedirectAttributes redirectAttributes) {
        boardService.remove(bno);

        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";
    }

    //게시글 수정
    @PostMapping("/modify")
    public String modify(BoardDto dto, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes) {

        boardService.modify(dto);

        redirectAttributes.addAttribute("page", requestDTO.getPage());
        redirectAttributes.addAttribute("type", requestDTO.getType());
        redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());
        redirectAttributes.addAttribute("bno", dto.getBno());

        return "redirect:/board/list";
    }


    //조회수
    @PostMapping("/updateViewCount")
    @ResponseBody
    public ResponseEntity<Long> updateViewCount(@RequestParam Long bno) {
        Long updatedViewCount = boardService.updateViewCount(bno);
        return ResponseEntity.ok(updatedViewCount);
    }


//    @GetMapping("/board/list/{category}")
//    public String getFilteredBoardList(@PathVariable String category, Model model) {
//        // 서버 측에서 해당 카테고리에 해당하는 게시물을 필터링하여 가져옴
//        List<BoardDto> filteredList = boardService.getFilteredBoardList(category);
//
//        // 모델에 필터링된 목록을 추가
//        model.addAttribute("result", filteredList);
//
//        return "board/list";
//    }




}


