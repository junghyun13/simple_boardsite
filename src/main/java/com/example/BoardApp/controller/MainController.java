package com.example.BoardApp.controller;

import com.example.BoardApp.dto.Board;
import com.example.BoardApp.dto.LoginInfo;
import com.example.BoardApp.dto.User;
import com.example.BoardApp.repository.BoardRepository;
import com.example.BoardApp.service.BoardService;
import com.example.BoardApp.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    @Autowired
    private BoardRepository boardRepository;

    private final UserService userService;
    private final BoardService boardService;

    // 회원가입 페이지
    @GetMapping("/userRegForm")
    public String userRegForm() {
        return "userRegForm";
    }

    // 회원가입 처리
    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        userService.addUser(name, email, password);
        return "redirect:/welcome";
    }

    // 환영 페이지
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    // 로그인 페이지
    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session
    ) {
        try {
            User user = userService.getUser(email);
            if (user.getPassword().equals(password)) {
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());
                session.setAttribute("loginInfo", loginInfo);
            } else {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception ex) {
            return "redirect:/loginform?error=" + ex.getMessage();
        }
        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 게시글 목록
    @GetMapping("/")
    public String list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            HttpSession session,
            Model model
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo);

        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page);
        int pageCount = (totalCount + 9) / 10;

        System.out.println("게시글 총 개수: " + totalCount);
        System.out.println("게시글 목록: " + list);
        System.out.println("현재 페이지: " + page);

        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", page);

        return "listin";
    }


    // 게시글 상세 보기
    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId, Model model) {
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board";
    }

    // 게시글 작성 폼
    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo", loginInfo);
        return "writeForm";
    }

    // 게시글 작성
    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("tags") String tags,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");

        // 세션에서 로그인 정보 확인
        if (loginInfo == null) {
            System.out.println("로그인 정보가 없습니다. 로그인 페이지로 리다이렉트합니다.");
            return "redirect:/loginform";
        }
        List<String> tagList = Arrays.asList(tags.split(","));
        // 로그로 데이터 출력
        System.out.println("로그인 사용자 ID: " + loginInfo.getUserId());
        System.out.println("제목: " + title);
        System.out.println("내용: " + content);
        System.out.println("테크: " + tags);

        try {
            // 게시글 저장 로직 호출
            boardService.addBoard(loginInfo.getUserId(), title, content,tagList);
            System.out.println("게시글이 정상적으로 저장되었습니다.");
        } catch (Exception e) {
            // 예외 발생 시 로그 출력
            System.out.println("게시글 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 자세한 예외 정보 출력
            return "redirect:/writeForm?error=" + e.getMessage();
        }

        // 정상적으로 저장된 경우 메인 페이지로 리다이렉트
        return "redirect:/";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("tag") String tag,
            Model model
    ) {
        List<Board> boards = boardService.getBoardsByTag(tag);
        model.addAttribute("list", boards);
        model.addAttribute("tag", tag);
        return "search";
    }


    // 게시글 삭제
    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        boardService.deleteBoard(boardId);
        return "redirect:/";
    }

    // 게시글 수정 폼
    @GetMapping("/updateform")
    public String updateform(
            @RequestParam("boardId") int boardId,
            HttpSession session,
            Model model
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }

    // 게시글 수정
    @PostMapping("/update")
    public String update(
            @RequestParam("boardId") int boardId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId=" + boardId;
    }
}
